//////////////////////////////////////////////////////////////////////////////
//
// CToken
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

public class CToken {
	int	_type;
	String _fname = null;
	int _line = -1;
	int _col  = -1;
	String _str = null;
	
	CToken(int type) { 
		_type = type; 
	}
	CToken(int type, String str) {
		_type = type;
		_str = str;
	}
	CToken(int type,  String fname, int line, int col) {
		_type = type; _fname = fname; _line = line; _col = col; 
	}
	CToken(int type,  String fname, int line, int col, String str) {
		_type = type; _fname = fname; _line = line; _col = col; _str = str; 
	}
	
	public boolean isTypedef() { return _type == CTokenType.TYPEDEF; }
	
	public boolean isStatic() { return _type == CTokenType.STATIC; }
	
	public String toString() {
		if (_str != null) return _str;
		
		switch (_type) {
		case CTokenType.AUTO:	return "auto";
		case CTokenType.BREAK:	return "break";
		case CTokenType.CASE:	return "case";
		case CTokenType.CHAR:	return "char";
		case CTokenType.CONST:	return "const";
		case CTokenType.CONTINUE:	return "continue";
		case CTokenType.DEFAULT:	return "default";
		case CTokenType.DO:	return "do";
		case CTokenType.DOUBLE:	return "double";
		case CTokenType.ELSE:	return "else";
		case CTokenType.ENUM:	return "enum";
		case CTokenType.EXTERN:	return "extern";
		case CTokenType.FLOAT:	return "float";
		case CTokenType.FOR:	return "for";
		case CTokenType.GOTO:	return "goto";
		case CTokenType.IF:	return "if";
		case CTokenType.INT:	return "int";
		case CTokenType.LONG:	return "long";
		case CTokenType.REGISTER:	return "register";
		case CTokenType.RETURN:	return "return";
		case CTokenType.SHORT:	return "short";
		case CTokenType.SIGNED:	return "signed";
		case CTokenType.SIZEOF:	return "sizeof";
		case CTokenType.STATIC:	return "static";
		case CTokenType.STRUCT:	return "struct";
		case CTokenType.SWITCH:	return "switch";
		case CTokenType.TYPEDEF:	return "typedef";
		case CTokenType.UNION:	return "union";
		case CTokenType.UNSIGNED:	return "unsigned";
		case CTokenType.VOID:	return "void";
		case CTokenType.VOLATILE:	return "volatile";
		case CTokenType.WHILE:	return "while";
		
		case CTokenType.O_PAREN:	return "(";
		case CTokenType.C_PAREN:	return ")";
		
		case CTokenType.O_BRACK:	return "[";
		case CTokenType.C_BRACK:	return "]";
		
		case CTokenType.O_BRACE:	return "{";
		case CTokenType.C_BRACE:	return "}";
		
		case CTokenType.NOT:	return "!";
		case CTokenType.NOT_EQUALS:	return "!=";
		
		case CTokenType.MOD:	return "%";
		case CTokenType.MOD_ASSIGN:	return "%=";
		
		case CTokenType.XOR:	return "^";
		case CTokenType.XOR_ASSIGN:	return "^=";
		
		case CTokenType.AMP:	return "&";
		case CTokenType.AMP_ASSIGN:	return "&=";
		case CTokenType.AMP_AMP:	return "&&";
		
		case CTokenType.VBAR:	return "|";
		case CTokenType.VBAR_ASSIGN:	return "|=";
		case CTokenType.VBAR_VBAR:	return "||";
		
		case CTokenType.STAR:	return "*";
		case CTokenType.STAR_ASSIGN:	return "*=";
		
		case CTokenType.SLASH:	return "/";
		case CTokenType.SLASH_ASSIGN:	return "/=";
		
		case CTokenType.PLUS:	return "+";
		case CTokenType.PLUS_ASSIGN:	return "+=";
		case CTokenType.PLUS_PLUS:	return "++";
		
		case CTokenType.MINUS:	return "-";
		case CTokenType.MINUS_ASSIGN:	return "-=";
		case CTokenType.MINUS_MINUS:	return "--";
		
		case CTokenType.LT:	return "<";
		case CTokenType.LT_EQ:	return "<=";
		case CTokenType.LSH:	return "<<";
		case CTokenType.LSH_ASSIGN:	return "<<=";
		
		case CTokenType.GT:	return ">";
		case CTokenType.GT_EQ:	return ">=";
		case CTokenType.RSH:	return ">>";
		case CTokenType.RSH_ASSIGN:	return ">>=";
		
		case CTokenType.POINTS_TO:	return "->";
		case CTokenType.DOT_DOT_DOT:	return "...";
		case CTokenType.ASSIGN:	return "=";
		case CTokenType.EQUALS:	return "==";
		
		case CTokenType.COMMA:	return ",";
		case CTokenType.DOT:	return ".";
		case CTokenType.SEMI:	return ";";
		case CTokenType.COLON:	return ":";
		case CTokenType.QMARK:	return "?";
		case CTokenType.POUND:	return "#";
		case CTokenType.POUND_POUND:	return "##";
		case CTokenType.TILDE:	return "~";
		
		case CTokenType.TYPEDEF_NAME:	return "<:TYPEDEF_NAME:>";
		case CTokenType.IDENTIFIER:	return "<:IDENTIFIER:>";
		case CTokenType.STRING:	return "<:STRING:>";
		case CTokenType.CHARACTER_CONSTANT:	return "<:CHARACTER_CONSTANT:>";
		case CTokenType.INTEGER_CONSTANT:	return "<:INTEGER_CONSTANT:>";
		case CTokenType.FLOATING_CONSTANT:	return "<:FLOATING_CONSTANT:>";
		
		
		case CTokenType.ERROR:	return "<:ERROR:>";
		case CTokenType.EOF:	return "<:EOF:>";
		default:          	return "<:UNHANDLED:>";
		}
	}
}
