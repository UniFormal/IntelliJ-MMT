package info.kwarc.mmt.intellij.ui.generalizer

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.{ToolWindow, ToolWindowFactory}
import com.intellij.ui.content.ContentFactory
import info.kwarc.mmt.intellij.ui.GeneralizerToolWindow
import info.kwarc.mmt.intellij.{MMT, Version}

class GeneralizerToolWindowFactory extends ToolWindowFactory {
  // For displaying a notification if the MMT version is too low
  private val featureName = "Generalizer"

  // The tab name inside the tool window, see [[Plugin]] where this is set up.
  private val displayName = "Generalizer"

  override def createToolWindowContent(project: Project, toolWindow: ToolWindow): Unit = {
    MMT.get(project) match {
      case Some(mmt) =>
        assert(
          GeneralizerToolWindowFactory.requiredMinimalMMTVersion <= mmt.mmtjar.version,
          "GeneralizerToolWindowFactory was used with an MMT version smaller than "
            + "GeneralizerToolWindowFactory.requiredMinimalMMTVersion. The caller should "
            + "check before for that using utils.ifVersion"
        )
      case None =>
      // Ignore, maybe the project hasn't initialized correctly yet.
      // In that case we cannot perform the sanity check above sadly.
    }

    val scalaToolWindowHelper = new ScalaGeneralizerToolWindowHelper(project)
    val myToolWindow = new GeneralizerToolWindow(
      project,
      scalaToolWindowHelper
    )
    // TODO memory leak?
    scalaToolWindowHelper.init(myToolWindow)

    val content = ContentFactory.SERVICE.getInstance.createContent(
      myToolWindow.getContent,
      displayName,
      false
    )

    myToolWindow.getContent.setVisible(true)

    toolWindow.getContentManager.addContent(content)
  }

}

object GeneralizerToolWindowFactory {
  def requiredMinimalMMTVersion = Version("16.0.0")
}