package info.kwarc.mmt.intellij.language

import java.util

import com.intellij.execution.process.ConsoleHighlighter
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.annotation.{AnnotationHolder, HighlightSeverity}
import com.intellij.lang.{ASTNode, ParserDefinition, PsiParser}
import com.intellij.lexer.{FlexAdapter, Lexer}
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.{FileType, SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.openapi.options.colors.{AttributesDescriptor, ColorDescriptor, ColorSettingsPage}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.tree.{IElementType, IFileElementType, TokenSet}
import com.intellij.psi.{FileViewProvider, PsiElement, PsiFile, TokenType}
import info.kwarc.mmt.intellij.MMT
import info.kwarc.mmt.intellij.language.psi.MMTParserTypes
import info.kwarc.mmt.intellij.language.psi.imps._
import javax.swing.Icon

class MMTLexerAdapter extends FlexAdapter(new MMTLexer(null))

class MMTFileBase(viewProvider: FileViewProvider) extends PsiFileBase(viewProvider, MMTLanguage.INSTANCE) {
  override def getFileType: FileType = MMTFileType.INSTANCE
}

class MMTParserDefinition extends ParserDefinition {
  val FILE = new IFileElementType(MMTLanguage.INSTANCE)
  val WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE)

  override def getWhitespaceTokens: TokenSet = TokenSet.create(MMTParserTypes.WHITESPACE)

  override def getFileNodeType: IFileElementType = FILE

  override def createElement(node: ASTNode): PsiElement = MMTParserTypes.Factory.createElement(node)

  override def createFile(viewProvider: FileViewProvider): PsiFile = new MMTFileBase(viewProvider)

  override def getCommentTokens: TokenSet = TokenSet.EMPTY // TokenSet.create(MMTParserTypes.MOD_COMMENT)

  override def createParser(project: Project): PsiParser = new MMTParser

  override def getStringLiteralElements: TokenSet = TokenSet.EMPTY

  override def spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements = SpaceRequirements.MAY

  override def createLexer(project: Project): Lexer = new MMTLexerAdapter
}

object Attributes {
  private def make(kw: String, key: TextAttributesKey) = TextAttributesKey.createTextAttributesKey(kw, key)

  lazy val keyword = make("mmt.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
  lazy val comment = make("mmt.COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
  lazy val derived = make("mmt.Derived", DefaultLanguageHighlighterColors.LINE_COMMENT)
  lazy val MD = make("mmt.MD", ConsoleHighlighter.RED_BRIGHT)
  lazy val DD = make("mmt.DD", ConsoleHighlighter.GREEN)
  lazy val OD = make("mmt.OD", ConsoleHighlighter.BLUE)
  lazy val name = make("mmt.Name", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
  lazy val uri = make("mmt.URI", DefaultLanguageHighlighterColors.NUMBER)
  lazy val term = make("mmt.term", DefaultLanguageHighlighterColors.STATIC_FIELD)
  lazy val not = make("mmt.notation", DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)
  lazy val active = make("mmt.active", ConsoleHighlighter.YELLOW_BRIGHT)

  def getChildren(element: PsiElement): List[PsiElement] = element.getNode.getChildren(null).map(_.getPsi).toList

  def highlight(ta: TextAttributesKey)(implicit element: PsiElement, holder: AnnotationHolder) =
    holder.newAnnotation(HighlightSeverity.INFORMATION,"").range(element).
      enforcedTextAttributes(ta.getDefaultAttributes).create()//.createInfoAnnotation(element, null).setTextAttributes(ta)
}

object LexingHighlighter extends SyntaxHighlighterBase {
  override def getHighlightingLexer: Lexer = new MMTLexerAdapter

  import Attributes._

  override def getTokenHighlights(tokenType: IElementType): Array[TextAttributesKey] = SyntaxHighlighterBase.pack(tokenType match {
    case MMTParserTypes.MD => MD
    case MMTParserTypes.DD => DD
    case MMTParserTypes.OD => OD
    // case MMTParserTypes.DERIVEDSIMPLE | MMTParserTypes.DER => derived
    case MMTParserTypes.THEORY_KEY |
         MMTParserTypes.ABBREV_KEY |
         MMTParserTypes.ALIAS_KEY |
         MMTParserTypes.ARROW_KEY |
         MMTParserTypes.EQ_KEY |
         MMTParserTypes.IMPLICIT_KEY |
         MMTParserTypes.IMPORT_KEY |
         MMTParserTypes.INCLUDE_KEY |
         MMTParserTypes.NAMESPACE_KEY |
         MMTParserTypes.NOT_KEY |
         MMTParserTypes.PARAM_KEY |
         MMTParserTypes.ROLE_KEY |
         MMTParserTypes.RULE_KEY |
         MMTParserTypes.STRUCTURE_KEY |
         MMTParserTypes.COLON_KEY |
         MMTParserTypes.FIXMETA_KEY |
         MMTParserTypes.VIEW_KEY |
         MMTParserTypes.DIAGRAM_KEY
    => keyword
    case _ => null
  }
  )
}

class Factory extends SyntaxHighlighterFactory {
  override def getSyntaxHighlighter(project: Project, virtualFile: VirtualFile): SyntaxHighlighter = {
    MMT.get(project) match {
      // Do not check whether virtualFile.getFileType.getName == "MMT" since
      // when an in-memory MMT document is loaded within an EditorTextField (e.g. in the
      // refactoring GUI), getName will return "null".
      case Some(_)  => LexingHighlighter
      case _ => null
    }
  }
}

class ColorSettings extends ColorSettingsPage {
  override def getHighlighter: SyntaxHighlighter = LexingHighlighter

  override def getAdditionalHighlightingTagToDescriptorMap: util.Map[String, TextAttributesKey] = null


  override def getAttributeDescriptors: Array[AttributesDescriptor] = {
    val keyword = new AttributesDescriptor("Keywords", Attributes.keyword)
    val comment = new AttributesDescriptor("Comments", Attributes.comment)
    val md = new AttributesDescriptor("Module Delimiter", Attributes.MD)
    val dd = new AttributesDescriptor("Declaration Delimiter", Attributes.DD)
    val od = new AttributesDescriptor("Object Delimiter", Attributes.OD)
    val derived = new AttributesDescriptor("(Potential) Dervied Declaration Keywords", Attributes.derived)
    val name = new AttributesDescriptor("Constant / Theory Names", Attributes.name)
    val uri = new AttributesDescriptor("MMT URIs", Attributes.uri)
    val term = new AttributesDescriptor("MMT Terms", Attributes.term)
    val not = new AttributesDescriptor("Notations", Attributes.not)
    Array(md, dd, od, keyword, comment, derived, name, uri, term, not)
  }

  override def getDisplayName: String = "MMT"

  override def getColorDescriptors: Array[ColorDescriptor] = ColorDescriptor.EMPTY_ARRAY

  override def getIcon: Icon = MMT.filetypeIcon

  import info.kwarc.mmt.utils._

  override def getDemoText: String = getResourceAsString("/mmt_resources/highlighting.mmt")
}

class MMTAnnotator extends com.intellij.lang.annotation.Annotator {

  import Attributes._


  override def annotate(element: PsiElement, holder: AnnotationHolder): Unit = {
    implicit val hold: AnnotationHolder = holder
    MMT.get(element.getProject).foreach { mmt =>
      element.getContainingFile
      implicit val elem: PsiElement = element
      element match {
        case _: MMTModcomment_impl | _: MMTDeclComment_impl | _: MMTObjComment_impl =>
          getChildren(element).init.foreach(i => highlight(comment)(i, holder)) // holder.createInfoAnnotation(i.getTextRange,"").setTextAttributes(LexingHighlighter.comment))
        case _: MMTNotComp_impl =>
          getChildren(element).tail.foreach(i => highlight(not)(i, holder))
        // highlight(LexingHighlighter.not)
        case _: MMTPname_impl =>
          highlight(name)
        case _: MMTTerm_impl =>
          highlight(term)
        case urie: MMTUri_impl =>
          highlight(uri)
        case _: MMTDerivedsimple_impl | _: MMTDerivedheader_impl =>
          highlight(derived)
        case e: MMTError_impl =>
          holder.newAnnotation(HighlightSeverity.ERROR,"Block needs closing").range(e.getParent).create()//.createErrorAnnotation(e.getParent, "Block needs closing")
        // getChildren(element).foreach(i => highlight(derived)(i,holder))
        case _ =>
      }
    }
  }
}