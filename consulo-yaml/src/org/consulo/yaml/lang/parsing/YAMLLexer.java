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

package org.consulo.yaml.lang.parsing;

import java.util.Iterator;

import org.consulo.yaml.lang.psi.YAMLTokenTypeFactory;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jvaymlb.DefaultYAMLFactory;
import org.jetbrains.jvaymlb.ScannerImpl;
import org.jetbrains.jvaymlb.tokens.ScannerExceptionToken;
import org.jetbrains.jvaymlb.tokens.Token;
import org.jruby.util.ByteList;
import com.intellij.lexer.LexerBase;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 11, 2008
 */
public class YAMLLexer extends LexerBase {
    protected CharSequence myBuffer;

    protected ScannerImpl myScanner;
    protected Iterator myIterator;
    protected Token myToken;
    protected Token myPrevToken;

    private int myStart;
    private int myEnd;

    private int myTokenStart;
    private int myTokenEnd;
    protected boolean initialStateRead;

    public void start(final CharSequence buffer, final int startOffset, final int endOffset, final int initialState) {
        myBuffer = buffer;

        myStart = startOffset;
        myEnd = endOffset;

        myToken = null;
        myPrevToken = null;

        myTokenStart = 0;
        myTokenEnd = 0;
        
        myScanner = new DefaultYAMLFactory().createScanner(ByteList.create(myBuffer.subSequence(myStart, myEnd)));
        myIterator = myScanner.iterator();
        initialStateRead = false;
        advance();
    }

    @Deprecated
    public void start(final char[] buffer, final int startOffset, final int endOffset, final int initialState) {
        throw new UnsupportedOperationException("Method start is not implemented in org.consulo.yaml.lang.parsing.YAMLLexer");
    }

    public int getState() {
        if (initialStateRead){
            return 1;
        } else {
            initialStateRead = true;
            return 0;
        }
    }

    @Nullable
    public IElementType getTokenType() {
        return myToken!=null ? YAMLTokenTypeFactory.getTypeForToken(myPrevToken, myToken) : null;
    }

    public int getTokenStart() {
        return myTokenStart;
    }

    public int getTokenEnd() {
        return myTokenEnd;
    }

    public void advance() {
        try {
            myTokenStart = myTokenEnd;

            if (myTokenStart<myEnd && myIterator.hasNext()) {
                do {
                    myPrevToken = myToken;
                    myToken = (Token) myIterator.next();
                } while (myToken.getStart()==myToken.getEnd() && myIterator.hasNext());
// We do not modify tokenStart to ensure, that all the regions are covered by lexer
                myTokenEnd = myToken.getEnd();
            } else {
                myToken = null;
            }
        } catch (Exception e) {
            if (myScanner.getOffset()==myTokenStart){
                myScanner.forward();
            }
            myTokenEnd = myScanner.getOffset();
            myToken = myTokenStart!=myTokenEnd ? new ScannerExceptionToken(myTokenStart, myTokenEnd) : null;
        }
    }

	@Override
	public CharSequence getBufferSequence()
	{
		return myBuffer;
	}

    public int getBufferEnd() {
        return myEnd;
    }
}
