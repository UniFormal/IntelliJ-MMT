package info.kwarc.mmt.intellij.ui.generalizer

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.content.ContentFactory
import info.kwarc.mmt.intellij.ui.GeneralizerToolWindow

class GeneralizerToolWindowFactory extends ToolWindowFactory {
    override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {
        val scalaToolWindowHelper = new ScalaGeneralizerToolWindowHelper(project)
        val myToolWindow = new GeneralizerToolWindow(
            project,
            scalaToolWindowHelper
        )
        // TODO memory leak?
        scalaToolWindowHelper.init(myToolWindow)
        val contentFactory = ContentFactory.SERVICE.getInstance

        val content = contentFactory.createContent(
            myToolWindow.getContent,
            "",
            false
        )

        toolWindow.getContentManager.addContent(content)
    }
}
