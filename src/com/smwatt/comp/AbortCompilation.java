//////////////////////////////////////////////////////////////////////////////
//
// AbortCompilation
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

public class AbortCompilation extends Exception {
	private static final long serialVersionUID = 1L;
	
	String	_reason;
	
	AbortCompilation(String reason) {
		_reason = reason;
	}
}
