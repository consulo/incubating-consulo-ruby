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

package org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.editorHandlers;

import com.intellij.codeInsight.CodeInsightSettings;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.RubyEditorHandlerUtil;
import org.jetbrains.plugins.ruby.ruby.lang.braceMatcher.RubyPairedBraceMatcher;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.HeredocsManager;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.08.2006
 */
public class RubyEnterHandler extends EditorWriteActionHandler implements RubyTokenTypes{

    private static final IElementType[] TOKENS_TO_INDENT = new IElementType[]{
            RubyTokenTypes.kEND,
            RubyTokenTypes.kELSE,
            RubyTokenTypes.kELSIF,
            RubyTokenTypes.kENSURE,
            RubyTokenTypes.kRESCUE,
            RubyTokenTypes.kWHEN
    };

    private static final TokenSet STRING_LIKE_TOKENS = TokenSet.orSet(
            BNF.tSTRING_LIKE_CONTENTS,
            BNF.tEXPR_SUBT_TOKENS,
            BNF.tESCAPE_SEQUENCES
    );

    private static final TokenSet NO_INDENT_AFTER = TokenSet.orSet(
        STRING_LIKE_TOKENS,
        TokenSet.create(
                tBLOCK_COMMENT_BEGIN,
                tBLOCK_COMMENT_CONTENT
        )
    );

    private EditorActionHandler myOriginalHandler;

    private static final String FAKE_WORD = "foo";
    private static final String NEWLINE = "\n";
    private static final String EMPTY_PATTERN = " *";
    private static final String NEWLINEx2 = "\n\n";

    public RubyEnterHandler(EditorActionHandler originalHandler) {
        myOriginalHandler = originalHandler;
    }

    public void executeWriteAction(Editor editor, DataContext dataContext) {
        if (!handleEnter(editor, dataContext) && myOriginalHandler != null) {
            if (myOriginalHandler.isEnabled(editor, dataContext)){
                myOriginalHandler.execute(editor, dataContext);
            }
        }
    }

    /**
     * Handles enter action
     *
     * @param editor      Current editor
     * @param dataContext Current dataTodo context
     * @return true if some special action perfomed, false otherwise
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    private boolean handleEnter(@NotNull final Editor editor, @NotNull final DataContext dataContext) {
        final Project project = DataContextUtil.getProject(dataContext);
        if (project == null){
            return false;
        }
        if (!RubyEditorHandlerUtil.shouldHandle(editor, dataContext)) {
            return false;
        }

        PsiDocumentManager.getInstance(project).commitDocument(editor.getDocument());

        int carret = editor.getCaretModel().getOffset();
        if (carret == 0) return false;

        final Document document = editor.getDocument();
        final int lineNumber = document.getLineNumber(carret);
        final CodeInsightSettings settings = CodeInsightSettings.getInstance();

        final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
        final HighlighterIterator iterator = highlighter.createIterator(carret-1);
        final IElementType type = !iterator.atEnd() ? iterator.getTokenType() : null;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// Enter in line comment //////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (type == TLINE_COMMENT){
            final String restString = editor.getDocument().getCharsSequence().
                    subSequence(carret, document.getLineEndOffset(lineNumber)).toString();
            if (!restString.matches(EMPTY_PATTERN)){
                EditorModificationUtil.insertStringAtCaret(editor, NEWLINE + " #");
                indentLineAndMoveCarret(project, editor, document, lineNumber+1);
                editor.getCaretModel().moveCaretRelatively(1, 0, false, false, true);
                return true;
            }
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// No indent next line after in strings, block comments etc ///////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (NO_INDENT_AFTER.contains(type)){
            EditorModificationUtil.insertStringAtCaret(editor, NEWLINE);
            return true;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// Automatical end insert if needed ///////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (settings.SMART_END_ACTION && !BNF.tDOT_OR_COLON.contains(type) && isEndNeeded(editor, carret)){
            final String toInsert = NEWLINEx2 + RubyTokenTypes.kEND.toString();
            EditorModificationUtil.insertStringAtCaret(editor, toInsert);
            indentLine(project, document, lineNumber+2);
            indentLineAndMoveCarret(project, editor, document,lineNumber+1);
            return true;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// Continuation indent after comma, binary_ops, etc ///////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        if (BNF.tCONTINUATION_INDENT.contains(type)){
            EditorModificationUtil.insertStringAtCaret(editor, NEWLINE + FAKE_WORD);
            final int startOffset = indentLineAndMoveCarret(project, editor, document, lineNumber+1);
            document.deleteString(startOffset, startOffset + FAKE_WORD.length());
            return true;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// Automatical closing heredocs if needed /////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        String heredocId;
        if ((heredocId = isHeredocNeeded(editor, carret))!=null){
            final String toInsert = NEWLINEx2 + HeredocsManager.getName(heredocId);
            EditorModificationUtil.insertStringAtCaret(editor, toInsert);
            if (HeredocsManager.isIndented(heredocId)){
                indentLine(project, document, lineNumber+2);
            }
            editor.getCaretModel().moveToOffset(document.getLineStartOffset(lineNumber+1));
            return true;
        }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////// Automatical indent lines, beginning with tokens to indent ////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final String text = document.getText().substring(lineStartOffset, carret).trim();
        for (IElementType t : TOKENS_TO_INDENT) {
            if (text.startsWith(t.toString())) {
                indentLine(project, document, lineNumber);
            }
        }

// Default
        EditorModificationUtil.insertStringAtCaret(editor, NEWLINE);
        indentLineAndMoveCarret(project, editor, document, lineNumber+1);

        return true;
    }

    /**
     * Indents line in editor
     * @param project Current project
     * @param document Current document
     * @param lineNumber Line number to indent
     * @return offset of indented line, or -1 if line with such number doesn`t exists
     */
    private int indentLine(@NotNull final Project project, @NotNull final Document document, final int lineNumber) {
        if (!(0 <= lineNumber && lineNumber < document.getLineCount())){
            return -1;
        }
        return CodeStyleManager.getInstance(project).adjustLineIndent(document, document.getLineStartOffset(lineNumber));
    }

    private int indentLineAndMoveCarret(@NotNull final Project project, @NotNull final Editor editor, @NotNull final Document document, final int lineNumber) {
        int offset = indentLine(project, document, lineNumber);
        if (offset != -1){
            editor.getCaretModel().moveToOffset(offset);
        }
        return offset;
    }


    public boolean isEnabled(Editor editor, DataContext dataContext) {
        return DataContextUtil.isEnabled(editor, dataContext, myOriginalHandler);
    }

    /**
     * Counts end balance in the whole file
     * @param editor Current editor
     * @param offset Current offset
     * @return true is balance>0, i.e. end needed, false otherwise
     */
    private static boolean isEndNeeded(@NotNull final Editor editor, final int offset) {
        if (offset == 0) return false;
        final Document document = editor.getDocument();
        final int lineNumber = document.getLineNumber(offset);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
        HighlighterIterator iterator = highlighter.createIterator(lineStartOffset);

        boolean endNeededTokenSeenInLine = false;
        while (!endNeededTokenSeenInLine && !iterator.atEnd() && iterator.getEnd()<=offset){
            if (RubyPairedBraceMatcher.kEND_BRACE_TOKENS.contains(!iterator.atEnd() ? iterator.getTokenType() : null)){
                endNeededTokenSeenInLine = true;
            }
            iterator.advance();
        }
// if no end needed token seen in line upto carret, we shouldn`t insert any 'end'
        if (!endNeededTokenSeenInLine){
            return false;
        }

        iterator = highlighter.createIterator(0);

        int balance = 0;
        while (!iterator.atEnd()) {
            IElementType token = iterator.getTokenType();
            if (RubyPairedBraceMatcher.kEND_BRACE_TOKENS.contains(token)){
                balance++;
            }
            if (token == kEND){
                balance--;
            }
            iterator.advance();
        }
        return balance > 0;
    }

    private static String isHeredocNeeded(@NotNull final Editor editor, final int offset) {
        if (offset == 0) return null;
        final Document document = editor.getDocument();
        final int lineNumber = document.getLineNumber(offset);
        final int lineStartOffset = document.getLineStartOffset(lineNumber);
        final EditorHighlighter highlighter = ((EditorEx)editor).getHighlighter();
        HighlighterIterator iterator = highlighter.createIterator(lineStartOffset);

        String heredocId = null;
        while (heredocId==null && !iterator.atEnd() && iterator.getEnd()<=offset){
            if (iterator.getTokenType() == tHEREDOC_ID){
                heredocId = document.getText().substring(iterator.getStart(), iterator.getEnd());
            }
            iterator.advance();
        }

// no heredocId found in string
        if (heredocId == null){
            return null;
        }

        iterator = highlighter.createIterator(0);

        int balance = 0;

        while (!iterator.atEnd()) {
            IElementType token = iterator.getTokenType();
            if (token == tHEREDOC_ID){
                 balance++;
            }
            if (BNF.tHEREDOC_ENDS.contains(token)){
                balance--;
            }
            iterator.advance();
        }
        return balance > 0 ? heredocId : null;
    }

}
