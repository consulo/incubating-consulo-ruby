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

import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorWriteActionHandler;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 29, 2006
 */
public abstract class RubyEditorHandlerUtil extends EditorWriteActionHandler
{

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean shouldHandle(final Editor editor, final DataContext dataContext)
	{
		// RUBY-1697
		if(!editor.isInsertMode())
		{
			return false;
		}
		if(DataContextUtil.getLanguage(dataContext) != RubyFileType.RUBY.getLanguage())
		{
			return false;
		}
		if(DataContextUtil.isReadOnly(editor))
		{
			return false;
		}
		if(DataContextUtil.getPsiFile(editor, dataContext) == null)
		{
			return false;
		}
		return true;
	}
}
