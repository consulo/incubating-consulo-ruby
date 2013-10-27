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

package org.jetbrains.plugins.ruby.ruby.ri;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 15, 2007
 */
public class RIHelpAction extends AnAction
{
	public static final TokenSet TOKENS_TO_SEARCH = TokenSet.orSet(BNF.kRESWORDS, BNF.tCID);

	@Override
	public void actionPerformed(@NotNull final AnActionEvent e)
	{
		assert canHelp(e);

		final DataContext dataContext = e.getDataContext();
		final Editor editor = DataContextUtil.getEditor(dataContext);
		final Project project = DataContextUtil.getProject(dataContext);
		assert editor != null;
		final SelectionModel selectionModel = editor.getSelectionModel();
		final Ref<String> toSearch = new Ref<String>(selectionModel.getSelectedText());
		if(toSearch.get() == null)
		{
			final CaretModel caretModel = editor.getCaretModel();
			final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
			final HighlighterIterator iterator = highlighter.createIterator(caretModel.getOffset());
			toSearch.set(editor.getDocument().getText().substring(iterator.getStart(), iterator.getEnd()));
		}
		assert toSearch.get() != null;
		assert project != null;

		ToolWindowManager.getInstance(project).getToolWindow("RDoc").show(null);
	}

	@Override
	public void update(final AnActionEvent e)
	{
		final Presentation presentation = e.getPresentation();
		presentation.setIcon(RubyIcons.RUBY_ICON);
		// visible only on ruby files
		presentation.setVisible(DataContextUtil.getLanguage(e.getDataContext()) instanceof RubyLanguage);
		presentation.setEnabled(canHelp(e));
	}

	/**
	 * Returns true if an action can be executed
	 *
	 * @param e Current action event
	 * @return true if help is available, false otherwise
	 */
	private boolean canHelp(@NotNull final AnActionEvent e)
	{
		final DataContext dataContext = e.getDataContext();
		final Project project = DataContextUtil.getProject(dataContext);
		if(project == null)
		{
			return false;
		}

		if((DataContextUtil.getEditor(dataContext) == null))
		{
			return false;
		}


		// check if editor is opened
		final Editor editor = DataContextUtil.getEditor(dataContext);
		if(editor == null)
		{
			return false;
		}
		// check if we in ruby file
		final Language language = DataContextUtil.getLanguage(dataContext);
		if(!(language instanceof RubyLanguage))
		{
			return false;
		}

		// check if some text is selected
		final SelectionModel selectionModel = editor.getSelectionModel();
		final String selection = selectionModel.getSelectedText();
		if(selection != null)
		{
			// we can search !!!
			return true;
		}

		// check if text at carret is SEARCHABLE
		final CaretModel caretModel = editor.getCaretModel();
		final EditorHighlighter highlighter = ((EditorEx) editor).getHighlighter();
		final int offset = caretModel.getOffset();
		if(offset >= editor.getDocument().getTextLength())
		{
			return false;
		}
		final HighlighterIterator iterator = highlighter.createIterator(offset);
		return TOKENS_TO_SEARCH.contains(!iterator.atEnd() ? iterator.getTokenType() : null);
	}
}
