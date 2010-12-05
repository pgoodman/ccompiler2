//////////////////////////////////////////////////////////////////////////////
//
// CType -- A representation for C type descriptions.
//
//////////////////////////////////////////////////////////////////////////////
// (C) Copyright 2005-2007 Stephen M. Watt.  All rights reserved.

package com.smwatt.comp;

import java.util.List;
import java.util.ArrayList;

import com.pag.sym.Env;
import static com.pag.diag.Message.E_COMOUND_DEPEND_SIZEOF_SELF;

// !!!
// !!! simplifying assumption: all types with size < 4 bytes are rounded up
// !!! to 4 bytes. the standard only guarantees minimum sizes ;)
// !!!

public abstract class CType { 
    
    static private int  _next_id       = 0;
    
    protected int       _id            = 0;
	public boolean		_isConst 	   = false;
	public boolean		_isVolatile    = false;
	public boolean      _isAddressable = false;
	
	public void acceptVisitor(CTypeVisitor v) {
	    v.visit(this);
	}
	
	public CType() {
	    _id = CType._next_id++;
	}
	
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
	abstract public boolean canBeAssignedTo(CType that);
	
	// TODO
	abstract public boolean canBePromotedTo(CType that);
	// TODO
	abstract public boolean canBeCastTo(CType that);
	
	abstract public CType copy();
	
	/* copy the basic things of a type */
	public CType copy(CType c) {
	    c._isAddressable = _isAddressable;
	    c._isConst = _isConst;
	    c._isVolatile = _isVolatile;
	    c._id = _id;
	    return c;
	}
	
	// TODO
	// Returns <code>null</code> if the types do not unify.
	//abstract public CType unify(CType that);
	
	abstract public int sizeOf(Env e);
	
	// traits through interfaces.. hoorah :P 
	public interface CTypeComparable { }
	public interface CTypeAdditive { }
	public interface CTypeMultiplicative extends CTypeAdditive { }
	public interface CTypeBooleanSensitive { }
	
	/**
     * Used for typedefs, named struct-s, union-s and enum-s.
     */
	/*
    public static abstract class CTypeNamed extends CType {
        C.CodeId        _id;
        CType           _optReally;
        
        CTypeNamed(C.CodeId id, CType optReally) 
        { _id = id; _optReally = optReally; }
        
        CType realType() {
            return (_optReally == null) ? new CTypeInvalid() : _optReally;
        }
    }*/
    
    public static abstract class CTypeArithmetic extends CType 
    implements CTypeComparable, CTypeMultiplicative, CTypeBooleanSensitive {
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
        
        @Override
        public CType copy(CType c) {
            ((CTypeArithmetic) c)._length = _length;
            return super.copy(c);
        }
        
        public void makeLong() {
            ++_length;
        }
    }
    
    public static abstract class CTypeIntegral extends CTypeArithmetic {
        /**
         * The "signedness" of the numeric type.
         * +1 => signed.
         * -1 => unsigned.
         *  0 => default.
         */
        public int _signed = 0;

        @Override
        public boolean canBeAssignedTo(CType that) {
            return (
                this._id == that._id
             || (that instanceof CTypeIntegral /* && (_signed == ((CTypeIntegral) that)._signed)*/ )
             || (that instanceof CTypeFloating) 
            );
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return (
                canBeAssignedTo(that) || (that instanceof CTypePointing)
            );
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            if(that instanceof CTypeIntegral) {
                CTypeIntegral intg = (CTypeIntegral) that;
                /*if(_signed == intg._signed) {
                    return _length <= intg._length;
                } else {
                    if(_signed)
                }*/
                return _length <= intg._length;
            }
            return (that instanceof CTypeFloating);
        }
        
        @Override
        public CType copy(CType c) {
            ((CTypeIntegral) c)._signed = _signed;
            return super.copy(c);
        }
    }
    
    public static abstract class CTypeFloating extends CTypeArithmetic {
        @Override
        public boolean canBeAssignedTo(CType that) {
            return (
                this._id == that._id
             || that instanceof CTypeIntegral
             || that instanceof CTypeFloating
            );
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return canBeAssignedTo(that);
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return (
                this._id == that._id
             || (
                     that instanceof CTypeFloating 
                  && _length <= ((CTypeFloating) that)._length
                )
            );
        }
    }
    
    public static abstract class CTypePointing extends CType 
    implements CTypeComparable, CTypeBooleanSensitive {
        public CType _pointeeType;
    }
    
    public static class CTypeInvalid extends CType { 
        
        public CTypeInvalid() {
            super();
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 0;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return false;
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return false;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return false;
        }

        @Override
        public CType copy() {
            return this;
        }
    }
    
    public static class CTypeVoid extends CType { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 0;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return (that instanceof CTypeVoid);
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return false;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return false;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeVoid());
        }
    }
    
    public static class CTypeInt extends CTypeIntegral { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            if(0 == _length) {
                return 4;
            } else if(0 < _length) {
                return 4; //Math.min(2, 4 + (2 * _length));
            } else {
                return 8; //Math.max(8, 4 + (4 * _length));
            }
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeInt());
        }
    }
    
    public static class CSizeT extends CTypeInt {
        public CSizeT() {
            _signed = -1;
            _length = 1;
        }
    }
    
    public static class CTypePtrDiffT extends CTypeInt {
        public CTypePtrDiffT() {
            _signed = 1;
            _length = 1;
        }
    }
    
    public static class CTypeChar extends CTypeIntegral {
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 4; //1;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeChar());
        }
    }
    
    public static class CTypeFloat extends CTypeFloating {
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        public int sizeOf(Env e) {
            return 4;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeFloat());
        }
    }
    
    public static class CTypeDouble extends CTypeFloating { 
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        public int sizeOf(Env e) {
            return _length != 0 ? 16 : 8;
        }
        
        @Override
        public CType copy() {
            return super.copy(new CTypeDouble());
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
        public CType            _retType;
        public List<CType>      _argTypes;
        public boolean          _moreArgs = false;  // Has "..." ?
        
        public CTypeFunction(CType returnType, List<CType> argTypes) {
            super();
            init(returnType, argTypes, argTypes == null);
        }
        public CTypeFunction(CType returnType, List<CType> argTypes, boolean moreArgs) {
            super();
            init(returnType, argTypes, moreArgs);
        }
        
        public CTypeFunction() {
            super();
        }
        
        public void init(CType returnType, List<CType> argTypes, boolean moreArgs) {
            _retType  = returnType;
            _argTypes = argTypes == null ? new ArrayList<CType>() : argTypes;
            _moreArgs = moreArgs;
        }
        
        public void setMoreArgs() { _moreArgs = true; }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        @Override
        public int sizeOf(Env e) {
            return 1;
        }
        @Override
        public boolean canBeAssignedTo(CType that) {
            if(that instanceof CTypeFunction) {
                CTypeFunction func = (CTypeFunction) that;
                if(this._id == that._id) {
                    return true;
                } else if(_retType != func._retType) {
                    return false;
                } else if(_argTypes.size() != func._argTypes.size()) {
                    return false;
                } else {
                    for(int i = 0; i < _argTypes.size(); ++i) {
                        if(_argTypes.get(i) != func._argTypes.get(i)) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
        }
        @Override
        public boolean canBeCastTo(CType that) {
            return that instanceof CTypeFunction;
        }
        @Override
        public boolean canBePromotedTo(CType that) {
            return false;
        }
        @Override
        public CType copy() {
            return super.copy(new CTypeFunction(_retType, _argTypes, _moreArgs));
        }
    }
    
    public static class CTypeFunctionPointer extends CTypePointing {
        
        // counter to keep track of the number of times we've taking the
        // address of the function.
        public int _count;
        
        public CTypeFunctionPointer(CType pointeeType, int count) {
            super();
            _pointeeType = pointeeType;
            _count = count;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return (
                that instanceof CTypeFunctionPointer 
             && (_pointeeType.canBeAssignedTo(
                    ((CTypeFunctionPointer) that)._pointeeType
                ))
            );
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return that instanceof CTypeFunctionPointer;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return false;
        }

        @Override
        public int sizeOf(Env e) {
            return 8;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeFunctionPointer(_pointeeType, _count));
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
    }
    
    public static class CTypeArray extends CTypePointing implements CTypeAdditive {
        public CTypeConstExpr          _optSize;
        
        public CTypeArray(CType elementType, CTypeConstExpr optSize) {
            super();
            _pointeeType = elementType;
            _optSize     = optSize;
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            
            // size of a pointer
            if(null == _optSize) {
                return 8;
            }
            
            return (
                (Integer) e.interpret(_optSize._expr)
            ).intValue() * _pointeeType.sizeOf(e);
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            if(this._id == that._id) {
                return true;
            } else if(that instanceof CTypePointing) {
                return _pointeeType.canBeAssignedTo(
                    ((CTypePointing) that)._pointeeType
                );
            }
            return false;
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return that instanceof CTypePointing;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return false;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeArray(_pointeeType, _optSize));
        }
    }
    public static class CTypePointer extends CTypePointing implements CTypeAdditive {
        
        public CTypePointer(CType pointeeType) {
            super();
            _pointeeType = pointeeType;
        }
        
        public static CTypePointing optNew(CType pointeeType) {
            if(pointeeType instanceof CTypeFunction) {
                return new CTypeFunctionPointer(pointeeType, 0);
            
            // handle the case where we can reference a function directly
            // or reference it with an & and still get a function pointer,
            // but once we do &&, we need to get a pointer to a function
            // pointer.
            } else if(pointeeType instanceof CTypeFunctionPointer) {
                CTypeFunctionPointer fptr = (CTypeFunctionPointer) pointeeType;
                if(0 == fptr._count) {
                    ++fptr._count;
                    return fptr;
                }
            }
            return new CTypePointer(pointeeType);
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 8;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return that instanceof CTypePointing && (
                _pointeeType.canBeAssignedTo(((CTypePointing) that)._pointeeType)
             || _pointeeType instanceof CTypeVoid
            );
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return that instanceof CTypePointer;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return that == this;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypePointer(_pointeeType));
        }
    }
    
    public static class CTypeEnum extends CType {
        
        C.CodeId                _optId;
        List<CTypeEnumerator>   _enumerators;
        
        CTypeEnum(C.CodeId optId, List<CTypeEnumerator> enumerators) {
            super();
            _optId = optId; 
            _enumerators = enumerators;
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 4;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return that instanceof CTypeIntegral;
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return that instanceof CTypeArithmetic;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return that instanceof CTypeIntegral;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeEnum(_optId, _enumerators));
        }
    }
    
    public abstract static class CTypeCompound extends CType {
        public List<CTypeField> _fields;
        int                     _size = -1;
        C.CodeId                _optId;
        
        /**
         * Go get some source position anywhere in this type.
         * @return
         */
        protected SourcePosition getSomeSourcePosition() {
            if(null != _optId) {
                return _optId.getSourcePosition();
            }
            
            for(CTypeField ff : _fields) {
                if(null != ff._id) {
                    return ff._id.getSourcePosition();
                }
            }
            
            return null;
        }
    }
    
    public static class CTypeStruct extends CTypeCompound {      
        
        CTypeStruct(C.CodeId optId, List<CTypeField> fields) {
            super();
            _optId = optId; 
            _fields = fields; 
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            if(0 <= _size) {
                return _size;
                
            // trying to compute the size of this type already
            } else if(-2 == _size) {
                e.diag.report(
                    E_COMOUND_DEPEND_SIZEOF_SELF,
                    getSomeSourcePosition()
                );
                return 0;
            }
            
            int size = 0;
            _size = -2;
            for(CTypeField field : _fields) {
                size += field.sizeOf(e);
            }
            _size = size;
            return size;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return that == this;
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return that == this;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return that == this;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeStruct(_optId, _fields));
        }
    }
    public static class CTypeUnion extends CTypeCompound { 
        
        CTypeUnion(C.CodeId optId, List<CTypeField> branches) {
            super();
            _optId = optId; 
            _fields = branches; 
        }
    
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            
            if(0 <= _size) {
                return _size;
            
            // trying to compute the size of this type already
            } else if(-2 == _size) {
                e.diag.report(
                    E_COMOUND_DEPEND_SIZEOF_SELF,
                    getSomeSourcePosition()
                );
            }
            
            int size = 0;
            _size = -2;
            for(CTypeField field : _fields) {
                size = Math.max(_size, field.sizeOf(e));
            }
            _size = size;
            return size;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return that == this;
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return that == this;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return that == this;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeUnion(_optId, _fields));
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
        
        public CTypeConstExpr(C.CodeExpr expr) {
            super();
            _expr = expr;
        }
        
        static CTypeConstExpr optNew(C.CodeExpr optExpr) {
            if (optExpr == null) {
                return null;
            } else {
                return new CTypeConstExpr(optExpr);
            }
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
    }
    
    public static class CTypeEnumerator {
        C.CodeId                _id;
        CTypeConstExpr          _optValue;
        
        CTypeEnumerator(C.CodeId id, CTypeConstExpr optValue) { 
            super();
            _id = id; 
            _optValue = optValue;
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
    }
    
    public static class CTypeField {
        public C.CodeId                _id;
        public CType                   _type;
        
        //CTypeConstExpr          _optWidth = null; // Useful non-null.
        int                     _size = -1;
        
        CTypeField(C.CodeId id, CType type) {
            super();
            _id = id;
            _type = type;
        }
        //CTypeField(C.CodeId id, CType type /* CTypeConstExpr optWidth */) {
        //    _id = id; _type = type; //_optWidth = optWidth;
        //}
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        public int sizeOf(Env e) {
            if(-1 != _size) {
                return _size;
            }
            _size = _type.sizeOf(e);
            return _size;
        }
    }
}
