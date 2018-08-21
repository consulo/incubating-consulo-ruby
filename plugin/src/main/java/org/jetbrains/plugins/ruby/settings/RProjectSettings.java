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

package org.jetbrains.plugins.ruby.settings;

import static org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions.Option;

import java.util.ArrayList;
import java.util.HashSet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.annotations.Transient;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.05.2007
 */

@State(name = "RProjectSettings", storages = @Storage(StoragePathMacros.WORKSPACE_FILE))
public class RProjectSettings implements PersistentStateComponent<RProjectSettings>
{
	public ArrayList<String> myOptions = new ArrayList<String>();
	@Transient
	private GeneratorOptions generatorsOptions = GeneratorOptions.createEmpty();

	public RProjectSettings()
	{
		generatorsOptions.setOption(Option.SKIP, true);
	}

	public static RProjectSettings getInstance(@NotNull final Project project)
	{
		return ServiceManager.getService(project, RProjectSettings.class);
	}

	@Override
	public RProjectSettings getState()
	{
		myOptions.clear();
		if(generatorsOptions != null)
		{
			final HashSet<Option> set = generatorsOptions.getOptions();
			for(Option option : set)
			{
				myOptions.add(option.toString());
			}
		}
		return this;
	}

	@Override
	public void loadState(final RProjectSettings state)
	{
		generatorsOptions.clear();
		for(String option : state.myOptions)
		{
			try
			{
				generatorsOptions.setOption(Option.valueOf(option), true);
			}
			catch(IllegalArgumentException e)
			{
				//I.e. option were renamed. Do nothing
			}
		}
	}

	@NotNull
	public GeneratorOptions getGeneratorsOptions()
	{
		return generatorsOptions;
	}
}