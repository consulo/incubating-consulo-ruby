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

package org.jetbrains.plugins.ruby.rails.run.configuration.server;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationFactory;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.08.2007
 */
public class RailsServerRunConfigurationFactory extends RubyRunConfigurationFactory
{
	public static final String SERVER_TITLE = RBundle.message("run.configuration.server.title");
	public final int RAILS_DEFAULT_PORT = 3000;

	public final String WIN_LOCALHOST = "127.0.0.1";
	public final String NOT_WIN_LOCALHOST = "0.0.0.0";


	private static String LOCALHOST;

	{
		LOCALHOST = SystemInfo.isWindows ? WIN_LOCALHOST : NOT_WIN_LOCALHOST;
	}

	public RailsServerRunConfigurationFactory(final ConfigurationType type)
	{
		super(type);
	}

	/**
	 * Initialize Rails Server Run config
	 *
	 * @param module Ruby / Java module
	 * @return Rails Server Run configuration settings
	 */
	public RunnerAndConfigurationSettings createRunConfigurationSettings(@NotNull final Module module)
	{
		final RunnerAndConfigurationSettings settings = RubyRunConfigurationUtil.create(module.getProject(), this, "");
		final RubyRunConfiguration conf = (RubyRunConfiguration) settings.getConfiguration();

		initDefaultConfiguration(module, conf);

		return settings;
	}

	/**
	 * Initialize Rails Server Run config
	 *
	 * @param module Ruby / Java module
	 * @param conf   RailsServer Run Configuration
	 */
	private void initDefaultConfiguration(@NotNull final Module module, @NotNull final RubyRunConfiguration conf)
	{

		initDefaultParams(conf);
		conf.setName(RailsServerRunConfigurationFactory.SERVER_TITLE + ':' + module.getName());

		conf.setScriptPath(RailsServerRunConfiguration.getServerScriptPathByModule(module));
		conf.setWorkingDirectory(RailsServerRunConfiguration.getRailsWorkDirByModule(module));
		conf.setModule(module);
	}

	@Override
	public RunConfiguration createTemplateConfiguration(final Project project)
	{
		final RubyRunConfiguration conf = new RailsServerRunConfiguration(project, this, "");
		initDefaultParams(conf);

		return conf;
	}

	@Override
	public String getName()
	{
		return SERVER_TITLE;
	}

	@Override
	public Icon getIcon()
	{
		return RubyIcons.RAILS_SERVER_RUN_CONFIGURATION;
	}

	@Override
	protected void initDefaultParams(final RubyRunConfiguration conf)
	{
		super.initDefaultParams(conf);

		final RailsServerRunConfiguration config = (RailsServerRunConfiguration) conf;
		config.setChoosePortManually(Boolean.TRUE);
		config.setPort(String.valueOf(RAILS_DEFAULT_PORT));
		config.setIPAddr(LOCALHOST);
		config.setRailsEnvironmentType(RailsServerRunConfiguration.RailsEnvironmentType.DEVELOPMENT);
		config.setServerType(RailsServerRunConfiguration.DEFAULT_SERVER);
	}
}
