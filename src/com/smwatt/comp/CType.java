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
import com.pag.val.CompileTimeInteger;
import com.smwatt.comp.C.CodeId;

import static com.pag.diag.Message.E_COMOUND_DEPEND_SIZEOF_SELF;

// !!!
// !!! simplifying assumption: all types with size < 4 bytes are rounded up
// !!! to 4 bytes. the standard only guarantees minimum sizes ;)
// !!!

public abstract class CType { 
    
    static private int  _next_id       = 0;
    
    public int          _id;
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
	
	public boolean breaksConstCorrectness(CType that) {
	    return that._isConst && !this._isConst /*|| (
            _isVolatile && !that._isVolatile
         || _isConst && that._isVolatile
         || _isVolatile && that._isConst
        )*/;
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
	
	// get the "internal" type of this type.. this is usually just the type
	// itself.
	public CType getInternalType() {
	    return this;
	}
	
	public void clearSize() {
	    
	}
	
	// TODO
	// Returns <code>null</code> if the types do not unify.
	//abstract public CType unify(CType that);
	
	abstract public int sizeOf(Env e);
	
	abstract public int alignAt();
	
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
            return !breaksConstCorrectness(that) && (
                that instanceof CTypeIntegral
            );
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return !breaksConstCorrectness(that) && (
                    that instanceof CTypePointing 
                 || that instanceof CTypeArithmetic
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
        
        @Override
        public int alignAt() {
            return 4;
        }
    }
    
    public static abstract class CTypeFloating extends CTypeArithmetic {
        @Override
        public boolean canBeAssignedTo(CType that) {
            return !breaksConstCorrectness(that) && (
            //    this._id == that._id
            // || that instanceof CTypeIntegral
            // || 
             that instanceof CTypeFloating
            );
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return !breaksConstCorrectness(that) 
                && that instanceof CTypeArithmetic;
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
        
        @Override
        public boolean canBeCastTo(CType that) {
            
            if(!(that instanceof CTypePointing)) {
                if(that instanceof CTypeIntegral) {
                    return true;
                }
                return false;
            }
            
            CTypePointing tt = (CTypePointing) that;
            
            // an attempt at const correctness :P
            if(tt._isVolatile != _isVolatile) {
                return false;
            } else if(_pointeeType._isVolatile != tt._pointeeType._isVolatile) {
                return false;
            } else if(_pointeeType._isConst && !tt._pointeeType._isConst) {
                return false;
            }
            
            if(_pointeeType instanceof CTypeVoid) {
                return true;
                
            } else if(tt._pointeeType instanceof CTypeVoid) {
                return true;
            }
            
            return this._pointeeType.canBeAssignedTo(
                tt._pointeeType
            );
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return false;
        }
    }
    
    /**
     * The purpose of this type is to fail loudly.
     * @author petergoodman
     *
     */
    public static class CTypeInvalid extends CType { 
        
        public CTypeInvalid() {
            super();
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 1;
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

        @Override
        public int alignAt() {
            return 1;
        }
    }
    
    /**
     * The purpose of this type is to silently fail.
     * @author petergoodman
     *
     */
    public static class CTypeBase extends CType { 
        
        public CTypeBase() {
            super();
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 1;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return true;
        }

        @Override
        public boolean canBeCastTo(CType that) {
            return true;
        }

        @Override
        public boolean canBePromotedTo(CType that) {
            return true;
        }

        @Override
        public CType copy() {
            return this;
        }

        @Override
        public int alignAt() {
            return 1;
        }
    }
    
    public static class CTypeVoid extends CType { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            return 1;
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            return !breaksConstCorrectness(that) && (that instanceof CTypeVoid);
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

        @Override
        public int alignAt() {
            return 1;
        }
    }
    
    public static class CTypeInt extends CTypeIntegral { 
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            if(0 == _length) {
                return 4;
            } else if(_length < 0) {
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
            return 1;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeChar());
        }
        
        @Override
        public int alignAt() {
            return 1;
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
        
        @Override
        public int alignAt() {
            return 8;
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
        
        @Override
        public int alignAt() {
            return 8;
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
            _isAddressable = true;
        }
        public CTypeFunction(CType returnType, List<CType> argTypes, boolean moreArgs) {
            super();
            init(returnType, argTypes, moreArgs);
            _isAddressable = true;
        }
        
        public CTypeFunction() {
            super();
            _isAddressable = true;
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
                return !breaksConstCorrectness(that);
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
        @Override
        public int alignAt() {
            return 1;
        }
    }
    
    public static class CTypeFunctionPointer extends CTypePointing {
        
        public CTypeFunctionPointer(CType pointeeType, boolean addressable) {
            super();
            _pointeeType = pointeeType;
            _isAddressable = addressable;
            pointeeType._isAddressable = true;
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
        public int sizeOf(Env e) {
            return 8;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeFunctionPointer(_pointeeType, _isAddressable));
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int alignAt() {
            return 8;
        }
    }
    
    public static class CTypeArray extends CTypePointer {
        public CTypeConstExpr          _optSize;
        
        public CTypeArray(CType elementType, CTypeConstExpr optSize) {
            super(elementType);
            _optSize     = optSize;
            _pointeeType._isAddressable = true;
        }
        
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }

        @Override
        public int sizeOf(Env e) {
            
            // size of a pointer
            if(null == _optSize) {
                return 8;
            }
            
            return (
                (CompileTimeInteger) e.interpret(_optSize._expr)
            ).value * _pointeeType.sizeOf(e);
        }

        @Override
        public boolean canBeAssignedTo(CType that) {
            if(that instanceof CTypePointing && !breaksConstCorrectness(that)) {
                return _pointeeType.canBeAssignedTo(
                    ((CTypePointing) that)._pointeeType
                );
            }
            return false;
        }

        @Override
        public CType copy() {
            return super.copy(new CTypeArray(_pointeeType, _optSize));
        }
        
        @Override
        public CType getInternalType() {
            return _pointeeType.getInternalType();
        }
    }
    public static class CTypePointer extends CTypePointing implements CTypeAdditive {
        
        public CTypePointer(CType pointeeType) {
            super();
            _pointeeType = pointeeType;
            
            // TODO? does this break the func pointer issues?
            _pointeeType._isAddressable = true;
        }
        
        public static CTypePointing optNew(CType pointeeType) {
            
            if(pointeeType instanceof CTypeFunction) {
                
                return new CTypeFunctionPointer(pointeeType, true);
            
            // handle the case where we can reference a function directly
            // or reference it with an & and still get a function pointer,
            // but once we do &&, we need to get a pointer to a function
            // pointer.
            } else if(pointeeType instanceof CTypeFunctionPointer) {
                CTypeFunctionPointer fptr = (CTypeFunctionPointer) pointeeType;
                
                if(fptr._isAddressable) {
                    fptr = (CTypeFunctionPointer) fptr.copy();
                    fptr._isAddressable = false;
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
        public CType copy() {
            return super.copy(new CTypePointer(_pointeeType));
        }

        @Override
        public int alignAt() {
            return 8;
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
            return that instanceof CTypeIntegral
                || _id == that._id;
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

        @Override
        public int alignAt() {
            return 4;
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
                return 1;
            }
            
            int size = 0;
            _size = -2;
            int total = 0;
            for(CTypeField field : _fields) {
                size += field.sizeOf(e, total);
                total += size;
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

        @Override
        public int alignAt() {
            if(0 == _fields.size()) {
                return 0;
            }
            return _fields.get(0)._type.alignAt();
        }
    }
    
    /**
     * This is a valid implementation of union types. Any code that depends
     * on the compiler overlapping the memory of union fields is necessarily
     * non-portable.
     * 
     * @author petergoodman
     *
     */
    public static class CTypeUnion extends CTypeStruct { 
        
        CTypeUnion(CodeId optId, List<CTypeField> fields) {
            super(optId, fields);
        }

        /*    
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
    */
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
        public C.CodeExpr _expr;
        
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
        public C.CodeId                 _id;
        public CType                    _type;
        
        //CTypeConstExpr          _optWidth = null; // Useful non-null.
        int                             _size = -1;
        public int                      _padding = 0;
        
        CTypeField(C.CodeId id, CType type) {
            super();
            _id = id;
            _type = type;
        }
        //CTypeField(C.CodeId id, CType type /* CTypeConstExpr optWidth */) {
        //    _id = id; _type = type; //_optWidth = optWidth;
        //}
        public void acceptVisitor(CTypeVisitor v) { v.visit(this); }
        
        public int sizeOf(Env e, int total) {            
            if(-1 != _size) {
                return _size;
            }
            _size = _type.sizeOf(e) + _padding;
            return _size;
        }
    }
}
