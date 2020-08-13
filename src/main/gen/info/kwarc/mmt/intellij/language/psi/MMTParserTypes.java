// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import info.kwarc.mmt.intellij.language.psi.imps.*;

public interface MMTParserTypes {

  IElementType ALIAS_COMP = new MMTElementType("ALIAS_COMP");
  IElementType ANY = new MMTElementType("ANY");
  IElementType COMPONENT = new MMTElementType("COMPONENT");
  IElementType DEF_COMP = new MMTElementType("DEF_COMP");
  IElementType DERIVED_DECL = new MMTElementType("DERIVED_DECL");
  IElementType DERIVED_HEADER = new MMTElementType("DERIVED_HEADER");
  IElementType DERIVED_SIMPLE = new MMTElementType("DERIVED_SIMPLE");
  IElementType DIAGRAM = new MMTElementType("DIAGRAM");
  IElementType DOCUMENT_SCOPED_COMMENT = new MMTElementType("DOCUMENT_SCOPED_COMMENT");
  IElementType ERROR = new MMTElementType("ERROR");
  IElementType FIXMETA = new MMTElementType("FIXMETA");
  IElementType IMPORT = new MMTElementType("IMPORT");
  IElementType INCLUDE = new MMTElementType("INCLUDE");
  IElementType KEYWRD = new MMTElementType("KEYWRD");
  IElementType META_COMP = new MMTElementType("META_COMP");
  IElementType META_DATUM = new MMTElementType("META_DATUM");
  IElementType MOD = new MMTElementType("MOD");
  IElementType MODULE_DECL = new MMTElementType("MODULE_DECL");
  IElementType MODULE_SCOPED_COMMENT = new MMTElementType("MODULE_SCOPED_COMMENT");
  IElementType NAMESPACE = new MMTElementType("NAMESPACE");
  IElementType NESTED_THEORY = new MMTElementType("NESTED_THEORY");
  IElementType NESTED_VIEW = new MMTElementType("NESTED_VIEW");
  IElementType NOT_COMP = new MMTElementType("NOT_COMP");
  IElementType OBJ_COMMENT = new MMTElementType("OBJ_COMMENT");
  IElementType PNAME = new MMTElementType("PNAME");
  IElementType PREC = new MMTElementType("PREC");
  IElementType ROLE_COMP = new MMTElementType("ROLE_COMP");
  IElementType RULE = new MMTElementType("RULE");
  IElementType STRUCTURE = new MMTElementType("STRUCTURE");
  IElementType STRUCTURE_BODY = new MMTElementType("STRUCTURE_BODY");
  IElementType TERM = new MMTElementType("TERM");
  IElementType THEORY = new MMTElementType("THEORY");
  IElementType THEORY_BODY = new MMTElementType("THEORY_BODY");
  IElementType THEORY_CONSTANT = new MMTElementType("THEORY_CONSTANT");
  IElementType THEORY_DECL = new MMTElementType("THEORY_DECL");
  IElementType THEORY_HEADER = new MMTElementType("THEORY_HEADER");
  IElementType TMLIST = new MMTElementType("TMLIST");
  IElementType TYPE_COMP = new MMTElementType("TYPE_COMP");
  IElementType URI = new MMTElementType("URI");
  IElementType URILIT = new MMTElementType("URILIT");
  IElementType URITOKEN = new MMTElementType("URITOKEN");
  IElementType VIEW = new MMTElementType("VIEW");
  IElementType VIEW_BODY = new MMTElementType("VIEW_BODY");
  IElementType VIEW_CONSTANT = new MMTElementType("VIEW_CONSTANT");
  IElementType VIEW_DECL = new MMTElementType("VIEW_DECL");
  IElementType VIEW_HEADER = new MMTElementType("VIEW_HEADER");
  IElementType WS = new MMTElementType("WS");

  IElementType ABBREV_KEY = new MMTTokenType("ABBREV_KEY");
  IElementType ALIAS_KEY = new MMTTokenType("ALIAS_KEY");
  IElementType ARROW_KEY = new MMTTokenType("ARROW_KEY");
  IElementType COLONEQ_KEY = new MMTTokenType("COLONEQ_KEY");
  IElementType COLON_KEY = new MMTTokenType("COLON_KEY");
  IElementType COMMA_KEY = new MMTTokenType("COMMA_KEY");
  IElementType COMMENT_KEY = new MMTTokenType("COMMENT_KEY");
  IElementType CONSTANT_KEY = new MMTTokenType("CONSTANT_KEY");
  IElementType DD = new MMTTokenType("DD");
  IElementType DIAGRAM_KEY = new MMTTokenType("DIAGRAM_KEY");
  IElementType EQ_KEY = new MMTTokenType("EQ_KEY");
  IElementType FIXMETA_KEY = new MMTTokenType("FIXMETA_KEY");
  IElementType IMPLICIT_KEY = new MMTTokenType("IMPLICIT_KEY");
  IElementType IMPORT_KEY = new MMTTokenType("IMPORT_KEY");
  IElementType INCLUDE_KEY = new MMTTokenType("INCLUDE_KEY");
  IElementType LINK_KEY = new MMTTokenType("LINK_KEY");
  IElementType MD = new MMTTokenType("MD");
  IElementType META_KEY = new MMTTokenType("META_KEY");
  IElementType NAMESPACE_KEY = new MMTTokenType("NAMESPACE_KEY");
  IElementType NOT_KEY = new MMTTokenType("NOT_KEY");
  IElementType OD = new MMTTokenType("OD");
  IElementType PARAM_KEY = new MMTTokenType("PARAM_KEY");
  IElementType PREC_KEY = new MMTTokenType("PREC_KEY");
  IElementType ROLE_KEY = new MMTTokenType("ROLE_KEY");
  IElementType RULE_KEY = new MMTTokenType("RULE_KEY");
  IElementType STRUCTURE_KEY = new MMTTokenType("STRUCTURE_KEY");
  IElementType THEORY_KEY = new MMTTokenType("THEORY_KEY");
  IElementType TOKEN = new MMTTokenType("TOKEN");
  IElementType URI_KEY = new MMTTokenType("URI_KEY");
  IElementType VIEW_KEY = new MMTTokenType("VIEW_KEY");
  IElementType WHITESPACE = new MMTTokenType("WHITESPACE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ALIAS_COMP) {
        return new MMTAliasComp_impl(node);
      }
      else if (type == ANY) {
        return new MMTAny_impl(node);
      }
      else if (type == COMPONENT) {
        return new MMTComponent_impl(node);
      }
      else if (type == DEF_COMP) {
        return new MMTDefComp_impl(node);
      }
      else if (type == DERIVED_DECL) {
        return new MMTDerivedDecl_impl(node);
      }
      else if (type == DERIVED_HEADER) {
        return new MMTDerivedHeader_impl(node);
      }
      else if (type == DERIVED_SIMPLE) {
        return new MMTDerivedSimple_impl(node);
      }
      else if (type == DIAGRAM) {
        return new MMTDiagram_impl(node);
      }
      else if (type == DOCUMENT_SCOPED_COMMENT) {
        return new MMTDocumentScopedComment_impl(node);
      }
      else if (type == ERROR) {
        return new MMTError_impl(node);
      }
      else if (type == FIXMETA) {
        return new MMTFixmeta_impl(node);
      }
      else if (type == IMPORT) {
        return new MMTImport_impl(node);
      }
      else if (type == INCLUDE) {
        return new MMTInclude_impl(node);
      }
      else if (type == KEYWRD) {
        return new MMTKeywrd_impl(node);
      }
      else if (type == META_COMP) {
        return new MMTMetaComp_impl(node);
      }
      else if (type == META_DATUM) {
        return new MMTMetaDatum_impl(node);
      }
      else if (type == MOD) {
        return new MMTMod_impl(node);
      }
      else if (type == MODULE_DECL) {
        return new MMTModuleDecl_impl(node);
      }
      else if (type == MODULE_SCOPED_COMMENT) {
        return new MMTModuleScopedComment_impl(node);
      }
      else if (type == NAMESPACE) {
        return new MMTNamespace_impl(node);
      }
      else if (type == NESTED_THEORY) {
        return new MMTNestedTheory_impl(node);
      }
      else if (type == NESTED_VIEW) {
        return new MMTNestedView_impl(node);
      }
      else if (type == NOT_COMP) {
        return new MMTNotComp_impl(node);
      }
      else if (type == OBJ_COMMENT) {
        return new MMTObjComment_impl(node);
      }
      else if (type == PNAME) {
        return new MMTPname_impl(node);
      }
      else if (type == PREC) {
        return new MMTPrec_impl(node);
      }
      else if (type == ROLE_COMP) {
        return new MMTRoleComp_impl(node);
      }
      else if (type == RULE) {
        return new MMTRule_impl(node);
      }
      else if (type == STRUCTURE) {
        return new MMTStructure_impl(node);
      }
      else if (type == STRUCTURE_BODY) {
        return new MMTStructureBody_impl(node);
      }
      else if (type == TERM) {
        return new MMTTerm_impl(node);
      }
      else if (type == THEORY) {
        return new MMTTheory_impl(node);
      }
      else if (type == THEORY_BODY) {
        return new MMTTheoryBody_impl(node);
      }
      else if (type == THEORY_CONSTANT) {
        return new MMTTheoryConstant_impl(node);
      }
      else if (type == THEORY_DECL) {
        return new MMTTheoryDecl_impl(node);
      }
      else if (type == THEORY_HEADER) {
        return new MMTTheoryHeader_impl(node);
      }
      else if (type == TMLIST) {
        return new MMTTmlist_impl(node);
      }
      else if (type == TYPE_COMP) {
        return new MMTTypeComp_impl(node);
      }
      else if (type == URI) {
        return new MMTUri_impl(node);
      }
      else if (type == URILIT) {
        return new MMTUrilit_impl(node);
      }
      else if (type == URITOKEN) {
        return new MMTUritoken_impl(node);
      }
      else if (type == VIEW) {
        return new MMTView_impl(node);
      }
      else if (type == VIEW_BODY) {
        return new MMTViewBody_impl(node);
      }
      else if (type == VIEW_CONSTANT) {
        return new MMTViewConstant_impl(node);
      }
      else if (type == VIEW_DECL) {
        return new MMTViewDecl_impl(node);
      }
      else if (type == VIEW_HEADER) {
        return new MMTViewHeader_impl(node);
      }
      else if (type == WS) {
        return new MMTWs_impl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
