package info.kwarc.mmt.intellij.ui

import java.util

import com.intellij.icons.AllIcons
import com.intellij.ide.SelectInTarget
import com.intellij.ide.projectView.impl._
import com.intellij.ide.projectView.impl.nodes._
import com.intellij.ide.projectView._
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import info.kwarc.mmt.utils._
import com.intellij.openapi.vfs._
import com.intellij.psi.{PsiDirectory, PsiFile, PsiManager}
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.PlatformIcons
import info.kwarc.mmt.intellij.{MMT, MMTDataKeys}
import javax.swing.Icon
import javax.swing.tree.DefaultMutableTreeNode

object Implicit {
  val viewSettings = new ViewSettings {
    override def isShowMembers: Boolean = true

    override def isStructureView: Boolean = true

    override def isShowModules: Boolean = true

    override def isFlattenPackages: Boolean = true

    override def isAbbreviatePackageNames: Boolean = true

    override def isHideEmptyMiddlePackages: Boolean = true

    override def isShowLibraryContents: Boolean = true
  }

}

import Implicit._

object PV {
  import Reflection._
  private var remoteGrps : List[String] = _
  private var remoteAs : List[String] = _
  def localGroups(mmt: MMT) = mmt.mmtjar.method("localGroups",RList(string),Nil)
  def remoteGroups(mmt : MMT) = if (remoteGrps != null) remoteGrps else {
    notifyWhile("Getting archive list from remote MathHub"){
      remoteGrps = mmt.mmtjar.method("remoteGroups",RList(string),Nil)
      //.asInstanceOf[Array[String]].toList
      remoteGrps
    }
  }
  def localArchs(mmt : MMT) = mmt.mmtjar.method("localArchs",RList(RPair(string,string)),Nil)
  def remoteArchs(mmt : MMT) = if (remoteAs != null) remoteAs else {
    remoteAs = mmt.mmtjar.method("remoteArchs",RList(string),Nil)
    remoteAs
  }
}

class MathHubTreeNode(project : Project) extends AbstractTreeNode[Project](project,project) {
  override def getChildren: util.Collection[_ <: AbstractTreeNode[_]] = {
    MMT.get(project) match {
      case Some(mmt) =>
        mmt.logged("Creating MathHub-Node") {
          val locals = PV.localGroups(mmt).map(id => new ArchiveGroupNode(id, mmt))
          val remotes = PV.remoteGroups(mmt).map(id => new ArchiveGroupNode(id, mmt))
          val ls = locals ::: remotes
          val helper = ProjectViewDirectoryHelper.getInstance(project)
          helper.createFileAndDirectoryNodes(List(toVF(mmt.mmtrc), toVF(mmt.msl), toVF(mmt.logfile)), viewSettings) ::: ls
        }
      case _ =>
        Nil.asInstanceOf[List[AbstractTreeNode[_]]]
    }
  }
  override def update(presentation: PresentationData): Unit = {
    presentation.setPresentableText("MathHub")
    presentation.addText("MathHub", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
    presentation.setIcon(MMT.projectIcon)
    presentation.setTooltip("Narf")
  }
}

object ArchiveNode {
  def getText(id: String, mmt : MMT) : (String,Boolean) = {
    val la = PV.localArchs(mmt)
    val ls = id match {
      case "Others" =>
        la.filter(!_._1.contains("/"))
      case _ =>
        la.filter(_._1.startsWith(id))
    }
    if (ls.isEmpty) ("~" + id + " [Not Installed]",false) else (id,true)
  }
}

class ArchiveGroupNode(id : String, mmt : MMT) extends ProjectViewNode[String](mmt.project,ArchiveNode.getText(id,mmt)._1,viewSettings) {
  val (name,isLocal) = ArchiveNode.getText(id,mmt)
  val icon = PlatformIcons.CLOSED_MODULE_GROUP_ICON
  val groupname = id

  private var bold = false

  override def contains(file: VirtualFile): Boolean = false

  override def getChildren: util.Collection[AbstractTreeNode[_]] = mmt.logged("Children of " + id) {
    val (ls,rs) = id match {
      case "Others" =>
        (
          PV.localArchs(mmt).filter(!_._1.contains("/")),
          PV.remoteArchs(mmt).filter(!_.contains("/"))
        )
      case _ =>
        (
          PV.localArchs(mmt).filter(_._1.startsWith(id)),
          PV.remoteArchs(mmt).filter(_.startsWith(id))
        )
    }
    val locals = ls.map{case (iid,qp) => new LocalArchiveNode(iid,mmt)}
    val remotes = rs.map(new RemoteArchiveNode(_,mmt))
    if (locals.nonEmpty) bold = true
    update()
    locals ::: remotes
  }

  override def update(presentation: PresentationData): Unit = {
    presentation.setPresentableText(name)
    if (bold) presentation.addText(name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
    presentation.setIcon(icon)
    presentation.setTooltip(id)
  }
}


class LocalArchiveNode(id : String, mmt : MMT) extends ProjectViewNode[String](mmt.project,id.split('/').last,viewSettings)/* ProjectViewModuleNode(mmt.project,mmt.LocalMathHub.getModule(id).get,viewSettings)*/ {
  val text = id.split('/').last
  val icon = AllIcons.Nodes.Module
  val archive = id

  override def contains(file: VirtualFile): Boolean = false

  override def update(presentation: PresentationData): Unit = {
    presentation.setPresentableText(text)
    presentation.addText(text, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
    presentation.setIcon(icon)
    presentation.setTooltip(id)
  }

  override def getChildren: util.Collection[AbstractTreeNode[_]] = mmt.logged("Children of " + id) {
    import Reflection._
    val tr = mmt.mmtjar.method("archiveInfo",RTriple(string,string,string),List(id))
    val (source,meta_inf,scala) = (File(tr._1),File(tr._2),File(tr._3))
    val sourceNode : List[AbstractTreeNode[_]] = if (source.toJava.exists()) List(MyDirectoryNode.sourceNode(mmt,source)) else Nil
    val metaNode = if (meta_inf.toJava.exists()) List(MyDirectoryNode.metaInfNode(mmt,meta_inf)) else
      if ((File(source).up / "MANIFEST.MF").exists)
      ProjectViewDirectoryHelper.getInstance(mmt.project).createFileAndDirectoryNodes(List(toVF(File(source).up / "MANIFEST.MF")),viewSettings).toList
      else Nil
    val scalaNode : List[AbstractTreeNode[_]] = if (scala.toJava.exists()) List(MyDirectoryNode.scalaNode(mmt,scala)) else Nil
    update()
    sourceNode ::: metaNode ::: scalaNode
  }

}

class RemoteArchiveNode(id : String, mmt : MMT) extends ProjectViewNode[String](mmt.project,"~"+id.split('/').last,viewSettings) {
  val text = "~" + id.split('/').last + " [Not Installed]"
  val icon = AllIcons.Nodes.Module
  val archive = id

  override def update(presentation: PresentationData): Unit = {
    presentation.setPresentableText(text)
    presentation.setIcon(icon)
    presentation.setTooltip(id)
  }

  override def contains(file: VirtualFile): Boolean = false

  override def getChildren: util.Collection[AbstractTreeNode[_]] = toJava(Nil.asInstanceOf[List[LocalArchiveNode]])
}

object FileToPSI {
  def toPSIDir(f : File, mmt : MMT) = {
    val psi = PsiManager.getInstance(mmt.project)
    psi.findDirectory(toVF(f))
  }
  def toPSIFile(f : File, mmt : MMT) = {
    val psi = PsiManager.getInstance(mmt.project)
    psi.findFile(toVF(f))
  }
}

object MyDirectoryNode {
  private def get(mmt : MMT, dir : File, icon : Icon, bold : Boolean) : AbstractTreeNode[_] = {
    val helper = ProjectViewDirectoryHelper.getInstance(mmt.project)
    val name = dir.name
    val ret = helper.createFileAndDirectoryNodes(List(toVF(dir)),viewSettings)
    ret.forEach {n =>
      n.getPresentation.setIcon(icon)
      if (bold) n.getPresentation.addText(name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
      return n
    }
    ???
  }
  def metaInfNode(mmt : MMT,dir : File) = get(mmt,dir,PlatformIcons.PACKAGE_ICON,false)
  def sourceNode(mmt : MMT,dir : File) = get(mmt,dir,PlatformIcons.MODULES_SOURCE_FOLDERS_ICON,true)
  def scalaNode(mmt : MMT,dir : File) = get(mmt,dir,PlatformIcons.SOURCE_FOLDERS_ICON,false)
}
/*
class MyDirectoryNode(mmt : MMT,dir : File) extends ProjectViewNode[PsiDirectory](mmt.project,FileToPSI.toPSIDir(dir,mmt),viewSettings) {// AbstractTreeNode[PsiDirectory](mmt.project,FileToPSI.toPSI(dir,mmt)) {
  lazy val helper = ProjectViewDirectoryHelper.getInstance(mmt.project)
  protected val icon = PlatformIcons.FOLDER_ICON
  private var name = dir.name

  override def getChildren: util.Collection[_ <: AbstractTreeNode[_]] = mmt.logged("Children of " + dir) {
    val subdirs = dir.children.filter(_.isDirectory)
    val files = dir.children.filter(!subdirs.contains(_))
    val dirnodes = subdirs.map(new MyDirectoryNode(mmt,_))
    val vfiles = files.map(f => fs.findFileByPath(f.toFilePath.toString()))
    dirnodes ::: helper.createFileAndDirectoryNodes(vfiles,viewSettings)
  }

  protected val bold = false

  override def update(presentation: PresentationData): Unit = {
    presentation.setPresentableText(name)
    if (bold) presentation.addText(name, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
    presentation.setIcon(icon)
  }

  override def contains(file: VirtualFile): Boolean = dir <= File(file.getUrl)
}

class MetaInfNode(mmt : MMT,dir : File) extends MyDirectoryNode(mmt : MMT,dir : File) {
  override protected val icon: Icon = PlatformIcons.PACKAGE_ICON
}

class SourceNode(mmt : MMT,dir : File) extends MyDirectoryNode(mmt : MMT,dir : File) {
  override protected val icon: Icon = PlatformIcons.MODULES_SOURCE_FOLDERS_ICON
  override protected val bold: Boolean = true
}

class ScalaNode(mmt : MMT,dir : File) extends MyDirectoryNode(mmt : MMT,dir : File) {
  override protected val icon: Icon = PlatformIcons.SOURCE_FOLDERS_ICON
}
*/

class MathHubPane(myProject : Project) extends ProjectViewPane(myProject) {
  override def getWeight: Int = 0

  override def createSelectInTarget(): SelectInTarget = {
    val s = super.createSelectInTarget()
    s
  }

  private lazy val mmt = MMT.get(myProject).get

  override def getIcon: Icon = MMT.projectIcon

  override val getTitle = "MathHub"

  override def getData(dataId: String): AnyRef = dataId match {
    case _ if MMTDataKeys.keys contains dataId =>
      val selected = getSelectionPaths
      val last = selected.headOption.map(_.getLastPathComponent)
      last match {
        case Some(stm: DefaultMutableTreeNode) if MMTDataKeys.remoteArchive.is(dataId) =>
          stm.getUserObject match {
            case an: RemoteArchiveNode =>
              an.archive
            case ag : ArchiveGroupNode if !ag.isLocal =>
              ag.groupname
            case _ => null
          }
        case _ => null
      }
    case _ => super.getData(dataId)
  }

  override def getId: String = "MathHub"

  override protected def createStructure(): ProjectAbstractTreeStructureBase =
    new ProjectTreeStructure(myProject, "MathHub") {
      override protected def createRoot(project: Project, settings: ViewSettings): AbstractTreeNode[_] =
        new MathHubTreeNode(project)
    }
}