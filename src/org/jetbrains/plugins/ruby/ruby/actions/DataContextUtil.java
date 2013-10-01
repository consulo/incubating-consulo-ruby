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

package org.jetbrains.plugins.ruby.ruby.actions;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.lang.Language;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.08.2006
 */
public class DataContextUtil
{

	/**
	 * Returns true, if current editting File is Ruby type or if original handler is enabled
	 *
	 * @param editor          Current Editor object
	 * @param dataContext     Current DataContext object
	 * @param originalHandler Original handler for some action
	 * @return true if enabled, false otherwise
	 */
	public static boolean isEnabled(@NotNull final Editor editor, @NotNull final DataContext dataContext, @NotNull final EditorActionHandler originalHandler)
	{
		//noinspection SimplifiableIfStatement
		if(getLanguage(dataContext) == RubyFileType.RUBY.getLanguage())
		{
			return true;
		}
		return originalHandler.isEnabled(editor, dataContext);
	}

	/**
	 * Returns current editting file
	 *
	 * @param editor      Current Editor object
	 * @param dataContext Current DataContext object
	 * @return PsiFile - current file
	 */
	public static PsiFile getPsiFile(@NotNull final Editor editor, @NotNull final DataContext dataContext)
	{
		return PsiDocumentManager.getInstance(getProject(dataContext)).getPsiFile(editor.getDocument());
	}

	/**
	 * Returns current project
	 *
	 * @param dataContext Current DataContext object
	 * @return Project object - current project
	 */
	@Nullable
	public static Project getProject(@NotNull final DataContext dataContext)
	{
		return DataKeys.PROJECT.getData(dataContext);
	}

	/**
	 * Returns current module
	 *
	 * @param dataContext Current DataContext object
	 * @return Module object - current module
	 */
	@Nullable
	public static Module getModule(@NotNull final DataContext dataContext)
	{
		return DataKeys.MODULE.getData(dataContext);
	}

	/**
	 * Returns current language
	 *
	 * @param dataContext Current DataContext object
	 * @return Language object - current language
	 */
	@Nullable
	public static Language getLanguage(@NotNull final DataContext dataContext)
	{
		return DataKeys.LANGUAGE.getData(dataContext);
	}

	/**
	 * Returns current editor
	 *
	 * @param dataContext Current DataContext object
	 * @return Editor object - current editor
	 */
	@Nullable
	public static Editor getEditor(@NotNull final DataContext dataContext)
	{
		return DataKeys.EDITOR.getData(dataContext);
	}

	/**
	 * @param editor Current editor
	 * @return true if editor cannot modify opened file
	 */
	public static boolean isReadOnly(@NotNull final Editor editor)
	{
		if(editor.isViewer())
		{
			return true;
		}
		Document document = editor.getDocument();
		return !document.isWritable();
	}

}
