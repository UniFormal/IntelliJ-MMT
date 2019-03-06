package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.content.ContentFactory
import info.kwarc.mmt.intellij.MMT

class GeneralizerToolWindowFactory extends ToolWindowFactory {
    override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {
        val myToolWindow = new GeneralizerToolWindow(project)
        val contentFactory = ContentFactory.SERVICE.getInstance

        val content = contentFactory.createContent(
            myToolWindow.getContent,
            "",
            false
        )

        // MMT.get(project).get.mmtjar.handleLine()
        toolWindow.getContentManager.addContent(content)
    }
}
