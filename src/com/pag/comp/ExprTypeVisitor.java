package com.pag.comp;

import static com.smwatt.comp.C.*;
import static com.pag.diag.Message.*;

import com.pag.sym.Env;
import com.smwatt.comp.C.Code;
import com.smwatt.comp.C.CodeCharacterConstant;
import com.smwatt.comp.C.CodeDeclaration;
import com.smwatt.comp.C.CodeDeclaratorArray;
import com.smwatt.comp.C.CodeDeclaratorFunction;
import com.smwatt.comp.C.CodeDeclaratorId;
import com.smwatt.comp.C.CodeDeclaratorInit;
import com.smwatt.comp.C.CodeDeclaratorPointer;
import com.smwatt.comp.C.CodeDeclaratorWidth;
import com.smwatt.comp.C.CodeDotDotDot;
import com.smwatt.comp.C.CodeEnumerationConstant;
import com.smwatt.comp.C.CodeEnumerator;
import com.smwatt.comp.C.CodeExprAssignment;
import com.smwatt.comp.C.CodeExprCall;
import com.smwatt.comp.C.CodeExprCast;
import com.smwatt.comp.C.CodeExprConditional;
import com.smwatt.comp.C.CodeExprField;
import com.smwatt.comp.C.CodeExprId;
import com.smwatt.comp.C.CodeExprInfix;
import com.smwatt.comp.C.CodeExprParen;
import com.smwatt.comp.C.CodeExprPointsTo;
import com.smwatt.comp.C.CodeExprPostfix;
import com.smwatt.comp.C.CodeExprPrefix;
import com.smwatt.comp.C.CodeExprSizeofType;
import com.smwatt.comp.C.CodeExprSizeofValue;
import com.smwatt.comp.C.CodeExprSubscript;
import com.smwatt.comp.C.CodeFloatingConstant;
import com.smwatt.comp.C.CodeFunction;
import com.smwatt.comp.C.CodeId;
import com.smwatt.comp.C.CodeInitializerList;
import com.smwatt.comp.C.CodeInitializerValue;
import com.smwatt.comp.C.CodeIntegerConstant;
import com.smwatt.comp.C.CodePointerStar;
import com.smwatt.comp.C.CodeSpecifierEnum;
import com.smwatt.comp.C.CodeSpecifierQualifier;
import com.smwatt.comp.C.CodeSpecifierStorage;
import com.smwatt.comp.C.CodeSpecifierStruct;
import com.smwatt.comp.C.CodeSpecifierType;
import com.smwatt.comp.C.CodeSpecifierTypedefName;
import com.smwatt.comp.C.CodeSpecifierUnion;
import com.smwatt.comp.C.CodeStatBreak;
import com.smwatt.comp.C.CodeStatCase;
import com.smwatt.comp.C.CodeStatCompound;
import com.smwatt.comp.C.CodeStatContinue;
import com.smwatt.comp.C.CodeStatDefault;
import com.smwatt.comp.C.CodeStatDo;
import com.smwatt.comp.C.CodeStatExpression;
import com.smwatt.comp.C.CodeStatFor;
import com.smwatt.comp.C.CodeStatGoto;
import com.smwatt.comp.C.CodeStatIf;
import com.smwatt.comp.C.CodeStatLabeled;
import com.smwatt.comp.C.CodeStatReturn;
import com.smwatt.comp.C.CodeStatSwitch;
import com.smwatt.comp.C.CodeStatWhile;
import com.smwatt.comp.C.CodeString;
import com.smwatt.comp.C.CodeTypeName;
import com.smwatt.comp.C.CodeUnit;
import com.smwatt.comp.C.CodeVisitor;

public class ExprTypeVisitor implements CodeVisitor {
    
    private Env env;
    
    public ExprTypeVisitor(Env ee) {
        env = ee;
    }

    public void visit(Code cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeUnit cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeFunction cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaration cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeId cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeTypeName cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeString cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeCharacterConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeIntegerConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeFloatingConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeEnumerationConstant cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDotDotDot cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierStorage cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierQualifier cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierType cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierTypedefName cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierStruct cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierUnion cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeSpecifierEnum cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeEnumerator cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorArray cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorFunction cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorInit cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorPointer cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorWidth cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeDeclaratorId cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodePointerStar cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeInitializerValue cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeInitializerList cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatBreak cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatCase cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatCompound cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatContinue cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatDefault cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatDo cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatExpression cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatFor cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatGoto cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatIf cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatLabeled cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatReturn cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatSwitch cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeStatWhile cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprAssignment cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprCast cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprConditional cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprInfix cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprParen cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprPostfix cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprPrefix cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprId cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprSizeofValue cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprSizeofType cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprCall cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprSubscript cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprField cc) {
        // TODO Auto-generated method stub
        
    }

    public void visit(CodeExprPointsTo cc) {
        // TODO Auto-generated method stub
        
    }
}
