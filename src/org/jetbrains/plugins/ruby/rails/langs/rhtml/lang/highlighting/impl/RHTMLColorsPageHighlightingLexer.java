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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.impl;

import com.intellij.lexer.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayCharSequence;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer.RHTMLRubyLexer;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.04.2007
 */

public class RHTMLColorsPageHighlightingLexer implements Lexer {
    private static final int HTML_MASK = 0x7FE0;
    private static final int RHTML_MASK = 0x1F;
    private static final int QUEUE_MASK = 0x3FFF8000;

    private static final int QUEUE_SHIFT = 15;
    private static final int HTML_SHIFT = 5;

    private final static int HTML_COMMENT_STATE = _HtmlLexer.COMMENT;


    private int myTokenStart;
    private int myTokenEnd;
    private int myState;

    private IElementType myTokenType;
    private Queue<Token> myCommentTokensQueue;

    private RHTMLRubyLexer myRHTMLRubyLexer = new RHTMLRubyLexer(true);
    private Lexer myHtmlLexer = new HtmlHighlightingLexer();
    private Lexer myCurHTMLLexer = null; // myHTMLLexer

    public void start(char[] buffer) {
        start(buffer, 0, buffer.length, 0);
    }

    public void start(char[] buffer, int startOffset, int endOffset) {
        start(buffer, startOffset, endOffset, 0);
    }

    public void start(char[] buffer, int startOffset, int endOffset, int initialState) {
        start(new CharArrayCharSequence(buffer), startOffset, endOffset, initialState);
    }


    /**
     * Packs rhtml lexer state and html lexer state in one integer.
     * @param rSt rhtml lexer state
     * @param hSt html lexer state
     * @return Packed state
     */
    private int packState(final int rSt, final int hSt) {
        return packRhtmlState(rSt) | packHtmlState(hSt);
    }

    /**
     * Packs rhtml lexer state to a special format
     * @param rSt rhtml lexer state
     * @return Packed state
     */
    private int packRhtmlState(final int rSt) {
        /**
         * WARNING: rst must be limited by 3 bites
         */
        return rSt & RHTML_MASK;
    }

    /**
     * Unpacks rhtml lexer state from packed format
     * @param state lexer state
     * @return Unpacked state
     */
    private int unpackRhtmlState(final int state) {
        return state & RHTML_MASK;
    }

    /**
     * Packs html lexer state to a special format
     * @param hSt html lexer state
     * @return Packed state
     */
    private int packHtmlState(final int hSt) {
        return (hSt << HTML_SHIFT) & HTML_MASK;
    }

    /**
     * Unpacks html lexer state from packed format
     * @param state lexer state
     * @return unpacked state
     */
    private int unpackHtmlState(final int state) {
        return (state & HTML_MASK) >> HTML_SHIFT;
    }



    public void start(final CharSequence buffer,
                      final int startOffset, final int endOffset, final int initialState) {
        myRHTMLRubyLexer.start(buffer, startOffset, endOffset, unpackRhtmlState(initialState));

        myCommentTokensQueue = new LinkedList<Token>();
        myTokenStart = myTokenEnd = startOffset;
        myTokenType = null;

        myState = initialState;

        setupHTMLLexer();
    }

    public CharSequence getBufferSequence() {
        return myRHTMLRubyLexer.getBufferSequence();
    }

    public int getState() {
        locateToken();
        return myState | ((myCommentTokensQueue.size() << QUEUE_SHIFT) & QUEUE_MASK);  //myHtmlLexer.getState()
    }


    private void locateToken() {
        if (myTokenType != null) {
            return;
        }
        myTokenStart = myTokenEnd;

        final Token queuedToken = myCommentTokensQueue.peek();
        if (queuedToken != null) {
            myTokenType = queuedToken.tokenType;
            myTokenEnd = queuedToken.tokenEnd;

            /**
             * Determine state:
             * We are in html comment tokens, thus html tokens may be only
             * RHTMLTokenTypeEx.XML_COMMENT_CHARACTERS and state = HTML_COMMENT_STATE
             */
             myState = packHtmlState(HTML_COMMENT_STATE) | (packRhtmlState(queuedToken.rhtmlLexerState));
            return;
        }

        myTokenType = myRHTMLRubyLexer.getTokenType();
        if (myTokenType == null) {
            // e.g. whole text was lexed.
            return;
        }

        //current rhtml state
        final int rSt = myRHTMLRubyLexer.getState();

        if (myTokenType == RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML) {
            myTokenType = myCurHTMLLexer.getTokenType();
            myTokenEnd = myCurHTMLLexer.getTokenEnd();
            //Determines state
            myState = packState(rSt, myCurHTMLLexer.getState());

            //If in HTML comment, then add it!
            if (myTokenType == RHTMLTokenType.XML_COMMENT_START) {
                //Eat whole HTML comment till XML_COMMENT_CLOSE_TAG
                fedHtmlCommentQueue();
            }
        } else {
            myTokenEnd = myRHTMLRubyLexer.getTokenEnd();
            //Determines state: use current rhtml state and last html state
            myState = packState(rSt, unpackHtmlState(myState));
        }
    }


    private void createAndOfferToken(final Lexer lexer, final IElementType tokenType) {
        Token token = new Token(myRHTMLRubyLexer.getState(), tokenType,
                                lexer.getTokenStart(), lexer.getTokenEnd());
        myCommentTokensQueue.offer(token);
    }

    private void fedHtmlCommentQueue() {
        IElementType tokenType;

        int prevState = myState;
        myState = packState(unpackRhtmlState(myState), HTML_COMMENT_STATE);

        //Put current token into Queue (for advance() method)
        createAndOfferToken(myCurHTMLLexer, myCurHTMLLexer.getTokenType());

        do {
            advanceWithoutCommentQueue();
            tokenType = myRHTMLRubyLexer.getTokenType();
            if (tokenType == null) {
                break;
            }
            if (tokenType == RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML) {
                tokenType = myCurHTMLLexer.getTokenType();

                //Exit if comment has been ended
                if (tokenType == RHTMLTokenType.XML_COMMENT_END) {
                    break;
                }

                //If it is another HTML lexem it should be COMMENT_CHARACTERS
                //because we are in html comment block;
                createAndOfferToken(myCurHTMLLexer, RHTMLTokenType.XML_COMMENT_CHARACTERS);
            } else {
                //insert ruby code as is.
                createAndOfferToken(myRHTMLRubyLexer, tokenType);
            }
        } while (true);

        myState = prevState;
    }

    public IElementType getTokenType() {
        locateToken();
        return myTokenType;
    }

    public int getTokenStart() {
        locateToken();
        return myTokenStart;
    }

    public int getTokenEnd() {
        locateToken();
        return myTokenEnd;
    }

    public void advance() {
        myTokenType = null;
        if (myCommentTokensQueue.poll() == null) {
            advanceWithoutCommentQueue();
        }
    }

    private void advanceWithoutCommentQueue() {
        IElementType tokenType = myRHTMLRubyLexer.getTokenType();
        if (tokenType == RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML) {
            myCurHTMLLexer.advance();
            if (myCurHTMLLexer.getTokenType() != null) {
                return;
            }
        }
        myRHTMLRubyLexer.advance();
        setupHTMLLexer();
    }

    private void setupHTMLLexer() {
        while (true) {
            IElementType tokenType = myRHTMLRubyLexer.getTokenType();
            if (tokenType == RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML) {
                myCurHTMLLexer = myHtmlLexer;
            } else {
                return;
            }

            myCurHTMLLexer.start(myRHTMLRubyLexer.getBufferSequence(),
                                 myRHTMLRubyLexer.getTokenStart(),
                                 myRHTMLRubyLexer.getTokenEnd(),
                                 unpackHtmlState(myState));
            if (myCurHTMLLexer.getTokenType() != null) {
                return;
            }
            myRHTMLRubyLexer.advance();
        }
    }

    private static class MyState implements LexerState {
        public Queue<Token> queue;

        public int state;
        public int start;

        public MyState(final Queue<Token> queue,
                       final int state, final int start) {
            this.queue = queue;
            this.state = state;
            this.start = start;
        }

        public short intern() {
            return 0;
        }
    }

    private static class Position implements LexerPosition {
        private final LexerPosition myHTMLPosition;
        private final LexerPosition myRhtmlPosition;
        private MyState myState;

        public Position(final LexerPosition htmlPosition,
                        final LexerPosition rhtmlPosition, final MyState state) {
            myRhtmlPosition = rhtmlPosition;
            myState = state;

            myHTMLPosition = htmlPosition;
        }

        public int getOffset() {
            final int rhtmlPos = myRhtmlPosition != null ? myRhtmlPosition.getOffset() : 0;
            final int htmlPos = myHTMLPosition == null ? 0 : myHTMLPosition.getOffset();
            return Math.max(rhtmlPos, htmlPos);
        }

        public LexerPosition getHTMLPosition() {
            return myHTMLPosition;
        }

        public LexerPosition getRhtmlPosition() {
            return myRhtmlPosition;
        }

        public MyState getState() {
            return myState;
        }
    }

    public LexerPosition getCurrentPosition() {
        final LinkedList<Token> queue = new LinkedList<Token>(myCommentTokensQueue);
        final MyState state = new MyState(queue, myState,
                                          myRHTMLRubyLexer.getTokenStart());

        return new Position(myCurHTMLLexer != null ? myCurHTMLLexer.getCurrentPosition()
                                                   : null,
                            myRHTMLRubyLexer.getCurrentPosition(), state);
    }

    public void restore(final LexerPosition position) {
        final Position p = (Position)position;

        myCommentTokensQueue = p.getState().queue;
        myTokenType = null;

        myTokenStart = myTokenEnd = p.getState().start;
        myState = p.getState().state;

        myRHTMLRubyLexer.restore(p.getRhtmlPosition());
        final LexerPosition rubyPos = p.getHTMLPosition();
        if (rubyPos != null && rubyPos.getOffset() < myCurHTMLLexer.getBufferEnd()) {
            myCurHTMLLexer.restore(rubyPos);
        } else {
            myCurHTMLLexer = null;
            setupHTMLLexer();
        }
    }

    public char[] getBuffer() {
        return myRHTMLRubyLexer.getBuffer();
    }

    public int getBufferEnd() {
        return myRHTMLRubyLexer.getBufferEnd();
    }

    private static class Token {
        public final int tokenStart;
        public final int tokenEnd;
        public final int rhtmlLexerState;
        public final IElementType tokenType;

        public Token(final int rhtmlLexerState, final IElementType tokenType,
                     final int tokenStart, final int tokenEnd) {
            this.tokenEnd = tokenEnd;
            this.tokenStart = tokenStart;
            this.tokenType = tokenType;
            this.rhtmlLexerState = rhtmlLexerState;
        }
    }
}