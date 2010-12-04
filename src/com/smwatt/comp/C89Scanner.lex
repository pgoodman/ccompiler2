//////////////////////////////////////////////////////////////////////////////
//
// C89Scanner.lex
//
//////////////////////////////////////////////////////////////////////////////
// Copyright 2005 Stephen M. Watt

package com.smwatt.comp;
import java_cup.runtime.*;

%%

%class      C89Scanner
%implements CScanner

%unicode
%cup
%line
%column

%{

  //-- The caller can tell us the filename.
  private String _fname = "-- Unknown File --";
  public void setFilename(String s) {
    _fname = s;
  }
  public String getFilename() {
    return _fname;
  }
  //-- The parser sets a "CTypedefOracle"
  private CTypedefOracle _tdoracle = null;
  
  public void setTypedefOracle(CTypedefOracle tdoracle) { 
  	_tdoracle = tdoracle; 
  }
  public CTypedefOracle getTypedefOracle() {
  	return _tdoracle;
  }
  private boolean isTypedefName(String s) {
  	return _tdoracle != null && _tdoracle.isTypedefName(s);
  }
  
  //-- These are used to make the symbols for the parser.
  private Symbol token(int type) {
    return new Symbol(type, yyline, yycolumn, new CToken(type, _fname, yyline, yycolumn));
  }
  private Symbol string(int type, String value) {
    return new Symbol(type, yyline, yycolumn, new CToken(type, _fname, yyline, yycolumn, value));
  }
  private Symbol specifier(int type) {
    return new Symbol(type, yyline, yycolumn, new CTokenSpecifier(type, _fname, yyline, yycolumn));
  }
  private Symbol operator(int type) {
    return new Symbol(type, yyline, yycolumn, new CTokenOperator(type, _fname, yyline, yycolumn));
  }

%}

D  =		[0-9]
L  =		[a-zA-Z_]
H  =		[a-fA-F0-9]
E  =		[Ee][+-]?{D}+
FType =		(f|F|l|L)
IType =		(u|U|l|L)*


%%


"/*"~"*/"					{ /* Ignore */ }
[ \t\v\n\r\f]				{ /* Ignore */ }

"auto"						{ return specifier(CTokenType.AUTO); }
"break"						{ return token    (CTokenType.BREAK); }
"case"						{ return token    (CTokenType.CASE); }
"char"						{ return specifier(CTokenType.CHAR); }
"const"						{ return specifier(CTokenType.CONST); }
"continue"					{ return token    (CTokenType.CONTINUE); }
"default"					{ return token    (CTokenType.DEFAULT); }
"do"						{ return token    (CTokenType.DO); }
"double"					{ return specifier(CTokenType.DOUBLE); }
"else"						{ return token    (CTokenType.ELSE); }
"enum"						{ return token    (CTokenType.ENUM); }
"extern"					{ return specifier(CTokenType.EXTERN); }
"float"						{ return specifier(CTokenType.FLOAT); }
"for"						{ return token    (CTokenType.FOR); }
"goto"						{ return token    (CTokenType.GOTO); }
"if"						{ return token    (CTokenType.IF); }
"int"						{ return specifier(CTokenType.INT); }
"long"						{ return specifier(CTokenType.LONG); }
"register"					{ return specifier(CTokenType.REGISTER); }
"return"					{ return token    (CTokenType.RETURN); }
"short"						{ return specifier(CTokenType.SHORT); }
"signed"					{ return specifier(CTokenType.SIGNED); }
"sizeof"					{ return token    (CTokenType.SIZEOF); }
"static"					{ return specifier(CTokenType.STATIC); }
"struct"					{ return token    (CTokenType.STRUCT); }
"switch"					{ return token    (CTokenType.SWITCH); }
"typedef"					{ return specifier(CTokenType.TYPEDEF); }
"union"						{ return token    (CTokenType.UNION); }
"unsigned"					{ return specifier(CTokenType.UNSIGNED); }
"void"						{ return specifier(CTokenType.VOID); }
"volatile"					{ return specifier(CTokenType.VOLATILE); }
"while"						{ return token    (CTokenType.WHILE); }

{L}({L}|{D})*				{ if (isTypedefName(yytext()))
								return string (CTokenType.TYPEDEF_NAME, yytext());
							  else
								return string (CTokenType.IDENTIFIER, yytext()); } 
                               
\"(\\.|[^\\\"])*\"			{ return string   (CTokenType.STRING, yytext()); }

'(\\.|[^\\'])+'				{ return string   (CTokenType.CHARACTER_CONSTANT, yytext()); }

0[xX]{H}+{IType}?			{ return string   (CTokenType.INTEGER_CONSTANT, yytext()); }
0{D}+{IType}?				{ return string   (CTokenType.INTEGER_CONSTANT, yytext()); }
{D}+{IType}?				{ return string   (CTokenType.INTEGER_CONSTANT, yytext()); }

{D}+{E}{FType}?				{ return string   (CTokenType.FLOATING_CONSTANT, yytext()); }
{D}*"."{D}+({E})?{FType}?	{ return string   (CTokenType.FLOATING_CONSTANT, yytext()); }
{D}+"."{D}*({E})?{FType}?	{ return string   (CTokenType.FLOATING_CONSTANT, yytext()); }

"("							{ return token    (CTokenType.O_PAREN); }
")"							{ return token    (CTokenType.C_PAREN); }

"["							{ return token    (CTokenType.O_BRACK); }
"]"							{ return token    (CTokenType.C_BRACK); }

"{"							{ return token    (CTokenType.O_BRACE); }
"}"							{ return token    (CTokenType.C_BRACE); }

"!"							{ return operator (CTokenType.NOT); }
"!="						{ return operator (CTokenType.NOT_EQUALS); }

"%"							{ return operator (CTokenType.MOD); }
"%="						{ return operator (CTokenType.MOD_ASSIGN); }

"^"							{ return operator (CTokenType.XOR); }
"^="						{ return operator (CTokenType.XOR_ASSIGN); }

"&"							{ return operator (CTokenType.AMP); }
"&="						{ return operator (CTokenType.AMP_ASSIGN); }
"&&"						{ return operator (CTokenType.AMP_AMP); }

"|"							{ return operator (CTokenType.VBAR); }
"|="						{ return operator (CTokenType.VBAR_ASSIGN); }
"||"						{ return operator (CTokenType.VBAR_VBAR); }

"*"							{ return operator (CTokenType.STAR); }
"*="						{ return operator (CTokenType.STAR_ASSIGN); }

"/"							{ return operator (CTokenType.SLASH); }
"/="						{ return operator (CTokenType.SLASH_ASSIGN); }

"+"							{ return operator (CTokenType.PLUS); }
"+="						{ return operator (CTokenType.PLUS_ASSIGN); }
"++"						{ return operator (CTokenType.PLUS_PLUS); }

"-"							{ return operator (CTokenType.MINUS); }
"-="						{ return operator (CTokenType.MINUS_ASSIGN); }
"--"						{ return operator (CTokenType.MINUS_MINUS); }

"<"							{ return operator (CTokenType.LT); }
"<="						{ return operator (CTokenType.LT_EQ); }
"<<"						{ return operator (CTokenType.LSH); }
"<<="						{ return operator (CTokenType.LSH_ASSIGN); }

">"							{ return operator (CTokenType.GT); }
">="						{ return operator (CTokenType.GT_EQ); }
">>"						{ return operator (CTokenType.RSH); }
">>="						{ return operator (CTokenType.RSH_ASSIGN); }

"->"						{ return operator (CTokenType.POINTS_TO); }
"..."						{ return token    (CTokenType.DOT_DOT_DOT); }
"="							{ return operator (CTokenType.ASSIGN); }
"=="						{ return operator (CTokenType.EQUALS); }

","							{ return operator (CTokenType.COMMA); }
"."							{ return operator (CTokenType.DOT); }
";"							{ return token    (CTokenType.SEMI); }
":"							{ return token    (CTokenType.COLON); }
"?"							{ return token    (CTokenType.QMARK); }
"#"							{ return token    (CTokenType.POUND); }
"##"						{ return token    (CTokenType.POUND_POUND); }
"~"							{ return operator (CTokenType.TILDE); }


.							{ return string   (CTokenType.ERROR, yytext()); }
<<EOF>>                 	{ return token    (CTokenType.EOF); }
