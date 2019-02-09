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

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 29.08.2006
 */
public interface RakeTask
{
	public static final String RAKE_COMMAND_DELIMITER = ":";

	/**
	 * @return Task Identifier
	 */
	public String getId();

	/**
	 * @return Task description
	 */
	public String getDescription();

	/**
	 * @return true if current Task has children, false otherwise
	 */
	public boolean isGroup();

	/**
	 * @return Children tasks
	 */
	@Nonnull
	public List<? extends RakeTask> getSubTasks();

	/**
	 * Returns full command if current Task isn`t a group of tasks, or null
	 *
	 * @return String - fullCommand
	 */
	@Nullable
	public String getFullCommand();

	/**
	 * Adds rake task, as a child task
	 *
	 * @param task Task to add
	 */
	public void addSubTask(final RakeTask task);
}
