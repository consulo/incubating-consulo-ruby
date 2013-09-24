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

package org.jetbrains.plugins.ruby.ruby.lang.lexer.managers;

import org.jetbrains.plugins.ruby.ruby.lang.lexer._RubyLexer;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.09.2006
 */

/**
 * Class used for basic reading from lexers zzBuffer.
 * One instance per each _RubyLexer instance.
 */
class ReadingManager {
    private _RubyLexer lexer;
    private CharSequence zzBuffer;
    private int lexerEnd;
    private int lexerStart;

    public ReadingManager(final _RubyLexer lexer){
        this.lexer = lexer;
        zzBuffer = lexer.getBuffer();
    }

    protected void reset(final int zzStart, final int zzEnd){
        zzBuffer = lexer.getBuffer();
        lexerStart = zzStart;
        lexerEnd = zzEnd;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////// Safe reading ////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * Reads a symbol
     * @param pos offset by the beginning of current token
     * @return (char)-1 if there is no symbol at position
     */
    public char safeReadAt(final int pos) {
        final int zzStartRead = lexer.getTokenStart();
        if (canReadAt(pos)){
            return zzBuffer.charAt(zzStartRead+pos);
        } else {
            return (char)-1;
        }
    }

    /**
     * @param pos offset by the beginning of current token
     * @return true, if there is a symbol at position
     */
    public boolean canReadAt(final int pos) {
        final int zzStartRead = lexer.getTokenStart();
        int loc = zzStartRead + pos;
        return (lexerStart<=loc && loc<lexerEnd);
    }


    /**
     * Reads a string
     * @param pos offset by the beginning of current token
     * @return string
     * @param sLen expected string length
     */
    public String safeReadStringAt(final int pos, final int sLen) {
        String result="";
        for (int i=0; canReadAt(pos+i) && i<sLen; i++) {
             result+=safeReadAt(pos+i);
        }
        return result;
    }

}
