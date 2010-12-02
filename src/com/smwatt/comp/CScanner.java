//////////////////////////////////////////////////////////////////////////////
//
// CScanner -- A scanner interface for typedef information.
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

public interface CScanner {
	void setTypedefOracle(CTypedefOracle o);
	CTypedefOracle getTypedefOracle();
}
