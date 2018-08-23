package info.kwarc.mmt.intellij.ui

import com.intellij.execution.console.{LanguageConsoleBuilder, LanguageConsoleImpl}
import com.intellij.execution.filters.TextConsoleBuilderFactory
import com.intellij.execution.impl.ConsoleViewImpl
import com.intellij.execution.ui.ConsoleView
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import info.kwarc.mmt.intellij.MMT

class ShellFactory extends ToolWindowFactory {
  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = MMT.get(project) match {
    case Some(mmt) =>
      // LanguageConsoleBuilder.GutteredLanguageConsole
      val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole
      val content = toolWindow.getContentManager.getFactory.createContent(consoleView.getComponent, "MMT Shell", true)
      consoleView.createConsoleActions()
      toolWindow.getContentManager.addContent(content)
      toolWindow.getContentManager.setSelectedContent(content)
      toolWindow.activate(null, false)
    case _ =>
  }
}

