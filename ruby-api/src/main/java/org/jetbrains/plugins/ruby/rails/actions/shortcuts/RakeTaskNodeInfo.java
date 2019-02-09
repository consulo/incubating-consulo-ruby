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

package org.jetbrains.plugins.ruby.rails.actions.shortcuts;

import javax.annotation.Nonnull;

import consulo.ui.image.Image;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTaskSerializableImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 24.03.2007
 */
public class RakeTaskNodeInfo extends NodeInfo<RakeTaskSerializableImpl>
{

	public static String getActionId(final String fullTaskCmd)
	{
		return "rake task-" + fullTaskCmd;
	}

	public static RakeTaskNodeInfo createRootNode()
	{
		final RakeTaskSerializableImpl task = new RakeTaskSerializableImpl(RBundle.message("dialog.register.shortcut.roots.raketasks"), null, null, true, null);
		return new RakeTaskNodeInfo(RailsIcons.RAKE_TASKS_ROOT_ICON, task, true);
	}

	public static RakeTaskNodeInfo createTaskNode(@Nonnull final String name, @Nonnull final RakeTaskSerializableImpl parentTask, final boolean isGroup)
	{
		final String parentCmd = parentTask.getFullCommand();
		String fullCmd = name;
		if(parentCmd != null)
		{
			fullCmd = parentCmd + RakeTask.RAKE_COMMAND_DELIMITER + fullCmd;
		}
		final RakeTaskSerializableImpl task = new RakeTaskSerializableImpl(name, null, fullCmd, isGroup, parentTask);
		parentTask.addSubTask(task);

		if(isGroup)
		{
			return new RakeTaskNodeInfo(RailsIcons.RAILS_FOLDER_OPENED, RailsIcons.RAILS_FOLDER_CLOSED, task, true);
		}
		return new RakeTaskNodeInfo(RailsIcons.RAKE_TASK_ICON, task, false);
	}

	@Override
	public String getActionId()
	{
		return getActionId(getData().getFullCommand());
	}

	public String toString()
	{
		return getData().getId();
	}

	private RakeTaskNodeInfo(final Image icon, final RakeTaskSerializableImpl task, final boolean isGroup)
	{
		this(icon, icon, task, isGroup);
	}

	private RakeTaskNodeInfo(final Image openIcon, final Image closedIcon, final RakeTaskSerializableImpl task, final boolean isGroup)
	{
		super(openIcon, closedIcon, task, isGroup);
	}
}
