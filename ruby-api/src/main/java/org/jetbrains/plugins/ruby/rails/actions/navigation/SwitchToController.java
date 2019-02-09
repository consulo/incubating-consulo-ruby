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

package org.jetbrains.plugins.ruby.rails.actions.navigation;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.actionSystem.EditorAction;
import com.intellij.openapi.editor.actionSystem.EditorActionHandler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.intellij.ui.awt.RelativePoint;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 4, 2007
 */
public class SwitchToController extends EditorAction
{
	private static final String CANT_NAVIGATE = RBundle.message("codeInsight.rails.switch.to.controller.cant.navigate");
	private static final String SWITCH_TO_CONTROLLER_TITLE = RBundle.message("codeInsight.rails.switch.to.controller.title");

	public SwitchToController()
	{
		super(new Handler());

		final Presentation presentation = getTemplatePresentation();
		presentation.setText(SWITCH_TO_CONTROLLER_TITLE);
		presentation.setDescription(RBundle.message("codeInsight.rails.switch.to.controller.description"));
		presentation.setIcon(RailsIcons.RAILS_VIEW_TO_CONTROLLER_MARKER);
	}

	protected static class Handler extends EditorActionHandler
	{
		@Override
		public void execute(Editor editor, DataContext dataContext)
		{

			// must be in rails module
			final Module module = dataContext.getData(CommonDataKeys.MODULE);
			if(module == null || !RailsFacetUtil.hasRailsSupport(module))
			{
				cantNavigate();
				return;
			}

			PsiFile psiFile = dataContext.getData(CommonDataKeys.PSI_FILE);
			if(psiFile == null)
			{
				cantNavigate();
				return;
			}

			// must be in view file
			final VirtualFile file = psiFile.getVirtualFile();
			if(file == null || !ViewsConventions.isValidViewFileName(file.getName()) || file.getParent() == null)
			{
				cantNavigate();
				return;
			}

			final RClass rClass = ControllersConventions.getControllerByViewFile(psiFile, module);
			if(rClass == null)
			{
				cantNavigate();
				return;
			}

			if(!isSwitchToControllerEnabled(psiFile, module))
			{
				cantNavigate();
				return;
			}

			final RelativePoint relativePoint = JBPopupFactory.getInstance().guessBestPopupLocation(dataContext);
			if(relativePoint == null)
			{
				cantNavigate();
				return;
			}

			switchToController(module, psiFile);
		}

		private void cantNavigate()
		{
			Messages.showInfoMessage(CANT_NAVIGATE, SWITCH_TO_CONTROLLER_TITLE);
		}

	}

	@SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
	public static boolean isSwitchToControllerEnabled(@Nonnull final PsiFile file, @Nonnull final Module module)
	{
		return ViewsConventions.isPartialViewName(file.getName()) && ControllersConventions.getControllerByViewFile(file, module) != null;
	}

	public static void switchToController(final Module module, final PsiFile file)
	{
		final RClass rClass = ControllersConventions.getControllerByViewFile(file, module);
		assert rClass != null;

		if(rClass instanceof Navigatable)
		{
			((Navigatable) rClass).navigate(true);
		}
		else
		{
			assert false;
		}
	}
}