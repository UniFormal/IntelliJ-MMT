package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory, ToolWindowManager}
import com.intellij.ui.content.ContentFactory
import javax.swing.JPanel

class DummyToolWindow extends ToolWindowFactory {
  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {}
}

trait MMTToolWindow {
  val panel: JPanel
  val displayName: String

  def init(project: Project, id : String): Unit = {
    val content = ContentFactory.SERVICE.getInstance().createContent(panel, displayName, false)
    // background {
    panel.setVisible(true)
    val tw = ToolWindowManager.getInstance(project).getToolWindow(id)
    tw.getContentManager.addContent(content)
    // }
  }
}
