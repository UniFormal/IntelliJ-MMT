package info.kwarc.mmt.intellij.language;

import com.intellij.psi.tree.IElementType;
import info.kwarc.mmt.intellij.language.psi.MMTParserTypes;

%%

%class MMTLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
/* Throws out of memory exception, weirdly
%eofval{
    return MMTParserTypes.EOF;
%eofval}
*/

/* %{
  Integer push = 0;
  Boolean debug = false;
  ArrayList<Integer> states = new ArrayList<>();
    private String stostring(Integer s) {
        if (s == 2) return "URI";
        else if (s == 0) return "INITIAL";
        else if (s == 12) return "PSEUDONAME";
        else if (s == 14) return "HEADER";
        else if (s == 4) return "MOD_COMMENT";
        else if (s == 20) return "TERMLIST";
        else if (s == 6) return "DECL_COMMENT";
        else if (s == 24) return "MAYBECONSTANT";
        else if (s == 28) return "MODULE";
        else if (s == 18) return "TERM";
        else if (s == 34) return "NOTATION";
        else if (s == 26) return "CONSTANT";
        else if (s == 30) return "DERIVEDDECL";
        else if (s == 32) return "OBJ_COMMENT";
        else if (s == 10) return "NAME";
        else return "UNKNOWNSTATE" + s;
    }
    private String statestostring() {
        Iterator<Integer> it = states.iterator();
        String start = "]";
        while (((Iterator) it).hasNext()) {
            start = stostring(it.next()) + start;
            if (it.hasNext()) start = ", " + start;
        }
        return "[" + start;
    }
    private IElementType send(IElementType e) {
        if (debug) {
            if (e == MMTParserTypes.TMSYM) System.out.print(".");
            else if (e == MMTParserTypes.COMMENT) System.out.print(".");
            if (e == MMTParserTypes.NOTATION) System.out.print(".");
            else System.out.println("Returning " + e.toString());
        }
        return e;
    }
    private void beginstate(int s) {
        if (debug) System.out.println("Begin state " + stostring(s) + ": " + statestostring());
        states.add(s);
        if (s ==24) push = 0;

        yybegin(s);
    }
    private void endstate(int s) {
        int last = states.get(states.toArray().length - 1);
        if (last != s) System.out.println("Last state of " + statestostring() + " not " + s);
        // assert last == s;
        if (debug) System.out.print("Ending state " + stostring(s) + ": ");
        if (!states.isEmpty()) states.remove(states.toArray().length - 1);
        if (debug) System.out.println(statestostring());
        int i = YYINITIAL;
        if (!states.isEmpty()) { i = states.get(states.toArray().length - 1); }
        if (debug) System.out.println("     New state: " + stostring(i));
        yybegin(i);
    }
%} */

// ABBREVCHAR = "j"
// ABBREV = {ABBREVCHAR}(("M"|"D"|"O")(D?))?
WHITE_SPACE= [\s]+ //[\r|\n|\r\n|[ \t\f]]
MOD_delim=[❚]
DECL_delim=[❙]
OBJ_delim=[❘]
// DELIMITER={MOD_delim}|{DECL_delim}|{OBJ_delim}
INPUT_CHAR=[^\s❚❙❘:=>☞]
// TMTOKEN = [\w]+|{INPUT_CHAR}
// NON_DELIM=[^❚❙❘]
TOKEN= {INPUT_CHAR}+
// NAMETOKEN=[^\s❚❙❘:=]+
// COMMENT_KEY = "//"|"/T"
// CONTENTCOMMENT = "/T"|"/t"

/*
%state URI
%state MOD_COMMENT
%state DECL_COMMENT
%state IMPORT
%state NAME
%state PSEUDONAME
%state HEADER
%state PARAMS
%state TERM
%state TERMLIST
%state THEORYBODY
%state MAYBECONSTANT
%state CONSTANT
%state MODULE
%state DERIVEDDECL
%state OBJ_COMMENT
%state NOTATION
*/

%%

<YYINITIAL> {
    "namespace"     { return MMTParserTypes.NAMESPACE_KEY; }
    "import"        { return MMTParserTypes.IMPORT_KEY; }
    "include"       { return MMTParserTypes.INCLUDE_KEY; }
    "theory"        { return MMTParserTypes.THEORY_KEY; }
    "implicit"      { return MMTParserTypes.IMPLICIT_KEY; }
    "view"          { return MMTParserTypes.VIEW_KEY; }
    "abbrev"        { return MMTParserTypes.ABBREV_KEY; }
    "structure"     { return MMTParserTypes.STRUCTURE_KEY; }
    "rule"          { return MMTParserTypes.RULE_KEY; }
    "role"          { return MMTParserTypes.ROLE_KEY; }
    "meta"          { return MMTParserTypes.META_KEY; }
    "link"          { return MMTParserTypes.LINK_KEY; }
    "prec"          { return MMTParserTypes.PREC_KEY; }
    "constant"      { return MMTParserTypes.CONSTANT_KEY; }
      "fixmeta"     { return MMTParserTypes.FIXMETA_KEY; }
    "@"             { return MMTParserTypes.ALIAS_KEY; }
    ":"             { return MMTParserTypes.COLON_KEY; }
    ">"             { return MMTParserTypes.PARAM_KEY; }
    "="             { return MMTParserTypes.EQ_KEY; }
    ","             { return MMTParserTypes.COMMA_KEY; }
    "->"|"⟶"|"→"    { return MMTParserTypes.ARROW_KEY; }
    "##"|"#"        { return MMTParserTypes.NOT_KEY; }
    "//"|"/T"       { return MMTParserTypes.COMMENT_KEY; }
      "☞"           { return MMTParserTypes.URI_KEY; }
    // {TOKEN}         { return MMTParserTypes.TOKEN; }

    {MOD_delim}     { return MMTParserTypes.MD; }
    {DECL_delim}    { return MMTParserTypes.DD; }
    {OBJ_delim}     { return MMTParserTypes.OD; }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {TOKEN}         { return MMTParserTypes.TOKEN; }
     // {ABBREV}           { }
     // .             {return send(MMTParserTypes.INVALID); }
}

/*
<YYINITIAL> {
    "namespace"     {beginstate(URI); return send(MMTParserTypes.NAMESPACE_KEY); }
    "import"        {beginstate(URI); beginstate(PSEUDONAME); return send(MMTParserTypes.IMPORT_KEY); }
    "theory"        {beginstate(HEADER); return send(MMTParserTypes.THEORY_KEY); }
    "implicit"      {return MMTParserTypes.IMPLICIT_KEY; }
    "view"          {return MMTParserTypes.VIEW_KEY; }
    {COMMENT}|{CONTENTCOMMENT}  {beginstate(MOD_COMMENT); return send(MMTParserTypes.COMMENT_KEY); }
    // {TOKEN}         { return MMTParserTypes.TOKEN; }

    {MOD_delim}     { return send(MMTParserTypes.MD); }
    {DECL_delim}    { return send(MMTParserTypes.DD); }
    {OBJ_delim}     { return send(MMTParserTypes.OD); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}
<MOD_COMMENT> {
    {TMTOKEN}         { return send(MMTParserTypes.COMMENT); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {DECL_delim}    { return send(MMTParserTypes.COMMENT); }
    {OBJ_delim}     { return send(MMTParserTypes.COMMENT); }
    {MOD_delim}     { endstate(MOD_COMMENT); yypushback(1); }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}
<HEADER> {
    "abbrev"        {return send(MMTParserTypes.ABBREV_KEY); }
    ":"             {beginstate(URI); return send(MMTParserTypes.TYPE_KEY); }
    ">"             {beginstate(TERMLIST); return send(MMTParserTypes.PARAM_KEY); }
    ","             {return send(MMTParserTypes.COMMA_KEY); }
    "="             {endstate(HEADER); beginstate(MODULE); return send(MMTParserTypes.EQ_KEY); }
    "->"|"⟶"        {beginstate(URI); return send(MMTParserTypes.ARROW_KEY); }

    {NAMETOKEN}         { return send(MMTParserTypes.NAME); }
    {MOD_delim}     { endstate(HEADER); return send(MMTParserTypes.MD); }
    {DECL_delim}    { endstate(HEADER); return send(MMTParserTypes.DD); }
    {OBJ_delim}     { return send(MMTParserTypes.OD); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}

<MODULE> {
    "structure"     {return send(MMTParserTypes.STRUCTURE_KEY); }
    "implicit"      {return send(MMTParserTypes.IMPLICIT_KEY); }
    "view"          {return send(MMTParserTypes.VIEW_KEY); }
    "rule"          {beginstate(TERMLIST); beginstate(URI); return send(MMTParserTypes.RULE_KEY); }
    "include"       {beginstate(TERMLIST);beginstate(URI); return send(MMTParserTypes.INCLUDE_KEY);}
    "theory"        {beginstate(HEADER); return send(MMTParserTypes.THEORY_KEY); }
    {COMMENT}|{CONTENTCOMMENT}  {beginstate(DECL_COMMENT); return send(MMTParserTypes.COMMENT_KEY); }


    {NAMETOKEN}     { beginstate(MAYBECONSTANT); }
    {MOD_delim}     { endstate(MODULE); return send(MMTParserTypes.MD); }
    {DECL_delim}    { return send(MMTParserTypes.DD); }
    {OBJ_delim}     { return send(MMTParserTypes.OD); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
      .             {endstate(MODULE); return send(MMTParserTypes.INVALID); }
}
<DECL_COMMENT> {
    {TMTOKEN}         { return send(MMTParserTypes.COMMENT); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {DECL_delim}    { endstate(DECL_COMMENT); yypushback(1); }
    {OBJ_delim}     { return send(MMTParserTypes.COMMENT); }
    {MOD_delim}     { endstate(DECL_COMMENT); yypushback(1); }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}

<TERM> {
    {TMTOKEN}         { return send(MMTParserTypes.TMSYM); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {DECL_delim}    { endstate(TERM); yypushback(1); return send(MMTParserTypes.TMSYM); }
    {OBJ_delim}     { endstate(TERM); yypushback(1); return send(MMTParserTypes.TMSYM); }
    {MOD_delim}     { endstate(TERM); yypushback(1); return send(MMTParserTypes.TMSYM); }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}

<NOTATION> {
    {TMTOKEN}       { return send(MMTParserTypes.NOTATION); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {DECL_delim}    { endstate(NOTATION); yypushback(1); }
    {OBJ_delim}     { endstate(NOTATION); yypushback(1); }
    {MOD_delim}     { endstate(NOTATION); yypushback(1); }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}

<TERMLIST> {
    ","             { return send(MMTParserTypes.COMMA_KEY); }
    {TMTOKEN}         { return send(MMTParserTypes.TMSYM);}
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {DECL_delim}    { endstate(TERMLIST); yypushback(1); return send(MMTParserTypes.TMSYM); }
    {OBJ_delim}     { endstate(TERMLIST); yypushback(1); return send(MMTParserTypes.TMSYM); }
    {MOD_delim}     { endstate(TERMLIST); yypushback(1); return send(MMTParserTypes.TMSYM); }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}

<MAYBECONSTANT> {
    ":"|"="|"#"   { endstate(MAYBECONSTANT); beginstate(CONSTANT); yypushback(push); return send(MMTParserTypes.NAME); }
    "role"|"meta" { endstate(MAYBECONSTANT); beginstate(CONSTANT); yypushback(3+push); return send(MMTParserTypes.NAME); }
    {DECL_delim}    { endstate(MAYBECONSTANT); yypushback(push); return send(MMTParserTypes.NAME); }
    {WHITE_SPACE}   { push+=1;}
      {ABBREV}           { }
    .             {  yypushback(push); endstate(MAYBECONSTANT); beginstate(DERIVEDDECL); return send(MMTParserTypes.DERIVED);}
}

<CONSTANT> {
    ":"         {beginstate(TERM); return send(MMTParserTypes.TYPE_KEY); }
    "="         {beginstate(TERM); return send(MMTParserTypes.EQ_KEY); }
    "##"|"#"         {beginstate(NOTATION); return send(MMTParserTypes.NOT_KEY); }
    "role"      {beginstate(PSEUDONAME); return send(MMTParserTypes.ROLE_KEY); }
    "meta"      {beginstate(TERM); return send(MMTParserTypes.META_KEY); }
    "@"         {beginstate(NAME); return send(MMTParserTypes.ALIAS_KEY); }
    {COMMENT}|{CONTENTCOMMENT}  {beginstate(OBJ_COMMENT); return send(MMTParserTypes.COMMENT_KEY); }
    {DECL_delim}    { endstate(CONSTANT); return send(MMTParserTypes.DD); }
    {OBJ_delim}     { return send(MMTParserTypes.OD); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
      .             {endstate(CONSTANT); return send(MMTParserTypes.INVALID); }
}
<OBJ_COMMENT> {
    {TMTOKEN}         { return send(MMTParserTypes.COMMENT); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {DECL_delim}    { endstate(OBJ_COMMENT); yypushback(1); }
    {OBJ_delim}     { endstate(OBJ_COMMENT); yypushback(1); }
    {MOD_delim}     { endstate(OBJ_COMMENT); yypushback(1); }
      {ABBREV}           { }
      .             {return send(MMTParserTypes.INVALID); }
}


<URI> {
    {TOKEN}         { endstate(URI); return send(MMTParserTypes.URI); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
      .             {endstate(URI); return send(MMTParserTypes.INVALID); }
}

<NAME> {
    {NAMETOKEN}     { endstate(NAME); return send(MMTParserTypes.NAME); }
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
      .             {endstate(NAME); return send(MMTParserTypes.INVALID); }
}
<PSEUDONAME> {
    {WHITE_SPACE}   { return MMTParserTypes.WHITESPACE; }
    {TOKEN}         { endstate(PSEUDONAME); return send(MMTParserTypes.PSEUDONAME); }
      {ABBREV}           { }
      .             {endstate(PSEUDONAME); return send(MMTParserTypes.INVALID); }
}

<DERIVEDDECL> {
    "="               {endstate(DERIVEDDECL); beginstate(MODULE); return send(MMTParserTypes.DERIVEDBODY); }
    {DECL_delim}      {endstate(DERIVEDDECL); yypushback(1); return send(MMTParserTypes.DERIVEDSHORT); }
    {TMTOKEN}         { return send(MMTParserTypes.DERIVED); }
    {OBJ_delim}       { return send(MMTParserTypes.DERIVED); }
    {WHITE_SPACE}     { return MMTParserTypes.WHITESPACE; }
      {ABBREV}           { }
    .                 { return send(MMTParserTypes.INVALID); }
}
*/

/*

<YYINITIAL> {
    {COMMENT}|{CONTENTCOMMENT}  {beginstate(COMMENT); }
    "namespace"       { beginstate(URI); return MMTParserTypes.NAMESPACE_KEY ;}
    "import"          { beginstate(IMPORT); return MMTParserTypes.IMPORT_KEY; }
    // {TOKEN}           { return MMTParserTypes.NAME; }
    "theory"          { beginstate(THEORYHEADER); beginstate(NAME); return MMTParserTypes.THEORY_KEY ; }
    // "implicit"        { return MMTParserTypes.IMPLICIT_KEY; }

    {MOD_delim}     { return MMTParserTypes.MD; }
    {DECL_delim}    { return MMTParserTypes.DD; }
    {OBJ_delim}     { return MMTParserTypes.OD; }
    {WHITE_SPACE}   { }
}

<THEORYBODY> {
    {MOD_delim}     { endstate(THEORYBODY); return MMTParserTypes.MD; }
    {WHITE_SPACE}   { }
    .               {beginstate(DECL); return MMTParserTypes.DECL; }
}

<DECL> {
    {DECL_delim}    { endstate(DECL); return MMTParserTypes.DD; }
    {MOD_delim}     { endstate(DECL); return MMTParserTypes.MD; }
    {WHITE_SPACE}   { }
    {TOKEN}         { }
    {OBJ_delim}     { }
}

<THEORYHEADER> {
    ":"             { beginstate(URI); return MMTParserTypes.TYPE_KEY; }
    ">"             { beginstate(TERMLIST); return MMTParserTypes.PARAM_KEY; }
    "abbrev"        { beginstate(TERM); return MMTParserTypes.ABBREV_KEY; }
    "="             { endstate(THEORYHEADER); beginstate(THEORYBODY); return MMTParserTypes.EQ_KEY; }
    {MOD_delim}     { endstate(THEORYHEADER); return MMTParserTypes.MD; }
    {WHITE_SPACE}   { }
}

<TERM> {
    {MOD_delim}      { endstate(TERM); return MMTParserTypes.MD; }
    {DECL_delim}     { endstate(TERM); return MMTParserTypes.DD; }
    {OBJ_delim}      { endstate(TERM); return MMTParserTypes.OD; }
}

<TERMLIST> {
    {OBJ_delim}     { endstate(TERMLIST); return MMTParserTypes.OD; }
    {MOD_delim}     { endstate(TERMLIST); return MMTParserTypes.MD; }
    {DECL_delim}    { endstate(TERMLIST); return MMTParserTypes.DD; }
    {WHITE_SPACE}   { }
    ","             { return MMTParserTypes.TERM; }
    {TOKEN}         { return MMTParserTypes.TERM; }
}

<IMPORT> {
    {TOKEN}         { beginstate(URI); return MMTParserTypes.NAME; }
    {MOD_delim}     { endstate(IMPORT); return MMTParserTypes.MD; }
    {WHITE_SPACE}   { }
}


<COMMENT> {
    {NON_DELIM}*    { endstate(COMMENT); return MMTParserTypes.COMMENT; }
}

<NAME> {
    {TOKEN}         { endstate(NAME); return MMTParserTypes.NAME; }
    {WHITE_SPACE}   { }
}

<URI> {
    {WHITE_SPACE}   { }
    {TOKEN}         { endstate(URI); return MMTParserTypes.URI; }
}

*/