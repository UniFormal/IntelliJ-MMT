package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.ex.EditorEx
import com.intellij.openapi.fileTypes.FileType
import com.intellij.openapi.project.Project
import com.intellij.ui.EditorTextField

class ScrollableMultilineEditorTextField(val document: Document, val project: Project, val fileType: FileType, override val isViewer: Boolean) extends EditorTextField(document, project, fileType, isViewer, false) {
  override protected def createEditor: EditorEx = {
    val editor = super.createEditor
    editor.setVerticalScrollbarVisible(true)
    editor.setHorizontalScrollbarVisible(true)
    editor.setCaretEnabled(true)
    editor
  }
}