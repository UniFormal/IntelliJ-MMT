package info.kwarc.mmt.intellij.ui.generalizer

import java.awt.event.{MouseAdapter, MouseEvent}

import com.intellij.openapi.project.Project
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiFile
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode
import info.kwarc.mmt.intellij.ui.GeneralizerToolWindow
import info.kwarc.mmt.intellij.{MMT, MMTJar}
import info.kwarc.mmt.utils.Reflection
import javax.swing.JTree
import javax.swing.tree.{DefaultMutableTreeNode, TreePath}

trait GeneralizerReflectionInterface {
  /**
    *
    * @param declaration Intended: GlobalName
    * @param entryText   Intended:
    * @param longDescription
    * @param file
    * @param textRangeStart
    * @param textRangeLength
    */
  def reportError(declaration: String, entryText: String, longDescription: String, file: String, textRangeStart: Int, textRangeLength: Int)

  def reportGeneralizedTheory(code: String)
}

class ScalaGeneralizerToolWindowHelper(var project: Project) {
  var generalizerToolWindow: GeneralizerToolWindow = _

  def init(generalizerToolWindow: GeneralizerToolWindow): Unit = {
    this.generalizerToolWindow = generalizerToolWindow
  }

  def delegateGeneralizeToReflection(
                                      mmtjar: MMTJar,
                                      rootNode: DefaultMutableTreeNode
                                    ): String = {
    /*mmtjar.method("generalize", Reflection.unit, List(
      callbacks.reportGeneralizedTheory _,
      callbacks.reportError _
    ))*/
    val generalizedCode = mmtjar.method("generalize", Reflection.string, List(rootNode))
    generalizedCode
  }

  def generalize(pathToT: String, pathToS: String, pathToR: String, pathToRToS: String): Unit = {
    MMT.get(project).foreach(mmt => {

      generalizerToolWindow.getErrorTreeRootNode.removeAllChildren()

      val generalizedCode = delegateGeneralizeToReflection(mmt.mmtjar, generalizerToolWindow.getErrorTreeRootNode)

      generalizerToolWindow.setGeneralizedCode(generalizedCode)
      // mmt.mmtjar

      // val newTheoryString: String = ""
      /*val error: (Int, Int, String, String, List[String]) => Unit = {
        case (start, length, file, main, extra) =>
          val tr = TextRange.from(psifile.getTextRange.getStartOffset + start, length)
          val int = psifile.getTextRange.intersection(tr)
          val region = if (int != null) int else psifile.getTextRange
          if (main.startsWith("Warning")) {
            holder.createWarningAnnotation(region, main)
          } else {
            holder.createErrorAnnotation(region, main)
            mmt.errorViewer.addError(main, extra, psifile, File(file), region)
          }
      }
      mmt.logged("Checking " + uri) {
        Jar.check(uri, text, clearFile, note, error)
        mmt.errorViewer.checkBtn.setSelected(false)
      }*/
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