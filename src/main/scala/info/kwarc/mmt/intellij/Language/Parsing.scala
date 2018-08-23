package info.kwarc.mmt.intellij.Language

import java.util

import com.intellij.execution.process.ConsoleHighlighter
import com.intellij.extapi.psi.PsiFileBase
import com.intellij.lang.ParserDefinition.SpaceRequirements
import com.intellij.lang.{ASTNode, ParserDefinition, PsiParser}
import com.intellij.lexer.{FlexAdapter, Lexer}
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.{FileType, SyntaxHighlighter, SyntaxHighlighterBase, SyntaxHighlighterFactory}
import com.intellij.openapi.options.colors.{AttributesDescriptor, ColorDescriptor, ColorSettingsPage}
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.{FileViewProvider, PsiElement, PsiFile, TokenType}
import com.intellij.psi.tree.{IElementType, IFileElementType, TokenSet}
import info.kwarc.mmt.api.utils.MMTSystem
import info.kwarc.mmt.intellij.Language.psi.MMTParserTypes
import info.kwarc.mmt.intellij.MMT
import javax.swing.Icon

class MMTLexerAdapter extends FlexAdapter(new MMTLexer(null))

class MMTFileBase(viewProvider : FileViewProvider) extends PsiFileBase(viewProvider,MMTLanguage.INSTANCE) {
  override def getFileType: FileType = MMTFile.INSTANCE
}

class MMTParserDefinition extends ParserDefinition {
  val FILE = new IFileElementType(MMTLanguage.INSTANCE)
  val WHITESPACES = TokenSet.create(TokenType.WHITE_SPACE)

  override def getWhitespaceTokens: TokenSet = TokenSet.create(MMTParserTypes.WHITESPACE)

  override def getFileNodeType: IFileElementType = FILE

  override def createElement(node: ASTNode): PsiElement = MMTParserTypes.Factory.createElement(node)

  override def createFile(viewProvider: FileViewProvider): PsiFile = new MMTFileBase(viewProvider)

  override def getCommentTokens: TokenSet = TokenSet.EMPTY// TokenSet.create(MMTParserTypes.MOD_COMMENT)

  override def createParser(project: Project): PsiParser = new MMTParser

  override def getStringLiteralElements: TokenSet = TokenSet.EMPTY

  override def spaceExistanceTypeBetweenTokens(left: ASTNode, right: ASTNode): ParserDefinition.SpaceRequirements = SpaceRequirements.MAY

  override def createLexer(project: Project): Lexer = new MMTLexerAdapter
}

object LexingHighlighter extends SyntaxHighlighterBase {
  override def getHighlightingLexer: Lexer = new MMTLexerAdapter

  private def make(kw: String, key: TextAttributesKey) = TextAttributesKey.createTextAttributesKey(kw, key)

  lazy val keyword = make("mmt.KEYWORD", DefaultLanguageHighlighterColors.KEYWORD)
  lazy val comment = make("mmt.COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT)
  lazy val derived = make("mmt.Derived",DefaultLanguageHighlighterColors.LINE_COMMENT)
  lazy val MD = make("mmt.MD", ConsoleHighlighter.RED_BRIGHT)
  lazy val DD = make("mmt.DD", ConsoleHighlighter.GREEN)
  lazy val OD = make("mmt.OD", ConsoleHighlighter.BLUE)
  lazy val name = make("mmt.Name",DefaultLanguageHighlighterColors.FUNCTION_DECLARATION)
  lazy val uri = make("mmt.URI",DefaultLanguageHighlighterColors.NUMBER)
  lazy val term = make("mmt.term",DefaultLanguageHighlighterColors.STATIC_FIELD)
  lazy val not = make("mmt.notation",DefaultLanguageHighlighterColors.DOC_COMMENT_TAG_VALUE)


  override def getTokenHighlights(tokenType: IElementType): Array[TextAttributesKey] = SyntaxHighlighterBase.pack(tokenType match {
    case MMTParserTypes.COMMENT | MMTParserTypes.COMMENT_KEY =>
      comment
    case MMTParserTypes.NOTATION => not
    case MMTParserTypes.MD => MD
    case MMTParserTypes.DD => DD
    case MMTParserTypes.OD => OD
    case MMTParserTypes.NAME => name
    case MMTParserTypes.TMSYM => term
    case MMTParserTypes.URI => uri
    case MMTParserTypes.DERIVED => derived
    case MMTParserTypes.THEORY_KEY |
         MMTParserTypes.ABBREV_KEY |
         // MMTParserTypes.ALIAS_KEY |
         // MMTParserTypes.ARROW_KEY |
         MMTParserTypes.EQ_KEY |
         MMTParserTypes.IMPLICIT_KEY |
         MMTParserTypes.IMPORT_KEY |
         MMTParserTypes.INCLUDE_KEY |
         MMTParserTypes.NAMESPACE_KEY |
         MMTParserTypes.NOT_KEY |
         MMTParserTypes.PARAM_KEY |
         MMTParserTypes.ROLE_KEY |
         MMTParserTypes.RULE_KEY |
         // MMTParserTypes.STRUCTURE_KEY |
         MMTParserTypes.TYPE_KEY /* |
      MMTParserTypes.VIEW_KEY */
    =>
      keyword
    case _ => null
  }
  )
}

class Factory extends SyntaxHighlighterFactory{
  override def getSyntaxHighlighter(project: Project, virtualFile: VirtualFile): SyntaxHighlighter = {
    MMT.get(project) match {
      case Some(mmt) if virtualFile.getFileType.getName == "MMT" => LexingHighlighter
      case _ => null
    }
  }
}

class ColorSettings extends ColorSettingsPage {
  override def getHighlighter: SyntaxHighlighter = LexingHighlighter

  override def getAdditionalHighlightingTagToDescriptorMap: util.Map[String, TextAttributesKey] = null

  override def getAttributeDescriptors: Array[AttributesDescriptor] = {
    val keyword = new AttributesDescriptor("Keywords",LexingHighlighter.keyword)
    val comment = new AttributesDescriptor("Comments",LexingHighlighter.comment)
    val md = new AttributesDescriptor("Module Delimiter",LexingHighlighter.MD)
    val dd = new AttributesDescriptor("Declaration Delimiter",LexingHighlighter.DD)
    val od = new AttributesDescriptor("Object Delimiter",LexingHighlighter.OD)
    val derived = new AttributesDescriptor("(Potential) Dervied Declaration Keywords",LexingHighlighter.derived)
    val name = new AttributesDescriptor("Constant / Theory Names",LexingHighlighter.name)
    val uri = new AttributesDescriptor("MMT URIs",LexingHighlighter.uri)
    val term = new AttributesDescriptor("MMT Terms",LexingHighlighter.term)
    val not = new AttributesDescriptor("Notations",LexingHighlighter.not)
    Array(md,dd,od,keyword,comment,derived,name,uri,term,not)
  }

  override def getDisplayName: String = "MMT"

  override def getColorDescriptors: Array[ColorDescriptor] = ColorDescriptor.EMPTY_ARRAY

  override def getIcon: Icon = MMT.icon

  override def getDemoText: String = MMTSystem.getResourceAsString("/mmt_resources/highlighting.mmt")
}