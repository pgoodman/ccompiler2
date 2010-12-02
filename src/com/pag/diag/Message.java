
package com.pag.diag;

import com.pag.diag.Type;
import static com.pag.diag.Type.*;

/**
 * Basic
 * @author petergoodman
 *
 */
public enum Message {
    
    E_UNDECLARED_VAR        (FATAL,         "The variable '%%' was never declared."),
    E_MULTIPLY_DECLARED_VAR (FATAL,         "The variable '%%' was already declared at %%."),
    
    R_TOP_LEVEL_LABEL       (RECOVERABLE,   "Labels are not allowed in the top-level scope. Found label '%%'."),
    R_TOP_LEVEL_STAT        (RECOVERABLE,   "If/Else/Return/Break/Continue/Switch/Default/Goto/For/While/Do..While/Case statements are not allowed in the top-level scope."),
    R_TOP_LEVEL_EXPR        (RECOVERABLE,   "Expression statements are not allowed in the top-level scope."),
    
    E_FUNC_DEF_REPEAT       (FATAL,         "The function '%%' was previously defined at %%."),
    E_INNER_FUNC            (FATAL,         "Functions cannot be defined inside other functions."),
    
    N_FUNC_DECL_REDUNDANT   (NOTE,          "Function declaration is redundant. Function was defined at %%."),
    N_FUNC_DECL_REPEAT      (NOTE,          "Function declaration is redundant. Function has previously been declared."),
    
    E_LABEL_REPEAT          (FATAL,         "The label '%%' was previously declared at %%."),
    N_LABEL_NOT_USED        (NOTE,          "The label '%%' was never used."),
    E_LABEL_UNKNOWN         (FATAL,         "The label '%%' was not defined in the function '%%'."),
    E_CASE_OUTSIDE_SWITCH   (FATAL,         "Case statements must be enclosed by a switch statement."),
    
    B_UNKNOWN_TYPEDEF_NAME  (BUG,           "The typedef name '%%' is not known."),
    E_VAR_SHADOW_TYPEDEF    (FATAL,         "The type name '%%' was re-defined as another type of symbol at %%."),
    E_PARAM_SHADOW          (FATAL,         "The function parameter '%%' was already declared at %%."),
    E_UNKNOWN_PARAM_DECL    (FATAL,         "Declaration for non-existant function parameter '%%' found."),
    
    E_VAR_REDEF             (FATAL,         "The variable '%%' was already defined in this scope at %%."),
    E_VAR_SHADOW_ENUMERATOR (FATAL,         "The variable '%%' shadows an enumerator value defined at %%."),
    W_VAR_SHADOW_ENUMERATOR (WARNING,       E_VAR_SHADOW_ENUMERATOR.format),
    E_VAR_REDEF_FUNC        (FATAL,         "A function with the same name as the variable '%%' was already declared at %%."),
    W_VAR_REDEF_FUNC        (WARNING,       E_VAR_REDEF_FUNC.format);
    
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
