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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.ide.IdeView;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 27.11.2006
 */
public abstract class AbstractScriptAction extends AnAction
{

	public AbstractScriptAction(@Nullable final String text, @Nullable final String description, @Nullable final Icon icon)
	{
		super(text, description, icon);
	}

	/**
	 * @return Caption for Generate dialog.
	 */
	protected abstract String getGenerateDialogTitle();

	/**
	 * @return Caption for error message.
	 */
	protected abstract String getErrorTitle();

	@SuppressWarnings({"UnusedParameters"})
	protected abstract void checkBeforeCreate(@NotNull final String newName, @Nullable final PsiDirectory directory) throws IncorrectOperationException;

	protected abstract String[] createScriptParameters(final String inputString, final String railsAppHomePath);

	protected abstract boolean validateBeforeInvokeDialog(final Module module);

	protected abstract PsiElement[] invokeDialog(@NotNull final Module module, @Nullable final PsiDirectory directory);

	@Override
	public void actionPerformed(final AnActionEvent e)
	{
		final DataContext dataContext = e.getDataContext();

		final IdeView view = LangDataKeys.IDE_VIEW.getData(dataContext);
		final Module module = CommonDataKeys.MODULE.getData(dataContext);
		final Sdk jdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);

		assert module != null;
		assert jdk != null;

		PsiDirectory dir = view == null ? null : view.getOrChooseDirectory();
		if(dir == null)
		{
			final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
			if(psiFile != null)
			{
				dir = psiFile.getParent();
			}
		}

		if(!validateBeforeInvokeDialog(module))
		{
			return;
		}

		final PsiElement[] createdElements = invokeDialog(module, dir);

		if(view != null)
		{
			for(PsiElement createdElement : createdElements)
			{
				view.selectElement(createdElement);
			}
		}
	}

	@Override
	public void update(@NotNull final AnActionEvent e)
	{
		final DataContext dataContext = e.getDataContext();
		final Presentation presentation = e.getPresentation();

		final Module module = CommonDataKeys.MODULE.getData(dataContext);
		boolean show = false;
		if(module != null)
		{
			show = RModuleUtil.getModuleOrJRubyFacetSdk(module) != null;
		}
		AnActionUtil.updatePresentation(presentation, show, show);
	}
}
