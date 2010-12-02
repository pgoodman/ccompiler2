
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
    E_LABEL_UNKNOWN         (FATAL,         "The label '%%' was not defined in the function '%%'."),
    E_CASE_OUTSIDE_SWITCH   (FATAL,         "Case statements must be enclosed by a switch statement.");
    
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
