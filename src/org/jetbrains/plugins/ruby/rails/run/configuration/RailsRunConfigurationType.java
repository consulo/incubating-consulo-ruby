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

package org.jetbrains.plugins.ruby.rails.run.configuration;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfigurationFactory;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;
import com.intellij.execution.Location;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 8, 2008
 */
public class RailsRunConfigurationType implements ConfigurationType
{
	private final RailsServerRunConfigurationFactory myRailsServerFactory;

	public RailsRunConfigurationType()
	{
		myRailsServerFactory = new RailsServerRunConfigurationFactory(this);
	}

	public static RailsRunConfigurationType getInstance()
	{
		return CONFIGURATION_TYPE_EP.findExtension(RailsRunConfigurationType.class);
	}

	@Override
	public String getDisplayName()
	{
		return RBundle.message("rails.run.configuration.type.name");
	}

	@Override
	public String getConfigurationTypeDescription()
	{
		return RBundle.message("rails.run.configuration.type.description");
	}

	@Override
	public Icon getIcon()
	{
		return RailsIcons.RAILS_RUN_CONFIGURATION_FOLDER;
	}

	@NotNull
	@Override
	public String getId()
	{
		return "RailsRunConfigurationType";
	}

	@Override
	public ConfigurationFactory[] getConfigurationFactories()
	{
		return new ConfigurationFactory[]{myRailsServerFactory};
	}

	@NotNull
	public RailsServerRunConfigurationFactory getWEBrickFactory()
	{
		return myRailsServerFactory;
	}

	//@Override
	public RunnerAndConfigurationSettings createConfigurationByLocation(final Location location)
	{
		return null;
	}

	//@Override
	public boolean isConfigurationByLocation(RunConfiguration runConfiguration, Location location)
	{
		return RubyRunConfigurationUtil.isConfigurationByElement(runConfiguration, location.getPsiElement());

	}
}
