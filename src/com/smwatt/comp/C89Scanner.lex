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
%cupsym CTokenType
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

    // buffer to store string and character literals as they are being
    // built
    StringBuffer buff = new StringBuffer();
    
    // used for string/character literals
    int next_token_type;
  
    /**
     * Add in an escaped character into a character literal or string
     * literal.
     */
    private boolean addEscapedCharToBuff(String text) {
        switch(text.charAt(1)) {
            case 'n': buff.append('\n'); break;
            case 't': buff.append('\t'); break;
            case 'v': buff.append((char) 11); break;
            case 'b': buff.append('\b'); break;
            case 'r': buff.append('\r'); break;
            case 'f': buff.append('\f'); break;
            case 'a': buff.append((char) 7); break;
            case '\\': buff.append('\\'); break;
            case '?': buff.append('?'); break;
            case '\'': buff.append('\''); break;
            case '"': buff.append('"'); break;
            
            // hex
            case 'x':
                buff.append((char) Integer.parseInt(text.substring(2), 16));
                break;
            
            // octal
            case '0': case '1': case '2': case '3': 
            case '4': case '5': case '6': case '7':
                buff.append((char) Integer.parseInt(text.substring(1), 8));
                break;
            
            // error
            default:
                next_token_type = CTokenType.error; /* TODO */
                buff.append('\0');
                /* TODO */ 
                return false;
        }
        return true;
    }
%}

D  =		[0-9]
L  =		[a-zA-Z_]
H  =		[a-fA-F0-9]
E  =		[Ee][+-]?{D}+
FType =		(f|F|l|L)
IType =		(u|U|l|L)*

/* K&R p. 193, A2.5.2 - Character Constants
 * note: - multi-character constants are implementation-defined.
 *       - hex characters have no limits on their hex digits.
 *       - behavior is undefined for undefined escaped character constants.     
 */
OctalChar       = "\\" ([0-7]{1,3})
HexChar         = "\\x" [0-9a-fA-F]+
SpecialChar     = "\\" .
EscapedChar     = {OctalChar} | {HexChar} | {SpecialChar}

%state STRING_LITERAL
%state CHAR_LITERAL

%%

<YYINITIAL> {
    
    "/*"~"*/"					{ /* Ignore */ }
    [ \t\v\n\r\f]				{ /* Ignore */ }
    
    "auto"						{ return specifier(CTokenType.AUTO); } /* BUG fixed */
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

                               
//\"(\\.|[^\\\"])*\"			{ return string   (CTokenType.STRING, yytext()); }
//'(\\.|[^\\'])+'				{ return string   (CTokenType.CHARACTER_CONSTANT, yytext()); }

    /* string and character literals. */
    \"      { buff.setLength(0);
              next_token_type = CTokenType.STRING; 
              yybegin(STRING_LITERAL); }
    
    \'      { buff.setLength(0);
              next_token_type = CTokenType.CHARACTER_CONSTANT; 
              yybegin(CHAR_LITERAL); }
}
          
/* string and character literals */

<STRING_LITERAL> {
    \"              { yybegin(YYINITIAL); 
                      return string(next_token_type, buff.toString()); }
    [^\n\r\"\\]+    { buff.append( yytext() ); }
    {EscapedChar}   { addEscapedCharToBuff(yytext()); }
    
    .               { next_token_type = CTokenType.ERROR; /* TODO */ }
}
    
<CHAR_LITERAL> {
    \'              { yybegin(YYINITIAL); 
                      return string(next_token_type, buff.toString()); }
    [^\n\r\'\\]+    { buff.append( yytext() ); }
    {EscapedChar}   { addEscapedCharToBuff(yytext()); }
    
    .               { next_token_type = CTokenType.ERROR; /* TODO */ }
}
    
<YYINITIAL> {

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
    "?"							{ return operator (CTokenType.QMARK); } /* BUG fixed */
    "#"							{ return token    (CTokenType.POUND); }
    "##"						{ return token    (CTokenType.POUND_POUND); }
    "~"							{ return operator (CTokenType.TILDE); }
}

<<EOF>>                     { return token    (CTokenType.EOF); }
.                           { return string   (CTokenType.ERROR, yytext()); }

