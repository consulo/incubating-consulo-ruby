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

import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.RakeUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */
public class BaseRailsFacetConfigurationImpl implements BaseRailsFacetConfigurationLowLevel
{

	// Serializable in BaseRailsFacetConfigurationExternalizer
	public boolean myShouldUseRSpecPlugin;

	private String myRailsRootDirPath;

	private boolean isInitialized;

	// Transient, we shouldn't serialize it!
	private String myRailsRootDirPathUrl;

	private StandardRailsPaths myRailsPaths;

	// This object is build on every RakeCommands changes. Represents logical structure of Rake commands.
	private RakeTask myRootRakeTask;

	// Generators
	private String[] myGenerators;
	private Module myModule;

	// Is being used only for RakeTasks, Generators regenerating after SDK changing
	private Sdk mySdk;


	@Override
	public boolean shouldUseRSpecPlugin()
	{
		return myShouldUseRSpecPlugin;
	}

	@Override
	public void setShouldUseRSpecPlugin(final boolean useRSpecPlugin)
	{
		myShouldUseRSpecPlugin = useRSpecPlugin;
	}

	@Override
	@NotNull
	public String getRailsApplicationRootPath()
	{
		return myRailsRootDirPath;
	}

	@Override
	@NotNull
	public String getRailsApplicationRootPathUrl()
	{
		return myRailsRootDirPathUrl;
	}

	@Override
	public void setRailsApplicationRootPath(@NotNull final String railsRootDirPath)
	{
		myRailsRootDirPath = railsRootDirPath;
		myRailsRootDirPathUrl = VirtualFileUtil.constructLocalUrl(railsRootDirPath);
		myRailsPaths = new StandardRailsPaths(railsRootDirPath);
	}

	@Override
	@NotNull
	public StandardRailsPaths getPaths()
	{
		return myRailsPaths;
	}

	@Override
	public void loadGenerators(final boolean forceRegenerate, @Nullable final Sdk sdk)
	{
		assert myModule != null;
		GeneratorsUtil.loadGeneratorsList(forceRegenerate, myModule.getProject(), sdk, myModule.getName(), this);
	}

	@Override
	public String[] getGenerators()
	{
		return myGenerators;
	}


	public void setGenerators(final String[] generators)
	{
		myGenerators = generators;
	}

	@Override
	public void loadRakeTasks(final boolean forceRegenerate, final Sdk sdk)
	{
		RakeUtil.loadRakeTasksTree(forceRegenerate, myModule.getProject(), sdk, myModule.getName(), this);
	}

	@Override
	public RakeTask getRakeTasks()
	{
		return myRootRakeTask;
	}

	public void setRakeTasks(final RakeTask rootTask)
	{
		myRootRakeTask = rootTask;
	}

	@Override
	public void setModule(@NotNull final Module uncommitedModule)
	{
		myModule = uncommitedModule;

		final String path = BaseRailsFacetConfigurationExternalizer.getInstance().expandPathIfPossible(this, getNullableRailsApplicationRootPath());
		if(path != null)
		{
			setRailsApplicationRootPath(path);
		}
	}

	@Override
	public Module getModule()
	{
		return myModule;
	}

	@Override
	public void reloadGenerators()
	{
		loadGenerators(true, RModuleUtil.getModuleOrJRubyFacetSdk(myModule));
	}

	@Override
	public void reloadRakeTasks()
	{
		loadRakeTasks(true, RModuleUtil.getModuleOrJRubyFacetSdk(myModule));
	}

	// Externalizing
	public void readExternal(final Element element) throws InvalidDataException
	{
		BaseRailsFacetConfigurationExternalizer.getInstance().readExternal(this, element);
	}

	public void writeExternal(final Element element) throws WriteExternalException
	{
		BaseRailsFacetConfigurationExternalizer.getInstance().writeExternal(this, element);
	}

	@Override
	@Nullable
	public String getNullableRailsApplicationRootPath()
	{
		return myRailsRootDirPath;
	}

	/**
	 * Only for BaseRailsFacet internal tasks
	 *
	 * @param sdk Sdk
	 */
	@Override
	public void setSdk(@Nullable final Sdk sdk)
	{
		mySdk = sdk;
	}

	/**
	 * Only for BaseRailsFacet internal tasks
	 *
	 * @return SDK
	 */
	@Override
	@Nullable
	public Sdk getSdk()
	{
		return mySdk;
	}

	@Override
	public void setInitialized()
	{
		isInitialized = true;
	}

	@Override
	public boolean isInitialized()
	{
		return isInitialized;
	}
}
