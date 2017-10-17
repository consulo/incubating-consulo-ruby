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

package org.jetbrains.plugins.ruby.rails.actions.templates;

import javax.swing.Icon;

import org.jetbrains.annotations.Nullable;
import com.intellij.ide.IdeView;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.fileTemplates.FileTemplateUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public abstract class CreateFromTemplateActionBase extends AnAction
{

	public CreateFromTemplateActionBase(final String text, final String description, final Icon icon)
	{
		super(text, description, icon);
	}


	@Override
	public final void actionPerformed(AnActionEvent e)
	{
		DataContext dataContext = e.getDataContext();

		IdeView view = dataContext.getData(LangDataKeys.IDE_VIEW);
		if(view == null)
		{
			return;
		}
		Project project = e.getProject();

		PsiDirectory dir = null;//PackageUtil.getOrChooseDirectory(view);
		if(dir == null)
		{
			return;
		}

		FileTemplate selectedTemplate = getTemplate(project, dir);
		if(selectedTemplate != null)
		{
			AnAction action = getReplacedAction(selectedTemplate);
			if(action != null)
			{
				action.actionPerformed(e);
			}
			else
			{
				FileTemplateManager.getInstance().addRecentName(selectedTemplate.getName());
				PsiElement createdElement = invokeDialogAndCreate(project, dir, selectedTemplate);
				if(createdElement != null)
				{
					view.selectElement(createdElement);
				}
			}
		}
	}

	protected abstract PsiElement invokeDialogAndCreate(final Project project, final PsiDirectory dir, final FileTemplate selectedTemplate);

	@SuppressWarnings({"UnusedParameters"})
	@Nullable
	protected abstract AnAction getReplacedAction(final FileTemplate template);

	@SuppressWarnings({"UnusedParameters"})
	protected abstract FileTemplate getTemplate(final Project project, final PsiDirectory dir);

	protected boolean canCreateFromTemplate(final AnActionEvent e, final FileTemplate template)
	{
		if(e == null)
		{
			return false;
		}
		final DataContext dataContext = e.getDataContext();
		IdeView view = dataContext.getData(LangDataKeys.IDE_VIEW);
		if(view == null)
		{
			return false;
		}

		PsiDirectory[] dirs = view.getDirectories();
		//noinspection SimplifiableIfStatement
		if(dirs.length == 0)
		{
			return false;
		}
		return FileTemplateUtil.canCreateFromTemplate(dirs, template);
	}
}