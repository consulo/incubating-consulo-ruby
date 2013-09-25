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

package org.jetbrains.plugins.ruby.addins.rspec.run.configuration;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecIcons;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import com.intellij.execution.LocatableConfigurationType;
import com.intellij.execution.Location;
import com.intellij.execution.RunManager;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 8, 2008
 */
public class RSpecRunConfigurationType implements LocatableConfigurationType
{
	private final RSpecRunConfigurationFactory myRSpecTestsFactory;

	public RSpecRunConfigurationType()
	{
		myRSpecTestsFactory = new RSpecRunConfigurationFactory(this);
	}

	public static RSpecRunConfigurationType getInstance()
	{
		return CONFIGURATION_TYPE_EP.findExtension(RSpecRunConfigurationType.class);
	}

	@Override
	public String getDisplayName()
	{
		return RBundle.message("rspec.run.configuration.type.name");
	}

	@Override
	public String getConfigurationTypeDescription()
	{
		return RBundle.message("rspec.run.configuration.type.description");
	}

	@Override
	public Icon getIcon()
	{
		return RSpecIcons.RUN_CONFIGURATION_ICON;
	}

	@NotNull
	@Override
	public String getId()
	{
		return "RSpecRunConfigurationType";
	}

	@Override
	public ConfigurationFactory[] getConfigurationFactories()
	{
		return new ConfigurationFactory[]{myRSpecTestsFactory};
	}

	@Override
	@Nullable
	public RunnerAndConfigurationSettings createConfigurationByLocation(final @NotNull Location location)
	{
		// if not in psi file
		final PsiElement locationElement = location.getPsiElement();
		final RFile containingFile = RubyPsiUtil.getRFile(locationElement);
		final OpenFileDescriptor openedFile = location.getOpenFileDescriptor();

		if(containingFile == null || openedFile == null)
		{
			// if in psi directory
			if(locationElement instanceof PsiDirectory)
			{
				return createRunAllSpecsInFolderConf((PsiDirectory) locationElement);
			}
			return null;
		}

		final VirtualFile file = openedFile.getFile();
		if(!RubyVirtualFileScanner.isRubyFile(file))
		{
			return null;
		}

		final Project project = location.getProject();
		// Configuration name
		final String name = file.getNameWithoutExtension();

		//if selected element is RSPEC Test File
		if(RSpecUtil.isRSpecTestFile(file))
		{
			return createRunRSpecScriptTestsConf(project, file, name);
		}
		return null;
	}

	@Override
	public boolean isConfigurationByLocation(RunConfiguration runConfiguration, Location location)
	{
		return RubyRunConfigurationUtil.isConfigurationByElement(runConfiguration, location.getPsiElement());
	}

	@Nullable
	private RunnerAndConfigurationSettings createRunRSpecScriptTestsConf(final Project project, final VirtualFile file, final String name)
	{
		// create corresponding configuration
		final RunnerAndConfigurationSettings settings = RunManager.getInstance(project).createRunConfiguration(name, myRSpecTestsFactory);

		final AbstractRubyRunConfiguration templateConfiguration = (AbstractRubyRunConfiguration) settings.getConfiguration();

		// setupFileCache not default configuration settings
		// module
		final Module module = RubyRunConfigurationUtil.getCorrespondingModule(project, file, templateConfiguration);
		templateConfiguration.setModule(module);
		if(module == null)
		{
			// module is null, we should set SDK
			//TODO templateConfiguration.setAlternativeSdk();
			return null;
		}

		//keeps template configuration's defaults
		if(TextUtil.isEmptyOrWhitespaces(templateConfiguration.getWorkingDirectory()))
		{
			// sets working dir
			final VirtualFile parentDir = file.getParent();
			final String dir = parentDir != null ? parentDir.getPath() : RailsFacetUtil.getRailsAppHomeDirPath(module);
			templateConfiguration.setWorkingDirectory(dir);
		}

		//specific settings
		final RSpecRunConfiguration conf = (RSpecRunConfiguration) templateConfiguration;

		//ignores template configuration's defaults
		// sets test type
		conf.setTestType(AbstractRubyRunConfiguration.TestType.TEST_SCRIPT);
		conf.setTestScriptPath(file.getPath());
		//keeps template configuration's defaults
		// with shift (by default for all)
		final String rubyArgs = RubyRunConfigurationUtil.collectArguments(RubyUtil.RUN_IN_CONSOLE_HACK_ARGUMENTS);
		RubyRunConfigurationUtil.setupRubyArgs(conf, rubyArgs);

		return settings;
	}

	private RunnerAndConfigurationSettings createRunAllSpecsInFolderConf(@NotNull final PsiDirectory psiDirectory)
	{
		final Project project = psiDirectory.getProject();
		final VirtualFile folder = psiDirectory.getVirtualFile();

		final String name = RBundle.message("rspec.run.configuration.test.default.name", folder.getName());
		final RunnerAndConfigurationSettings settings = RunManager.getInstance(project).createRunConfiguration(name, myRSpecTestsFactory);

		final RSpecRunConfiguration conf = (RSpecRunConfiguration) settings.getConfiguration();

		// search module for file
		final Module module = RubyRunConfigurationUtil.getCorrespondingModule(project, folder, conf);
		if(module == null || !RModuleUtil.hasRubySupport(module) || !RSpecModuleSettings.getInstance(module).shouldUseRSpecTestFramework())
		{

			return null;
		}

		// init run configuration
		//ignores template's defaults
		conf.setModule(module);
		conf.setTestType(AbstractRubyRunConfiguration.TestType.ALL_IN_FOLDER);
		conf.setTestsFolderPath(folder.getPath());

		//keeps template's defaults
		if(TextUtil.isEmptyOrWhitespaces(conf.getTestFileMask()))
		{
			conf.setTestFileMask(RSpecRunConfiguration.DEFAULT_TESTS_SEARCH_MASK);
		}
		if(TextUtil.isEmptyOrWhitespaces(conf.getSpecArgs()))
		{
			conf.setSpecArgs(RSpecRunConfiguration.DEFAULT_SPEC_ARGS);
		}

		if(TextUtil.isEmptyOrWhitespaces(conf.getWorkingDirectory()))
		{
			conf.setWorkingDirectory(folder.getPath());
		}
		return settings;
	}
}
