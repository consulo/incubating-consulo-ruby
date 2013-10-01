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

package org.jetbrains.plugins.ruby.rails.actions.rake;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.AnActionUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.ui.Messages;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 17, 2008
 */
public class ReloadRakeTasksAction extends AnAction
{
	@Override
	public void actionPerformed(@NotNull final AnActionEvent event)
	{
		final Module module = DataContextUtil.getModule(event.getDataContext());
		assert module != null;

		//Save all opened documents
		FileDocumentManager.getInstance().saveAllDocuments();

		final BaseRailsFacetConfiguration conf = RailsFacetUtil.getRailsFacetConfiguration(module);
		assert conf != null;

		if(RModuleUtil.getModuleOrJRubyFacetSdk(module) == null)
		{
			final String msg = RBundle.message("rails.facet.action.regenerate.rakeTasks.error.wrong.sdk");
			final String title = RBundle.message("action.registered.shortcut.execute.disabled.title");
			Messages.showErrorDialog(module.getProject(), msg, title);

			return;
		}
		conf.reloadRakeTasks();
	}

	@Override
	public void update(@NotNull final AnActionEvent event)
	{
		final Module module = DataContextUtil.getModule(event.getDataContext());

		// show only on RailsModuleType and valid Ruby SDK with rails installed
		final boolean isVisible = module != null && RailsFacetUtil.hasRailsSupport(module);

		AnActionUtil.updatePresentation(event.getPresentation(), isVisible, isVisible);
	}
}