package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.actionSystem._
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import info.kwarc.mmt.api.archives
import info.kwarc.mmt.api.frontend.actions.LMHInstall
import info.kwarc.mmt.api.utils.File
import info.kwarc.mmt.api.utils.time.Time
import info.kwarc.mmt.intellij.{MMT, MMTDataKeys}
import info.kwarc.mmt.intellij.util._

import scala.concurrent.Future

object AllActions {
  private lazy val am = ActionManager.getInstance()
  private lazy val topmenu = am.getAction("MainMenu").asInstanceOf[DefaultActionGroup]
  private lazy val mmtmenu = ("MMTPlugin.Menu",new DefaultActionGroup {
    getTemplatePresentation.setText("MMT")
    getTemplatePresentation.setDescription("MMT Actions")
  })
  private lazy val contextmenu = am.getAction("ProjectViewPopupMenu").asInstanceOf[DefaultActionGroup]// ActionPlaces.PROJECT_VIEW_POPUP

  private lazy val test = ("MMTPlugin.Test",new MMTTest)
  private lazy val install =("MMTPlugin.InstallArchive",new InstallArchive)
  private lazy val reset = ("MMTPlugin.Reset",new Reset)

  def apply: Unit = if (am.getActionIds("MMTPlugin").isEmpty) {
    am.registerAction(mmtmenu._1,mmtmenu._2)
    am.registerAction(test._1,test._2)
    am.registerAction(install._1,install._2)
    am.registerAction(reset._1,reset._2)
    topmenu.add(mmtmenu._2)
    mmtmenu._2.add(test._2)
    mmtmenu._2.add(reset._2)
    contextmenu.add(install._2)
  }
  def remove: Unit = if (am.getActionIds("MMTPlugin").nonEmpty) {
    am.unregisterAction(test._1)
    am.unregisterAction(reset._1)
    am.unregisterAction(install._1)

    topmenu.remove(mmtmenu._2)
    contextmenu.remove(install._2)
    am.unregisterAction(mmtmenu._1)
  }
}

class MMTTest extends AnAction("MMT Info") {
  getTemplatePresentation.setDescription("Basic information on running MMT instance")
  override def actionPerformed(event: AnActionEvent): Unit = {
    implicit val pr : Project = event.getProject
    MMT.get match {
      case None =>
        Messages.showErrorDialog("Not a MathHub/MMT Project", "MMT")
      case Some(mmt) =>
        import mmt._
        val base = File(project.getBasePath)
        Messages.showMessageDialog(project, {
          "Controller loaded: " + {
            if (controller != null) "Yes" else "No"
          } + "\n" +
            "MathHub Base: " + base.toString() + "\n" +
            "Startup .msl File: " + msl.toString() + "\n" +
            "mmtrc File: " + mmtrc.toString + "\n" +
            "Module Source: " + ModuleRootManager.getInstance(mh).getContentRoots.mkString(", ") + "\n" +
            "Archives:\n" + LocalMathHub.archives.map("    - " + _).mkString("\n") + "\n" +
            "Remotes:\n" + LocalMathHub.remotes.map("    - " + _).mkString("\n")
        }, "MMT", Messages.getInformationIcon)
    }
  }
}

class InstallArchive extends AnAction("Install Archive") {

  import com.intellij.openapi.actionSystem.AnActionEvent

  override def update(event: AnActionEvent): Unit = {
    val archive = event.getData(MMTDataKeys.remoteArchive)
    event.getPresentation.setVisible(archive != null)
  }

  override def actionPerformed(e: AnActionEvent): Unit = {
    val id = e.getData(MMTDataKeys.remoteArchive)
    implicit val project = e.getData(CommonDataKeys.PROJECT)
    val mmt = MMT.get(project).get

    background {
      notifyWhile("Installing " + id + " + dependencies...") {
        println(Time.measure(mmt.LocalMathHub.mathhub.installEntry(id, None, true))._1)
        Thread.sleep(10)
        writable {
          mmt.refreshPane
        }
      }
    }
    /*
    val not = inotify("Installing " + id + " + dependencies...")

    Future {
      writable {
        errorMsg {
          println(Time.measure {
            mmt.LocalMathHub.mathhub.installEntry(id, None, true)
          }._1)
          mmt.refreshPane
          not.expire()
        }
      }
    }(scala.concurrent.ExecutionContext.global)
  */
  }
}

class Reset extends AnAction("Reset") {
  override def actionPerformed(e: AnActionEvent): Unit = {
    MMT.get(e.getProject).getOrElse(return ()).reset
  }
}