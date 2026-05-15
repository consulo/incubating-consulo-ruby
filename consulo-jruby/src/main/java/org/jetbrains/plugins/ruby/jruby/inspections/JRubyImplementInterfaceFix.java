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

package org.jetbrains.plugins.ruby.jruby.inspections;

import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import consulo.language.editor.inspection.LocalQuickFix;
import consulo.language.editor.inspection.ProblemDescriptor;
import consulo.localize.LocalizeValue;
import consulo.codeEditor.Editor;
import consulo.fileEditor.FileEditor;
import consulo.fileEditor.FileEditorManager;
import consulo.fileEditor.TextEditor;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import rb.implement.ImplementHandler;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 29, 2008
 */
public class JRubyImplementInterfaceFix implements LocalQuickFix
{
	protected Symbol mySymbol;
	protected PsiElement myEndElement;

	public JRubyImplementInterfaceFix(@Nonnull final PsiElement endElement, @Nonnull final Symbol symbol)
	{
		myEndElement = endElement;
		mySymbol = symbol;
	}

	@Override
	@Nonnull
	public LocalizeValue getName()
	{
		return LocalizeValue.localizeTODO("Implement methods");
	}

	@Nonnull
	public String getFamilyName()
	{
		return "JRuby";
	}

	@Override
	public void applyFix(@Nonnull final Project project, @Nonnull final ProblemDescriptor descriptor)
	{
		final ImplementHandler handler = null; //(ImplementHandler) RubyLanguage.INSTANCE.getImplementMethodsHandler();
		if(handler != null)
		{
			// Looking for editor
			Editor editor = null;
			final PsiFile file = myEndElement.getContainingFile();
			final VirtualFile virtualFile = file.getVirtualFile();
			if(virtualFile != null)
			{
				for(FileEditor fileEditor : FileEditorManager.getInstance(project).getEditors(virtualFile))
				{
					if(fileEditor instanceof TextEditor)
					{
						editor = ((TextEditor) fileEditor).getEditor();
						break;
					}
				}
			}
			handler.execute(editor, project, myEndElement, mySymbol);
		}
	}
}
