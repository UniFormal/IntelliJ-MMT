package info.kwarc.mmt.intellij.Language

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi._
import com.intellij.psi.scope.PsiScopeProcessor
import com.intellij.psi.search.{FileTypeIndex, GlobalSearchScope}
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import com.intellij.util.indexing.FileBasedIndex
import info.kwarc.mmt.api.{DPath, NamespaceMap, Path}
import info.kwarc.mmt.intellij.Language.psi.{MMTTheory, MMTTheoryheader}
import info.kwarc.mmt.intellij.Language.psi.imps.{MMTImport_impl, MMTNamespace_impl, MMTPname_impl}
import info.kwarc.mmt.intellij.MMT
import info.kwarc.mmt.intellij.util._
import org.jetbrains.plugins.hocon.CommonUtil.TextRange

trait URIHelper extends PsiElement { self =>
  protected lazy val mmt = MMT.get(self.getNode.getPsi.getProject).get
  protected lazy val file = self.getNode.getPsi.getContainingFile
  protected def ns: DPath = {
    val nss = PsiTreeUtil.findChildrenOfType(file,classOf[MMTNamespace_impl])
    if (nss != null) {
      nss.reverse.find(_.getTextOffset < self.getNode.getPsi.getTextOffset).foreach { ns =>
        return Path.parseD(ns.getUri.getText, mmt.controller.getNamespaceMap)
      }
    }
    mmt.controller.backend.resolvePhysical(file) match {
      case Some((a,_)) => a.ns match {
        case Some(d : DPath) => return d
        case _ =>
      }
      case _ =>
    }
    mmt.controller.getNamespaceMap.base.doc
  }
  protected def nsMap : NamespaceMap = {
    val imports = PsiTreeUtil.findChildrenOfType(file,classOf[MMTImport_impl])
    var nsm = mmt.controller.getNamespaceMap(ns)
    if (imports != null) {
      imports.foreach { i =>
        nsm = nsm.add(i.getPname.getText, Path.parse(i.getUri.getText, nsm).toPath)
      }
    }
    nsm
  }
}


class URIElement_impl(node : ASTNode) extends ASTWrapperPsiElement(node) with URIHelper {
  def uri : String =
    Path.parse(node.getText,nsMap).toPath

  override def getReference: PsiReference = new URIReference(this,new TextRange(0,node.getTextLength))

  override def getReferences: Array[PsiReference] = Array(getReference)
}

trait HasURI extends PsiNameIdentifierOwner with URIHelper {
  def getRefURI : String
  def getNamePSI : PsiElement

  override def setName(name: String): PsiElement = ???
  override def getNameIdentifier: PsiElement = getNamePSI
}
class TheoryElement_impl(node : ASTNode) extends ASTWrapperPsiElement(node) with HasURI {
  override def getRefURI: String = {
    val head = findNotNullChildByClass(classOf[MMTTheoryheader])
    val n = head.getPname.getText
    (ns ? n).toPath
  }

  override def getName: String = getRefURI
  override def getNamePSI: PsiElement = this.asInstanceOf[MMTTheory].getTheoryheader.getPname
  // override def getElement = this.asInstanceOf[MMTTheory].getTheoryheader.getPname
}

class URIReference(element: PsiElement,textRange: TextRange) extends PsiReferenceBase(element,textRange) {
  private lazy val project = myElement.getProject
  private lazy val uri = element match {
    case e : URIElement_impl => e.uri
    case _ => ""
  }
  private lazy val psiman = PsiManager.getInstance(project)
  private def allfiles = FileTypeIndex.getFiles(MMTFile.INSTANCE,GlobalSearchScope.allScope(project)).toList
  private def elems = allfiles.flatMap {vf =>
    val sf = psiman.findFile(vf)
    if (sf != null) {
      PsiTreeUtil.findChildrenOfType(sf,classOf[HasURI])
    } else Nil
  }

  private def multiResolve(incompleteCode: Boolean): Array[ResolveResult] = {
    elems.collect {
      case e if e.getRefURI.startsWith(uri) => new PsiElementResolveResult(e)//.asInstanceOf[ASTWrapperPsiElement])
    }.toArray
  }

  override def resolve(): PsiElement = {
    val ls = multiResolve(false)
    if (ls.length == 1) ls.head.getElement else null
  }

  override def isReferenceTo(element: PsiElement): Boolean = element match {
    case h : HasURI => h.getRefURI == uri
    case _ => false
  }

  override def getVariants: Array[AnyRef] = {
    elems.toArray
  }
}

/*
class URIReferenceContributor extends PsiReferenceContributor {
  override def registerReferenceProviders(registrar: PsiReferenceRegistrar): Unit = {
    registrar.registerReferenceProvider(PlatformPatterns.psiElement(classOf[URIElement_impl]),
      new PsiReferenceProvider {
        override def getReferencesByElement(element: PsiElement, context: ProcessingContext): Array[PsiReference] = {
          val elem = element.asInstanceOf[URIElement_impl]
          Array(new URIReference(elem,elem.getTextRange))
        }
      }
    )
  }
}
*/