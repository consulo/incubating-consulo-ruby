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

package org.jetbrains.plugins.ruby.ruby.actions.editor.handlers;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.TypedActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.TokenBNF;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.08.2006
 */
public class RubyTypedHandler implements TypedActionHandler, RubyTokenTypes {

    private TypedActionHandler myOriginalHandler;

    private static final TokenSet INSERT_QUOTE_OR_BRACE_BEFORE = TokenSet.orSet(
            BNF.tBINARY_OPS,

            TokenSet.create(
                    tDOT,
                    tDOT2,
                    tDOT3,

                    tCOMMA,

                    tCOLON,
                    tCOLON2,
                    tCOLON3,

                    tQUESTION,
                    tEXCLAMATION,

                    tWHITE_SPACE,
                    tEOL,

                    tRPAREN,
                    tRBRACE,
                    tRBRACK,

                    // close expression in string
                    tSTRING_DEND
            )
    );

    public RubyTypedHandler(final TypedActionHandler originalEditorActionHandler) {
        this.myOriginalHandler = originalEditorActionHandler;
    }


    /**
     * Handles typing action
     *
     * @param editor      Current editor
     * @param charTyped   Characted typed
     * @param dataContext Current dataTodo context
     * @return true if some special action perfomed, false otherwise
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SimplifiableIfStatement"})
    private boolean handleTyping(final Editor editor, final char charTyped, final DataContext dataContext) {
        final Project project = DataContextUtil.getProject(dataContext);
        if (project == null) {
            return false;
        }

        // If we`re in autocomplete list, we should use standart lookupManager
        if (LookupManager.getInstance(project).getActiveLookup()!=null){
            return false;
        }

        if (!RubyEditorHandlerUtil.shouldHandle(editor, dataContext)) {
            return false;
        }

        String text = editor.getDocument().getText();
        final CodeInsightSettings settings = CodeInsightSettings.getInstance();

// Quote typed handling
        if (settings.AUTOINSERT_PAIR_QUOTE && TextUtil.isQuote(charTyped)) {
            return handleQuoteTyped(editor, charTyped, text);
        }

// Open brace typed handling
        if (settings.AUTOINSERT_PAIR_BRACKET && TextUtil.isOpenBrace(charTyped)) {
            return handleOpenBrace(editor, charTyped, text);
        }

// Close brace typed handling
        if (TextUtil.isCloseBrace(charTyped)) {
            return handleCloseBrace(editor, charTyped, text);
        }

// tPipe handling
        if (settings.AUTOINSERT_PAIR_BRACKET && charTyped == '|' && editor.getCaretModel().getOffset() > 0) {
          final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
          final int caret = editor.getCaretModel().getOffset();
          final HighlighterIterator iterator = highlighter.createIterator(caret);
          final boolean atPipe = iterator.getTokenType()==tPIPE;
// Look for first previous not comment or whitespace token
          IElementType type;
          do {
            iterator.retreat();
            type = iterator.getTokenType();
          } while (type!=null && BNF.tWHITESPACES_OR_COMMENTS.contains(type));

// If after kDO or {
          if (!atPipe && BNF.tCODE_BLOCK_BEG_TOKENS.contains(type)){
            EditorModificationUtil.insertStringAtCaret(editor, "||");
            editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
            return true;
          }
// Just skip pipe
          if (atPipe && !BNF.tCODE_BLOCK_BEG_TOKENS.contains(type)){
            editor.getCaretModel().moveCaretRelatively(1, 0, false, false, true);
            return true;
          }
        }

// Handle %w<carret> and typing ! etc
        EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{charTyped}));
        final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
        final HighlighterIterator iterator = highlighter.createIterator(editor.getCaretModel().getOffset()-1);
        final IElementType tokenType = !iterator.atEnd() ? iterator.getTokenType() : null;

// if are string, regexp, words or we have to close brace
        if (settings.AUTOINSERT_PAIR_BRACKET &&
                (TokenBNF.tSTRINGS_BEGINNINGS.contains(tokenType) || TokenBNF.tWORDS_BEGINNINGS.contains(tokenType))) {
            EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{charTyped == '<' ? '>' : charTyped}));
            editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
        }
        return true;
    }

    private boolean handleCloseBrace(final Editor editor, final char closeBrace, final String text) {
        final int caret = editor.getCaretModel().getOffset();
        if (caret >= text.length()) {
            return false;
        }
        TokenSet openBraceTypes = null;
        IElementType closeBraceType = null;
        if (closeBrace == ')') {
            openBraceTypes = TokenBNF.tLPARENS;
            closeBraceType = RubyTokenTypes.tRPAREN;
        }
        if (closeBrace == '}') {
            openBraceTypes = TokenBNF.tLBRACES;
            closeBraceType = RubyTokenTypes.tRBRACE;
        }
        if (closeBrace == ']') {
            openBraceTypes = TokenBNF.tLBRACKS;
            closeBraceType = RubyTokenTypes.tRBRACK;
        }
        assert openBraceTypes != null;

        // If we are in comments or strings like, we should skip
        final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
        final IElementType type = highlighter.createIterator(caret).getTokenType();
        if (TokenBNF.tCOMMENTS.contains(type) ||
            TokenBNF.tSTRING_TOKENS.contains(type) || TokenBNF.tREGEXP_TOKENS.contains(type) || TokenBNF.tWORDS_TOKENS.contains(type)){
          EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(closeBrace));
          return true;
        }

        // if you are printing corresponding symbol, just move carret.
        if (closeBrace == text.charAt(caret) && getBraceBalance(editor, openBraceTypes, closeBraceType) >= 0) {
            editor.getCaretModel().moveCaretRelatively(1, 0, false, false, true);
            return true;
        }

        return false;
    }

    private boolean handleOpenBrace(final Editor editor, final char openBrace, final String text) {
        char closeBrace = TextUtil.getCloseDelim(openBrace);

// we cannot just create iterator on empty text!!!
        if (text.length() == 0) {
            EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{openBrace, closeBrace}));
            editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
            return true;
        }

        EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{openBrace}));
        final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
        final int caret = editor.getCaretModel().getOffset();
        final HighlighterIterator iterator = highlighter.createIterator(caret-1);

        final IElementType tokenType = !iterator.atEnd() ? iterator.getTokenType(): null;
        iterator.advance();
        final IElementType nextTokenType = !iterator.atEnd() ? iterator.getTokenType(): null;

// if are string, regexp, words or we have to close brace
        if (iterator.atEnd() || INSERT_QUOTE_OR_BRACE_BEFORE.contains(nextTokenType) ||
                tSTRING_DBEG == tokenType ||
                TokenBNF.tSTRINGS_BEGINNINGS.contains(tokenType) || TokenBNF.tWORDS_BEGINNINGS.contains(tokenType)) {
            EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{closeBrace}));
            editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
            return true;
        }

// We do this to prevent standart handler
        return true;
    }

    private boolean handleQuoteTyped(final Editor editor, final char quote, final String text) {
// we cannot just create iterator on empty text!!!
        if (text.length() == 0) {
            EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{quote, quote}));
            editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
            return true;
        }

        final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
        final int caret = editor.getCaretModel().getOffset();
        final HighlighterIterator iterator = highlighter.createIterator(caret);
        final IElementType tokenType = !iterator.atEnd() ? iterator.getTokenType() : null;

// if in string, we should put only 1 quote
        if (tokenType != tSTRING_DEND && BNF.tSTRING_TOKENS.contains(tokenType)) {
// if carret is right before closing qoute, we don`t insert anything
            if (caret >= text.length() || text.charAt(caret) != quote) {
                EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(quote));
            } else {
                editor.getCaretModel().moveCaretRelatively(1, 0, false, false, true);
            }
            return true;
        } else
// we should insert 2 quotes
        if (iterator.atEnd() || INSERT_QUOTE_OR_BRACE_BEFORE.contains(tokenType)){
            EditorModificationUtil.insertStringAtCaret(editor, String.valueOf(new char[]{quote, quote}));
            editor.getCaretModel().moveCaretRelatively(-1, 0, false, false, true);
            return true;
        }
        return false;
    }


    @Override
	public void execute(final Editor editor, final char charTyped, final DataContext dataContext) {
        if (!handleTyping(editor, charTyped, dataContext) && myOriginalHandler != null) {
            myOriginalHandler.execute(editor, charTyped, dataContext);
        }
    }

    private static int getBraceBalance(@NotNull final Editor editor,
                                       final TokenSet openBraces,
                                       final IElementType closeBrace) {
        final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
        final HighlighterIterator iterator = highlighter.createIterator(0);

        int balance = 0;
        while (!iterator.atEnd()) {
            IElementType type = iterator.getTokenType();
            if (openBraces.contains(type)) {
                balance++;
            }
            if (closeBrace == type) {
                balance--;
            }
            iterator.advance();
        }
        return balance;
    }

}
