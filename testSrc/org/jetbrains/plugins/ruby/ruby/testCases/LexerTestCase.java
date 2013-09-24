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

package org.jetbrains.plugins.ruby.ruby.testCases;

import com.intellij.lexer.Lexer;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NonNls;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.04.2007
 */
public abstract class LexerTestCase extends BaseRubyFileSetTestCase {
    @NonNls
    private Lexer myLexer;
    private boolean myInDebugMode;

    public LexerTestCase(final String dataPath) {
        this(dataPath,  false);
    }

    public LexerTestCase(final String dataPath, final boolean inDebugMode) {
        super(dataPath);
        myInDebugMode = inDebugMode;
    }

    public void setLexer(final Lexer myLexer) {
        this.myLexer = myLexer;
    }

    protected String transform(final List<String> data) throws Exception {
        StringBuffer dataBuff = new StringBuffer();
        for (String s : data) {
            dataBuff.append(s);
        }
        return startLexer(dataBuff.toString().trim(), myLexer).trim();
    }

    protected String startLexer(final String content, Lexer lexer) {
        final StringBuilder output = new StringBuilder();

        if (myInDebugMode) {
            System.out.println("[" + content + "]\n");
        }

        lexer.start(content, 0, content.length(), 0);

        while (lexer.getTokenType() != null) {
            final int s = lexer.getTokenStart();
            final int e = lexer.getTokenEnd();
            final IElementType tokenType = lexer.getTokenType();
            final String str = tokenType + ": [" + s + ", " + e + "], {" + content.substring(s, e) + "}\n";
            output.append(str);

            if (myInDebugMode) {
                System.out.print(str);
            }
            lexer.advance();
        }
        return output.toString();
    }
}