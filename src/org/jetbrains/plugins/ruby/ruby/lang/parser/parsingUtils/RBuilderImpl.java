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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.stringLike.Heredoc;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyElementType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 28.05.2006
 */
public class RBuilderImpl implements RubyTokenTypes, RBuilder {
    // builder used in parsing
    private PsiBuilder myBuilder;

    // Next token of myBuilder
    private IElementType myNextToken = null;
    // Next Not EOL token of myBuilder
    private IElementType myNextNotEolToken = null;

    private boolean isInHeredoc = false;

    private final long startTime;

    private final HashMap<TokenSet, String> myErrorCache = new HashMap<TokenSet, String>();

    public RBuilderImpl(final PsiBuilder psiBuilder) {
        startTime = System.currentTimeMillis();
        myBuilder = psiBuilder;
        initNextTokens();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// Engine core functions //////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void advanceLexer() {
        myBuilder.getTokenType();
        myBuilder.advanceLexer();
        initNextTokens();
    }

    public void error(@NotNull final String error) {
        myBuilder.getTokenType();
        myBuilder.error(error);
    }

    public RMarker mark() {
        return mark(true);
    }


    public RMarker mark(boolean passWhiteSpacesAndComments) {
        if (passWhiteSpacesAndComments) {
            myBuilder.getTokenType();
        }
        return new RMarkerImpl(this, myBuilder.mark());
    }

    @Nullable
    public IElementType getTokenType() {
        return myNextToken;
    }

    @Nullable
    public IElementType getNotEolTokenType() {
        return myNextNotEolToken;
    }

    public boolean eof() {
        return myNextToken == null;
    }

    public boolean eofIgnoreEOL() {
        return myNextNotEolToken == null;
    }


    public void initNextTokens() {
        final PsiBuilder.Marker rollBackMarker = myBuilder.mark();
        myNextToken = myBuilder.getTokenType();
        while (myBuilder.getTokenType() == tEOL) {
            myBuilder.advanceLexer();
        }
        myNextNotEolToken = myBuilder.getTokenType();
        rollBackMarker.rollbackTo();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////// Compare Token //////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean compare(final IElementType type) {
        boolean found = type == myNextToken;
        if (found) {
// move to the token
            myBuilder.getTokenType();
        }
        return found;
    }

    public boolean compare(final TokenSet types) {
        boolean found = types.contains(myNextToken);
        if (found) {
// move to the token
            myBuilder.getTokenType();
        }
        return found;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////// CompareToken with ignore tokens/////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean compareIgnoreEOL(final IElementType type) {
        boolean found = type == myNextNotEolToken;
        if (found) {
// move to the token
            passEOLs();
            myBuilder.getTokenType();
        }
        return found;
    }

    public boolean compareIgnoreEOL(final TokenSet types) {
        boolean found = types.contains(myNextNotEolToken);
        if (found) {
// move to the token
            passEOLs();
            myBuilder.getTokenType();
        }
        return found;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//////// Check and Eat /////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public boolean compareAndEat(final IElementType type) {
        boolean found = compare(type);
        if (found) {
            advanceLexer();
        }
        return found;
    }

    public boolean compareAndEat(final TokenSet types) {
        boolean found = compare(types);
        if (found) {
            advanceLexer();
        }
        return found;
    }

    public boolean compareAndEatIgnoreEOL(final IElementType type) {
        boolean found = compareIgnoreEOL(type);
        if (found) {
            advanceLexer();
        }
        return found;
    }

    public boolean compareAndEatIgnoreEOL(final TokenSet types) {
        boolean found = compareIgnoreEOL(types);
        if (found) {
            advanceLexer();
        }
        return found;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////// Check Matches //////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void match(final IElementType token) {
        match(token, ErrorMsg.expected(token));
    }

    public void match(final IElementType token, final String errorMessage) {
        if (!compareAndEat(token)) {
            error(errorMessage);
        }
    }

    public void match(final TokenSet tokens) {
        match(tokens, ErrorMsg.expected(tokens, this));
    }

    public void match(final TokenSet tokens, final String errorMessage) {
        if (!compareAndEat(tokens)) {
            error(errorMessage);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////// checkMatches ignoring EOL ////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void matchIgnoreEOL(final IElementType token) {
        matchIgnoreEOL(token, ErrorMsg.expected(token));
    }

    public void matchIgnoreEOL(final IElementType token, final String errorMessage) {
        passHeredocs();
        boolean found = compareAndEatIgnoreEOL(token);
        if (!found) {
            error(errorMessage);
        }
    }

    public void matchIgnoreEOL(final TokenSet tokens) {
        matchIgnoreEOL(tokens, ErrorMsg.expected(tokens, this));
    }

    public void matchIgnoreEOL(final TokenSet tokens, final String errorMessage) {
        passHeredocs();
        boolean found = compareAndEatIgnoreEOL(tokens);
        if (!found) {
            error(errorMessage);
        }
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////// Pass Tokens //////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    public boolean passHeredocs() {
        if (!isInHeredoc && BNF.tHEREDOC_VALUE_BEGINNINGS.contains(myNextNotEolToken)){
            isInHeredoc = true;
            passEOLs();
            Heredoc.parse(this);
            isInHeredoc = false;
            initNextTokens();
            return true;
        }
        return false;
    }

    public boolean passEOLs() {
        boolean seen = false;
        while (compareAndEat(tEOL)) {
            seen = true;
        }
        return seen;
    }

    public void passJunks() {
        myBuilder.getTokenType();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
///////// Parse Single Token ///////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public IElementType parseSingleToken(final IElementType type, final IElementType statementType) {
        return parseSingleToken(type, statementType, ErrorMsg.expected(type));
    }

    public IElementType parseSingleToken(final IElementType type, final IElementType statementType, final String errorMessage) {
        final RMarker statementMarker = mark();
        match(type, errorMessage);
        statementMarker.done(statementType);
        return statementType;
    }

    public IElementType parseSingleToken(final TokenSet types, final IElementType statementType) {
        return parseSingleToken(types, statementType, ErrorMsg.expected(types, this));
    }

    public IElementType parseSingleToken(final TokenSet types, final IElementType statementType, final String errorMessage) {
        final RMarker statementMarker = mark();
        match(types, errorMessage);
        statementMarker.done(statementType);
        return statementType;
    }

    public ASTNode getTreeBuilt() {
        return myBuilder.getTreeBuilt();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//// DEBUG /////////////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private int STMT = 0;
    private int EXPR = 0;
    private int ARG = 0;
    private int PRIMARY = 0;

    public boolean isDEBUG() {
        return Logger.getInstance(RBuilderImpl.class.getName()).isDebugEnabled();
    }

    public void STMT() {
        STMT++;
    }

    public void EXPR() {
        EXPR++;
    }

    public void ARG() {
        ARG++;
    }

    public void PRIMARY() {
        PRIMARY++;
    }

    public void printDebugStats() {
        System.err.println("TIME " + (System.currentTimeMillis() - startTime) + "\n");
        System.err.println("STMT " + STMT);
        System.err.println("EXPR " + EXPR);
        System.err.println("ARG " + ARG);
        System.err.println("PRIMARY " + PRIMARY);
    }

    public boolean isAcceptibleErrorToken(IElementType myToken) {
        return myToken instanceof RubyElementType;
    }

    public Map<TokenSet, String> getErrorCache() {
        return myErrorCache;
    }
}
