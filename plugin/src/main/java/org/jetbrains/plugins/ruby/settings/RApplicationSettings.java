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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.annotations.Transient;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: Oct 11, 2007
 */
@State(name = "RApplicationSettings", storages = @Storage("ruby.xml"))
public class RApplicationSettings implements PersistentStateComponent<RApplicationSettings>
{
	//Output console
	public boolean useConsoleOutputRubyStacktraceFilter = true;
	public boolean useConsoleOutputOtherFilters = true;
	public boolean useConsoleColorMode = true;

	// Project view
	public boolean useRubySpecificProjectView = false;

	//Other params
	@Transient
	private boolean myJsSupportEnabled = false;

	//Enviroment
	public String additionalEnvPATH = TextUtil.EMPTY_STRING;

	public static RApplicationSettings getInstance()
	{
		return ServiceManager.getService(RApplicationSettings.class);
	}

	@Override
	public RApplicationSettings getState()
	{
		return this;
	}

	@Override
	public void loadState(@NotNull final RApplicationSettings settings)
	{
		additionalEnvPATH = settings.additionalEnvPATH;

		//console filters
		useConsoleOutputRubyStacktraceFilter = settings.useConsoleOutputRubyStacktraceFilter;
		useConsoleOutputOtherFilters = settings.useConsoleOutputOtherFilters;

		// project view
		useRubySpecificProjectView = settings.useRubySpecificProjectView;
	}

	public boolean isJsSupportEnabled()
	{
		return myJsSupportEnabled;
	}

	public void setJsSupportEnabled(final boolean jsSupportEnabled)
	{
		myJsSupportEnabled = jsSupportEnabled;
	}
}
