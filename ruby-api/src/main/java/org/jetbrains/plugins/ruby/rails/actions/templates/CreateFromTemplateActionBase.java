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

import consulo.dataContext.DataContext;
import consulo.fileTemplate.FileTemplate;
import consulo.fileTemplate.FileTemplateManager;
import consulo.language.editor.util.IdeView;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.project.Project;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.image.Image;
import jakarta.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public abstract class CreateFromTemplateActionBase extends AnAction
{
	public CreateFromTemplateActionBase(final String text, final String description, final Image icon)
	{
		super(text, description, icon);
	}


	@Override
	public final void actionPerformed(AnActionEvent e)
	{
		DataContext dataContext = e.getDataContext();

		IdeView view = dataContext.getData(IdeView.KEY);
		if(view == null)
		{
			return;
		}
		Project project = e.getData(Project.KEY);

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
		IdeView view = dataContext.getData(IdeView.KEY);
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
		return true;
		//return FileTemplateUtil.canCreateFromTemplate(dirs, template);
	}
}