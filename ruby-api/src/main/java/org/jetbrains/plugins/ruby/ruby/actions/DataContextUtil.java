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

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.language.Language;
import consulo.language.editor.CommonDataKeys;
import consulo.language.editor.LangDataKeys;
import consulo.language.psi.PsiDocumentManager;
import consulo.language.psi.PsiFile;
import consulo.project.Project;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import consulo.dataContext.DataContext;
import consulo.document.Document;
import consulo.codeEditor.Editor;
import consulo.codeEditor.action.EditorActionHandler;
import consulo.module.Module;

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
	public static boolean isEnabled(@Nonnull final Editor editor, @Nonnull final DataContext dataContext, @Nonnull final EditorActionHandler originalHandler)
	{
		//noinspection SimplifiableIfStatement
		if(getLanguage(dataContext) == RubyFileType.INSTANCE.getLanguage())
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
	public static PsiFile getPsiFile(@Nonnull final Editor editor, @Nonnull final DataContext dataContext)
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
	public static Project getProject(@Nonnull final DataContext dataContext)
	{
		return dataContext.getData(CommonDataKeys.PROJECT);
	}

	/**
	 * Returns current module
	 *
	 * @param dataContext Current DataContext object
	 * @return Module object - current module
	 */
	@Nullable
	public static Module getModule(@Nonnull final DataContext dataContext)
	{
		return dataContext.getData(CommonDataKeys.MODULE);
	}

	/**
	 * Returns current language
	 *
	 * @param dataContext Current DataContext object
	 * @return Language object - current language
	 */
	@Nullable
	public static Language getLanguage(@Nonnull final DataContext dataContext)
	{
		return dataContext.getData(LangDataKeys.LANGUAGE);
	}

	/**
	 * Returns current editor
	 *
	 * @param dataContext Current DataContext object
	 * @return Editor object - current editor
	 */
	@Nullable
	public static Editor getEditor(@Nonnull final DataContext dataContext)
	{
		return dataContext.getData(CommonDataKeys.EDITOR);
	}

	/**
	 * @param editor Current editor
	 * @return true if editor cannot modify opened file
	 */
	public static boolean isReadOnly(@Nonnull final Editor editor)
	{
		if(editor.isViewer())
		{
			return true;
		}
		Document document = editor.getDocument();
		return !document.isWritable();
	}

}
