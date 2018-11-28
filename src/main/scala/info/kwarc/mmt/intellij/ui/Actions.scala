package info.kwarc.mmt.intellij.ui

import com.intellij.openapi.actionSystem._
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.ui.Messages
import info.kwarc.mmt.intellij.{MMT, MMTDataKeys}
import info.kwarc.mmt.utils._


object Actions {
  private lazy val am = ActionManager.getInstance()
  trait MMTActionTrait { self : AnAction =>
    protected def id : String
    protected def descr : String
    getTemplatePresentation.setDescription(descr)
    am.registerAction("MMTPlugin." + id,this)
    def remMMT = am.unregisterAction(id)
  }
  abstract class MMTAction(val id : String, text : String, val descr : String = "") extends AnAction(text) with MMTActionTrait
  class MMTActionGroup(val id : String, text : String, val descr : String = "") extends DefaultActionGroup with MMTActionTrait {
    getTemplatePresentation.setText(text)
  }

  private lazy val topmenu = am.getAction("ToolsMenu")/*(IdeActions.GROUP_MAIN_MENU)*/.asInstanceOf[DefaultActionGroup]
  private lazy val mmtmenu = new MMTActionGroup("Menu","MMT","MMT Actions") {
    getTemplatePresentation.setIcon(MMT.icon)
  }
  private lazy val contextmenu = am.getAction("ProjectViewPopupMenu").asInstanceOf[DefaultActionGroup]// ActionPlaces.PROJECT_VIEW_POPUP



  private lazy val test = new MMTTest
  private lazy val install = new InstallArchive
  private lazy val reset = new Reset

  def addAll: Unit = if (am.getActionIds("MMTPlugin").isEmpty) {
    topmenu.add(mmtmenu)
    mmtmenu.add(test)
    mmtmenu.add(reset)
    contextmenu.add(install)
    mmtmenu.setPopup(true)
  }
  def removeAll: Unit = if (am.getActionIds("MMTPlugin").nonEmpty) {
    test.remMMT
    reset.remMMT
    install.remMMT

    topmenu.remove(mmtmenu)
    contextmenu.remove(install)
    mmtmenu.remMMT
  }
}
import Actions._
class MMTTest extends MMTAction("test","MMT Info","Basic information on running MMT instance") {
  override def actionPerformed(event: AnActionEvent): Unit = {
    val project : Project = event.getProject
    MMT.get(project) match {
      case None =>
        Messages.showErrorDialog("Not a MathHub/MMT Project", "MMT")
      case Some(mmt) =>
        val base = File(project.getBasePath)
        Messages.showMessageDialog(project, {
          "MMT version: " + mmt.mmtjar.version + "\n" +
            "MathHub Base: " + base.toString
        }, "MMT", Messages.getInformationIcon)
    }
  }
}

class InstallArchive extends MMTAction("InstallArchive", "Install Archive") {

  import com.intellij.openapi.actionSystem.AnActionEvent

  override def update(event: AnActionEvent): Unit = {
    val archive = event.getData(MMTDataKeys.remoteArchive)
    event.getPresentation.setVisible(archive != null)
  }

  override def actionPerformed(e: AnActionEvent): Unit = {
    val id : String = e.getData(MMTDataKeys.remoteArchive)
    implicit val project = e.getData(CommonDataKeys.PROJECT)
    val mmt = MMT.get(project).get

    background {
      notifyWhile("Installing " + id + " + dependencies...") {
        println(Time.measure(mmt.mmtjar.method("install",Reflection.unit,List(id)))._1)
        writable {
          mmt.refreshPane()
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

class Reset extends MMTAction("Reset","Reset") {
  override def actionPerformed(e: AnActionEvent): Unit = {
    MMT.get(e.getProject).getOrElse(return ()).reset()
  }
}