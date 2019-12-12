package info.kwarc.mmt.intellij.ui

import java.awt.datatransfer.StringSelection
import java.awt.{Font, MouseInfo, Point, Toolkit}
import java.awt.event.{ActionEvent, ActionListener, MouseAdapter, MouseEvent, MouseListener}

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.editor.event.{CaretEvent, CaretListener}
import com.intellij.openapi.fileEditor.{FileEditorManager, OpenFileDescriptor}
import com.intellij.psi.{PsiDocumentManager, PsiFile}
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.{PatchedDefaultMutableTreeNode, Tree}
import info.kwarc.mmt.intellij.{MMT, MMTJar}
import info.kwarc.mmt.utils.Reflection
import javax.swing.tree.{DefaultMutableTreeNode, DefaultTreeModel, TreePath}
import javax.swing.{BoxLayout, JCheckBox, JMenuItem, JPanel, JPopupMenu, JScrollPane, SwingUtilities}

class Sidekick(mmtjar: MMTJar) extends MMTToolWindow with ActionListener with CaretListener {
  override val displayName: String = "Document Tree"
  val panel = new JPanel

  val cb = new JCheckBox("Navigate")

  val root = new PatchedDefaultMutableTreeNode("Document Tree")
  val docTree = new Tree(root)
  val scp = new JBScrollPane(docTree)

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

  docTree.setComponentPopupMenu(createContextMenu())
  docTree.setInheritsPopupMenu(true)

  docTree.addMouseListener(new MouseAdapter {
    override def mouseClicked(e: MouseEvent): Unit = {
      super.mouseClicked(e)

      if (!e.isPopupTrigger && e.getButton == MouseEvent.BUTTON1) {
        getPointedToNodeAndElement(e.getX, e.getY).map {
          case (_, element) => navigateTo(element)
        }
      }
    }
  })

  private def model = docTree.getModel.asInstanceOf[DefaultTreeModel]

  def doDoc(dS: String): Unit = ApplicationManager.getApplication.invokeLater { () =>
    root.removeAllChildren()
    mmtjar.method("syntaxTree", Reflection.unit, List(root, dS))
    model.reload()
    docTree.revalidate()
  }

  private def redraw(): Unit = ApplicationManager.getApplication.invokeLater { () =>
    model.reload()
    docTree.revalidate()
  }

  private var current: Option[PsiFile] = None

  def setFile(f: PsiFile): Unit = {
    current = Some(f)
  }

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

  /**
   * @see [[getPointedToNodeAndElement(x: Int, y: Int)]]
   */
  private def getPointedToNodeAndElement(p: Point): Option[(DefaultMutableTreeNode, Element)] = {
    getPointedToNodeAndElement(p.x, p.y)
  }

  /**
   * Get the node together with the user data element which is pointed to by (x, y) in the sidekick [[docTree]].
   *
   * If there is no associated data element, None is returned.
   */
  private def getPointedToNodeAndElement(x: Int, y: Int): Option[(DefaultMutableTreeNode, Element)] = {
    val path = docTree.getPathForLocation(x, y)
    if (path != null && current.isDefined) {
      path.getPath.lastOption.map {
        case n: DefaultMutableTreeNode =>
          val element = n.getUserObject.asInstanceOf[Element]

          (n, element)
      }
    } else {
      None
    }
  }

  /**
   * Create the context menu for [[docTree]] with appropriate menu items and action listeners.
   * @return A JPopupMenu you should give to the [[docTree]] via [[docTree.setComponentPopupMenu()]] while having
   *         set [[docTree.setInheritsPopupMenu()]] to true.
   */
  private def createContextMenu(): JPopupMenu = {
    val contextMenu = new JPopupMenu

    val navigateToItem = new JMenuItem("Navigate to...")
    val copyPathItem = new JMenuItem("Copy path")
    val showSyntaxItem = new JMenuItem("Generate Surface Syntax")

    contextMenu.add(navigateToItem)
    contextMenu.add(copyPathItem)
    contextMenu.add(showSyntaxItem)

    // Capture clicked position for context menu actions
    // ====================================================
    // Since we want to have context menu enablance and context menu actions
    // depending on the clicked node in [[docTree]], we must capture the clicked
    // position as a listener to [[docTree]].

    var lastMouseClickedPoint = new Point(0, 0) // relative to [[docTree]]

    docTree.addMouseListener(new MouseAdapter {
      override def mousePressed(e: MouseEvent): Unit = {
        // NB: mousePressed is also fired on right-click, mouseClicked apparently not with set popup menu!
        super.mousePressed(e)

        // retrieve clicked point relative to [[docTree]]
        // shortly copied from https://stackoverflow.com/a/15239030
        // Thanks to author Cody Smith <https://stackoverflow.com/users/1476289/cody-smith>
        val point = MouseInfo.getPointerInfo.getLocation
        SwingUtilities.convertPointFromScreen(point, docTree)
        lastMouseClickedPoint = point

        // Activate and deactivate context menu items now as needed
        val element = getClickedElement()

        val pathAvailable = element match {
          case Some(element) => element.getPath.isDefined
          case None => false
        }

        navigateToItem.setEnabled(true)
        copyPathItem.setEnabled(pathAvailable)
        showSyntaxItem.setEnabled(pathAvailable)
      }
    })

    /**
     * Helper function to get the clicked [[Element]] if any.
     */
    def getClickedElement(): Option[Element] = {
      getPointedToNodeAndElement(lastMouseClickedPoint) map {
        case (_, element) => element
      }
    }

    // Setup style and action listeners of menu items
    // ===============================================
    val boldFont = navigateToItem.getFont.deriveFont(Font.BOLD)
    navigateToItem.setFont(boldFont)
    navigateToItem.addActionListener(_ => getClickedElement().map(navigateTo))

    copyPathItem.addActionListener(_ => {
      getClickedElement().flatMap(_.getPath).map(path =>
        Toolkit.getDefaultToolkit.getSystemClipboard.setContents(
          new StringSelection(path),
          null
        ))
    })

    showSyntaxItem.addActionListener((_: ActionEvent) => {
      getClickedElement().flatMap(_.getPath).map(path => {
        val presentedSyntax = mmtjar.method(
          "presentSyntax",
          Reflection.string,
          List(path)
        )

        MMT.getProject.map(project =>
          new SidekickPresentedSyntaxDialog(project, path, presentedSyntax).showAndGet()
        )
      })
    })

    contextMenu
  }

  private def navigateTo(element: Element): Unit = {
    current match {
      case Some(psiFile) =>
        val elem = psiFile.findElementAt(element.getOffset)
        val descriptor = new OpenFileDescriptor(psiFile.getProject, psiFile.getContainingFile.getVirtualFile)
        val editor = FileEditorManager.getInstance(psiFile.getProject).openTextEditor(descriptor, true)
        editor.getCaretModel.moveToOffset(elem.getTextOffset)

      case None =>
    }
  }

  override def caretPositionChanged(event: CaretEvent): Unit = {
    super.caretPositionChanged(event)
    if (cb.isSelected) {
      MMT.get(event.getEditor.getProject).getOrElse(return ())
      val doc = event.getEditor.getDocument
      val man = PsiDocumentManager.getInstance(event.getEditor.getProject)
      val caret = event.getCaret
      val psi = man.getPsiFile(doc)
      if (psi != null && current.contains(psi)) {
        val offset = caret.getOffset
        findNode(offset).foreach {
          n =>
            val tp = new TreePath(n.getPath.asInstanceOf[Array[Object]])
            ApplicationManager.getApplication.invokeLater {
              () =>
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

  override def actionPerformed(actionEvent: ActionEvent): Unit = {
  }
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