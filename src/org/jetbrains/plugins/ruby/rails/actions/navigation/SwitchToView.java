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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.codeInsight.daemon.RailsLineMarkerNavigator;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import com.intellij.ide.util.gotoByName.GotoFileCellRenderer;
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
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.awt.RelativePoint;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 4, 2007
 */
public class SwitchToView extends EditorAction
{
	private static final String CANT_NAVIGATE = RBundle.message("codeInsight.rails.switch.to.view.cant.navigate");
	private static final String SWITCH_TO_VIEW_TITLE = RBundle.message("codeInsight.rails.switch.to.view.title");

	public SwitchToView()
	{
		super(new Handler());

		final Presentation presentation = getTemplatePresentation();
		presentation.setText(RBundle.message("codeInsight.rails.switch.to.view.title"));
		presentation.setDescription(RBundle.message("codeInsight.rails.switch.to.view.description"));
		presentation.setIcon(RailsIcons.RAILS_ACTION_TO_VIEW_MARKER);
	}

	protected static class Handler extends EditorActionHandler
	{
		@Override
		public void execute(Editor editor, DataContext dataContext)
		{
			final Module module = CommonDataKeys.MODULE.getData(dataContext);
			if(module == null || !RailsFacetUtil.hasRailsSupport(module))
			{
				cantNavigate();
				return;
			}

			final PsiFile psiFile = CommonDataKeys.PSI_FILE.getData(dataContext);
			if(psiFile == null)
			{
				cantNavigate();
				return;
			}

			final int offset = editor.getCaretModel().getOffset();
			final PsiElement psiElement = psiFile.findElementAt(offset);
			if(psiElement == null)
			{
				cantNavigate();
				return;
			}

			// must be in ruby file
			final VirtualFile file = psiFile.getVirtualFile();
			if(file == null || !RubyVirtualFileScanner.isRubyFile(file) || file.getParent() == null)
			{
				cantNavigate();
				return;
			}

			final RMethod rMethod = RubyPsiUtil.getContainingRMethod(psiElement);
			final RClass rClass = RubyPsiUtil.getContainingRClass(psiElement);
			if(rMethod == null && rClass == null)
			{
				cantNavigate();
				return;
			}

			if(!isSwitchToViewEnabled(rMethod, module) && !isSwitchToViewEnabled(rClass, module))
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

			if(rMethod != null)
			{
				switchToView(module, rMethod, relativePoint);
			}
			else
			{
				switchToView(module, rClass, relativePoint);
			}
		}

		private void cantNavigate()
		{
			Messages.showInfoMessage(CANT_NAVIGATE, SWITCH_TO_VIEW_TITLE);
		}
	}

	public static void switchToView(final Module module, final RContainer container, final RelativePoint relativePoint)
	{

		final int maxSize = WindowManagerEx.getInstanceEx().getFrame(module.getProject()).getSize().width;
		if(container instanceof RMethod)
		{
			final RMethod method = (RMethod) container;
			final RClass rClass = RubyPsiUtil.getContainingRClassByContainer(method);
			assert rClass != null;

			final String controllerDirUrl = getRClassDirectoryUrl(rClass);
			final String controllerName = ControllersConventions.getControllerNameByClassName(rClass);
			if(controllerDirUrl == null || controllerName == null)
			{
				return;
			}

			final List<VirtualFile> fileList = ViewsConventions.getViews(method, controllerDirUrl, controllerName, module);
			final PsiFile[] psiFiles = getPsiFiles(module, fileList);
			RailsLineMarkerNavigator.openTargets(psiFiles, RBundle.message("codeInsight.rails.navigation.title.action.view", method.getName()), new GotoFileCellRenderer(maxSize), relativePoint);
		}
		else if(container instanceof RClass)
		{
			final RClass rClass = (RClass) container;
			final String controllerDirUrl = getRClassDirectoryUrl(rClass);
			final String controllerName = ControllersConventions.getControllerNameByClassName(rClass);
			if(controllerDirUrl == null || controllerName == null)
			{
				return;
			}

			final List<VirtualFile> fileList = ViewsConventions.getPartialViews(controllerDirUrl, controllerName, module);

			final PsiFile[] psiFiles = getPsiFiles(module, fileList);
			RailsLineMarkerNavigator.openTargets(psiFiles, RBundle.message("codeInsight.rails.navigation.title.action.partial.view", rClass.getName()), new GotoFileCellRenderer(maxSize), relativePoint);
		}
	}

	private static PsiFile[] getPsiFiles(Module module, List<VirtualFile> fileList)
	{
		final VirtualFile[] files = fileList.toArray(new VirtualFile[fileList.size()]);
		PsiFile[] psiFiles = new PsiFile[files.length];
		for(int i = 0; i < files.length; i++)
		{
			psiFiles[i] = PsiManager.getInstance(module.getProject()).findFile(files[i]);
		}
		return psiFiles;
	}

	private static String getRClassDirectoryUrl(@NotNull final RClass rClass)
	{
		final VirtualFile file = rClass.getContainingFile().getVirtualFile();
		assert file != null;

		final VirtualFile parentDir = file.getParent();
		assert parentDir != null;

		return parentDir.getUrl();
	}

	public static boolean isSwitchToViewEnabled(@Nullable final RContainer methodOrClass, @NotNull final Module module)
	{
		if(methodOrClass instanceof RMethod)
		{
			final RMethod method = (RMethod) methodOrClass;
			if(!ControllersConventions.isValidActionMethod(method))
			{
				return false;
			}

			//All actions are public methods!
			if(method.getAccessModifier() != AccessModifier.PUBLIC)
			{
				return false;
			}

			final String name = method.getName();
			// Action is method of Controller class
			final RVirtualContainer parentContainer = method.getParentContainer();
			if(parentContainer == null || !(parentContainer instanceof RClass))
			{
				return false;
			}

			final VirtualFile viewsFolder = ViewsConventions.getViewsFolderByClass(module, (RClass) parentContainer);

			return viewsFolder != null && ViewsConventions.containsViewsByViewsFolder(viewsFolder, name);

		}
		else if(methodOrClass instanceof RClass)
		{
			final VirtualFile viewsFolder = ViewsConventions.getViewsFolderByClass(module, (RClass) methodOrClass);
			return viewsFolder != null && !ViewsConventions.findPartialViews(viewsFolder).isEmpty();
		}
		return false;
	}
}
