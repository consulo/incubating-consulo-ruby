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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion;

import com.intellij.codeInsight.completion.BasicInsertHandler;
import com.intellij.codeInsight.completion.CompletionContext;
import com.intellij.codeInsight.completion.LookupData;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.psi.PsiDocumentManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.05.2007
 */
public class RHTMLTagInsertHandler extends BasicInsertHandler {
    private void insertRubyCodeCloseTag(final Editor editor, char completionChar) {
        // completionChar: '-', '=', '#'
        final String separatorStr = (completionChar == ' ' || completionChar == '\n'
            ? ""
            : completionChar) + "  " + RHTMLCompletionData.RHTML_INJECTION_CLOSE;
        EditorModificationUtil.insertStringAtCaret(editor, separatorStr);
        PsiDocumentManager.getInstance(editor.getProject()).commitDocument(editor.getDocument());
        editor.getCaretModel().moveCaretRelatively(-3, 0, false, false, true);
    }

    public void handleInsert(final CompletionContext context,
                             final int startOffset,
                             final LookupData data,
                             final LookupItem item,
                             final boolean signatureSelected,
                             final char completionChar) {

        super.handleInsert(context, startOffset, data, item, signatureSelected, completionChar);
        insertRubyCodeCloseTag(context.editor, completionChar);
    }
}