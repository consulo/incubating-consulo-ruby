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

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.AnActionUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.ruby.actions.DataContextUtil;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.module.Module;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 30.08.2006
 */
public class RakeActionGroup extends ActionGroup
{
	private final static String NAME = "Rake";
	private final RakeTask myTask;

	public RakeActionGroup()
	{
		this(null);
	}

	public RakeActionGroup(@Nullable final RakeTask task)
	{
		super(task == null ? NAME : task.getId(), true);
		myTask = task;
		if(myTask != null)
		{
			getTemplatePresentation().setIcon(RailsIcons.RAKE_GROUP_ICON);
		}
	}

	@Override
	public void update(@NotNull AnActionEvent event)
	{
		final Module module = DataContextUtil.getModule(event.getDataContext());

		// show only on module with enabled Rails support and valid Ruby SDK with rails installed
		final boolean isVisible = module != null && RailsFacetUtil.hasRailsSupport(module);
		final boolean isEnabled;
		if(isVisible)
		{
			final BaseRailsFacetConfiguration railsConf = RailsFacetUtil.getRailsFacetConfiguration(module);

			assert railsConf != null; //Can't ne null, has been already checked
			isEnabled = railsConf.getRakeTasks() != null;
		}
		else
		{
			isEnabled = false;
		}

		AnActionUtil.updatePresentation(event.getPresentation(), isVisible, isEnabled);
	}


	@Override
	public AnAction[] getChildren(@Nullable final AnActionEvent event)
	{
		if(event == null)
		{   //TODO any sense?
			return AnActionUtil.NO_ACTIONS;
		}

		final Module module = DataContextUtil.getModule(event.getDataContext());
		if(module == null)
		{
			return AnActionUtil.NO_ACTIONS;
		}

		// if we still dont` have any children
		final BaseRailsFacetConfiguration configuration = RailsFacetUtil.getRailsFacetConfiguration(module);
		assert configuration != null;

		final RakeTask rakeTask = myTask != null ? myTask : configuration.getRakeTasks();
		if(rakeTask == null)
		{
			return AnActionUtil.NO_ACTIONS;
		}

		final ArrayList<AnAction> myChildren = new ArrayList<AnAction>();
		for(RakeTask task : rakeTask.getSubTasks())
		{
			if(task.isGroup())
			{
				myChildren.add(new RakeActionGroup(task));
			}
			else
			{
				myChildren.add(new RakeAction(task));
			}
		}
		return myChildren.toArray(new AnAction[myChildren.size()]);
	}

}
