//////////////////////////////////////////////////////////////////////////////
//
// CTypedefStack
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.

package com.smwatt.comp;

import java.util.HashSet;


//TODO: For now just add.  Later manage levels.
public class CTypedefStack implements CTypedefOracle {
	
	HashSet<String> _tdnames = new HashSet<String>();
	
	public void pushLevel() {
		//System.out.println("Pushing typedef level\n");
	}
	public void popLevel() {
		//System.out.println("Popping level\n");
	}
	public boolean isTypedefName(String s) {
		return _tdnames.contains(s);
	}
	private String getDeclaratorName(C.CodeDeclarator dtor) {
		C.CodeId id = dtor.getOptId();
		return id == null ? null : id._s;
	}
	public void seeDeclaration(C.CodeDeclaration decl) {
		boolean isTypedef = false;
		
		// TODO Share code with CIDBinder
		for (C.CodeSpecifier s: decl._lspec) {
			if (s instanceof C.CodeSpecifierStorage) {
				C.CodeSpecifierStorage ss = (C.CodeSpecifierStorage) s;
				if (ss.isTypedef()) isTypedef = true;
			}
		}
		if (!isTypedef) return;
		
		for (C.CodeDeclarator d: decl._ldtor) {
			String tdname = getDeclaratorName(d);
			if (tdname != null) _tdnames.add(tdname);
		}
	}
}
