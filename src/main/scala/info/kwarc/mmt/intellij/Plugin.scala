package info.kwarc.mmt.intellij

import java.net.URLClassLoader
import java.util.Calendar

import com.intellij.ide.projectView.ProjectView
import com.intellij.ide.util.PropertiesComponent
import com.intellij.notification.{Notification, NotificationType, Notifications}
import com.intellij.openapi.actionSystem.DataKey
import com.intellij.openapi.application.{ApplicationManager, PathManager}
import com.intellij.openapi.components.{ProjectComponent, ServiceManager}
import com.intellij.openapi.editor.actionSystem.{EditorActionHandler, EditorActionManager}
import com.intellij.openapi.module.{Module, ModuleManager}
import com.intellij.openapi.progress.ProcessCanceledException
import com.intellij.openapi.progress.util.AbstractProgressIndicatorBase
import com.intellij.openapi.project.{Project, ProjectManager, ProjectUtil}
import com.intellij.openapi.roots.ModuleRootManager
import com.intellij.openapi.util.IconLoader
import com.intellij.openapi.wm.{ToolWindowAnchor, ToolWindowManager}
import com.intellij.psi.PsiManager
import com.intellij.ui.treeStructure.Tree
import info.kwarc.mmt.intellij.checking.ErrorViewerPanel
import info.kwarc.mmt.utils._
import info.kwarc.mmt.intellij.language.Abbreviations
import info.kwarc.mmt.intellij.ui.{Actions, MathHubPane, ShellViewer, Sidekick}
import info.kwarc.mmt.utils
import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

import scala.util.Try

object MMT {
  val requiredVersion = Version("15.0.0")

  lazy val icon : Icon = IconLoader.getIcon("/img/icon.png")

  def get(project : Project) : Option[MMT] = {
    val mmtc = ServiceManager.getService(project, classOf[MMTProject])
    if (mmtc.isMMT) Some(mmtc.get) else None
  }
  def getProject : Option[Project] = ProjectManager.getInstance().getOpenProjects.find(MMT.get(_).isDefined)
  def getMMT = getProject.flatMap(get)
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

class MMTJar(mmtjarfile : File, mmt : MMT) {
  import Reflection._
  private object Report {
    def apply(s : String) : Unit = {
      val not = new Notification("MMT","MMT Error",s,NotificationType.ERROR)
      ApplicationManager.getApplication.invokeLater{ () => Notifications.Bus.notify(not) }
      background { Thread.sleep(5000) }.onComplete {_ => not.expire()}(scala.concurrent.ExecutionContext.global)
    }
  }
  val reflection = new Reflection(new URLClassLoader(Array(mmtjarfile.toURI.toURL),this.getClass.getClassLoader))
  private val mmtjar = reflection.getClass("info.kwarc.mmt.intellij.MMTPluginInterface").getInstance(mmt.mathpath.toString :: Report :: Nil)
  def method[A](name : String,tp : Reflection.ReturnType[A], args : List[Any]) : A = {
    // mmt.log("Reflecting method " + name)
    mmtjar.method(name, tp, args)
  }
  def getArchiveRoots = method("getArchiveRoots", RList(string),Nil).map(File(_))
  def abbrevs = method("abbrevs",RList(RPair(string,string)),Nil)
  def init = method("init",unit,Nil)
  def clear = method("clear",unit,Nil)
  def handleLine(s : String) = method("handleLine",unit,List(s))
  def version = Version(method("version",string,Nil))
}

case class Version(s : String) {
  private val split = s.trim.split('.')
  val a = split.head.toInt
  val b = split.tail.head.toInt
  val c = split.tail.tail.head.toInt

  override def equals(obj: Any): Boolean = obj match {
    case that : Version =>
      this.a==that.a && this.b==that.b && this.c==that.c
    case _ => false
  }

  def <=(that : Version) =
      this.a <= that.a ||
      (this.a==that.a && this.b <= that.b) ||
      (this.a==that.a && this.b==that.b && this.c<=that.c)
  def <(that : Version) = this!=that && this<=that
  override def toString: String = s
}

class MMT(val project : Project) {
  lazy val mathpath = {
    val dir = ProjectUtil.guessProjectDir(project)
    if (dir == null) throw new Error("Could not determine project directory")
    File(dir.getCanonicalPath)
  }
  private val mmtjarfile = File(PropertiesComponent.getInstance(project).getValue(MMTDataKeys.mmtjar))
  lazy val mmtjar = new MMTJar(mmtjarfile,this)

  lazy val errorViewer = new ErrorViewerPanel(mmtjar)
  lazy val shellViewer = new ShellViewer(mmtjar)
  lazy val sidekick = new Sidekick(mmtjar)
  val mh : Module = {
    val modules = ModuleManager.getInstance(project).getModules
    modules.find(_.getModuleTypeName == MathHubModule.id).getOrElse {
      throw new Error("No MathHub Module found")
    }
  }

  private var _indent : List[String] = Nil

  def log(s : String) = {
    if (!logfile.exists()) {
      logfile.createNewFile()
    }
    val time = Calendar.getInstance().getTime.toString+ ": " + _indent.mkString("")
    val _strs = s.split('\n')
    val str = (time + _strs.head) :: _strs.tail.map(st => time.map(_ => ' ') + st).toList
    File.append(logfile,str.mkString("\n") + "\n")
  }

  def logged[A](msg : String)(f : => A) = try {
    log(msg + " {")
    _indent ::= " - "
    f
  } catch {
    case pc : ProcessCanceledException =>
      log("Process cancelled by IDE")
      throw pc
    case t : Throwable =>
      log(t.getMessage + "\n" + t.getStackTrace.map(_.toString).mkString("\n"))
      throw t
  } finally {
    if (_indent.nonEmpty) _indent = _indent.tail
    log("} (" + msg + ")")
  }

  val mmtrc = mathpath / "mmtrc"
  val msl = mathpath / "startup.msl"
  val logfile = mathpath / "intellij.log"

  def init: Unit = {
    File.write(logfile,"")
    logged("Initializing") {
      mmtjar.init
      assert(Abbreviations.elements.head != null)
      reset(true)
      val tw = ToolWindowManager.getInstance(project).registerToolWindow("MMT", true, ToolWindowAnchor.BOTTOM)
      tw.setIcon(MMT.icon)
      errorViewer.init(tw)
      shellViewer.init(tw)
      val sw = ToolWindowManager.getInstance(project).registerToolWindow("Document Tree",false,ToolWindowAnchor.LEFT)
      sw.setIcon(MMT.icon)
      sidekick.init(sw)
      // background {
        tw.show(null)
        sw.show(null)
      // }
      import Reflection._
      background {
        mmtjar.method("checkUpdate", ROption(RPair(string, string)), Nil) match {
          case Some((v, url)) if mmtjar.version < Version(v) =>
            val str = "A new version of the mmt.jar (" + v + ") is available. Download at <a href=\"" + url + "\">" + url + "</a>."
            inotifyP(str, "MMT update")
          case _ =>
        }
      }
    }
  }

  def reset(startup : Boolean = false) = writable { logged("Resetting") {
    mmtjar.clear
    val model = ModuleRootManager.getInstance(mh).getModifiableModel
    def entries = model.getContentEntries
    if (entries.isEmpty) model.addContentEntry(toVF(mathpath))
    val psi = PsiManager.getInstance(project)
    logged("Adding Archives") {
      mmtjar.getArchiveRoots.foreach { f =>
        log(f.toString)
        val dir = f / "scala"
        if (toVF(dir)!=null) {
          entries.headOption foreach { e =>
            e.addSourceFolder(toVF(dir), false)
          }
        }
      }
    }
    if (model.getModuleLibraryTable.getLibraryByName("MMT API") == null) {
      log("Adding mmt.jar to class path")
      val mmtlib = model.getModuleLibraryTable.createLibrary()
      val libmod = mmtlib.getModifiableModel
      libmod.setName("MMT API")
      libmod.addJarDirectory(toVF(mmtjarfile/*File(PathManager.getPluginsPath) / "MMTPlugin" / "lib"*/).getParent,false)
      libmod.commit()
    }
    model.commit()
    refreshPane
  }}

  def refreshPane : Unit = logged("Refreshing MathHub Pane") {
    val pv = ProjectView.getInstance(project)
    // _pane = new MathHubPane(project)
    val pane = pv.getProjectViewPaneById("MathHub")
    if (pane != null) { /*
      logged("Removing pane") {
        pv.removeProjectPane(pane)
      }
      logged("Disposing pane") {
        pane.dispose()
      } */
      pv.refresh()
    } else pv.addProjectPane(new MathHubPane(project))
    Try{pv.changeView("MathHub")}
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
      mmt = Some(new MMT(pr))
      mmt.get.init
      mmt.get.logged("Adding Actions") {
        Actions.addAll
      }
    }
  }

  override def projectClosed(): Unit = {
    Actions.removeAll
    super.projectClosed()
  }
}

