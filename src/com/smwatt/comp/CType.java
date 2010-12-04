//////////////////////////////////////////////////////////////////////////////
//
// CType -- A representation for C type descriptions.
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.  All rights reserved.

package com.smwatt.comp;

import java.util.List;
import java.util.ArrayList;

// !!!
// !!! simplifying assumption: all types with size < 4 bytes are rounded up
// !!! to 4 bytes. the standard only guarantees minimum sizes ;)
// !!!

public abstract class CType { 
    
	boolean		_isConst 	= false;
	boolean		_isVolatile = false;
	
	public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
	
	/**
	 * Return the referenced type for typedefs, and named structs,
	 * named unions and named enums.
	 */
	CType realType() {
		return this;
	}
	
	/**
	 * Apply C type promotion rules
	 * TODO
	 */
	CType promote() {
		return this;
	}
	
	// TODO
	public boolean canBeAssignedTo(CType that) {
		return true;
	}
	// TODO
	public boolean canBePromotedTo(CType that) {
		return true;
	}
	// TODO
	public boolean canBeCastTo(CType that) {
		return true;
	}
	// TODO
	// Returns <code>null</code> if the types do not unify.
	public CType unify(CType that) {
		return this;
	}
	
	abstract public int sizeOf();
	
	/**
     * Used for typedefs, named struct-s, union-s and enum-s.
     */
    public static abstract class CTypeNamed extends CType {
        C.CodeId        _id;
        CType           _optReally;
        
        CTypeNamed(C.CodeId id, CType optReally) 
        { _id = id; _optReally = optReally; }
        
        CType realType() {
            return (_optReally == null) ? new CTypeInvalid() : _optReally;
        }
    }
    
    public static abstract class CTypeArithmetic extends CType {
        /**
         * The "length" of the numeric type:
         * +n => long^n.
         * -n => short^n.
         *  0 => default.
         *  
         *  Not all combinations are legal:
         *  ANSI C89 allows at most one on "int" and "double", and none 
         *  on other types.  If not type is given, "int" is assumed.
         */
        public int _length = 0;
    }
    
    public static abstract class CTypeIntegral extends CTypeArithmetic {
        /**
         * The "signedness" of the numeric type.
         * +1 => signed.
         * -1 => unsigned.
         *  0 => default.
         */
        public int _signed = 0;
    }
    
    public static abstract class CTypeFloating extends CTypeArithmetic { }
    
    public static abstract class CTypePointing extends CType {
        CType _pointeeType;
    }
    
    public static class CTypeInvalid extends CType { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            return 0;
        }
    }
    
    public static class CTypeVoid extends CType { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            return 0;
        }
    }
    
    public static class CTypeInt extends CTypeIntegral { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            if(0 == _length) {
                return 4;
            } else if(0 < _length) {
                return 4; //Math.min(2, 4 + (2 * _length));
            } else {
                return 8; //Math.max(8, 4 + (4 * _length));
            }
        }
    }
    
    public static class CSizeT extends CTypeInt {
        public CSizeT() {
            _signed = -1;
            _length = 1;
        }
    }
    
    public static class CTypeChar extends CTypeIntegral {
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            return 4; //1;
        }
    }
    
    public static class CTypeFloat extends CTypeFloating {
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        public int sizeOf() {
            return 4;
        }
    }
    
    public static class CTypeDouble extends CTypeFloating { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        public int sizeOf() {
            return _length != 0 ? 16 : 8;
        }
    }
    
    
    /**
     * Represents a function type in a canonical form.
     * 
     * K&R-style prototypes are represented with 
     * _argTypes.size() == 0 and _moreArgs == true.
     * 
     * New prototypes of the form f(void) are represented with
     * _argTypes.size() == 0 and _moreArgs == false.
     */
    public static class CTypeFunction extends CType {
        CType                   _retType;
        public List<CType>      _argTypes;
        boolean                 _moreArgs = false;  // Has "..." ?
        
        CTypeFunction(CType returnType, List<CType> argTypes) {
            _retType  = returnType;
            _argTypes = argTypes == null ? new ArrayList<CType>() : argTypes;
            _moreArgs = argTypes == null;
        }
        CTypeFunction(CType returnType, List<CType> argTypes, boolean moreArgs) {
            _retType  = returnType;
            _argTypes = argTypes == null ? new ArrayList<CType>() : argTypes;
            _moreArgs = moreArgs;
        }
        public void setMoreArgs() { _moreArgs = true; }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        @Override
        public int sizeOf() {
            return 8;
        }
    }
    public static class CTypeArray extends CTypePointing {
        CTypeConstExpr          _optSize;
        
        CTypeArray(CType elementType, CTypeConstExpr optSize) {
            _pointeeType = elementType;
            _optSize     = optSize;
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            
            // size of a pointer
            if(null == _optSize) {
                return 8;
            }
            
            return ((Integer) _optSize._expr._const_val).intValue() * _pointeeType.sizeOf();
        }
    }
    public static class CTypePointer extends CTypePointing {
        public CTypePointer(CType pointeeType) {
            _pointeeType = pointeeType;
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            return 8;
        }
    }
    
    public static class CTypeEnum extends CType { 
        C.CodeId        _optId;
        List<CTypeEnumerator>   _enumerators;
        
        CTypeEnum(C.CodeId optId, List<CTypeEnumerator> enumerators)
        { _optId = optId; _enumerators = enumerators; }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            return 4;
        }
    }
    public static class CTypeStruct extends CType { 
        C.CodeId            _optId;
        List<CTypeField>    _fields;
        int _size           = -1;
        
        CTypeStruct(C.CodeId optId, List<CTypeField> fields)
        { _optId = optId; _fields = fields; }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            if(-1 != _size) {
                return _size;
            }
            
            _size = 0;
            for(CTypeField field : _fields) {
                _size += field.sizeOf();
            }
            return _size;
        }
    }
    public static class CTypeUnion  extends CType { 
        C.CodeId            _optId;
        List<CTypeField>    _branches;
        int _size           = -1;
        
        CTypeUnion(C.CodeId optId, List<CTypeField> branches)
        { _optId = optId; _branches = branches; }
    
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            _size = 0;
            for(CTypeField field : _branches) {
                _size = Math.max(_size, field.sizeOf());
            }
            return _size;
        }
    }
    
    /*
    public static class CTypeNamedTypedef extends CTypeNamed {
        CTypeNamedTypedef(C.CodeId id) { super(id, null); } 
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            // TODO Auto-generated method stub
            return 0;
        }
    }
    public static class CTypeNamedStruct extends CTypeNamed {
        CTypeNamedStruct(C.CodeId id) { super(id, null); }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            // TODO Auto-generated method stub
            return 0;
        }
    }
    public static class CTypeNamedUnion extends CTypeNamed {
        CTypeNamedUnion(C.CodeId id) { super(id, null); }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf() {
            // TODO Auto-generated method stub
            return 0;
        }
    }
    public static class CTypeNamedEnum extends CTypeNamed {
        CTypeNamedEnum(C.CodeId id) { super(id, null); }
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        @Override
        public int sizeOf() {
            // TODO Auto-generated method stub
            return 0;
        }
    }
    */
    
    public static class CTypeConstExpr {
        C.CodeExpr _expr;
        
        CTypeConstExpr(C.CodeExpr expr) {
            _expr = expr;
        }
        static CTypeConstExpr optNew(C.CodeExpr optExpr) {
            if (optExpr == null) 
                return null;
            else
                return new CTypeConstExpr(optExpr);
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
    }
    public static class CTypeEnumerator {
        C.CodeId                _id;
        CTypeConstExpr          _optValue;
        
        CTypeEnumerator(C.CodeId id, CTypeConstExpr optValue)
        { _id = id; _optValue = optValue; }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
    }
    public static class CTypeField {
        C.CodeId                _id;
        CType                   _type;
        CTypeConstExpr          _optWidth = null; // Useful non-null.
        int                     _size = -1;
        
        CTypeField(C.CodeId id, CType type) {
            _id = id; _type = type;
        }
        CTypeField(C.CodeId id, CType type, CTypeConstExpr optWidth) {
            _id = id; _type = type; _optWidth = optWidth;
        }
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        public int sizeOf() {
            if(-1 != _size) {
                return _size;
            }
            _size = _type.sizeOf();
            return _size;
        }
    }
}
