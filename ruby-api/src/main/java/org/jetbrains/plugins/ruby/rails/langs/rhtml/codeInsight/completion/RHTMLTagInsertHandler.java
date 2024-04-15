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

import consulo.codeEditor.Editor;
import consulo.codeEditor.util.EditorModificationUtil;
import consulo.language.editor.completion.lookup.InsertHandler;
import consulo.language.editor.completion.lookup.InsertionContext;
import consulo.language.editor.completion.lookup.LookupElement;
import consulo.language.psi.PsiDocumentManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.05.2007
 */
public class RHTMLTagInsertHandler implements InsertHandler<LookupElement>
{
	private void insertRubyCodeCloseTag(final Editor editor, char completionChar)
	{
		// completionChar: '-', '=', '#'
		final String separatorStr = (completionChar == ' ' || completionChar == '\n' ? "" : completionChar) + "  " + RHTMLCompletionData.RHTML_INJECTION_CLOSE;
		EditorModificationUtil.insertStringAtCaret(editor, separatorStr);
		PsiDocumentManager.getInstance(editor.getProject()).commitDocument(editor.getDocument());
		editor.getCaretModel().moveCaretRelatively(-3, 0, false, false, true);
	}

	@Override
	public void handleInsert(InsertionContext context, LookupElement item)
	{
		insertRubyCodeCloseTag(context.getEditor(), context.getCompletionChar());
	}
}