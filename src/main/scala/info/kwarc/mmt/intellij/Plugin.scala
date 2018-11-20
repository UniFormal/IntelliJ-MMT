package info.kwarc.mmt.intellij

import java.net.URLClassLoader

import com.intellij.execution.filters.TextConsoleBuilder
import com.intellij.ide.projectView.ProjectView
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.{ProjectComponent, ServiceManager}
import com.intellij.openapi.module.{Module, ModuleManager}
import com.intellij.openapi.project.{Project, ProjectManager}
import com.intellij.openapi.projectRoots.SdkType
import com.intellij.openapi.roots.libraries.Library
import com.intellij.openapi.roots.{ModuleRootManager, OrderRootType, ProjectRootManager}
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.{ToolWindowAnchor, ToolWindowManager}
import com.intellij.psi.{PsiFile, PsiManager}
import com.intellij.ui.content.{ContentFactory, ContentManager}
import info.kwarc.mmt.api.{NamespaceMap, utils}
import info.kwarc.mmt.api.archives.lmh.MathHub
import info.kwarc.mmt.api.frontend.{Controller, MMTConfig}
import info.kwarc.mmt.api.utils.{File, MMTSystem}
import info.kwarc.mmt.intellij.language.{Abbreviations, ErrorViewer}
import info.kwarc.mmt.intellij.ui.{Actions, MathHubPane, ShellViewer}
import javax.swing.{Icon, JComponent, SwingUtilities}
import util._

import scala.util.Try

object MMT {
  lazy val icon : Icon = /* {
    import javax.swing.ImageIcon
    val imgURL = resource("img/icon.png")
    var icon = new ImageIcon(imgURL, "MMT icon")
    val img = icon.getImage
    new ImageIcon(img.getScaledInstance(16,16,java.awt.Image.SCALE_SMOOTH))
  } */ IconLoader.getIcon("/img/icon.png")

  def get(project : Project) : Option[MMT] = {
    val mmtc = ServiceManager.getService(project, classOf[MMTPlugin])
    if (mmtc.isMMT) Some(mmtc.get) else None
  }
  def getProject : Option[Project] = ProjectManager.getInstance().getOpenProjects.find(MMT.get(_).isDefined)
  def getMMT = getProject.flatMap(get)

  // TODO -------------------------------------------------------------------
  lazy val file : File = File("/home/jazzpirate/work/MMT/deploy/mmt.jar")
  def loader = new URLClassLoader(Array(file.toURI.toURL))
  def version = {
    val ctrlcls = loader.loadClass("info.kwarc.mmt.api.frontend.Controller")
    val ctrl = ctrlcls.getConstructor().newInstance()
    ctrlcls.getMethod("getVersion").invoke(ctrl)
  }
  def internalVersion = MMTSystem.getResourceAsString("/versioning/system.txt")
  // TODO -------------------------------------------------------------------
}

abstract class MMT {
  // TODO set reporter
  val controller : Controller
  val msl : File
  val mmtrc : File
  lazy val errorViewer = new ErrorViewer(controller)
  lazy val shellViewer = new ShellViewer(controller)
  // lazy val report = controller.report
  val mh : Module
  val project : Project
  lazy val mathpath = File(project.getBasePath)
  object LocalMathHub {
    lazy val mathhub = new MathHub(controller,mathpath,MathHub.defaultURL,true) {
      lazy val all = notifyWhile("Getting archive list from remote MathHub...") {
        available_()
      }
    }
    def remotes = mathhub.all.filter(id => !localArchs.exists(_._1 == id))
    def archives = controller.backend.getArchives.map(_.id)
    lazy val localGroups = archives.map { id =>
      if (id.contains("/")) id.split('/').head
      else "Others"
    }.distinct
    def remoteGroups = mathhub.all.collect{
      case id if id.contains("/") =>
        id.split('/').head
    }.distinct.filter(!localGroups.contains(_))
    def localArchs = archives.map{ id => (id,
      if (id.contains("/")) id.split('/').mkString(".")
      else "Others." + id)
    }
  }

  private var _pane : MathHubPane = _

  def init: Unit = {
    assert(Abbreviations.elements.head!=null)
    reset
    val tw = ToolWindowManager.getInstance(project).registerToolWindow("MMT",true,ToolWindowAnchor.BOTTOM)
    tw.setIcon(MMT.icon)
    errorViewer.init(tw)
    shellViewer.init(tw)
    background {
      tw.show(null)
    }
  }

  def reset = writable {
    controller.clear
    val model = ModuleRootManager.getInstance(mh).getModifiableModel
    val entries = model.getContentEntries
    val psi = PsiManager.getInstance(project)
    LocalMathHub.archives.foreach { id =>
      val a = controller.backend.getArchive(id).get
      val dir = a.root / "scala"
      if (dir.toJava.exists()) {
        entries.headOption foreach {e =>
          e.addSourceFolder(dir,false)
        }
      }
    }
    if (model.getModuleLibraryTable.getLibraryByName("MMT API") == null) {
      val mmtlib = model.getModuleLibraryTable.createLibrary()
      val libmod = mmtlib.getModifiableModel
      libmod.addJarDirectory(File(PathManager.getPluginsPath) / "MMTPlugin" / "lib",false)
      libmod.commit()
    }
    model.commit()
    refreshPane
  }

  def refreshPane = {
    val pv = ProjectView.getInstance(project)
    _pane = new MathHubPane(project)
    val pane = pv.getProjectViewPaneById("MathHub")
    if (pane != null) {
      pane.dispose()
      pv.removeProjectPane(pane)
    }
    pv.addProjectPane(_pane)
    Try(pv.changeView("MathHub"))

  }
}


class MMTPlugin(pr : Project) extends ProjectComponent {

  // val sdk = ProjectRootManager.getInstance(pr).getProjectSdk

  private var mmt : Option[MMT] = None

  def get = mmt.get

  def isMMT = mmt.isDefined

  override def initComponent(): Unit = {
    super.initComponent()
  }

  override def disposeComponent(): Unit = {
    super.disposeComponent()
  }

  override def getComponentName: String = "MMT-ProjectComponent"

  override def projectOpened(): Unit = {
    super.projectOpened()
    val modules = ModuleManager.getInstance(pr).getModules
    val mhO = modules.find(_.getModuleTypeName == MathHubModule.id)
    if (mhO.isDefined) {
      val ctrl = new Controller
      val home = File(pr.getBasePath)

      /** Options */
      val mslf = home / "startup.msl" //MMTOptions.startup.get.getOrElse("startup.msl")
      if (mslf.toJava.exists())
        ctrl.runMSLFile(mslf, None)
        else {
        mslf.createNewFile()
        File.append(mslf,"extension info.kwarc.mmt.odk.Plugin")
      }

      val rc = home / "mmtrc" // MMTOptions.config.get.getOrElse("mmtrc")
      if(!rc.toJava.exists()) {
        // import java.io.FileOutputStream
        //val in = getClass.getResourceAsStream("/mmtrc")

        rc.createNewFile()
        /*
        val out = new FileOutputStream(rc)
        try { //copy stream
          val buffer = new Array[Byte](1024)
          var bytesRead = 0
          while ( {
            bytesRead = in.read(buffer)
            bytesRead != -1
          }) out.write(buffer, 0, bytesRead)
        } finally if (out != null) out.close()
        */
        File.append(rc,"\n","#backends\n","lmh .")
      }

      ctrl.loadConfig(MMTConfig.parse(rc), false)

      /** MathHub Folder */
      ctrl.setHome(home)
      ctrl.addArchive(home)

      mmt = Some(new MMT {
        override val controller: Controller = ctrl
        override val msl: File = mslf
        override val mmtrc: File = rc
        val mh = mhO.get
        val project = pr
      })
      Actions.addAll
      mmt.get.init
    }
  }

  override def projectClosed(): Unit = {
    Actions.removeAll
    super.projectClosed()
  }
}

object MMTDataKeys {
  lazy val keys = List(archive,archiveGroup,localArchive,remoteArchive).map(_.getName)
  val archiveGroup = DataKey.create[String]("mmt.ArchiveGroup")
  val archive = DataKey.create[String]("mmt.Archive")
  val localArchive = DataKey.create[String]("mmt.LocalArchive")
  val remoteArchive = DataKey.create[String]("mmt.RemoteArchive")
}

/*
class MMTJar extends SdkType("mmt.jar") {

}
*/