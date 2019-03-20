package info.kwarc.mmt.intellij.ui

import java.awt.event.{ActionEvent, ActionListener}

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import info.kwarc.mmt.intellij.{MMT, MMTJar}
import info.kwarc.mmt.utils.Reflection

class ShellViewer(mmt : MMTJar) extends ActionListener with MMTToolWindow {
  private val shell = new ShellForm
  val panel = shell.panel
  val displayName: String = "Shell"

  shell.input.addActionListener(this)
  shell.btn_run.addActionListener(this)
  /*
  private object doLine extends Function1[String,Unit] {
    override def apply(v1: String): Unit = shell.output.append(v1 + "\n")
  }
  */
  val doLine : String => Unit = s => shell.output.append(s + "\n")

  override def init(tw: ToolWindow): Unit = {
    import Reflection._
    super.init(tw)
    val fun = new RFunction {
      def apply(v1:String): Unit = doLine(v1)
    }
    mmt.method("shell",unit,List(fun))
    doLine("This is the MMT Shell")
  }

  override def actionPerformed(e: ActionEvent): Unit = doAction

  private def doAction: Unit = {
    val s = shell.input.getText
    shell.input.setText("")
    mmt.handleLine(s)
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

