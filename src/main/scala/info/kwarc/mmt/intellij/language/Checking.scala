package info.kwarc.mmt.intellij.language

import java.awt.{BorderLayout, Font}
import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent}

import com.intellij.lang.annotation.{AnnotationHolder, ExternalAnnotator}
import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.fileEditor.{FileEditorManager, OpenFileDescriptor}
import com.intellij.openapi.util.TextRange
import com.intellij.psi.{PsiDocumentManager, PsiFile}
import com.intellij.ui.treeStructure.{PatchedDefaultMutableTreeNode, Tree}
import info.kwarc.mmt
import info.kwarc.mmt.utils.{File, Reflection, URI}
import info.kwarc.mmt.intellij.ui.AbstractErrorViewer
import info.kwarc.mmt.intellij.ui.MMTToolWindow
import info.kwarc.mmt.intellij.{MMT, MMTJar}
import info.kwarc.mmt.utils
import javax.swing.tree._
import javax.swing._

class ExtAnnotator extends ExternalAnnotator[Option[MMT],Option[MMT]] {

  override def apply(psifile: PsiFile, mmtO: Option[MMT], holder: AnnotationHolder): Unit = mmtO match {
    case Some(mmt) if mmt.errorViewer.doCheck =>
      val mmtjar = mmt.mmtjar
      object Jar {
        private val cls = mmtjar.reflection.getClass("info.kwarc.mmt.intellij.checking.Checker")
        private val checker = mmtjar.method("checker",Reflection.Reflected(cls),Nil)
        // private val checkerclass = mmtjar.classLoader.loadClass("info.kwarc.mmt.intellij.checking.Checker")
        // private val jarchecker = mmtjar.method("checker")
        def check(uri : URI, text : String,
                  clearFile: String => Unit,
                  note: (String,String) => Unit,
                  errorCont : (Int,Int,String,String,List[String]) => Unit
                 ) = {
          val cf = new Reflection.RFunction {
            def apply(v1 : String) : Unit = clearFile(v1)
          }
          val nt = new Reflection.RFunction {
            def apply(v1 : String, v2 : String) : Unit = note(v1,v2)
          }
          val ec = new Reflection.RFunction {
            def apply(v1 : Int, v2 : Int, v3 : String, v4 : String, v5 : List[String]) : Unit =
              errorCont(v1,v2,v3,v4,v5)
          }
          checker.method("check",Reflection.unit,List(uri.toString,text,cf,nt,ec))
        }

      }
      var not : Option[Notification] = None
      val uri = URI(psifile.getVirtualFile.toString)
      val text = psifile.getText
      val clearFile : String => Unit = { f =>
        mmt.errorViewer.clearFile(File(f))
      }
      val note : (String,String) => Unit = { case (str,file) =>
        not match {
          case Some(n) => n.expire()
          case _ =>
        }
        not = Some(new Notification("MMT",File(file).name,str,NotificationType.INFORMATION))
        Notifications.Bus.notify(not.get)
        if (str == "Done.") {
          Thread.sleep(1000)
          not.get.expire()
          not = None
        }
      }
      val error : (Int,Int,String,String,List[String]) => Unit = { case (start,length,file,main,extra) =>
        val tr = TextRange.from(psifile.getTextRange.getStartOffset + start,length)
        val int = psifile.getTextRange.intersection(tr)
        val region = if (int != null) int else psifile.getTextRange
        holder.createErrorAnnotation(region,main)
        mmt.errorViewer.addError(main,extra,psifile,File(file),region)
      }
      Jar.check(uri,text,clearFile,note,error)
    case _ =>
  }

  override def collectInformation(file: PsiFile, editor: Editor, hasErrors: Boolean): Option[MMT] = {
    MMT.get(editor.getProject)
  }

  override def doAnnotate(collectedInfo: Option[MMT]): Option[MMT] = {
    collectedInfo
  }

}

class ErrorViewer(mmtjar : MMTJar) extends ActionListener with MMTToolWindow {
  private val aev = new AbstractErrorViewer
  private object Jar {
    private val cls = mmtjar.reflection.getClass("info.kwarc.mmt.intellij.checking.ErrorViewer")
    private val jarev = mmtjar.method("errorViewer",Reflection.Reflected(cls),Nil)
    def clearFile(file : File) = jarev.method("clearFile",Reflection.unit,List(file.toString))
  }

  val panel: JPanel = aev.panel
  val displayName: String = "Errors"

  val root = new PatchedDefaultMutableTreeNode("Errors")
  val errorTree = new Tree(root)
  aev.btn_clear.addActionListener(this)
  aev.btn_clearAll.addActionListener(this)
  aev.check.addActionListener(this)
  ApplicationManager.getApplication.invokeLater { () =>
    aev.pane.setLayout(new BorderLayout())
    val scp = new JScrollPane(errorTree)
    aev.pane.add(scp)
    errorTree.setVisible(true)
    errorTree.setRootVisible(false)
    errorTree.revalidate()
    aev.panel.revalidate()
  }
  // errorTree.setFont(new Font("Dialog",Font.PLAIN,12))

  def doCheck = aev.check.isSelected
  errorTree.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      super.mouseClicked(e)
      val path = errorTree.getPathForLocation(e.getX,e.getY)
      if (path != null) path.getPath.lastOption match {
        case Some(el : ErrorLine) =>
          val elem = el.psiFile.findElementAt(el.textrange.getStartOffset)
          val descriptor= new OpenFileDescriptor(el.psiFile.getProject,el.psiFile.getContainingFile.getVirtualFile)
          val editor = FileEditorManager.getInstance(el.psiFile.getProject).openTextEditor(descriptor,true)
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
    Jar.clearFile(file)
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
    val top = getTop(file) /*
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
    } */
    top.add(node)
    // val row = errorTree.getRowForPath(new TreePath(Array(root,top,top.getLastChild).asInstanceOf[Array[Object]]))
    // errorTree.setSelectionRow(row)
    redraw
  }

  def status(file : File,s : String) = add(file,StatusLine(s))

  class ErrorLine(message : String,val textrange : TextRange,val psiFile: PsiFile) extends PatchedDefaultMutableTreeNode(message) {
    def addLine(s : String) = add(new PatchedDefaultMutableTreeNode(s))
  }
  case class StatusLine(message : String) extends PatchedDefaultMutableTreeNode(message)

  def addError(short : String, long : List[String], psifile : PsiFile, file : File, sr : TextRange) = {
    //val filetop = getTop(file)
    val entry = new ErrorLine(short.trim,sr,psifile)
    long.reverse.foreach(s => entry.addLine(s.trim))
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
      mmtjar.clear
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