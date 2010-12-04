//////////////////////////////////////////////////////////////////////////////
//
// AbortCompilation
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

public class AbortCompilation extends Exception {
	private static final long serialVersionUID = 1L;
	
	String	_reason = "";
	
	public AbortCompilation(String reason) {
		_reason = reason;
	}

    public AbortCompilation() {
        // TODO Auto-generated constructor stub
    }
}
