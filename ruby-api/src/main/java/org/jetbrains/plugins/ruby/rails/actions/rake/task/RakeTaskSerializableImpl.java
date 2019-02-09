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

package org.jetbrains.plugins.ruby.rails.actions.rake.task;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.actions.rake.RakeCommand;
import org.jetbrains.plugins.ruby.rails.actions.rake.RakeUtil;
import com.intellij.util.xmlb.annotations.Transient;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 25.03.2007
 */
public class RakeTaskSerializableImpl implements RakeTask, Serializable
{
	public String fullCmd;
	public String description;
	public String id;
	public boolean group;
	public ArrayList<RakeTaskSerializableImpl> children = new ArrayList<RakeTaskSerializableImpl>();
	@Transient
	private RakeTaskSerializableImpl parent;

	public RakeTaskSerializableImpl()
	{
	}

	public RakeTaskSerializableImpl(final String id, final String description, final String fullCmd, final boolean group, @Nullable final RakeTaskSerializableImpl parent)
	{
		this.id = id;
		this.description = description;
		this.fullCmd = fullCmd;
		this.group = group;
		this.parent = parent;
	}

	@Override
	public String getId()
	{
		return id;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public boolean isGroup()
	{
		return group;
	}

	@Override
	@Nonnull
	public List<? extends RakeTask> getSubTasks()
	{
		return children;
	}

	@Override
	@Nullable
	public String getFullCommand()
	{
		return fullCmd;
	}

	@Override
	public void addSubTask(final RakeTask task)
	{
		if(task instanceof RakeTaskSerializableImpl)
		{
			children.add((RakeTaskSerializableImpl) task);
		}
	}

	public void removeFromParent()
	{
		if(parent != null)
		{
			parent.children.remove(this);
		}
	}

	/**
	 * Adds new rake command to RakeTask
	 *
	 * @param command Rake command
	 */
	public void registerNewCommand(final RakeCommand command)
	{
		final String[] ids = command.getCommand().split(RAKE_COMMAND_DELIMITER);
		RakeTaskSerializableImpl parent = this;
		for(int i = 0; i < ids.length; i++)
		{
			final boolean isGroup = (i < ids.length - 1);
			RakeTaskSerializableImpl task = (RakeTaskSerializableImpl) RakeUtil.findSubTaskById(ids[i], isGroup, parent);
			if(task == null)
			{
				task = new RakeTaskSerializableImpl(ids[i], isGroup ? null : command.getDescription(), null, isGroup, parent);
				parent.addSubTask(task);
			}
			parent = task;
		}
		parent.setFullCommand(command.getCommand());
	}

	/**
	 * Sets the full command for Task
	 *
	 * @param command The comamnd to set up
	 */
	public void setFullCommand(final String command)
	{
		fullCmd = command;
	}

	public void setParent(final RakeTaskSerializableImpl parent)
	{
		this.parent = parent;
	}
}