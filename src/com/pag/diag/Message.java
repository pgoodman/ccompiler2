
package com.pag.diag;

import com.pag.diag.Type;
import static com.pag.diag.Type.*;

/**
 * Basic
 * @author petergoodman
 *
 */
public enum Message {
    
    E_UNDECLARED_VAR        (ERROR,         "The variable '%%' was never declared."),
    E_MULTIPLY_DECLARED_VAR (ERROR,         "The variable '%%' was already declared at %%."),
    
    R_TOP_LEVEL_LABEL       (RECOVERABLE,   "Labels are not allowed in the top-level scope. Found label '%%'."),
    R_TOP_LEVEL_STAT        (RECOVERABLE,   "If/Else/Return/Break/Continue/Switch/Default/Goto/For/While/Do..While/Case statements are not allowed in the top-level scope."),
    R_TOP_LEVEL_EXPR        (RECOVERABLE,   "Expression statements are not allowed in the top-level scope."),
    
    E_FUNC_DEF_REPEAT       (ERROR,         "The function '%%' was previously defined at %%."),
    E_INNER_FUNC            (ERROR,         "Functions cannot be defined inside other functions."),
    
    N_FUNC_DECL_REDUNDANT   (NOTE,          "Function declaration is redundant. Function was defined at %%."),
    N_FUNC_DECL_REPEAT      (NOTE,          "Function declaration is redundant. Function has previously been declared."),
    
    E_LABEL_REPEAT          (ERROR,         "The label '%%' was previously declared at %%."),
    N_LABEL_NOT_USED        (NOTE,          "The label '%%' was never used."),
    E_LABEL_UNKNOWN         (ERROR,         "The label '%%' was not defined in the function '%%'."),
    E_CASE_OUTSIDE_SWITCH   (ERROR,         "Case (case ...:) statements must be enclosed by a switch statement."),
    E_DEFAULT_OUTSIDE_SWITCH(ERROR,         "Default (default:) statements must be enclosed by a switch statement."),
    
    B_UNKNOWN_TYPEDEF_NAME  (BUG,           "The typedef name '%%' is not known."),
    E_VAR_SHADOW_TYPEDEF    (ERROR,         "The type name '%%' was re-defined as another type of symbol at %%."),
    E_PARAM_SHADOW          (ERROR,         "The function parameter '%%' was already declared at %%."),
    W_PARAM_SHADOW          (WARNING,       "The function parameter '%%' shadows a previously declared symbol at %%."),
    E_UNKNOWN_PARAM_DECL    (ERROR,         "Declaration for non-existant function parameter '%%' found."),
    
    E_VAR_REDEF             (ERROR,         "The variable '%%' was already defined in this scope at %%."),
    W_VAR_REDEF             (WARNING,       "The local variable '%%' shadows a global variable of the same name at %%."),
    E_VAR_SHADOW_ENUMERATOR (ERROR,         "The variable '%%' shadows an enumerator value defined at %%."),
    W_VAR_SHADOW_ENUMERATOR (WARNING,       E_VAR_SHADOW_ENUMERATOR.format),
    E_VAR_REDEF_FUNC        (ERROR,         "A function with the same name as the variable '%%' was already declared at %%."),
    W_VAR_REDEF_FUNC        (WARNING,       E_VAR_REDEF_FUNC.format),
    E_VAR_UNKNOWN           (ERROR,         "The variable '%%' must be declared before it is used."),
    
    E_FIELD_REDEF           (ERROR,         "The field '%%' was already defined at %%."),
    
    E_COMPOUND_TYPE_UNKNOWN (ERROR,         "The type '%% %%' was never declared."),
    E_COMPOUND_TYPE_REDEF   (ERROR,         "The type '%% %%' cannot be re-declared in this scope. Previous declaration was at %%."),
    N_COMPOUND_TYPE_REDEF   (NOTE,          "Forward declaration of type '%% %%' is redundant. Previous declaration was at %%."),
    
    E_ENUMERATOR_SHADOW     (ERROR,         "The enumeration constant '%%' shadows a symbol of the same name which was declared at %%."),
    E_ENUMERATOR_INTEGRAL   (ERROR,         "Enumeration constants must have integral type."),
    
    E_SIGNED_NON_INTEGRAL_T (ERROR,         "Only integral types can be un/signed."),
    E_SHORT_LONG_NON_INTEGRAL_T (ERROR,     "Only integral/floating point types can be short/long."),
    E_SHORT_LONG_T          (ERROR,         "An integral type cannot be both short and long."),
    E_MULTI_SIGNED          (ERROR,         "An integral type can have only one un/signed specifier."),
    E_MULTI_QUALIFIER       (ERROR,         "A type can have only one const/volatile qualifier."),
    E_TOO_MANY_TYPE_SPECS   (ERROR,         "Too many type specifiers in type."),
    
    E_INCOMPLETE_MULTI_ARRAY(ERROR,         "Multidimensional array has incomplete type. Only the first dimension of a multi-dimensional array can be missing."),
    E_AUTO_REG_OUTSIDE_FUNC (ERROR,         "auto/register storage classes are not allowed outside of a function scope."),
    
    E_ARRAY_SIZE_NOT_POS    (ERROR,         "Arrays must have a non-zero positive size. The size of the array ends up being %%."),
    E_FUNC_RETURN_ARRAY     (ERROR,         "Functions are not allowed to return arrays."),
    E_POINTER_MULTI_QUALIF  (ERROR,         "Pointer declarators can only have one type qualifier."),
    R_FIELD_WIDTH_STRUCT_ONLY(RECOVERABLE,  "Field widths can only be specified inside of structs."),
    E_FIELD_WIDTH_INT_ONLY  (ERROR,         "Field widths can only be specified for integral types."),
    E_FIELD_WIDTH_POS       (ERROR,         "Structure field widths must evaluate to non-zero positive integers."),
    E_FIELD_WIDTH_TOO_WIDE  (ERROR,         "Structure field widths cannot exceed the width of the type of the field on which they are declared."),
    E_COMPOUND_CONTAIN_SELF (ERROR,         "A compound type (struct,union) cannot contain itself as a field. To do this, use a pointer to itself."),
    E_COMOUND_DEPEND_SIZEOF_SELF(ERROR,     "A compound type (struct,union) depends on the size of itself. For example: \n    struct foo {\n        int bar[sizeof(struct foo);\n    }"),
    E_SYMBOL_FUNC_TYPE      (ERROR,         "This symbol cannot have a function type. Did you mean to use a function pointer?"),
    
    E_NON_CONSTANT_EXPR     (ERROR,         "Cannot evaluate non-constant expression."),
    
    E_EXPR_NOT_COMPOUND_T   (ERROR,         "Expression does not have struct/union type."),
    E_EXPR_NOT_COMPOUND_PT  (ERROR,         "Expression does not have struct/union pointer type."),
    
    E_SUBSCRIPT_POINTER     (ERROR,         "Cannot take subscript of value with non-pointer/array type."),
    E_SUBSCRIPT_INTEGRAL    (ERROR,         "The subscript operator expects an expression of integral type."),
    
    E_CALL_FUNC_PTR         (ERROR,         "Cannot perform a function call on an expression that doesn't have a function pointer type."),
    E_CALL_FUNC_MISSING_ARGS(ERROR,         "Too few parameters were passed to the function to be called."),
    E_CALL_FUNC_EXTRA_ARGS  (ERROR,         "Too many parameters were passed to the function to be called."),
    E_CALL_BAD_ARG_TYPE     (ERROR,         "This function parameter's type is not compatible with the expected type."),
    E_BAD_OP_FOR_TYPE       (ERROR,         "This %% operator cannot be used on expressions of this type."),
    
    E_EXPR_HAS_CONST_TYPE   (ERROR,         "Cannot modify expression whose type is const-qualified."),
    E_EXPR_NOT_ADDRESSABLE  (ERROR,         "Cannot take address of this expression."),
    E_DEREF_FUNC_POINTER    (ERROR,         "Function pointers cannot be de-referenced."),
    E_DEREF_NON_POINTER     (ERROR,         "Non-pointers/arrays cannot be de-referenced."),
    
    E_INFIX_CANT_UNIFY      (ERROR,         "The types of the infix expression cannot be unified."),
    
    B_BUG                   (BUG,           "The following bug was reported: %%."),;
    
    public final Type type;
    public final String format;
    
    /**
     * Initialize a diagnostic message.
     */
    private Message(Type tt, String fmt) {
        type = tt;
        format = fmt;
    }
}
