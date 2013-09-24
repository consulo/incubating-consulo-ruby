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

import com.intellij.execution.LocatableConfigurationType;
import com.intellij.execution.Location;
import com.intellij.execution.RunnerAndConfigurationSettings;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsComponents;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfigurationFactory;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 8, 2008
 */
public class RailsRunConfigurationType  implements LocatableConfigurationType {
    private final RailsServerRunConfigurationFactory myRailsServerFactory;

    public static RailsRunConfigurationType getInstance() {
        return ApplicationManager.getApplication().getComponent(RailsRunConfigurationType.class);
    }

    public RailsRunConfigurationType() {
        myRailsServerFactory = new RailsServerRunConfigurationFactory(this);
    }

    public String getDisplayName() {
        return RBundle.message("rails.run.configuration.type.name");
    }

    public String getConfigurationTypeDescription() {
        return RBundle.message("rails.run.configuration.type.description");
    }

    public Icon getIcon() {
        return RailsIcons.RAILS_RUN_CONFIGURATION_FOLDER;
    }

	@NotNull
	@Override
	public String getId()
	{
		return null;
	}

	public ConfigurationFactory[] getConfigurationFactories() {
        return new ConfigurationFactory[]{myRailsServerFactory};
    }

    @NotNull
    public String getComponentName() {
        return RailsComponents.RAILS_RUN_CONFIGURATION_TYPE;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public RailsServerRunConfigurationFactory getWEBrickFactory() {
        return myRailsServerFactory;
    }
    
    public RunnerAndConfigurationSettings createConfigurationByLocation(final Location location) {
        return null;
    }

	@Override
	public boolean isConfigurationByLocation(RunConfiguration runConfiguration, Location location)
	{
		return false;
	}

	public boolean isConfigurationByElement(@NotNull final RunConfiguration configuration,
                                            @NotNull final Project project,
                                            @NotNull final PsiElement element) {
        return RubyRunConfigurationUtil.isConfigurationByElement(configuration, element);

    }
}
