//////////////////////////////////////////////////////////////////////////////
//
// SourcePosition
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

// TODO Should have a compilation history ...

public class SourcePosition implements Comparable<SourcePosition> {
	// TODO file
	int    _line;
	int    _col;
	String _fname;
	
	static SourcePosition DontKnow = 
		new SourcePosition("?!?!? I don't know ?!?!?", -1, -1);
	
	SourcePosition(String fname, int line, int col) { 
		_fname = fname; 
		_line = line; 
		_col = col;
	}
	
	public int compareTo(SourcePosition that) {
		if (_line < that._line) return -1;
		if (_line > that._line) return +1;
		if (_col  < that._col ) return -1;
		if (_col  > that._col ) return +1;
		return 0;
	}
	
	public String toString() {
		return "[\"" + _fname + "\", line " + (_line + 1) + ", column " + _col +"]";
	}
}
