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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import java.util.HashSet;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.05.2007
 */
public class GeneratorOptions
{
	private final HashSet<Option> myOptions = new HashSet<Option>();

	private GeneratorOptions()
	{
	}

	public void setOption(@NotNull final Option option, boolean value)
	{
		if(value)
		{
			myOptions.add(option);
		}
		else
		{
			myOptions.remove(option);
		}
	}

	public boolean containsValue(final Option option)
	{
		return myOptions.contains(option);
	}

	public HashSet<Option> getOptions()
	{
		return myOptions;
	}

	public void clear()
	{
		myOptions.clear();
	}

	public static GeneratorOptions createEmpty()
	{
		return new GeneratorOptions();
	}

	public enum Option
	{
		PRETEND,
		FORCE,
		SKIP,
		BACK_TRACE,
		SVN,
		SVN_SHOW_CONFIRMATION
	}
}
