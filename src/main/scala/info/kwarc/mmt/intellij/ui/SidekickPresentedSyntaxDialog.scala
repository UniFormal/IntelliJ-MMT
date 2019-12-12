package info.kwarc.mmt.intellij.ui

import java.awt.BorderLayout

import com.intellij.openapi.editor.EditorFactory
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import info.kwarc.mmt.intellij.language.MMTFileType
import javax.swing.{JComponent, JPanel}

class SidekickPresentedSyntaxDialog(project: Project, presentedPath: String, presentedSyntax: String) extends DialogWrapper(project) {
  init()
  setTitle(s"Presented Syntax of ${presentedPath}")

  override def createCenterPanel(): JComponent = {
    val dialogPanel: JPanel = new JPanel(new BorderLayout)

    val editor = new ScrollableMultilineEditorTextField(
      EditorFactory.getInstance().createDocument(presentedSyntax),
      project,
      MMTFileType.INSTANCE,  // file type
      true          // readonly
    );

    dialogPanel.add(editor, BorderLayout.CENTER)

    dialogPanel
  }
}
