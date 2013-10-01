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

import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.actions.editor.handlers.RubyEditorHandlerUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;
import com.intellij.openapi.project.Project;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.08.2006
 */
public class RubyBackspaceHandler extends EditorWriteActionHandler
{
	private EditorActionHandler myOriginalHandler;

	public RubyBackspaceHandler(EditorActionHandler originalHandler)
	{
		myOriginalHandler = originalHandler;
	}

	@Override
	public void executeWriteAction(Editor editor, DataContext dataContext)
	{
		if(!handleBackspace(editor, dataContext) && myOriginalHandler != null)
		{
			if(myOriginalHandler.isEnabled(editor, dataContext))
			{
				myOriginalHandler.execute(editor, dataContext);
			}
		}
	}

	/**
	 * Handles backspace action
	 *
	 * @param editor      Current editor
	 * @param dataContext Current dataTodo context
	 * @return true if some special action perfomed, false otherwise
	 */
	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	private boolean handleBackspace(Editor editor, DataContext dataContext)
	{
		Project project = DataContextUtil.getProject(dataContext);
		if(project == null)
		{
			return false;
		}
		if(!RubyEditorHandlerUtil.shouldHandle(editor, dataContext))
		{
			return false;
		}

		int carret = editor.getCaretModel().getOffset();
		if(carret == 0)
		{
			return false;
		}

		String text = editor.getDocument().getText();
		char c = text.charAt(carret - 1);
		char correspondChar = TextUtil.getCorrespondingChar(c);

		if(correspondChar != (char) -1)
		{
			if(carret != text.length() && text.charAt(carret) == correspondChar)
			{
				editor.getSelectionModel().setSelection(carret - 1, carret + 1);
				EditorModificationUtil.deleteSelectedText(editor);
				return true;
			}
		}

		return false;
	}


	@Override
	public boolean isEnabled(Editor editor, DataContext dataContext)
	{
		return DataContextUtil.isEnabled(editor, dataContext, myOriginalHandler);
	}

}
