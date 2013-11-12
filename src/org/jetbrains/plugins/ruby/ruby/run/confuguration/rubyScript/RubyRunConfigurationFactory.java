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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript;

import javax.swing.Icon;

import org.consulo.ruby.module.extension.RubyModuleExtension;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;
import org.mustbe.consulo.module.extension.ModuleExtensionHelper;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.ConfigurationType;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.project.Project;

public class RubyRunConfigurationFactory extends ConfigurationFactory
{

	public RubyRunConfigurationFactory(final ConfigurationType type)
	{
		super(type);
	}

	@Override
	public RunConfiguration createTemplateConfiguration(final Project project)
	{
		final RubyRunConfiguration conf = new RubyRunConfiguration(project, this, "");
		initDefaultParams(conf);

		return conf;
	}

	@Override
	public boolean isApplicable(@NotNull Project project)
	{
		return ModuleExtensionHelper.getInstance(project).hasModuleExtension(RubyModuleExtension.class);
	}

	@Override
	public String getName()
	{
		return RBundle.message("run.configuration.script.name");
	}

	@Override
	public Icon getIcon()
	{
		return RubyIcons.RUBY_RUN_CONFIGURATION_SCRIPT;
	}

	/**
	 * Sets configuration default params
	 *
	 * @param conf configuration
	 */
	protected void initDefaultParams(final RubyRunConfiguration conf)
	{
		final String args = RubyRunConfigurationUtil.collectArguments(RubyUtil.RUN_IN_CONSOLE_HACK_ARGUMENTS);
		conf.setRubyArgs(args);
	}
}

