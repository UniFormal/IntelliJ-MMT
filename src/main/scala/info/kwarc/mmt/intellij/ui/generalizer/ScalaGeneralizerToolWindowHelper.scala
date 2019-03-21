package info.kwarc.mmt.intellij.ui.generalizer

import java.awt.event.{MouseAdapter, MouseEvent}

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode
import info.kwarc.mmt.intellij.MMT
import info.kwarc.mmt.intellij.ui.GeneralizerToolWindow
import info.kwarc.mmt.utils.Reflection
import javax.swing.JTree
import javax.swing.tree.TreePath

class ScalaGeneralizerToolWindowHelper(var project: Project) {
  var generalizerToolWindow: GeneralizerToolWindow = _

  def init(generalizerToolWindow: GeneralizerToolWindow): Unit = {
    this.generalizerToolWindow = generalizerToolWindow
  }

  def generalize(pathToR: String, pathToS: String, pathToRToS: String, pathToT: String): Unit = {
    MMT.get(project).foreach(mmt => {

      val rootNode = generalizerToolWindow.getErrorTreeRootNode
      rootNode.removeAllChildren()

      val generalizedCode = mmt.mmtjar.method(
        "generalize",
        Reflection.string,
        List(
          rootNode,
          pathToR,
          pathToS,
          pathToRToS,
          pathToT
        )
      )

      generalizerToolWindow.refreshErrorTree()
      generalizerToolWindow.setGeneralizedCode(generalizedCode)
    })
  }
}

class TextRangeReferencingTreeNode[T](val userData: T, val file: PsiFile, val textRange: TextRange) extends PatchedDefaultMutableTreeNode(userData) {

}

object TreeUtils {
  def addDoubleClickListenerToTree(tree: JTree, handler: TreePath => Unit): Unit = {
    tree.addMouseListener(new MouseAdapter {
      override def mouseClicked(e: MouseEvent): Unit = {
        super.mouseClicked(e)

        // TODO Check for primary (not necessarily left) mouse button

        val selRow = tree.getRowForLocation(e.getX, e.getY)
        val selPath = tree.getPathForLocation(e.getX, e.getY)
        if (selRow != -1) {
          if (e.getClickCount == 1) {
            // mySingleClick(selRow, selPath)
          }
          else if (e.getClickCount == 2) {
            handler(selPath)
          }
        }
      }
    })
  }
}