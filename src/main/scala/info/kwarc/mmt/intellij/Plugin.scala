package info.kwarc.mmt.intellij

import java.net.URLClassLoader

import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.PathManager
import com.intellij.openapi.components.{ProjectComponent, ServiceManager}
import com.intellij.openapi.module.{Module, ModuleManager}
import com.intellij.openapi.project.{Project, ProjectManager}
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.{ToolWindowAnchor, ToolWindowManager}
import com.intellij.psi.PsiManager
import info.kwarc.mmt.utils._
import info.kwarc.mmt.intellij.language.{Abbreviations, ErrorViewer}
import info.kwarc.mmt.intellij.ui.{Actions, MathHubPane, ShellViewer}
import javax.swing.Icon

import scala.util.Try

object MMT {
  lazy val icon : Icon = IconLoader.getIcon("/img/icon.png")

  def get(project : Project) : Option[MMT] = {
    val mmtc = ServiceManager.getService(project, classOf[MMTProject])
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
  // def internalVersion = MMTSystem.getResourceAsString("/versioning/system.txt")
  // TODO -------------------------------------------------------------------
}

object MMTDataKeys {
  lazy val keys = List(archive,archiveGroup,localArchive,remoteArchive).map(_.getName)
  val archiveGroup = DataKey.create[String]("mmt.ArchiveGroup")
  val archive = DataKey.create[String]("mmt.Archive")
  val localArchive = DataKey.create[String]("mmt.LocalArchive")
  val remoteArchive = DataKey.create[String]("mmt.RemoteArchive")
  val mmtjar = "mmt.MMTJar"
}

/*
case class MMTJar(jarfile : File, mathpath : File) {
  import scala.reflect.runtime.universe

  private val classLoader = new URLClassLoader(Array(jarfile.toURI.toURL))//,getClass.getClassLoader)
  private val mirror = universe.runtimeMirror(classLoader)
  private lazy val _pluginclass = mirror.staticClass("info.kwarc.mmt.intellij.MMTPlugin")//classLoader.loadClass("info.kwarc.mmt.intellij.MMTPlugin")
  private lazy val pluginclass = mirror.reflectClass(_pluginclass)
  private lazy val constructor = pluginclass.symbol.toType.decl(universe.termNames.CONSTRUCTOR).asMethod
  private lazy val plugin = pluginclass.reflectConstructor(constructor).apply(mathpath.toString)//.getConstructor(classOf[String]).newInstance(mathpath.toString)
  private lazy val symbol = mirror.classSymbol(plugin.getClass)
  private lazy val pluginMirror = mirror.reflect(plugin)
  def method(name : String, args : Any*) = {
    val decl = symbol.toType.decl(universe.TermName(name)).asMethod//.member(universe.TermName(name)).asInstanceOf[universe.MethodSymbol]
    val t = decl.asMethod.returnType
    val method = pluginMirror.reflectMethod(decl)
    val res = method.apply(args)
    val ret = mirror.reflect(res)
    ret.instance
    // method.apply(args)
  }
  def clear : Unit = method("clear")
  def handleLine(s : String) : Unit = method("handleLine",s)
  def version = method("version").asInstanceOf[String]
}
*/

class MMTJar(mmtjarfile : File, mathpath : File) {
  import Reflection._
  val reflection = new Reflection(new URLClassLoader(Array(mmtjarfile.toURI.toURL),this.getClass.getClassLoader))
  private val mmtjar = reflection.getClass("info.kwarc.mmt.intellij.MMTPluginInterface").getInstance(mathpath.toString :: Nil)
  def method[A](name : String,tp : Reflection.ReturnType[A], args : List[Any]) : A = mmtjar.method(name,tp,args)
  def getArchiveRoots = method("getArchiveRoots", RList(string),Nil).map(File(_))
  def abbrevs = method("abbrevs",RList(RPair(string,string)),Nil)
  def init = method("init",unit,Nil)
  def clear = method("init",unit,Nil)
  def handleLine(s : String) = method("handleLine",unit,List(s))
  def version = method("version",string,Nil)
}

class MMT(val project : Project) {
  private lazy val mathpath = File(project.getBasePath)
  private val mmtjarfile = File(PropertiesComponent.getInstance(project).getValue(MMTDataKeys.mmtjar))
  lazy val mmtjar = new MMTJar(mmtjarfile,mathpath)

  lazy val errorViewer = new ErrorViewer(mmtjar)
  lazy val shellViewer = new ShellViewer(mmtjar)
  val mh : Module = {
    val modules = ModuleManager.getInstance(project).getModules
    modules.find(_.getModuleTypeName == MathHubModule.id).getOrElse {
      throw new Error("No MathHub Module found")
    }
  }

  val mmtrc = mathpath / "mmtrc"
  val msl = mathpath / "startup.msl"

  private var _pane : MathHubPane = _

  def init: Unit = {
    mmtjar.init
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
    mmtjar.clear
    val model = ModuleRootManager.getInstance(mh).getModifiableModel
    def entries = model.getContentEntries
    if (entries.isEmpty) model.addContentEntry(project.getBaseDir)
    val psi = PsiManager.getInstance(project)
    mmtjar.getArchiveRoots.foreach { f =>
      val dir = f / "scala"
      if (dir.toJava.exists()) {
        entries.headOption foreach {e =>
          e.addSourceFolder(toVF(dir),false)
        }
      }
    }
    if (model.getModuleLibraryTable.getLibraryByName("MMT API") == null) {
      val mmtlib = model.getModuleLibraryTable.createLibrary()
      val libmod = mmtlib.getModifiableModel
      libmod.addJarDirectory(toVF(File(PathManager.getPluginsPath) / "MMTPlugin" / "lib"),false)
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


class MMTProject(pr : Project) extends ProjectComponent {

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
      val home = File(pr.getBasePath)

      mmt = Some(new MMT(pr))
      Actions.addAll
      mmt.get.init
    }
  }

  override def projectClosed(): Unit = {
    Actions.removeAll
    super.projectClosed()
  }
}

