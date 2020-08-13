// This is a generated file. Not intended for manual editing.
package info.kwarc.mmt.intellij.language;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static info.kwarc.mmt.intellij.language.psi.MMTParserTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class MMTParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return file(b, l + 1);
  }

  /* ********************************************************** */
  // ALIAS_KEY pname
  public static boolean aliasComp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "aliasComp")) return false;
    if (!nextTokenIs(b, ALIAS_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ALIAS_KEY);
    r = r && pname(b, l + 1);
    exit_section_(b, m, ALIAS_COMP, r);
    return r;
  }

  /* ********************************************************** */
  // TOKEN | keywrd | COLON_KEY | COLONEQ_KEY | COMMENT_KEY | EQ_KEY | NOT_KEY | ALIAS_KEY | PARAM_KEY | ARROW_KEY | COMMA_KEY
  public static boolean any(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "any")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ANY, "<any>");
    r = consumeToken(b, TOKEN);
    if (!r) r = keywrd(b, l + 1);
    if (!r) r = consumeToken(b, COLON_KEY);
    if (!r) r = consumeToken(b, COLONEQ_KEY);
    if (!r) r = consumeToken(b, COMMENT_KEY);
    if (!r) r = consumeToken(b, EQ_KEY);
    if (!r) r = consumeToken(b, NOT_KEY);
    if (!r) r = consumeToken(b, ALIAS_KEY);
    if (!r) r = consumeToken(b, PARAM_KEY);
    if (!r) r = consumeToken(b, ARROW_KEY);
    if (!r) r = consumeToken(b, COMMA_KEY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // objComment | typeComp | defComp | notComp | roleComp | metaComp | aliasComp
  public static boolean component(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "component")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, COMPONENT, "<component>");
    r = objComment(b, l + 1);
    if (!r) r = typeComp(b, l + 1);
    if (!r) r = defComp(b, l + 1);
    if (!r) r = notComp(b, l + 1);
    if (!r) r = roleComp(b, l + 1);
    if (!r) r = metaComp(b, l + 1);
    if (!r) r = aliasComp(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // EQ_KEY term
  public static boolean defComp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "defComp")) return false;
    if (!nextTokenIs(b, EQ_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ_KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, DEF_COMP, r);
    return r;
  }

  /* ********************************************************** */
  // (derivedHeader theoryBody (MD|error)) | (derivedSimple DD)
  public static boolean derivedDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedDecl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DERIVED_DECL, "<derived decl>");
    r = derivedDecl_0(b, l + 1);
    if (!r) r = derivedDecl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // derivedHeader theoryBody (MD|error)
  private static boolean derivedDecl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedDecl_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = derivedHeader(b, l + 1);
    r = r && theoryBody(b, l + 1);
    r = r && derivedDecl_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD|error
  private static boolean derivedDecl_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedDecl_0_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  // derivedSimple DD
  private static boolean derivedDecl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedDecl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = derivedSimple(b, l + 1);
    r = r && consumeToken(b, DD);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // any any+ OD? EQ_KEY
  public static boolean derivedHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedHeader")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DERIVED_HEADER, "<derived header>");
    r = any(b, l + 1);
    r = r && derivedHeader_1(b, l + 1);
    r = r && derivedHeader_2(b, l + 1);
    r = r && consumeToken(b, EQ_KEY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // any+
  private static boolean derivedHeader_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedHeader_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!any(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "derivedHeader_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // OD?
  private static boolean derivedHeader_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedHeader_2")) return false;
    consumeToken(b, OD);
    return true;
  }

  /* ********************************************************** */
  // any any+
  public static boolean derivedSimple(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedSimple")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, DERIVED_SIMPLE, "<derived simple>");
    r = any(b, l + 1);
    r = r && derivedSimple_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // any+
  private static boolean derivedSimple_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "derivedSimple_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!any(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "derivedSimple_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // DIAGRAM_KEY pname (COLON_KEY uri)? COLONEQ_KEY (any|OD|DD)* (MD | error)
  public static boolean diagram(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "diagram")) return false;
    if (!nextTokenIs(b, DIAGRAM_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, DIAGRAM_KEY);
    r = r && pname(b, l + 1);
    r = r && diagram_2(b, l + 1);
    r = r && consumeToken(b, COLONEQ_KEY);
    r = r && diagram_4(b, l + 1);
    r = r && diagram_5(b, l + 1);
    exit_section_(b, m, DIAGRAM, r);
    return r;
  }

  // (COLON_KEY uri)?
  private static boolean diagram_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "diagram_2")) return false;
    diagram_2_0(b, l + 1);
    return true;
  }

  // COLON_KEY uri
  private static boolean diagram_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "diagram_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON_KEY);
    r = r && uri(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (any|OD|DD)*
  private static boolean diagram_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "diagram_4")) return false;
    while (true) {
      int c = current_position_(b);
      if (!diagram_4_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "diagram_4", c)) break;
    }
    return true;
  }

  // any|OD|DD
  private static boolean diagram_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "diagram_4_0")) return false;
    boolean r;
    r = any(b, l + 1);
    if (!r) r = consumeToken(b, OD);
    if (!r) r = consumeToken(b, DD);
    return r;
  }

  // MD | error
  private static boolean diagram_5(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "diagram_5")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COMMENT_KEY (any|OD|DD)* (MD | error)
  public static boolean documentScopedComment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentScopedComment")) return false;
    if (!nextTokenIs(b, COMMENT_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT_KEY);
    r = r && documentScopedComment_1(b, l + 1);
    r = r && documentScopedComment_2(b, l + 1);
    exit_section_(b, m, DOCUMENT_SCOPED_COMMENT, r);
    return r;
  }

  // (any|OD|DD)*
  private static boolean documentScopedComment_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentScopedComment_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!documentScopedComment_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "documentScopedComment_1", c)) break;
    }
    return true;
  }

  // any|OD|DD
  private static boolean documentScopedComment_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentScopedComment_1_0")) return false;
    boolean r;
    r = any(b, l + 1);
    if (!r) r = consumeToken(b, OD);
    if (!r) r = consumeToken(b, DD);
    return r;
  }

  // MD | error
  private static boolean documentScopedComment_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "documentScopedComment_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // any*
  public static boolean error(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "error")) return false;
    Marker m = enter_section_(b, l, _NONE_, ERROR, "<error>");
    while (true) {
      int c = current_position_(b);
      if (!any(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "error", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // mod*
  static boolean file(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file")) return false;
    while (true) {
      int c = current_position_(b);
      if (!mod(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "file", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // FIXMETA_KEY uri (MD|error)
  public static boolean fixmeta(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fixmeta")) return false;
    if (!nextTokenIs(b, FIXMETA_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, FIXMETA_KEY);
    r = r && uri(b, l + 1);
    r = r && fixmeta_2(b, l + 1);
    exit_section_(b, m, FIXMETA, r);
    return r;
  }

  // MD|error
  private static boolean fixmeta_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "fixmeta_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // IMPORT_KEY pname uri (MD | error)
  public static boolean import_$(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_$")) return false;
    if (!nextTokenIs(b, IMPORT_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, IMPORT_KEY);
    r = r && pname(b, l + 1);
    r = r && uri(b, l + 1);
    r = r && import_3(b, l + 1);
    exit_section_(b, m, IMPORT, r);
    return r;
  }

  // MD | error
  private static boolean import_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "import_3")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // INCLUDE_KEY (urilit|uri) tmlist? (OD EQ_KEY (urilit|uri))?
  public static boolean include(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include")) return false;
    if (!nextTokenIs(b, INCLUDE_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, INCLUDE_KEY);
    r = r && include_1(b, l + 1);
    r = r && include_2(b, l + 1);
    r = r && include_3(b, l + 1);
    exit_section_(b, m, INCLUDE, r);
    return r;
  }

  // urilit|uri
  private static boolean include_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_1")) return false;
    boolean r;
    r = urilit(b, l + 1);
    if (!r) r = uri(b, l + 1);
    return r;
  }

  // tmlist?
  private static boolean include_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_2")) return false;
    tmlist(b, l + 1);
    return true;
  }

  // (OD EQ_KEY (urilit|uri))?
  private static boolean include_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_3")) return false;
    include_3_0(b, l + 1);
    return true;
  }

  // OD EQ_KEY (urilit|uri)
  private static boolean include_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, OD, EQ_KEY);
    r = r && include_3_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // urilit|uri
  private static boolean include_3_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "include_3_0_2")) return false;
    boolean r;
    r = urilit(b, l + 1);
    if (!r) r = uri(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COMMENT_KEY | NAMESPACE_KEY | IMPORT_KEY | ABBREV_KEY | THEORY_KEY  | VIEW_KEY | DIAGRAM_KEY | RULE_KEY | INCLUDE_KEY | PREC_KEY | ROLE_KEY | META_KEY | LINK_KEY | CONSTANT_KEY | IMPLICIT_KEY | STRUCTURE_KEY | FIXMETA_KEY | URI_KEY
  public static boolean keywrd(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "keywrd")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, KEYWRD, "<keywrd>");
    r = consumeToken(b, COMMENT_KEY);
    if (!r) r = consumeToken(b, NAMESPACE_KEY);
    if (!r) r = consumeToken(b, IMPORT_KEY);
    if (!r) r = consumeToken(b, ABBREV_KEY);
    if (!r) r = consumeToken(b, THEORY_KEY);
    if (!r) r = consumeToken(b, VIEW_KEY);
    if (!r) r = consumeToken(b, DIAGRAM_KEY);
    if (!r) r = consumeToken(b, RULE_KEY);
    if (!r) r = consumeToken(b, INCLUDE_KEY);
    if (!r) r = consumeToken(b, PREC_KEY);
    if (!r) r = consumeToken(b, ROLE_KEY);
    if (!r) r = consumeToken(b, META_KEY);
    if (!r) r = consumeToken(b, LINK_KEY);
    if (!r) r = consumeToken(b, CONSTANT_KEY);
    if (!r) r = consumeToken(b, IMPLICIT_KEY);
    if (!r) r = consumeToken(b, STRUCTURE_KEY);
    if (!r) r = consumeToken(b, FIXMETA_KEY);
    if (!r) r = consumeToken(b, URI_KEY);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // META_KEY term | LINK_KEY term
  public static boolean metaComp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaComp")) return false;
    if (!nextTokenIs(b, "<meta comp>", LINK_KEY, META_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, META_COMP, "<meta comp>");
    r = metaComp_0(b, l + 1);
    if (!r) r = metaComp_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // META_KEY term
  private static boolean metaComp_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaComp_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, META_KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // LINK_KEY term
  private static boolean metaComp_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "metaComp_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LINK_KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // META_KEY uri tmlist
  public static boolean meta_datum(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "meta_datum")) return false;
    if (!nextTokenIs(b, META_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, META_KEY);
    r = r && uri(b, l + 1);
    r = r && tmlist(b, l + 1);
    exit_section_(b, m, META_DATUM, r);
    return r;
  }

  /* ********************************************************** */
  // namespace | import | fixmeta | meta_datum (MD|error) | rule (MD|error) | documentScopedComment | theory | view | diagram
  public static boolean mod(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MOD, "<mod>");
    r = namespace(b, l + 1);
    if (!r) r = import_$(b, l + 1);
    if (!r) r = fixmeta(b, l + 1);
    if (!r) r = mod_3(b, l + 1);
    if (!r) r = mod_4(b, l + 1);
    if (!r) r = documentScopedComment(b, l + 1);
    if (!r) r = theory(b, l + 1);
    if (!r) r = view(b, l + 1);
    if (!r) r = diagram(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // meta_datum (MD|error)
  private static boolean mod_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod_3")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = meta_datum(b, l + 1);
    r = r && mod_3_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD|error
  private static boolean mod_3_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod_3_1")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  // rule (MD|error)
  private static boolean mod_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = rule(b, l + 1);
    r = r && mod_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD|error
  private static boolean mod_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "mod_4_1")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // COMMENT_KEY (any|OD)*
  public static boolean moduleScopedComment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleScopedComment")) return false;
    if (!nextTokenIs(b, COMMENT_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT_KEY);
    r = r && moduleScopedComment_1(b, l + 1);
    exit_section_(b, m, MODULE_SCOPED_COMMENT, r);
    return r;
  }

  // (any|OD)*
  private static boolean moduleScopedComment_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleScopedComment_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!moduleScopedComment_1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "moduleScopedComment_1", c)) break;
    }
    return true;
  }

  // any|OD
  private static boolean moduleScopedComment_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "moduleScopedComment_1_0")) return false;
    boolean r;
    r = any(b, l + 1);
    if (!r) r = consumeToken(b, OD);
    return r;
  }

  /* ********************************************************** */
  // (include | moduleScopedComment | meta_datum) (DD | error)
  public static boolean module_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MODULE_DECL, "<module decl>");
    r = module_decl_0(b, l + 1);
    r = r && module_decl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // include | moduleScopedComment | meta_datum
  private static boolean module_decl_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_decl_0")) return false;
    boolean r;
    r = include(b, l + 1);
    if (!r) r = moduleScopedComment(b, l + 1);
    if (!r) r = meta_datum(b, l + 1);
    return r;
  }

  // DD | error
  private static boolean module_decl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "module_decl_1")) return false;
    boolean r;
    r = consumeToken(b, DD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // NAMESPACE_KEY uri (MD | error)
  public static boolean namespace(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace")) return false;
    if (!nextTokenIs(b, NAMESPACE_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAMESPACE_KEY);
    r = r && uri(b, l + 1);
    r = r && namespace_2(b, l + 1);
    exit_section_(b, m, NAMESPACE, r);
    return r;
  }

  // MD | error
  private static boolean namespace_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "namespace_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // theoryHeader ((ABBREV_KEY ((term DD) | error)) | (EQ_KEY theoryBody (MD | error)))
  public static boolean nested_theory(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory")) return false;
    if (!nextTokenIs(b, THEORY_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = theoryHeader(b, l + 1);
    r = r && nested_theory_1(b, l + 1);
    exit_section_(b, m, NESTED_THEORY, r);
    return r;
  }

  // (ABBREV_KEY ((term DD) | error)) | (EQ_KEY theoryBody (MD | error))
  private static boolean nested_theory_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nested_theory_1_0(b, l + 1);
    if (!r) r = nested_theory_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ABBREV_KEY ((term DD) | error)
  private static boolean nested_theory_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ABBREV_KEY);
    r = r && nested_theory_1_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (term DD) | error
  private static boolean nested_theory_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory_1_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nested_theory_1_0_1_0(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // term DD
  private static boolean nested_theory_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term(b, l + 1);
    r = r && consumeToken(b, DD);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ_KEY theoryBody (MD | error)
  private static boolean nested_theory_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ_KEY);
    r = r && theoryBody(b, l + 1);
    r = r && nested_theory_1_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD | error
  private static boolean nested_theory_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_theory_1_1_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // view_header ((ABBREV_KEY term DD) | (EQ_KEY view_body MD))
  public static boolean nested_view(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_view")) return false;
    if (!nextTokenIs(b, "<nested view>", IMPLICIT_KEY, VIEW_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, NESTED_VIEW, "<nested view>");
    r = view_header(b, l + 1);
    r = r && nested_view_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (ABBREV_KEY term DD) | (EQ_KEY view_body MD)
  private static boolean nested_view_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_view_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = nested_view_1_0(b, l + 1);
    if (!r) r = nested_view_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ABBREV_KEY term DD
  private static boolean nested_view_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_view_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ABBREV_KEY);
    r = r && term(b, l + 1);
    r = r && consumeToken(b, DD);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ_KEY view_body MD
  private static boolean nested_view_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "nested_view_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ_KEY);
    r = r && view_body(b, l + 1);
    r = r && consumeToken(b, MD);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // NOT_KEY any+ prec?
  public static boolean notComp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notComp")) return false;
    if (!nextTokenIs(b, NOT_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NOT_KEY);
    r = r && notComp_1(b, l + 1);
    r = r && notComp_2(b, l + 1);
    exit_section_(b, m, NOT_COMP, r);
    return r;
  }

  // any+
  private static boolean notComp_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notComp_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!any(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "notComp_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // prec?
  private static boolean notComp_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "notComp_2")) return false;
    prec(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // COMMENT_KEY any*
  public static boolean objComment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objComment")) return false;
    if (!nextTokenIs(b, COMMENT_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT_KEY);
    r = r && objComment_1(b, l + 1);
    exit_section_(b, m, OBJ_COMMENT, r);
    return r;
  }

  // any*
  private static boolean objComment_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "objComment_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!any(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "objComment_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TOKEN | keywrd
  public static boolean pname(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "pname")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PNAME, "<pname>");
    r = consumeToken(b, TOKEN);
    if (!r) r = keywrd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // PREC_KEY TOKEN
  public static boolean prec(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "prec")) return false;
    if (!nextTokenIs(b, PREC_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, PREC_KEY, TOKEN);
    exit_section_(b, m, PREC, r);
    return r;
  }

  /* ********************************************************** */
  // ROLE_KEY any+
  public static boolean roleComp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "roleComp")) return false;
    if (!nextTokenIs(b, ROLE_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ROLE_KEY);
    r = r && roleComp_1(b, l + 1);
    exit_section_(b, m, ROLE_COMP, r);
    return r;
  }

  // any+
  private static boolean roleComp_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "roleComp_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = any(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!any(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "roleComp_1", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // RULE_KEY uri term
  public static boolean rule(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rule")) return false;
    if (!nextTokenIs(b, RULE_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, RULE_KEY);
    r = r && uri(b, l + 1);
    r = r && term(b, l + 1);
    exit_section_(b, m, RULE, r);
    return r;
  }

  /* ********************************************************** */
  // IMPLICIT_KEY? STRUCTURE_KEY pname COLON_KEY ((uri ((ABBREV_KEY term (DD|error)) | (EQ_KEY structureBody (MD|error)))) | error)
  public static boolean structure(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure")) return false;
    if (!nextTokenIs(b, "<structure>", IMPLICIT_KEY, STRUCTURE_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, STRUCTURE, "<structure>");
    r = structure_0(b, l + 1);
    r = r && consumeToken(b, STRUCTURE_KEY);
    r = r && pname(b, l + 1);
    r = r && consumeToken(b, COLON_KEY);
    r = r && structure_4(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IMPLICIT_KEY?
  private static boolean structure_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_0")) return false;
    consumeToken(b, IMPLICIT_KEY);
    return true;
  }

  // (uri ((ABBREV_KEY term (DD|error)) | (EQ_KEY structureBody (MD|error)))) | error
  private static boolean structure_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = structure_4_0(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // uri ((ABBREV_KEY term (DD|error)) | (EQ_KEY structureBody (MD|error)))
  private static boolean structure_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = uri(b, l + 1);
    r = r && structure_4_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (ABBREV_KEY term (DD|error)) | (EQ_KEY structureBody (MD|error))
  private static boolean structure_4_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4_0_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = structure_4_0_1_0(b, l + 1);
    if (!r) r = structure_4_0_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ABBREV_KEY term (DD|error)
  private static boolean structure_4_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ABBREV_KEY);
    r = r && term(b, l + 1);
    r = r && structure_4_0_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DD|error
  private static boolean structure_4_0_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4_0_1_0_2")) return false;
    boolean r;
    r = consumeToken(b, DD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  // EQ_KEY structureBody (MD|error)
  private static boolean structure_4_0_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4_0_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ_KEY);
    r = r && structureBody(b, l + 1);
    r = r && structure_4_0_1_1_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD|error
  private static boolean structure_4_0_1_1_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structure_4_0_1_1_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (viewDecl | theoryConstant)*
  public static boolean structureBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structureBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, STRUCTURE_BODY, "<structure body>");
    while (true) {
      int c = current_position_(b);
      if (!structureBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "structureBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // viewDecl | theoryConstant
  private static boolean structureBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "structureBody_0")) return false;
    boolean r;
    r = viewDecl(b, l + 1);
    if (!r) r = theoryConstant(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (urilit|any)+
  public static boolean term(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TERM, "<term>");
    r = term_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!term_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "term", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // urilit|any
  private static boolean term_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "term_0")) return false;
    boolean r;
    r = urilit(b, l + 1);
    if (!r) r = any(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // theoryHeader ((ABBREV_KEY term) | (EQ_KEY theoryBody) | error) (MD | error)
  public static boolean theory(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory")) return false;
    if (!nextTokenIs(b, THEORY_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = theoryHeader(b, l + 1);
    r = r && theory_1(b, l + 1);
    r = r && theory_2(b, l + 1);
    exit_section_(b, m, THEORY, r);
    return r;
  }

  // (ABBREV_KEY term) | (EQ_KEY theoryBody) | error
  private static boolean theory_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = theory_1_0(b, l + 1);
    if (!r) r = theory_1_1(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ABBREV_KEY term
  private static boolean theory_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ABBREV_KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ_KEY theoryBody
  private static boolean theory_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ_KEY);
    r = r && theoryBody(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD | error
  private static boolean theory_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (theory_decl)*
  public static boolean theoryBody(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryBody")) return false;
    Marker m = enter_section_(b, l, _NONE_, THEORY_BODY, "<theory body>");
    while (true) {
      int c = current_position_(b);
      if (!theoryBody_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "theoryBody", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  // (theory_decl)
  private static boolean theoryBody_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryBody_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = theory_decl(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // CONSTANT_KEY? pname (component OD)* component?
  public static boolean theoryConstant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryConstant")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, THEORY_CONSTANT, "<theory constant>");
    r = theoryConstant_0(b, l + 1);
    r = r && pname(b, l + 1);
    r = r && theoryConstant_2(b, l + 1);
    r = r && theoryConstant_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CONSTANT_KEY?
  private static boolean theoryConstant_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryConstant_0")) return false;
    consumeToken(b, CONSTANT_KEY);
    return true;
  }

  // (component OD)*
  private static boolean theoryConstant_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryConstant_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!theoryConstant_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "theoryConstant_2", c)) break;
    }
    return true;
  }

  // component OD
  private static boolean theoryConstant_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryConstant_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = component(b, l + 1);
    r = r && consumeToken(b, OD);
    exit_section_(b, m, null, r);
    return r;
  }

  // component?
  private static boolean theoryConstant_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryConstant_3")) return false;
    component(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // THEORY_KEY ((pname (COLON_KEY uri)? (PARAM_KEY tmlist OD)?) | error)
  public static boolean theoryHeader(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader")) return false;
    if (!nextTokenIs(b, THEORY_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, THEORY_KEY);
    r = r && theoryHeader_1(b, l + 1);
    exit_section_(b, m, THEORY_HEADER, r);
    return r;
  }

  // (pname (COLON_KEY uri)? (PARAM_KEY tmlist OD)?) | error
  private static boolean theoryHeader_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = theoryHeader_1_0(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // pname (COLON_KEY uri)? (PARAM_KEY tmlist OD)?
  private static boolean theoryHeader_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pname(b, l + 1);
    r = r && theoryHeader_1_0_1(b, l + 1);
    r = r && theoryHeader_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (COLON_KEY uri)?
  private static boolean theoryHeader_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader_1_0_1")) return false;
    theoryHeader_1_0_1_0(b, l + 1);
    return true;
  }

  // COLON_KEY uri
  private static boolean theoryHeader_1_0_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader_1_0_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON_KEY);
    r = r && uri(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (PARAM_KEY tmlist OD)?
  private static boolean theoryHeader_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader_1_0_2")) return false;
    theoryHeader_1_0_2_0(b, l + 1);
    return true;
  }

  // PARAM_KEY tmlist OD
  private static boolean theoryHeader_1_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theoryHeader_1_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PARAM_KEY);
    r = r && tmlist(b, l + 1);
    r = r && consumeToken(b, OD);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // module_decl | nested_theory | structure | nested_view  | ((rule | theoryConstant) (DD | error)) | derivedDecl
  public static boolean theory_decl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_decl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, THEORY_DECL, "<theory decl>");
    r = module_decl(b, l + 1);
    if (!r) r = nested_theory(b, l + 1);
    if (!r) r = structure(b, l + 1);
    if (!r) r = nested_view(b, l + 1);
    if (!r) r = theory_decl_4(b, l + 1);
    if (!r) r = derivedDecl(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (rule | theoryConstant) (DD | error)
  private static boolean theory_decl_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_decl_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = theory_decl_4_0(b, l + 1);
    r = r && theory_decl_4_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // rule | theoryConstant
  private static boolean theory_decl_4_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_decl_4_0")) return false;
    boolean r;
    r = rule(b, l + 1);
    if (!r) r = theoryConstant(b, l + 1);
    return r;
  }

  // DD | error
  private static boolean theory_decl_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "theory_decl_4_1")) return false;
    boolean r;
    r = consumeToken(b, DD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // (term COMMA_KEY)* term
  public static boolean tmlist(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tmlist")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TMLIST, "<tmlist>");
    r = tmlist_0(b, l + 1);
    r = r && term(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (term COMMA_KEY)*
  private static boolean tmlist_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tmlist_0")) return false;
    while (true) {
      int c = current_position_(b);
      if (!tmlist_0_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "tmlist_0", c)) break;
    }
    return true;
  }

  // term COMMA_KEY
  private static boolean tmlist_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "tmlist_0_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = term(b, l + 1);
    r = r && consumeToken(b, COMMA_KEY);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // COLON_KEY term
  public static boolean typeComp(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "typeComp")) return false;
    if (!nextTokenIs(b, COLON_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COLON_KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, TYPE_COMP, r);
    return r;
  }

  /* ********************************************************** */
  // (uritoken COLON_KEY uritoken) | uritoken
  public static boolean uri(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "uri")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, URI, "<uri>");
    r = uri_0(b, l + 1);
    if (!r) r = uritoken(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // uritoken COLON_KEY uritoken
  private static boolean uri_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "uri_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = uritoken(b, l + 1);
    r = r && consumeToken(b, COLON_KEY);
    r = r && uritoken(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // URI_KEY uri
  public static boolean urilit(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "urilit")) return false;
    if (!nextTokenIs(b, URI_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, URI_KEY);
    r = r && uri(b, l + 1);
    exit_section_(b, m, URILIT, r);
    return r;
  }

  /* ********************************************************** */
  // TOKEN | keywrd
  public static boolean uritoken(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "uritoken")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, URITOKEN, "<uritoken>");
    r = consumeToken(b, TOKEN);
    if (!r) r = keywrd(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // view_header ((ABBREV_KEY term) | (EQ_KEY view_body)| error) (MD | error)
  public static boolean view(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view")) return false;
    if (!nextTokenIs(b, "<view>", IMPLICIT_KEY, VIEW_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VIEW, "<view>");
    r = view_header(b, l + 1);
    r = r && view_1(b, l + 1);
    r = r && view_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (ABBREV_KEY term) | (EQ_KEY view_body)| error
  private static boolean view_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = view_1_0(b, l + 1);
    if (!r) r = view_1_1(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // ABBREV_KEY term
  private static boolean view_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, ABBREV_KEY);
    r = r && term(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // EQ_KEY view_body
  private static boolean view_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_1_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, EQ_KEY);
    r = r && view_body(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // MD | error
  private static boolean view_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_2")) return false;
    boolean r;
    r = consumeToken(b, MD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // CONSTANT_KEY? pname ((defComp (DD|error)) | error)
  public static boolean viewConstant(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewConstant")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VIEW_CONSTANT, "<view constant>");
    r = viewConstant_0(b, l + 1);
    r = r && pname(b, l + 1);
    r = r && viewConstant_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // CONSTANT_KEY?
  private static boolean viewConstant_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewConstant_0")) return false;
    consumeToken(b, CONSTANT_KEY);
    return true;
  }

  // (defComp (DD|error)) | error
  private static boolean viewConstant_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewConstant_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = viewConstant_2_0(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // defComp (DD|error)
  private static boolean viewConstant_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewConstant_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = defComp(b, l + 1);
    r = r && viewConstant_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DD|error
  private static boolean viewConstant_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewConstant_2_0_1")) return false;
    boolean r;
    r = consumeToken(b, DD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // module_decl | (viewConstant (DD | error))
  public static boolean viewDecl(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewDecl")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VIEW_DECL, "<view decl>");
    r = module_decl(b, l + 1);
    if (!r) r = viewDecl_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // viewConstant (DD | error)
  private static boolean viewDecl_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewDecl_1")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = viewConstant(b, l + 1);
    r = r && viewDecl_1_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // DD | error
  private static boolean viewDecl_1_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "viewDecl_1_1")) return false;
    boolean r;
    r = consumeToken(b, DD);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // viewDecl*
  public static boolean view_body(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_body")) return false;
    Marker m = enter_section_(b, l, _NONE_, VIEW_BODY, "<view body>");
    while (true) {
      int c = current_position_(b);
      if (!viewDecl(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "view_body", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // IMPLICIT_KEY? VIEW_KEY ((pname COLON_KEY ((uri ARROW_KEY (uri | error)) | error)) | error)
  public static boolean view_header(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header")) return false;
    if (!nextTokenIs(b, "<view header>", IMPLICIT_KEY, VIEW_KEY)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, VIEW_HEADER, "<view header>");
    r = view_header_0(b, l + 1);
    r = r && consumeToken(b, VIEW_KEY);
    r = r && view_header_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // IMPLICIT_KEY?
  private static boolean view_header_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header_0")) return false;
    consumeToken(b, IMPLICIT_KEY);
    return true;
  }

  // (pname COLON_KEY ((uri ARROW_KEY (uri | error)) | error)) | error
  private static boolean view_header_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = view_header_2_0(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // pname COLON_KEY ((uri ARROW_KEY (uri | error)) | error)
  private static boolean view_header_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = pname(b, l + 1);
    r = r && consumeToken(b, COLON_KEY);
    r = r && view_header_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (uri ARROW_KEY (uri | error)) | error
  private static boolean view_header_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header_2_0_2")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = view_header_2_0_2_0(b, l + 1);
    if (!r) r = error(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // uri ARROW_KEY (uri | error)
  private static boolean view_header_2_0_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header_2_0_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = uri(b, l + 1);
    r = r && consumeToken(b, ARROW_KEY);
    r = r && view_header_2_0_2_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // uri | error
  private static boolean view_header_2_0_2_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "view_header_2_0_2_0_2")) return false;
    boolean r;
    r = uri(b, l + 1);
    if (!r) r = error(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // WHITESPACE
  public static boolean ws(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "ws")) return false;
    if (!nextTokenIs(b, WHITESPACE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, WHITESPACE);
    exit_section_(b, m, WS, r);
    return r;
  }

}
