package info.kwarc.mmt.intellij.Language

import java.awt.BorderLayout
import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent}

import com.intellij.lang.annotation.{AnnotationHolder, ExternalAnnotator}
import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.{FileEditorManager, OpenFileDescriptor}
import com.intellij.openapi.project.{Project, ProjectManager}
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.psi.{PsiDocumentManager, PsiFile, PsiManager}
import com.intellij.ui.treeStructure.{PatchedDefaultMutableTreeNode, Tree}
import info.kwarc.mmt.api
import info.kwarc.mmt.api.documents.{Document, MRef}
import info.kwarc.mmt.api.frontend.Controller
import info.kwarc.mmt.api.{DPath, ErrorHandler, MMTTaskProgress, MMTTaskProgressListener, Parsed, Path, utils}
import info.kwarc.mmt.api.parser.{ParsingStream, SourceRegion}
import info.kwarc.mmt.api.symbols.{Declaration, FinalConstant, Structure}
import info.kwarc.mmt.api.utils.{File, FilePath, URI}
import info.kwarc.mmt.intellij.{MMT, MMTProjectTemplate}
import javax.swing.tree._
import javax.swing._

import scala.collection.mutable

object Conversions {
  implicit def convert(sr : SourceRegion, psi : PsiFile) : TextRange = {
    val start = sr.start.offset
    val length = sr.length
    val tr = TextRange.from(psi.getTextRange.getStartOffset + start,length)
    val int = psi.getTextRange.intersection(tr)
    if (int != null) int else psi.getTextRange
  }
}

import Conversions._
import info.kwarc.mmt.intellij.util._

class ExtAnnotator extends ExternalAnnotator[Option[MMT],Option[MMT]] {
  override def apply(psifile: PsiFile, mmtO: Option[MMT], holder: AnnotationHolder): Unit = mmtO match {
    case Some(mmt) if mmt.errorViewer.doCheck =>
      val uri = URI(psifile.getVirtualFile.toString)
      val file = utils.FileURI.unapply(uri).getOrElse(return)
      //val uri = utils.FileURI(file)
      val text = psifile.getText
      val nsMap = mmt.controller.getNamespaceMap
      val ps = mmt.controller.backend.resolvePhysical(file) orElse mmt.controller.backend.resolveAnyPhysicalAndLoad(file) match {
        case None =>
          ParsingStream.fromString(text, DPath(uri), file.getExtension.getOrElse(""), Some(nsMap))
        case Some((a, p)) =>
          ParsingStream.fromSourceFile(a, FilePath(p), Some(ParsingStream.stringToReader(text)), Some(nsMap))
      }
      val progress = new Progresser(file)(holder,mmt.errorViewer)
      ps.addListener(progress)
      mmt.errorViewer.clearFile(file)
      val error = new ErrorForwarder(psifile,file,mmt.controller,holder,mmt.errorViewer)
      // background {
        val doc = mmt.controller.read(ps, true, true)(error) match {
          case d: Document =>
            progress.done(d)
          case _ => //throw ImplementationError("document expected")
        }
        // add narrative structure of doc to outline tree
        // val tree = new SideKickParsedData(path.toJava.getName)
        // val root = tree.root
        // buildTreeDoc(root, doc)
        // tree
      // }
    case _ =>
  }

  override def collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): Option[MMT] = {
    MMT.get(editor.getProject)
  }

  override def doAnnotate(collectedInfo: Option[MMT]): Option[MMT] = {
    // println("Here 2")
    collectedInfo
  }

}

class Progresser(file : File)(implicit holder : AnnotationHolder,ev : ErrorViewer) extends MMTTaskProgressListener {
  // import Attributes._
  private var not : Option[Notification] = None
  override def apply(p: MMTTaskProgress): Unit = p match {
    case Parsed(s) =>
      // ev.status(file,"Checking: " + c.path)
      not match {
        case Some(n) => n.expire()
        case _ =>
      }
      val str = s match {
        case c : Declaration => c.path.module.name + "?" + c.name
        case _ => s.path.name
      }
      not = Some(new Notification("MMT",file.name,"Checking " + str,NotificationType.INFORMATION))
      Notifications.Bus.notify(not.get)
      /*
      SourceRef.get(c) match {
        case Some(r) =>

          // implicit val psi = file.findElementAt(r.region.start.offset)
          // holder.createAnnotation(HighlightSeverity.WARNING,r.region,"Checking...")
          // highlight(active)

        case _ =>
      }
      */
    case _ =>
  }
  def done(d : Document) = {
    not match {
      case Some(n) => n.expire()
      case _ =>
    }
    not = Some(new Notification("MMT",file.name,"Done.",NotificationType.INFORMATION))
    Notifications.Bus.notify(not.get)
    Thread.sleep(1000)
    not.get.expire()
    not = None
    ev.finish(file,d)
  }
}

case class PluginError(e : api.Error,psifile : PsiFile, file : File) {
  import info.kwarc.mmt.api._
  import archives.source
  import objects._
  import parser._
  import utils.MyList._

  private lazy val mmt = MMT.get(psifile.getProject).get
  lazy val (region,main,extra) = e match {
    case s: SourceError =>
      (convert(s.ref.region,psifile),s.mainMessage,s.extraMessages)
    case e:Invalid =>
      var mainMessage = e.shortMsg
      var extraMessages : List[String] = e.extraMessage.split("\n").toList
      val causeOpt: Option[metadata.HasMetaData] = e match {
        case e: InvalidObject => Some(e.obj)
        case e: InvalidElement => Some(e.elem)
        case e: InvalidUnit =>
          val steps = e.history.getSteps
          extraMessages :::= steps.map(_.present(o => mmt.controller.presenter.asString(o)))
          val declOpt = e.unit.component.map(p => mmt.controller.localLookup.get(p.parent))
          // WFJudgement must exist because we always start with it
          // find first WFJudgement whose region is within the failed checking unit
          declOpt.flatMap {decl =>
            SourceRef.get(decl).flatMap {bigRef =>
              steps.mapFind {s =>
                s.removeWrappers match {
                  case j: WFJudgement =>
                    SourceRef.get(j.wfo) flatMap {smallRef =>
                      if (bigRef contains smallRef) {
                        mainMessage += ": " + mmt.controller.presenter.asString(j.wfo)
                        Some(j.wfo)
                      } else
                        None
                    }
                  case _ =>
                    None
                }
              }
            }.orElse(declOpt)
          }
      }
      val ref = causeOpt.flatMap {cause => SourceRef.get(cause)}.getOrElse {
        mainMessage = "error with unknown location: " + mainMessage
        SourceRef(utils.FileURI(file), SourceRegion(SourcePosition(0,0,0), SourcePosition(0,0,0)))
      }
      (convert(ref.region,psifile),mainMessage,extraMessages)
    case e: Error =>
      (TextRange.EMPTY_RANGE,"error with unknown location: " + e.getMessage,e.extraMessage.split("\n").toList)
  }
  def passOn(holder : AnnotationHolder) = holder.createErrorAnnotation(region,main)
  def passOn(ev : ErrorViewer) = ev.addError(main,extra,psifile,file,region)
}

class ErrorForwarder(psifile : PsiFile, file: File, controller : Controller,holder : AnnotationHolder, ev : ErrorViewer) extends ErrorHandler {

  override protected def addError(e: api.Error): Unit = {
    val err = PluginError(e,psifile,file)
    err.passOn(holder)
    err.passOn(ev)
  }
}

class ErrorViewer(controller : Controller) extends ActionListener {
  private val docs : mutable.HashMap[File,List[Path]] = mutable.HashMap.empty
  def finish(file : File, doc : Document) = {
    val ls = doc.path :: doc.getDeclarations.collect {
      case r : MRef => r.target
    }
    docs.update(file,ls)
  }

  val aev = new AbstractErrorViewer
  aev.btn_clear.addActionListener(this)
  aev.btn_clearAll.addActionListener(this)
  aev.check.addActionListener(this)
  val root = new PatchedDefaultMutableTreeNode("Errors")
  val errorTree = new Tree(root)
  ApplicationManager.getApplication.invokeLater { () =>
    aev.pane.setLayout(new BorderLayout())
    val scp = new JScrollPane(errorTree)
    aev.pane.add(scp)
    errorTree.setVisible(true)
    errorTree.setRootVisible(false)
    errorTree.revalidate()
    aev.panel.revalidate()
  }
  def doCheck = aev.check.isSelected
  errorTree.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      super.mouseClicked(e)
      val path = errorTree.getPathForLocation(e.getX,e.getY)
      if (path != null) path.getPath.lastOption match {
        case Some(ErrorLine(_,tr,psi)) =>
          val elem = psi.findElementAt(tr.getStartOffset)
          val descriptor= new OpenFileDescriptor(psi.getProject,psi.getContainingFile.getVirtualFile)
          val editor = FileEditorManager.getInstance(psi.getProject).openTextEditor(descriptor,true)
          editor.getCaretModel.moveToOffset(elem.getTextOffset)
        case _ =>
      }
    }
  })
  private def model = errorTree.getModel.asInstanceOf[DefaultTreeModel]

  private def redraw = ApplicationManager.getApplication.invokeLater { () =>
    model.reload()
    errorTree.revalidate()
  }

  implicit def convert[A](e : java.util.Enumeration[A]): List[PatchedDefaultMutableTreeNode] = {
    var ls = Nil.asInstanceOf[List[PatchedDefaultMutableTreeNode]]
    while (e.hasMoreElements) ls ::= e.nextElement().asInstanceOf[PatchedDefaultMutableTreeNode]
    ls.reverse
  }

  def clearFile(file : File) = {
    root.children().find(_.getUserObject == file).foreach(root.remove)
    docs.get(file).foreach(_.foreach(controller.delete))
    redraw
  }

  private def getTop(file: File) = root.children().find(_.getUserObject == file).getOrElse {
    val nt = new PatchedDefaultMutableTreeNode(file)
    root.add(nt)
    redraw
    errorTree.expandPath(new TreePath(nt.getPath.asInstanceOf[Array[Object]]))
    nt
  }

  private def add(file : File,node : PatchedDefaultMutableTreeNode) = synchronized {
    val top = getTop(file)
    (top.getLastLeaf,node) match {
      case (s : StatusLine,t : StatusLine) =>
        top.remove(s)
        top.add(node)
      case (s : StatusLine,_) =>
        top.remove(s)
        top.add(node)
        top.add(s)
      case (_,_) =>
        top.add(node)
    }
    redraw
    val row = errorTree.getRowForPath(new TreePath(Array(root,top,top.getLastChild).asInstanceOf[Array[Object]]))
    errorTree.setSelectionRow(row)
  }

  def status(file : File,s : String) = add(file,StatusLine(s))

  case class ErrorLine(message : String,tr : TextRange,psiFile: PsiFile) extends PatchedDefaultMutableTreeNode(message) {
    def addLine(s : String) = add(new PatchedDefaultMutableTreeNode(s))
  }
  case class StatusLine(message : String) extends PatchedDefaultMutableTreeNode(message)

  def addError(short : String, long : List[String], psifile : PsiFile, file : File, sr : TextRange) = {
    //val filetop = getTop(file)
    val entry = ErrorLine(short,sr,psifile)
    long.reverse.foreach(entry.addLine)
    add(file,entry)
    // filetop.add(entry)
    // redraw
  }

  override def actionPerformed(e: ActionEvent): Unit = {
    if (e.getActionCommand == "Clear") {
      root.removeAllChildren()
      redraw
    } else if (e.getActionCommand == "Clear All") {
      root.removeAllChildren()
      controller.clear
      redraw
    } else if (e.getActionCommand == "Type Checking" && aev.check.isSelected) {
      MMT.getProject match {
        case Some(pr) =>
          val editor = FileEditorManager.getInstance(pr).getSelectedTextEditor
          val psifile = PsiDocumentManager.getInstance(pr).getPsiFile(editor.getDocument)
          // ???
        case _ =>
      }
    }
  }
}