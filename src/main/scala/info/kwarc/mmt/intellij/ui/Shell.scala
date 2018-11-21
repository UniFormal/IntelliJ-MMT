package info.kwarc.mmt.intellij.ui

import java.awt.event.{ActionEvent, ActionListener}

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import info.kwarc.mmt.api.frontend.{Controller, ReportHandler}
import info.kwarc.mmt.intellij.MMT

class ShellViewer(controller : Controller) extends ActionListener with MMTToolWindow {
  private val shell = new ShellForm
  val panel = shell.panel
  val displayName: String = "Shell"

  shell.input.addActionListener(this)
  shell.btn_run.addActionListener(this)

  private def print(s : String) = shell.output.append(s + "\n")

  private val handler = new ReportHandler("IntelliJ Shell") {
    override def apply(ind: Int, caller: => String, group: String, msgParts: List[String]): Unit = {
      msgParts.foreach {msg => print(indentString(ind) + group + ": " + msg)}
    }
  }

  override def init(tw: ToolWindow): Unit = {
    super.init(tw)
    print("This is the MMT Shell")
    controller.report.addHandler(handler)
  }

  override def actionPerformed(e: ActionEvent): Unit = doAction

  private def doAction: Unit = {
    val s = shell.input.getText
    shell.input.setText("")
    controller.handleLine(s)
  }
  /*
  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = MMT.get(project) match {
    case Some(mmt) =>
      // LanguageConsoleBuilder.GutteredLanguageConsole
      // val consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole
      val content = toolWindow.getContentManager.getFactory.createContent(shell, "MMT ShellForm", true)
      // consoleView.createConsoleActions()
      toolWindow.getContentManager.addContent(content)
      toolWindow.getContentManager.setSelectedContent(content)
      toolWindow.activate(null, false)
    case _ =>
  }
  */
}

