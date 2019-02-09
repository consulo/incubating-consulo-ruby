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

package org.jetbrains.plugins.ruby.rails.facet.configuration;

import java.util.Map;

import javax.annotation.Nonnull;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;
import com.intellij.openapi.components.PathMacroManager;
import com.intellij.openapi.module.Module;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */
public class BaseRailsFacetConfigurationExternalizer extends SettingsExternalizer
{
	private static final BaseRailsFacetConfigurationExternalizer INSTANCE = new BaseRailsFacetConfigurationExternalizer();

	@NonNls
	public static final String RAILS_FACET_CONFIG_ID = "RAILS_FACET_CONFIG_ID";
	@NonNls
	public static final String SHOULD_USE_RSPEC_PLUGIN = "SHOULD_USE_RSPEC_PLUGIN";
	@NonNls
	public static final String RAILS_FACET_APPLIATION_ROOT = "RAILS_FACET_APPLICATION_ROOT";

	public void writeExternal(@Nonnull final BaseRailsFacetConfigurationLowLevel config, @Nonnull final Element elem)
	{
		writeOption(SHOULD_USE_RSPEC_PLUGIN, String.valueOf(config.shouldUseRSpecPlugin()), elem);

		//save collapsed path
		final String expandedAppHomePath = config.getRailsApplicationRootPath();
		writeOption(RAILS_FACET_APPLIATION_ROOT, String.valueOf(collapseIfPossible(config, expandedAppHomePath)), elem);
	}

	private String collapseIfPossible(final BaseRailsFacetConfigurationLowLevel config, final String expandedPath)
	{
		final String collapsedPath;
		final Module module = config.getModule();
		if(module == null || expandedPath == null)
		{
			return expandedPath;
		}

		return PathMacroManager.getInstance(module).collapsePath(expandedPath);
	}

	public void readExternal(@Nonnull final BaseRailsFacetConfigurationLowLevel config, @Nonnull final Element elem)
	{
		//noinspection unchecked
		final Map<String, String> optionsByName = buildOptionsByElement(elem);
		config.setShouldUseRSpecPlugin(Boolean.valueOf(optionsByName.get(SHOULD_USE_RSPEC_PLUGIN)));

		//rails app root path
		final String raislAppRootPathCollapsed = optionsByName.get(RAILS_FACET_APPLIATION_ROOT);
		config.setRailsApplicationRootPath(expandPathIfPossible(config, raislAppRootPathCollapsed));

		//initialized
		config.setInitialized();
	}

	@Nullable
	public String expandPathIfPossible(final BaseRailsFacetConfigurationLowLevel config, @Nullable final String pathCollapsed)
	{
		final String pathExpanded;
		final Module module = config.getModule();
		if(module == null || pathCollapsed == null)
		{
			return pathCollapsed;
		}

		//expand
		return PathMacroManager.getInstance(module).expandPath(pathCollapsed);
	}

	public static BaseRailsFacetConfigurationExternalizer getInstance()
	{
		return INSTANCE;
	}

	@Override
	public String getID()
	{
		return RAILS_FACET_CONFIG_ID;
	}
}