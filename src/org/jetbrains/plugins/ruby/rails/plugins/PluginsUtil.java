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

package org.jetbrains.plugins.ruby.rails.plugins;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.run.RailsScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunnerArgumentsProvider;
import org.jetbrains.plugins.ruby.ruby.run.RunContentDescriptorFactory;
import org.jetbrains.plugins.ruby.ruby.run.filters.RFileLinksFilter;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.process.ProcessAdapter;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.util.ActionRunner;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 5, 2007
 */
public class PluginsUtil
{
	@NonNls
	public static final String RAILS_SCRIPT_PLUGIN_SCRIPT = "script/plugin";
	public static final String INSTALL_PLUGIN = "install";


	/**
	 * Installs plugin, but doesn't refreshe VFS
	 *
	 * @param sdk
	 * @param module      Rails module
	 * @param rSpecArgs   Args for RSpec generator
	 * @param descFactory User Factory for creating non default run content descriptors
	 * @param nextAction  This action will be invoked after process has terminated or finished
	 */
	public static void installPluginAndUpdateModuleContent(final Sdk sdk, final Module module, final String rSpecArgs, @Nullable final RunContentDescriptorFactory descFactory, @Nullable final ActionRunner.InterruptibleRunnable nextAction)
	{
		final Project project = module.getProject();
		final String title = RBundle.message("rails.plugins.install.process.title", module.getName());
		final String errorTitle = RBundle.message("rails.plugins.install.error.title");
		final String[] params = createPluginInstallScriptParameters(rSpecArgs);

		try
		{
			final RubyScriptRunnerArgumentsProvider provider = new RubyScriptRunnerArgumentsProvider(params, null, null);

			final ProcessAdapter processListener = new ProcessAdapter()
			{
				@Override
				public void processTerminated(ProcessEvent event)
				{
					if(nextAction != null)
					{
						IdeaInternalUtil.runInsideWriteAction(nextAction);
					}
					RailsFacetUtil.refreshRailsAppHomeContent(module);
				}
			};
			final Filter[] filters = {new RFileLinksFilter(module)};
			RailsScriptRunner.runRailsScriptInCosole(sdk, module, processListener, filters, null, true, title, provider, descFactory);
		}
		catch(Exception exp)
		{
			RubyScriptRunner.showErrorMessage(project, errorTitle, exp);
		}
	}

	protected static String[] createPluginInstallScriptParameters(final String argsString)
	{
		final List<String> parameters = new ArrayList<String>();
		final String scriptPath = pathToCmdLinePathFormat("." + File.separator + RAILS_SCRIPT_PLUGIN_SCRIPT);

		parameters.add(scriptPath);
		parameters.add(INSTALL_PLUGIN);

		RubyScriptRunnerArgumentsProvider.collectArguments(argsString, parameters);

		return parameters.toArray(new String[parameters.size()]);
	}

	@NotNull
	private static String pathToCmdLinePathFormat(@NotNull final String scriptPath)
	{
		final String formatedStr = scriptPath.replace(VirtualFileUtil.VFS_PATH_SEPARATOR, File.separatorChar);
		if(formatedStr.contains(" "))
		{
			return "\"" + scriptPath + "\"";
		}
		return formatedStr;
	}

}
