//////////////////////////////////////////////////////////////////////////////
//
// C.CodePrinter
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.io.PrintStream;
import java.util.List;

import com.smwatt.comp.C.CodeDeclaratorParen;

public class CCodePrinter implements C.CodeVisitor {
	TabbingStream	_tout;
	boolean			_isDebugMode = false;
	
	
	public CCodePrinter(TabbingStream out) {
		_tout = out;
	}
	public CCodePrinter(PrintStream out) {
		_tout = new TabbingStream(out);
	}
	public void print(C.Code cc) { visit(cc); }

	//-- Helper methods --------------------------------------------------
	private void printSep(List<? extends C.Code> lcc, String sep) {
		int i = 0;
		for (C.Code cc: lcc) {
			if (i++ > 0) _tout.print(sep);
			visit(cc);
		}
	}
	private void printIndented(List<? extends C.Code> lcc, String sep, String fin) {
		_tout.incIndent();
		int i = 0, sz = lcc.size();
		for (C.Code cc: lcc) {
			_tout.printlnIndent();
			visit(cc);
			_tout.print(++i < sz ? sep : fin);
		}
		_tout.decIndent();
		_tout.printlnIndent();
	}
	private void printStatIndented(C.CodeStat cc) {
		if (cc instanceof C.CodeStatCompound) 
			visit(cc);
		else {
			_tout.incIndent();
			_tout.printlnIndent();
			visit(cc);
			_tout.decIndent();
		}
	}
	private void printStructUnionEnum(
		C.CodeSpecifierStructUnionEnum cc, 
		String start, String sep, String fin) 
	{
		_tout.print(start);
		_tout.print(" ");
		if (cc._optId != null) 
			visit(cc._optId);
		if (cc._optId != null && cc._optParts != null)
			_tout.print(" ");
		if (cc._optParts != null) {
			_tout.print("{");
			printIndented(cc._optParts, sep, fin);
			_tout.print("}");
		}
	}
	
	///////////////////////////////////////////////////////////////////////
	//
	// Top Level
	//
	///////////////////////////////////////////////////////////////////////

	public void setDebugMode(boolean isDebugMode) {
		_isDebugMode = isDebugMode;
	}
	public void visit(C.Code cc) {
	    if(null != cc) {
	        cc.acceptVisitor(this);
	    }
	}
	public void visit(C.CodeUnit cc) {
		//if (_isDebugMode) cc._scope.display(_tout); // TODO
		int i = 0;
		for (C.Code c: cc._l) {
			if (i++ > 0) _tout.println();  // Blank lines between.
			visit(c);
			if (c instanceof C.CodeDeclaration) _tout.print(";");
			_tout.println();
		}
	}	
	public void visit(C.CodeFunction cc) {
		printSep(cc._lspec, " ");
		if (cc._lspec.size() > 0) _tout.print(" ");
		visit(cc._head);
		printIndented(cc._ldecl, ";", ";");
		visit(cc._body);
	}
	public void visit(C.CodeDeclaration cc) {
		printSep(cc._lspec, " ");
		if (cc._lspec.size() > 0 && cc._ldtor.size() > 0) _tout.print(" ");
		printSep(cc._ldtor, ", ");
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Basic Items
	//
	///////////////////////////////////////////////////////////////////////

	public void visit(C.CodeId cc) {
		_tout.print(cc._s);
		// TODO
		/*
		if (_isDebugMode) {
			if (cc._optSymAnnotation != null) {
				if (cc._optSymAnnotation._scope == null)
					_tout.print("<!>");
				else
					_tout.print("<"+cc._optSymAnnotation._scope._depth+">");
			}
		}*/
	}
	public void visit(C.CodeTypeName cc) {
		printSep(cc._lspec, " ");
		_tout.print(" ");
		visit(cc._dtor);
	}
	public void visit(C.CodeString cc) {
		_tout.print(cc._s); 
	}
	public void visit(C.CodeCharacterConstant cc) { 
		_tout.print(cc._s); 
	}
	public void visit(C.CodeIntegerConstant cc) { 
		_tout.print(cc._s); 
	}
	public void visit(C.CodeFloatingConstant cc) { 
		_tout.print(cc._s); 
	}
	public void visit(C.CodeEnumerationConstant cc) { 
		_tout.print(cc._s); 
	}
	public void visit(C.CodeDotDotDot cc) { 
		_tout.print("..."); 
	}
	

	///////////////////////////////////////////////////////////////////////
	//
	// Specifiers
	//
	///////////////////////////////////////////////////////////////////////

	// * extends C.CodeSpecifier {
	public void visit(C.CodeSpecifierStorage cc) {
		_tout.print(cc._spec.toString());
	}
	public void visit(C.CodeSpecifierQualifier cc) {
		_tout.print(cc._spec.toString());
	}
	public void visit(C.CodeSpecifierType cc) {
		_tout.print(cc._spec.toString());
	}
	public void visit(C.CodeSpecifierTypedefName cc) { 
		visit(cc._id); 
	}
	public void visit(C.CodeSpecifierStruct cc) {
		printStructUnionEnum(cc, "struct", ";", ";");
	}
	public void visit(C.CodeSpecifierUnion cc) {
		printStructUnionEnum(cc, "union", ";", ";");
	}
	public void visit(C.CodeSpecifierEnum cc) {
		printStructUnionEnum(cc, "enum", ",", "");
	}
	public void visit(C.CodeEnumerator cc) {
		visit(cc._id);
		if (cc._optValue != null) {
			_tout.print(" = ");
			visit(cc._optValue);
		}
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Declarators
	//
	///////////////////////////////////////////////////////////////////////

	public void visit(C.CodeDeclaratorArray cc) {
		if (cc._optAr != null) visit(cc._optAr);
		_tout.print("[");
		if (cc._optIndex != null) visit(cc._optIndex);
		_tout.print("]");
	}
	public void visit(C.CodeDeclaratorFunction cc) {
		if (cc._optFn != null) visit(cc._optFn);
		_tout.print("(");
		printSep(cc._argl, ", ");
		_tout.print(")"); 
	}
	public void visit(C.CodeDeclaratorInit cc) {
		visit(cc._dtor);
		_tout.print(" = ");
		visit(cc._initializer);
	}
	public void visit(C.CodeDeclaratorPointer cc) { 
		visit(cc._star);
		if (cc._optPointee != null) visit(cc._optPointee);
	}
	public void visit(C.CodeDeclaratorWidth cc) {
		visit(cc._dtor);
		_tout.print(": ");
		visit(cc._width);
	}
	public void visit(C.CodeDeclaratorId cc) {
		visit(cc._id);
	}
	public void visit(C.CodePointerStar cc) { 
		_tout.print("*");
		printSep(cc._lspec, " ");
		if (cc._lspec.size() > 0 && cc._optStar != null) _tout.print(" ");
		if (cc._optStar != null) visit(cc._optStar);
	}
	public void visit(C.CodeInitializerValue cc) {
		visit(cc._value);
	}
	public void visit(C.CodeInitializerList cc) {
		_tout.print("{");
		printSep(cc._list, ", ");
		_tout.print("}");
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Statements
	//
	///////////////////////////////////////////////////////////////////////

	public void visit(C.CodeStatBreak cc) {
		_tout.print("break;");
	}
	public void visit(C.CodeStatCase cc) {
		_tout.print("case ");
		visit(cc._value);
		_tout.print(":");
		if (cc._stat instanceof C.CodeStatCase    || 
			cc._stat instanceof C.CodeStatDefault
		)
			_tout.printlnIndentRelative(-1);
		else
			_tout.tabTo(_tout.getCurrentIndent());
		visit(cc._stat);
	}
	public void visit(C.CodeStatCompound cc) {
		_tout.print("{");
		//if (_isDebugMode) // TODO
		//	cc._scope.display(_tout);
		if (cc._ldecl.size() > 0)
			printIndented(cc._ldecl, ";", ";");
		_tout.incIndent();
		for (C.CodeStat s : cc._lstat) {
			if (s instanceof C.CodeStatLabeled || 
				s instanceof C.CodeStatCase    || 
				s instanceof C.CodeStatDefault
			) 
				_tout.printlnIndentRelative(-1);
			else
				_tout.printlnIndent();
			visit(s);
		}
		_tout.decIndent();
		_tout.printlnIndent();
		_tout.print("}");
	}
	public void visit(C.CodeStatContinue cc) {
		_tout.print("continue;");
	}
	public void visit(C.CodeStatDefault cc) {
		_tout.print("default:");
		_tout.tabTo(_tout.getCurrentIndent());
		visit(cc._stat);
	}
	public void visit(C.CodeStatDo cc) {
		_tout.print("do ");
		printStatIndented(cc._stat);
		_tout.println();
		_tout.print("while (");
		visit(cc._test);
		_tout.print(");");
	}
	public void visit(C.CodeStatExpression cc) {
		if (cc._optExpr != null) visit(cc._optExpr);
		_tout.print(";");
	}
	public void visit(C.CodeStatFor cc) {
		_tout.print("for (");
		if (cc._optInit != null) visit(cc._optInit);
		_tout.print("; ");
		if (cc._test != null) visit(cc._test);
		_tout.print("; ");
		if (cc._optStep != null) visit(cc._optStep);
		_tout.print(") ");
		printStatIndented(cc._stat);
	} 
	public void visit(C.CodeStatGoto cc) {
		_tout.print("goto ");
		visit(cc._label);
		_tout.print(";");
	}
	public void visit(C.CodeStatIf cc) {
		_tout.print("if (");
		visit(cc._test);
		_tout.print(") ");
		printStatIndented(cc._stat);
		if (cc._optElstat != null) {
			_tout.printlnIndent();
			_tout.print("else ");
			printStatIndented(cc._optElstat);
		}
	}
	public void visit(C.CodeStatLabeled cc) {
		visit(cc._label);
		_tout.print(":");
		_tout.tabTo(_tout.getCurrentIndent());
		visit(cc._stat);
	}
	public void visit(C.CodeStatReturn cc) {
		if (cc._optExpr == null)
			_tout.print("return;");
		else {
			_tout.print("return ");
			visit(cc._optExpr);
			_tout.print(";");
		}
	}
	public void visit(C.CodeStatSwitch cc) {
		_tout.print("switch (");
		visit(cc._expr);
		_tout.print(") ");
		printStatIndented(cc._stat);
	}
	public void visit(C.CodeStatWhile cc) {
		_tout.print("while (");
		visit(cc._test);
		_tout.print(") ");
		printStatIndented(cc._stat);
	}

	///////////////////////////////////////////////////////////////////////
	//
	// Expressions
	//
	///////////////////////////////////////////////////////////////////////

	public void visit(C.CodeExprAssignment cc) {
		visit(cc._a);
		_tout.print(" ");
		_tout.print(cc._op.toString());
		_tout.print(" ");
		visit(cc._b);
	}
	public void visit(C.CodeExprCast cc) {
		_tout.print("(");
		visit(cc._typename);
		_tout.print(") ");
		visit(cc._expr);
	}
	public void visit(C.CodeExprConditional cc) {
		visit(cc._test);
		_tout.print(" ? ");
		visit(cc._a);
		_tout.print(" : ");
		visit(cc._b);
	}
	public void visit(C.CodeExprInfix cc) {
		visit(cc._a);
		_tout.print(" ");
		_tout.print(cc._op.toString());
		_tout.print(" ");
		visit(cc._b);
	}
	public void visit(C.CodeExprParen cc) {
		_tout.print("(");
		visit(cc._expr);
		_tout.print(")");
	}
	public void visit(C.CodeExprPostfix cc) {
		visit(cc._a);
		_tout.print(cc._op.toString());
	}
	public void visit(C.CodeExprPrefix cc) {
		_tout.print(cc._op.toString());
		visit(cc._a);
	}
	public void visit(C.CodeExprId cc) { 
		visit(cc._id);
	}
	public void visit(C.CodeExprSizeofValue cc) {
		_tout.print("sizeof ");
		visit(cc._expr);
	}
	public void visit(C.CodeExprSizeofType cc) {
		_tout.print("sizeof(");
		visit(cc._tname);
		_tout.print(")");
	}
	public void visit(C.CodeExprCall cc) {
		visit(cc._fun);
		_tout.print("(");
		printSep(cc._argl, ", ");
		_tout.print(")");
	}
	/*
	public void visit(C.CodeExprSubscript cc) {
		visit(cc._arr);
		_tout.print("[");
		visit(cc._idx);
		_tout.print("]");
	}
	*/
	public void visit(C.CodeExprField cc) {
		visit(cc._ob);
		_tout.print(".");
		visit(cc._id);
	}
	public void visit(C.CodeExprPointsTo cc) {
		visit(cc._ob);
		_tout.print("->");
		visit(cc._id);
	}
    public void visit(CodeDeclaratorParen cc) {
        _tout.print("(");
        visit(cc._decl);
        _tout.print(")");
    }
}
