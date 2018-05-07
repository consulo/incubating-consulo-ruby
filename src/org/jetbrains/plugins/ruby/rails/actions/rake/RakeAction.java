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
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.AnActionUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;
import consulo.awt.TargetAWT;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 30.08.2006
 */
class RakeAction extends AnAction
{
	final private RakeTask myTask;

	public RakeAction(@NotNull final RakeTask task)
	{
		super(task.getId(), task.getDescription(), TargetAWT.to(RailsIcons.RAKE_TASK_ICON));
		myTask = task;
	}

	@Override
	public void actionPerformed(final AnActionEvent e)
	{
		RakeUtil.runRakeTask(e.getDataContext(), myTask);
	}


	@Override
	public void update(final AnActionEvent e)
	{
		final Module module = DataContextUtil.getModule(e.getDataContext());
		final boolean show = RubySdkUtil.isKindOfRubySDK(RModuleUtil.getModuleOrJRubyFacetSdk(module));
		AnActionUtil.updatePresentation(e.getPresentation(), true, show);
	}
}
