
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
    E_VOID_VAR              (ERROR,         "The variable '%%' cannot have type void."),
    
    R_TOP_LEVEL_LABEL       (RECOVERABLE,   "Labels are not allowed in the top-level scope. Found label '%%'."),
    R_TOP_LEVEL_STAT        (RECOVERABLE,   "If/Else/Return/Break/Continue/Switch/Default/Goto/For/While/Do..While/Case\n    statements are not allowed in the top-level scope."),
    R_TOP_LEVEL_EXPR        (RECOVERABLE,   "Expression statements are not allowed in the top-level scope."),
    
    E_FUNC_DEF_REPEAT       (ERROR,         "The function '%%' was previously defined at %%."),
    E_INNER_FUNC            (ERROR,         "Functions cannot be defined inside other functions."),
    
    N_FUNC_DECL_REDUNDANT   (NOTE,          "Function declaration is redundant. Function was defined at %%."),
    N_FUNC_DECL_REPEAT      (NOTE,          "Function declaration is redundant. Function has previously been declared."),
    
    E_LABEL_REPEAT          (ERROR,         "The label '%%' was previously declared at %%."),
    N_LABEL_NOT_USED        (NOTE,          "The label '%%' was never used."),
    E_LABEL_UNKNOWN         (ERROR,         "The label '%%' was not defined in the function '%%'."),
    E_CASE_OUTSIDE_SWITCH   (ERROR,         "Case (case ...:) statements must be enclosed by a switch statement."),
    E_BREAK_OUTSIDE_STAT    (ERROR,         "Break statements must be enclosed in either a loop (for,while,do..while) statement\n    or in a switch statement."),
    E_CONTINUE_OUTSIDE_LOOP (ERROR,         "Continue statements must be enclodes in a loop (for,while,do..while) statement."),
    E_DEFAULT_OUTSIDE_SWITCH(ERROR,         "Default (default:) statements must be enclosed by a switch statement."),
    
    B_UNKNOWN_TYPEDEF_NAME  (BUG,           "The typedef name '%%' is not known."),
    E_VAR_SHADOW_TYPEDEF    (ERROR,         "The type name '%%' was re-defined as another type of symbol at %%."),
    E_PARAM_SHADOW          (ERROR,         "The function parameter '%%' was already declared at %%."),
    W_PARAM_SHADOW          (WARNING,       "The function parameter '%%' shadows a previously declared symbol at %%."),
    E_UNKNOWN_PARAM_DECL    (ERROR,         "Declaration for non-existant function parameter '%%' found."),
    
    E_VAR_REDEF             (ERROR,         "The variable '%%' was already defined in this scope at %%."),
    W_VAR_REDEF             (WARNING,       "The local variable '%%' shadows a global variable of the same name\n    at %%."),
    E_VAR_SHADOW_ENUMERATOR (ERROR,         "The variable '%%' shadows an enumerator value defined at %%."),
    W_VAR_SHADOW_ENUMERATOR (WARNING,       E_VAR_SHADOW_ENUMERATOR.format),
    E_VAR_REDEF_FUNC        (ERROR,         "A function with the same name as the variable '%%' was already declared\n    at %%."),
    W_VAR_REDEF_FUNC        (WARNING,       E_VAR_REDEF_FUNC.format),
    E_VAR_UNKNOWN           (ERROR,         "The variable '%%' must be declared before it is used."),
    
    E_FIELD_REDEF           (ERROR,         "The field '%%' was already defined at %%."),
    
    E_COMPOUND_TYPE_UNKNOWN (ERROR,         "The type '%% %%' was never declared."),
    E_COMPOUND_TYPE_REDEF   (ERROR,         "The type '%% %%' cannot be re-declared in this scope. Previous declaration\n    was at %%."),
    N_COMPOUND_TYPE_REDEF   (NOTE,          "Forward declaration of type '%% %%' is redundant. Previous declaration was\n    at %%."),
    
    E_ENUMERATOR_SHADOW     (ERROR,         "The enumeration constant '%%' shadows a symbol of the same name which was\n    declared at %%."),
    E_ENUMERATOR_INTEGRAL   (ERROR,         "Enumeration constants must have integral type."),
    
    E_SIGNED_NON_INTEGRAL_T (ERROR,         "Only integral types can be un/signed."),
    E_SHORT_LONG_NON_INTEGRAL_T (ERROR,     "Only integral/floating point types can be short/long."),
    E_SHORT_LONG_T          (ERROR,         "An integral type cannot be more than one short/long specifiers."),
    E_MULTI_SIGNED          (ERROR,         "An integral type can have only one un/signed specifier."),
    E_MULTI_QUALIFIER       (ERROR,         "A type can have only one const/volatile qualifier."),
    E_TOO_MANY_TYPE_SPECS   (ERROR,         "Too many type specifiers in type."),
    
    E_INCOMPLETE_MULTI_ARRAY(ERROR,         "Multidimensional array has incomplete type. Only the first dimension of a\n    multi-dimensional array can be missing."),
    E_INCOMPLETE_ARRAY      (ERROR,         "Array has incomplete type. A dimension must be specified."),
    E_AUTO_REG_OUTSIDE_FUNC (ERROR,         "auto/register storage classes are not allowed outside of a function scope."),
    
    E_ARRAY_SIZE_NOT_POS    (ERROR,         "Arrays must have a non-zero positive size. The size of the array ends up being %%."),
    E_FUNC_RETURN_ARRAY     (ERROR,         "Functions are not allowed to return arrays."),
    E_POINTER_MULTI_QUALIF  (ERROR,         "Pointer declarators can only have one type qualifier."),
    R_FIELD_WIDTH_STRUCT_ONLY(RECOVERABLE,  "Field widths can only be specified inside of structs."),
    E_FIELD_WIDTH_INT_ONLY  (ERROR,         "Field widths can only be specified for integral types."),
    E_FIELD_WIDTH_INTEGRAL  (ERROR,         "Field widths must have integral type."),
    E_FIELD_WIDTH_POS       (ERROR,         "Structure field widths must evaluate to non-zero positive integers."),
    E_FIELD_WIDTH_TOO_WIDE  (ERROR,         "Structure field widths cannot exceed the width of the type of the field on\n    which they are declared."),
    E_COMPOUND_CONTAIN_SELF (ERROR,         "A compound type (struct,union) cannot contain itself as a field. To do this,\n    use a pointer to itself."),
    E_COMOUND_DEPEND_SIZEOF_SELF(ERROR,     "A compound type (struct,union) depends on the size of itself. For example: \n    struct foo {\n        int bar[sizeof(struct foo);\n    }"),
    E_SYMBOL_FUNC_TYPE      (ERROR,         "This symbol cannot have a function type. Did you mean to use a function pointer?"),
    E_FUNC_RETURN_FUNC      (ERROR,         "Functions are not allowed to return functions. Did you mean to return a function pointer?"),
    
    E_NON_CONSTANT_EXPR     (ERROR,         "Cannot evaluate non-constant expression."),
    
    E_EXPR_NOT_COMPOUND_T   (ERROR,         "Expression does not have struct/union type."),
    E_EXPR_NOT_COMPOUND_PT  (ERROR,         "Expression does not have struct/union pointer type."),
    
    E_SUBSCRIPT_POINTER     (ERROR,         "Cannot take subscript of value with non-pointer/array type."),
    E_SUBSCRIPT_INTEGRAL    (ERROR,         "The subscript operator expects an expression of integral type."),
    
    E_CALL_FUNC_PTR         (ERROR,         "Cannot perform a function call on an expression that doesn't have a function\n    pointer type."),
    E_CALL_FUNC_MISSING_ARGS(ERROR,         "Too few parameters were passed to the function to be called."),
    E_CALL_FUNC_EXTRA_ARGS  (ERROR,         "Too many parameters were passed to the function to be called."),
    E_CALL_BAD_ARG_TYPE     (ERROR,         "The type of the expression passed into this function is not assignable to the\n    parameter type that the function expects in the expression's place."),
    E_BAD_OP_FOR_TYPE       (ERROR,         "This %% operator cannot be used on expressions of this type."),
    
    E_EXPR_HAS_CONST_TYPE   (ERROR,         "Cannot modify expression whose type is const-qualified."),
    E_EXPR_NOT_ADDRESSABLE  (ERROR,         "Cannot take address of this expression."),
    E_DEREF_FUNC_POINTER    (ERROR,         "Function pointers cannot be de-referenced."),
    E_DEREF_NON_POINTER     (ERROR,         "Non-pointers/arrays cannot be de-referenced."),
    
    E_INFIX_CANT_UNIFY      (ERROR,         "The types of the infix expression cannot be unified."),
    E_TYPES_NOT_COMPARABLE  (ERROR,         "These expressions cannot be compared."),
    E_SIGNED_SHIFT          (ERROR,         "The bitwise shifting operators are undefined for signed integers."),
    E_SIGNS_DONT_AGREE      (ERROR,         "The signs of the operands of the binary expression do not agree."),
    E_CANT_ADD_POINTERS     (ERROR,         "Pointers cannot be added together."),
    E_ONLY_ADD_INT_TO_PTR   (ERROR,         "Only integral types can be added to pointers."),
    E_EXPR_CANT_GO_BOOL     (ERROR,         "One (or both) of the expressions used in this logical connective cannot be\n    used in a boolean context."),
    E_TEST_CANT_GO_BOOL     (ERROR,         "The type of the expression used in the place of the logical test cannot be\n    used in a boolean context."),
    
    E_INVALID_TYPE          (ERROR,         "Invalid type detected. This is likely caused by a nearby typing error, where\n    the initial bad type has propagated up and caused an error here."),
    E_FUNC_MIX_DECLS        (ERROR,         "This function mixes old-style and new-style declarators."),
    
    E_ILLEGAL_CAST          (ERROR,         "Illegal type cast."),
    E_ARRAY_DIM_INTEGRAL    (ERROR,         "Array dimensions must have integral type."),
    E_CASE_SWITCH_DISAGREE  (ERROR,         "The type of the value in the case statement cannot be compared with the type\n    of the value being switched on at %%."),
    E_SWITCH_ON_ARITHMETIC  (ERROR,         "Can't use switch statement on an expression whose type is non-arithmetic."),
    E_BAD_RETURN_TYPE       (ERROR,         "Type of (possible) expression in return statement does not match the return\n    type of the enclosing function."),
    E_ASSIGN_TO_CONST       (ERROR,         "Can't assign to an expression whose type is const-qualified."),
    E_ASSIGN_TO_VALUE       (ERROR,         "Can't assign to an expression that evaluates into a value (rather than an\n    addressable location in memory)."),
    E_ASSIGN_INITIAL        (ERROR,         "Can't initialize variable with expression. The type of the expression is\n    not assignable to the type of the variable."),
    E_CANT_DO_ASSIGNMENT    (ERROR,         "Unable to assign right-hand side of expression to left-hand side."),
    
    E_INIT_ARRAY_WITHOUT_LIST(ERROR,        "Cannot initialize array without an initializer list."),
    E_INVALID_INIT_LIST_TYPE(ERROR,         "The type of at least one of the values in the initializer list for this\n    array cannot be assigned into a slot in this array."),
    E_INIT_LIST_TOO_LONG    (ERROR,         "The initializer list is too long for array type declared."),
    E_INIT_LIST_NOT_DIVISIBLE(ERROR,        "The length initializer list is not a multiple of the specified dimensions of the array."),
    E_INIT_LIST_AUTO        (ERROR,         "Can't automatically initialize the missing values in the initializer list."),
        
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
