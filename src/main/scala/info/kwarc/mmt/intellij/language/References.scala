package info.kwarc.mmt.intellij.language

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.openapi.util.TextRange
import com.intellij.psi._
import com.intellij.psi.search.{FileTypeIndex, GlobalSearchScope}
import com.intellij.psi.util.PsiTreeUtil
import info.kwarc.mmt.intellij.language.psi.{MMTTheory, MMTTheoryheader}
import info.kwarc.mmt.intellij.language.psi.imps._
import info.kwarc.mmt.intellij.MMT
import info.kwarc.mmt.utils._

trait URIHelper extends PsiElement { self =>
  import Reflection._
  protected lazy val project = self.getNode.getPsi.getProject
  protected lazy val mmt = MMT.get(project).get
  protected lazy val file = self.getNode.getPsi.getContainingFile
  protected def ns: String = {
    val nss = PsiTreeUtil.findChildrenOfType(file,classOf[MMTNamespace_impl])
    if (nss != null) {
      nss.reverse.find(_.getTextRange.getStartOffset < self.getNode.getPsi.getTextRange.getStartOffset).foreach { ns =>
        return mmt.mmtjar.method("parseNamespace",string,ns.getUri.getText :: Nil)// Path.parseD(ns.getUri.getText, mmt.controller.getNamespaceMap)
      }
    }
    mmt.mmtjar.method("getNamespaceFromFile",string,toFile(file).toString :: Nil)
  }

  protected def nsMap : ReflectedInstance = {
    val imports = PsiTreeUtil.findChildrenOfType(file,classOf[MMTImport_impl])
    // var nsm = mmt.controller.getNamespaceMap(ns)
    val ls : List[(String,String)] = if (imports != null) {
      imports.map { i =>
        (i.getPname.getText,i.getUri.getText)
        // nsm = nsm.add(i.getPname.getText, Path.parse(i.getUri.getText, nsm).toPath)
      }
    } else Nil
    mmt.mmtjar.method("getNamespaceMap",Reflected(mmt.mmtjar.reflection.getClass("info.kwarc.mmt.api.NamespaceMap")),List(ns,ls))
  }
}


class URIElement_impl(node : ASTNode) extends ASTWrapperPsiElement(node) with URIHelper {
  import Reflection._
  lazy val uri : String = if (this.getParent.isInstanceOf[MMTRule_impl]) {
    val ret = mmt.mmtjar.method("resolvePath",string,List(node.getText,nsMap))
    ret
  } else {
    val ret = mmt.mmtjar.method("resolvePathSimple",string,List(node.getText,nsMap))
    ret
  }
    // Path.parse(node.getText,nsMap)

  override def getReference: PsiReference = {
    val oS = mmt.mmtjar.method("getReference",ROption(string),List(uri))
    oS match {
      case Some(classname) =>
        new PsiReferenceBase(this,new TextRange(0,node.getTextLength)) {
          override def resolve(): PsiElement = {
            val aClass = JavaPsiFacade.getInstance(project).findClass(classname,GlobalSearchScope.allScope(project))
            if (aClass != null) aClass.getSourceElement
            else null
          }
          override def getVariants: Array[AnyRef] = Array()
        }
      case _ =>
        new URIReference(this,new TextRange(0,node.getTextLength))
    }
  }

  override def getReferences: Array[PsiReference] = Array(getReference)
}

trait HasURI extends PsiNameIdentifierOwner with URIHelper {
  def getRefURI : String
  def getNamePSI : PsiElement

  override def setName(name: String): PsiElement = ???
  override def getNameIdentifier: PsiElement = getNamePSI

  def parentTheory = recurseParents(this)

  private def recurseParents(s : PsiElement) : Option[TheoryElement_impl] = s.getParent match {
    case null => None
    case r : TheoryElement_impl => Some(r)
    case o => recurseParents(o)
  }

}
abstract class TheoryElement_impl(node : ASTNode) extends ASTWrapperPsiElement(node) with HasURI {
  lazy val getRefURI: String = {
    val head = findNotNullChildByClass(classOf[MMTTheoryheader])
    val name = head.getPname.getText
    parentTheory match {
      case Some(p) => p.getRefURI + "/" + name
      case _ => ns + "?" + name
    }
  }

  def getTheoryheader() : MMTTheoryheader

  override def getName: String = getRefURI
  override def getNamePSI: PsiElement = this.getTheoryheader.getPname

  override def getTextOffset: Int = if (getNamePSI != null) {getNamePSI.getTextOffset - super.getTextOffset} else 0

  override def getTextLength: Int = if (getNamePSI != null) getNamePSI.getTextLength else 0
  // override def getElement = this.asInstanceOf[MMTTheory].getTheoryheader.getPname
}

class URIReference(element: PsiElement,textRange: TextRange) extends PsiReferenceBase(element,textRange) {
  private lazy val project = myElement.getProject
  val uri = element match {
    case e : URIElement_impl => e.uri
    case _ => ""
  }
  override def getValue: String = uri

  private lazy val psiman = PsiManager.getInstance(project)
  private def allfiles = FileTypeIndex.getFiles(MMTFile,GlobalSearchScope.allScope(project)).toList
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
/*
  override def isReferenceTo(element: PsiElement): Boolean = element match {
    // case h : HasURI => h.getRefURI == uri
    case e : MMTPname_impl if e.getParent.isInstanceOf[HasURI] =>
      e.getParent.asInstanceOf[HasURI].getRefURI == uri
    case _ => false
  }
*/
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