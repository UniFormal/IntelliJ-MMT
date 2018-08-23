package info.kwarc.mmt.intellij.ui

import java.util

import com.intellij.icons.AllIcons
import com.intellij.ide.projectView.impl._
import com.intellij.ide.projectView.impl.nodes._
import com.intellij.ide.projectView._
import com.intellij.ide.util.treeView.AbstractTreeNode
import com.intellij.openapi.project.Project
import com.intellij.openapi.roots.ModuleRootManager
import info.kwarc.mmt.intellij.util._
import com.intellij.openapi.vfs._
import com.intellij.psi.{PsiDirectory, PsiFile, PsiManager}
import com.intellij.ui.SimpleTextAttributes
import com.intellij.util.PlatformIcons
import info.kwarc.mmt.api.archives
import info.kwarc.mmt.api.utils.{File, mmt}
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
  def get(project : Project) : AbstractTreeNode[Project] = {
    val ret = new MathHubTreeNode(project)
    ret
  }
}

class MathHubTreeNode(project : Project) extends AbstractTreeNode[Project](project,project) {
  override def getChildren: util.Collection[_ <: AbstractTreeNode[_]] = {
    MMT.get(project) match {
      case Some(mmt) =>
        val locals = mmt.LocalMathHub.localGroups.map(id => new ArchiveGroupNode(id, mmt))
        val remotes = mmt.LocalMathHub.remoteGroups.map(id => new ArchiveGroupNode(id, mmt))
        val ls = locals ::: remotes
        val helper = ProjectViewDirectoryHelper.getInstance(project)
        helper.createFileAndDirectoryNodes(List(toVF(mmt.mmtrc),toVF(mmt.msl)),viewSettings) ::: ls
      case _ => Nil.asInstanceOf[List[AbstractTreeNode[_]]]
    }
  }
  override def update(presentation: PresentationData): Unit = {
    presentation.setPresentableText("MathHub")
    presentation.addText("MathHub", SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES)
    presentation.setIcon(MMT.icon)
    presentation.setTooltip("Narf")
  }
}

object ArchiveNode {
  def getText(id: String, mmt : MMT) = {
    val ls = id match {
      case "Others" =>
        mmt.LocalMathHub.localArchs.filter(!_._1.contains("/"))
      case _ =>
        mmt.LocalMathHub.localArchs.filter(_._1.startsWith(id))
    }
    if (ls.isEmpty) "~" + id + " [Not Installed]" else id
  }
}

class ArchiveGroupNode(id : String, mmt : MMT) extends ProjectViewNode[String](mmt.project,ArchiveNode.getText(id,mmt),viewSettings) {
  val name = ArchiveNode.getText(id,mmt)
  val icon = PlatformIcons.CLOSED_MODULE_GROUP_ICON
  val groupname = id

  private var bold = false

  override def contains(file: VirtualFile): Boolean = false

  override def getChildren: util.Collection[AbstractTreeNode[_]] = {
    val (ls,rs) = id match {
      case "Others" =>
        (
          mmt.LocalMathHub.localArchs.filter(!_._1.contains("/")),
          mmt.LocalMathHub.remotes.filter(!_.contains("/"))
        )
      case _ =>
        (
          mmt.LocalMathHub.localArchs.filter(_._1.startsWith(id)),
          mmt.LocalMathHub.remotes.filter(_.startsWith(id))
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

  override def getChildren: util.Collection[AbstractTreeNode[_]] = {
    val archive = mmt.controller.backend.getArchive(id).get
    val meta_inf = archive.root / "META-INF"
    val source = archive / archives.source
    val scala = archive.root / "scala" // TODO redirectable dimension, if existent
    val sourceNode : List[MyDirectoryNode] = if (source.toJava.exists()) List(new SourceNode(mmt,source)) else Nil
    val metaNode : List[MyDirectoryNode] = List(new MetaInfNode(mmt,meta_inf))
    val scalaNode : List[MyDirectoryNode] = if (scala.toJava.exists()) List(new ScalaNode(mmt,scala)) else Nil
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
    psi.findDirectory(f)
  }
  def toPSIFile(f : File, mmt : MMT) = {
    val psi = PsiManager.getInstance(mmt.project)
    psi.findFile(f)
  }
}

class MyDirectoryNode(mmt : MMT,dir : File) extends ProjectViewNode[PsiDirectory](mmt.project,FileToPSI.toPSIDir(dir,mmt),viewSettings) {// AbstractTreeNode[PsiDirectory](mmt.project,FileToPSI.toPSI(dir,mmt)) {
  lazy val helper = ProjectViewDirectoryHelper.getInstance(mmt.project)
  protected val icon = PlatformIcons.DIRECTORY_CLOSED_ICON
  private var name = dir.name

  override def getChildren: util.Collection[_ <: AbstractTreeNode[_]] = {
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

class MathHubPane(project : Project) extends ProjectViewPane(project) {
  override def getWeight: Int = -1

  override def getIcon: Icon = MMT.icon

  override val getTitle = "MathHub"
  private lazy val root = PV.get(project)

  override def getData(dataId: String): AnyRef = dataId match {
    case _ if MMTDataKeys.keys contains dataId =>
      val treestructure = myTreeStructure
      val selected = getSelectionPaths
      val last = selected.headOption.map(_.getLastPathComponent)
      last match {
        case Some(stm: DefaultMutableTreeNode) if MMTDataKeys.remoteArchive.is(dataId) =>
          stm.getUserObject match {
            case an: RemoteArchiveNode =>
              an.archive
            case _ => null
          }
        case _ => null
      }
    case _ => super.getData(dataId)
  }

  override def getId: String = "MathHub"

  override protected def createStructure(): ProjectAbstractTreeStructureBase =
    new ProjectTreeStructure(project, "MathHubPane") {
      override protected def createRoot(project: Project, settings: ViewSettings): AbstractTreeNode[_] = {
        root
      }
    }
}