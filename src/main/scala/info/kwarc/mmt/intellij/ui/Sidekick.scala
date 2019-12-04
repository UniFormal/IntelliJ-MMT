package info.kwarc.mmt.intellij.ui

import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent}

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.event.{CaretEvent, CaretListener}
import com.intellij.openapi.fileEditor.{FileEditorManager, OpenFileDescriptor}
import com.intellij.psi.{PsiDocumentManager, PsiFile}
import com.intellij.ui.treeStructure.{PatchedDefaultMutableTreeNode, Tree}
import info.kwarc.mmt.intellij.{MMT, MMTJar}
import info.kwarc.mmt.utils.Reflection
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeModel, TreePath}
import javax.swing.{BoxLayout, JCheckBox, JPanel, JScrollPane}

class Sidekick(mmtjar: MMTJar) extends MMTToolWindow with ActionListener with CaretListener {
  override val displayName: String = "Document Tree"
  val panel = new JPanel

  val cb = new JCheckBox("Navigate")

  val root = new PatchedDefaultMutableTreeNode("Document Tree")
  val docTree = new Tree(root)
  val scp = new JScrollPane(docTree)

  ApplicationManager.getApplication.invokeLater { () =>
    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS))
    panel.add(cb)
    panel.add(scp)
    cb.setSelected(false)
    cb.setVisible(true)
    docTree.setVisible(true)
    docTree.setRootVisible(false)
    docTree.revalidate()
    panel.revalidate()
  }

  private def model = docTree.getModel.asInstanceOf[DefaultTreeModel]

  def doDoc(dS: String) = ApplicationManager.getApplication.invokeLater { () =>
    root.removeAllChildren()
    mmtjar.method("syntaxTree", Reflection.unit, List(root, dS))
    model.reload()
    docTree.revalidate()
  }

  private def redraw = ApplicationManager.getApplication.invokeLater { () =>
    model.reload()
    docTree.revalidate()
  }

  private var current: Option[PsiFile] = None

  def setFile(f: PsiFile) = current = Some(f)

  /**
   * Our userdata object for syntax tree nodes.
   *
   * Keep synchronized with info.kwarc.mmt.intellij.MMTPluginInterface.TreeBuilder.Ret in UniFormal/MMT.
   */
  private type Element = {
    def getPath: Option[String]
    def getOffset: Int
    def getEnd: Int
  }

  docTree.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      super.mouseClicked(e)
      val path = docTree.getPathForLocation(e.getX, e.getY)
      if (path != null && current.isDefined) path.getPath.lastOption match {
        case Some(n: DefaultMutableTreeNode) if n.getUserObject.isInstanceOf[Element] =>
          val element = n.getUserObject.asInstanceOf[Element]

          println("clicked")

          if (e.getButton == MouseEvent.BUTTON1) {
            primaryMouseClicked(n, element)
          } else if (e.getButton == MouseEvent.BUTTON3) {
            secondaryMouseClicked(n, element)
          }
        case _ =>
      }
    }

    // Focus left-clicked element in file
    private def primaryMouseClicked(node: DefaultMutableTreeNode, element: Element): Unit = current match {
      case Some(psiFile) =>
        val elem = current.get.findElementAt(element.getOffset)
        val descriptor = new OpenFileDescriptor(current.get.getProject, current.get.getContainingFile.getVirtualFile)
        val editor = FileEditorManager.getInstance(current.get.getProject).openTextEditor(descriptor, true)
        editor.getCaretModel.moveToOffset(elem.getTextOffset)

      case None =>
    }

    // Show presented syntax of right-clicked element in a dialog
    private def secondaryMouseClicked(node: DefaultMutableTreeNode, element: Element): Unit = element.getPath match {
      case Some(path) =>
        val presentedSyntax = mmtjar.method(
          "presentSyntax",
          Reflection.string,
          List(path)
        )

        MMT.getProject.map(project =>
          new SidekickPresentedSyntaxDialog(project, path, presentedSyntax).showAndGet()
        )
      case None =>
    }
  })


  override def caretPositionChanged(event: CaretEvent): Unit = {
    super.caretPositionChanged(event)
    if (cb.isSelected) {
      val mmt = MMT.get(event.getEditor.getProject).getOrElse(return ())
      val doc = event.getEditor.getDocument
      val man = PsiDocumentManager.getInstance(event.getEditor.getProject)
      val caret = event.getCaret
      val psi = man.getPsiFile(doc)
      if (psi != null && current.contains(psi)) {
        val offset = caret.getOffset
        findNode(offset).foreach { n =>
          val tp = new TreePath(n.getPath.asInstanceOf[Array[Object]])
          ApplicationManager.getApplication.invokeLater { () =>
            if (!docTree.isCollapsed(tp)) collapseAll()
            docTree.setSelectionPath(tp)
            scp.scrollRectToVisible(docTree.getPathBounds(tp))
            // docTree.expandPath(new TreePath(n.getPath.init.asInstanceOf[Array[Object]]))
          }
        }
      }
    }
  }

  private def collapseAll(node: DefaultMutableTreeNode = root): Unit = {
    val enum = node.children()
    while (enum.hasMoreElements) {
      val next = enum.nextElement().asInstanceOf[DefaultMutableTreeNode]
      collapseAll(next)
      docTree.collapsePath(new TreePath(next.getPath.asInstanceOf[Array[Object]]))
    }
  }

  private def findNode(offset: Int, node: DefaultMutableTreeNode = root): Option[DefaultMutableTreeNode] = {
    val enum = node.children()
    while (enum.hasMoreElements) {
      val next = enum.nextElement().asInstanceOf[DefaultMutableTreeNode]
      val uo = next.getUserObject
      if (uo.isInstanceOf[Element]) {
        val e = uo.asInstanceOf[Element]
        if (e.getOffset <= offset && offset <= e.getEnd) return Some(findNode(offset, next).getOrElse(next))
      }
    }
    None
  }

  override def actionPerformed(actionEvent: ActionEvent): Unit = {}
}

/*
class SidekickListener extends TypedActionHandler {
  override def execute(editor: Editor, charTyped: Char, dataContext: DataContext): Unit = {
    val mmt = MMT.get(editor.getProject).getOrElse(return ())
    val psi = PsiDocumentManager.getInstance(editor.getProject).getPsiFile(editor.getDocument)
    if (psi!=null) mmt.sidekick.navigate(psi,editor.getCaretModel.getOffset)
  }
}
*/