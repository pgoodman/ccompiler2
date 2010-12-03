//////////////////////////////////////////////////////////////////////////////
//
// CTypePrinter
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.io.PrintStream;
import static com.smwatt.comp.CType.*;

public class CTypePrinter implements CTypeVisitor {
		
	CCodePrinter    _ccp;
	XMLOutputStream _xo;
	
	public CTypePrinter(PrintStream out) { 
		TabbingStream _tout = new TabbingStream(out);
		_tout.setTabSize(4);
		_ccp = new CCodePrinter(_tout);
		_xo  = new XMLOutputStream(_tout);
	}
	
	public void print(CType ct) { visit(ct); }
	
	/////////////////////////////////////////////////////////////////////
	private void openElement(String tag, CType ct) {
		_xo.openElement(tag);
		if (ct != null) {
			_xo.putTrueAttribute("const",    ct._isConst);
			_xo.putTrueAttribute("volatile", ct._isVolatile);
		}
	}
	private void openElement(String tag, CType ct, C.CodeId optId) {
		openElement(tag, ct);
		if (optId != null)   
			_xo.putAttribute("id", optId._s);
	}
	/////////////////////////////////////////////////////////////////////
	
	public void visit(CType ct) {
		ct.acceptVisitor(this);
	}
	
	public void visit(CTypeInvalid ct) {
		openElement("invalid", ct);
		_xo.endElement();
	}
	public void visit(CTypeVoid ct) {
		openElement("void", ct);
		_xo.endElement();
	}
	public void visit(CTypeInt ct) {
		openElement("int", ct);
		_xo.putNonZeroAttribute("signed",  ct._signed);
		_xo.putNonZeroAttribute("length",  ct._length);
		_xo.endElement();
	}
	public void visit(CTypeChar ct) {
		openElement("char", ct);
		_xo.putNonZeroAttribute("signed",  ct._signed);
		_xo.putNonZeroAttribute("length",  ct._length);
		_xo.endElement();
	}
	public void visit(CTypeFloat ct) { 
		openElement("float", ct);
		_xo.putNonZeroAttribute("length", ct._length);
		_xo.endElement();
	}
	public void visit(CTypeDouble ct) {
		openElement("double", ct);
		_xo.putNonZeroAttribute("length", ct._length);
		_xo.endElement();
	}
	public void visit(CTypeFunction ct) {
		openElement("fun", ct);
		_xo.putTrueAttribute("moreArgs", ct._moreArgs);
		visit(ct._retType);
		for (CType a: ct._argTypes) visit(a);
		_xo.endElement();
	}
	public void visit(CTypeArray ct) {
		openElement("arr", ct);
		visit(ct._pointeeType);
		if (ct._optSize != null) visit(ct._optSize);
		_xo.endElement();
	}
	public void visit(CTypePointer ct) {
		openElement("ptr", ct);
		visit(ct._pointeeType);
		_xo.endElement();
	}
	public void visit(CTypeEnum ct) {
	    openElement("enum", ct, ct._optId);
		for (CTypeEnumerator etor: ct._enumerators)  visit(etor);
		_xo.endElement();
	}
	public void visit(CTypeStruct ct) {
	    openElement("struct", ct, ct._optId);
		for (CTypeField field: ct._fields)  visit(field);
		_xo.endElement();
	}
	public void visit(CTypeUnion ct) {
	    openElement("union", ct, ct._optId);
		for (CTypeField branch: ct._branches)  visit(branch);
		_xo.endElement();
	}
	public void visit(CTypeNamedTypedef ct) {
	    openElement("tdname", ct, ct._id);
	    _xo.putAttribute("haveDef", ct._optReally != null);
		_xo.endElement();
	}
	public void visit(CTypeNamedStruct ct) {
	    openElement("sname", ct, ct._id);
	    _xo.putAttribute("haveDef", ct._optReally != null);
		_xo.endElement();
	}
	public void visit(CTypeNamedUnion  ct) {
	    openElement("uname", ct, ct._id);
	    _xo.putAttribute("haveDef", ct._optReally != null);
		_xo.endElement();
	}
	public void visit(CTypeNamedEnum   ct) {
	    openElement("ename", ct, ct._id);
	    _xo.putAttribute("haveDef", ct._optReally != null);
		_xo.endElement();
	}
	public void visit(CTypeConstExpr ob) {
		_xo.openElement("const");
		_xo.beginContent();
		_ccp.visit(ob._expr);
		_xo.endElement();
	}
	public void visit(CTypeEnumerator ob) {
		openElement("enumerator", null, ob._id);
		putOptConstExpr("value", ob._optValue);
		_xo.endElement();
	}
	public void visit(CTypeField ob) {
		openElement("field", null, ob._id);
		visit(ob._type);
		putOptConstExpr("width", ob._optWidth);
		_xo.endElement();
	}
	public void putOptConstExpr(String tag, CTypeConstExpr ct) {
		if (ct != null) {
			_xo.openElement(tag);
			visit(ct);
			_xo.endElement();
		}
	}
}
