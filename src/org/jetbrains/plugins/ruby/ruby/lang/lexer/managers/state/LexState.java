/*
 * Copyright 2000-2008 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 13, 2006
 */

public class LexState implements StateComponent{
// after kDEF
    private boolean inDef = false;                      // EXPR_FNAME

// after kALIAS, upto semicolon or eol
    private boolean inAlias = false;                    // EXPR_FNAME

// after kUNDEF, upto semicolon or eol
    private boolean inUndef = false;                    // EXPR_FNAME

// after tSYMBEG
    private boolean afterSymBeg = false;                // EXPR_FNAME

// in line after tHEREDOC
    private boolean afterHeredoc = false;

// in line after kFOR, kUNTIL, kWHILE
    private boolean doCondExpected = false;

    private Expr expr = Expr.BEG;

//////// Getters ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean isInDef() {
        return inDef;
    }

    public boolean isInAlias() {
        return inAlias;
    }

    public boolean isInUndef() {
        return inUndef;
    }

    public boolean isAfterHeredoc() {
        return afterHeredoc;
    }

    public boolean isAfterSymBeg() {
        return afterSymBeg;
    }

    public boolean isDoCondExpected() {
        return doCondExpected;
    }

    public Expr getExpr() {
        return expr;
    }

//////// Setters ///////////////////////////////////////////////////////////////////////////////////////////////////////

    public void setInDef(boolean inDef) {
        this.inDef = inDef;
    }

    public void setInAlias(boolean inAlias) {
        this.inAlias = inAlias;
    }

    public void setInUndef(boolean inUndef) {
        this.inUndef = inUndef;
    }

    public void setAfterHeredoc(boolean afterHereDoc) {
        this.afterHeredoc = afterHereDoc;
    }

    public void setAfterSymBeg(boolean afterSymBeg) {
        this.afterSymBeg = afterSymBeg;
    }

    public void setDoCondExpected(boolean doCondExpected) {
        this.doCondExpected = doCondExpected;
    }

    public void setExpr(Expr expr) {
        this.expr = expr;
    }

    /**
     * @return true if it`s a special state
     */
    public boolean isSpecialState(){
        return isAfterHeredoc() ||
                isAfterSymBeg() ||
                isInDef() || isInAlias() || isInUndef() || isDoCondExpected() ||  
                getExpr()!= Expr.BEG;
    }
}

