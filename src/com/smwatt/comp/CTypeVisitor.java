//////////////////////////////////////////////////////////////////////////////
//
// CTypeVisitor -- Generic traversal of CType objects
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import static com.smwatt.comp.CType.*;

public interface CTypeVisitor {
	public void visit(CType            ct);
	
	public void visit(CTypeInvalid     ct);
	public void visit(CTypeVoid        ct);
	public void visit(CTypeInt         ct);
	public void visit(CTypeChar        ct);
	public void visit(CTypeFloat       ct);
	public void visit(CTypeDouble      ct);
	public void visit(CTypeFunction    ct);
	public void visit(CTypeArray       ct);
	public void visit(CTypePointer     ct);
	public void visit(CTypeEnum        ct);
	public void visit(CTypeStruct      ct);
	public void visit(CTypeUnion       ct);
	
	/*
	public void visit(CTypeNamedTypedef ct);
	public void visit(CTypeNamedStruct ct);
	public void visit(CTypeNamedUnion  ct);
	public void visit(CTypeNamedEnum   ct);
    */
	
	public void visit(CTypeConstExpr   ob);
	public void visit(CTypeEnumerator  ob);
	public void visit(CTypeField       ob);
}
