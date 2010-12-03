package com.pag.comp;

import com.pag.sym.Env;
import com.smwatt.comp.C.Code;

public interface Phase {
    public boolean apply(Env env, Code code);
}
