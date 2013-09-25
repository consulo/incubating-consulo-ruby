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

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyLexer;
import org.jetbrains.plugins.ruby.ruby.lang.lexer._RubyLexer;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.LexerPosition;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.text.CharArrayCharSequence;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 31.03.2007
 */
public class RHTMLRubyLexer  extends MergingLexerAdapter {
    private static final TokenSet TOKENS_TO_MERGE = TokenSet.create(RHTMLTokenTypeEx.RHTML_INJECTION_IN_RUBY);

    public RHTMLRubyLexer() {
        this(false);
    }

    /**
     * Is used when lexer should return ruby and rhtml lexems, e.g. for ColorsPage 
     * @param inHighlighingMode If true lexer will returns rhtml lexems as is instead of special stub
     */
    public RHTMLRubyLexer(final boolean inHighlighingMode) {
        super(new _RHTMLRubyLexer(inHighlighingMode), TOKENS_TO_MERGE);
    }
}

class _RHTMLRubyLexer extends Lexer {
    private Lexer myRHTMLLexer = new _RHTMLLexer();
    private RubyLexer myRubyLexer = new RubyLexer();

    private Lexer myCurRubyLexer = null;
    private boolean myInHighlighingMode;

    public _RHTMLRubyLexer(final boolean inHighlighingMode) {
        myInHighlighingMode = inHighlighingMode;
    }

    public void start(char[] buffer) {
        start(buffer, 0, buffer.length, 0);
    }

    public void start(char[] buffer, int startOffset, int endOffset) {
        start(buffer, startOffset, endOffset, 0);
    }

    public void start(char[] buffer, int startOffset, int endOffset, int initialState) {
        start(new CharArrayCharSequence(buffer), startOffset, endOffset, initialState);
    }

    @Override
	public void start(final CharSequence buffer, final int startOffset, final int endOffset, final int initialState) {
        myRHTMLLexer.start(buffer, startOffset, endOffset, initialState);
        setupRubyLexer();
    }

    @Override
	public CharSequence getBufferSequence() {
        return myRHTMLLexer.getBufferSequence();
    }

    @Override
	public int getState() {
        return myRHTMLLexer.getState();
    }

    @Override
	public IElementType getTokenType() {
        IElementType tokenType = myRHTMLLexer.getTokenType();
        if (tokenType == null) {
            return null;
        }
        if (tokenType == RHTMLTokenType.RUBY_CODE_CHARACTERS) {
            return myCurRubyLexer.getTokenType();
        } else {
            return !myInHighlighingMode
                    ? RHTMLTokenTypeEx.RHTML_INJECTION_IN_RUBY
                    : tokenType;
        }
    }

    @Override
	public int getTokenStart() {
        IElementType tokenType = myRHTMLLexer.getTokenType();
        if (tokenType == RHTMLTokenType.RUBY_CODE_CHARACTERS) {
            return myCurRubyLexer.getTokenStart();
        } else {
            return myRHTMLLexer.getTokenStart();
        }
    }

    @Override
	public int getTokenEnd() {
        IElementType tokenType = myRHTMLLexer.getTokenType();
        if (tokenType == RHTMLTokenType.RUBY_CODE_CHARACTERS) {
            return myCurRubyLexer.getTokenEnd();
        } else {
            return myRHTMLLexer.getTokenEnd();
        }
    }

    @Override
	public void advance() {
        IElementType tokenType = myRHTMLLexer.getTokenType();
        if (tokenType == RHTMLTokenType.RUBY_CODE_CHARACTERS) {
            myCurRubyLexer.advance();
            if (myCurRubyLexer.getTokenType() != null) {
                return;
            }
        }
        myRHTMLLexer.advance();
        setupRubyLexer();
    }

    private void setupRubyLexer() {
        while (true) {
            IElementType tokenType = myRHTMLLexer.getTokenType();
            if (tokenType == RHTMLTokenType.RUBY_CODE_CHARACTERS) {
                myCurRubyLexer = myRubyLexer;
            } else {
                return;
            }

            myCurRubyLexer.start(myRHTMLLexer.getBufferSequence(), myRHTMLLexer.getTokenStart(), myRHTMLLexer.getTokenEnd(), _RubyLexer.YYINITIAL);
            if (myCurRubyLexer.getTokenType() != null) {
                return;
            }
            myRHTMLLexer.advance();
        }
    }

    private static class Position implements LexerPosition {
        private final LexerPosition myRubyPosition;
        private final LexerPosition myRhtmlPosition;

        public Position(final LexerPosition rubyPosition, final LexerPosition rhtmlPosition) {
            myRubyPosition = rubyPosition;
            myRhtmlPosition = rhtmlPosition;
        }

        @Override
		public int getOffset() {
            final int rhtmlPos = myRhtmlPosition != null ? myRhtmlPosition.getOffset() : 0;
            final int rubyPos = myRubyPosition == null ? 0 : myRubyPosition.getOffset();
            return Math.max(rhtmlPos, rubyPos);
        }

        public LexerPosition getRubyPosition() {
            return myRubyPosition;
        }

        public LexerPosition getRhtmlPosition() {
            return myRhtmlPosition;
        }

        @Override
		public int getState() {
            return 0;
        }
    }

    @Override
	public LexerPosition getCurrentPosition() {
        return new Position(myCurRubyLexer != null ? myCurRubyLexer.getCurrentPosition()
                                                   : null,
                            myRHTMLLexer.getCurrentPosition());
    }

    @Override
	public void restore(final LexerPosition position) {
        final Position p = (Position)position;
        myRHTMLLexer.restore(p.getRhtmlPosition());

        final LexerPosition rubyPos = p.getRubyPosition();
        if (rubyPos != null && rubyPos.getOffset() < myCurRubyLexer.getBufferEnd()) {
            myCurRubyLexer.restore(rubyPos);
        } else {
            myCurRubyLexer = null;
            setupRubyLexer();
        }
    }



    @Override
	public int getBufferEnd() {
        return myRHTMLLexer.getBufferEnd();
    }
}
