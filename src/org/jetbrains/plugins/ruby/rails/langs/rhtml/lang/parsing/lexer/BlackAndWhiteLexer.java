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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer;

import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayCharSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.04.2007
 */

/**
 * Separates lexems in two sets
 */
public abstract class BlackAndWhiteLexer implements Lexer {
    private Lexer mySeparatorLexer;
    public IElementType myWhiteToken;
    public IElementType myWhiteFragmentEnd;
    public IElementType myBlackToken;

    /**
     * @param separatorLexer Lexer. It's tokens will analyze black and wite separator
     * @param whiteToken This token is significant for us.
     * @param whiteFragmentEnd This token tell us that white fragment has just finished.
     * @param blackToken This will be returned for all other(no significant) tokens
     */
    public BlackAndWhiteLexer(final Lexer separatorLexer,
                              @NotNull final IElementType whiteToken,
                              @Nullable final IElementType whiteFragmentEnd,
                              @NotNull final IElementType blackToken) {

        mySeparatorLexer = separatorLexer;
        myWhiteToken = whiteToken;
        myWhiteFragmentEnd = whiteFragmentEnd;
        myBlackToken = blackToken;
    }

    protected abstract boolean isWhiteData(final IElementType tokenType);

    public void start(final char[] buffer) {
        start(buffer, 0, buffer.length, 0);
    }

    public void start(final char[] buffer, final int startOffset, final int endOffset) {
        start(buffer, startOffset, endOffset, 0);
    }

    public void start(final char[] buffer,
                      final int startOffset, final int endOffset, final int initialState) {
        start(new CharArrayCharSequence(buffer), startOffset, endOffset, initialState);
    }

    public void start(final CharSequence buffer,
                      final int startOffset, final int endOffset, final int initialState) {
        mySeparatorLexer.start(buffer, startOffset, endOffset, initialState);
    }

    public CharSequence getBufferSequence() {
        return mySeparatorLexer.getBufferSequence();
    }

    public int getState() {
        return mySeparatorLexer.getState();
    }

    @Nullable
    public IElementType getTokenType() {
        IElementType tokenType = mySeparatorLexer.getTokenType();
        if (tokenType == null) {
            return null;
        }
        return isWhiteData(tokenType) ? myWhiteToken : myBlackToken;
    }

    public boolean isOnWhiteFragmentEnd() {
        return mySeparatorLexer.getTokenType() == myWhiteFragmentEnd;
    }

    public int getTokenStart() {
        return mySeparatorLexer.getTokenStart();
    }

    public int getTokenEnd() {
        return mySeparatorLexer.getTokenEnd();
    }

    public void advance() {
        mySeparatorLexer.advance();
    }

    public LexerPosition getCurrentPosition() {
        return mySeparatorLexer.getCurrentPosition();
    }

    public void restore(final LexerPosition position) {
        mySeparatorLexer.restore(position);
    }

    @SuppressWarnings({"deprecation"})
    public char[] getBuffer() {
        return mySeparatorLexer.getBuffer();
    }

    public int getBufferEnd() {
        return mySeparatorLexer.getBufferEnd();
    }
}