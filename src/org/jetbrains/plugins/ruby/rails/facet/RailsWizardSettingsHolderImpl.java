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

package org.jetbrains.plugins.ruby.rails.facet;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.addins.rspec.rails.facet.ui.wizard.tabs.RSpecComponentsInstallerTab;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.RailsWizardSettingsHolder;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 10, 2008
 */
public class RailsWizardSettingsHolderImpl implements RailsWizardSettingsHolder
{
	private Sdk mySdk;

	private Generate myGenerateRailsAppWay = Generate.NEW;
	private String myRailsApplicatlionRootRelativePath;
	private String myDbNameToPreconfigure;
	private RSpecConfiguration myRSpecConf;

	protected RailsWizardSettingsHolderImpl()
	{
	}

	public static RailsWizardSettingsHolder createDefaultConf()
	{
		final RailsWizardSettingsHolderImpl settings = new RailsWizardSettingsHolderImpl();

		settings.setRSpecConf(RSpecComponentsInstallerTab.getStoredDefaultConf());

		return settings;
	}

	@Override
	public Generate getAppGenerateWay()
	{
		return myGenerateRailsAppWay;
	}

	@Override
	public void setAppGenerateWay(@NotNull final Generate generateWay)
	{
		myGenerateRailsAppWay = generateWay;
	}

	@Override
	@Nullable
	public String getDBNameToPreconfigure()
	{
		return myDbNameToPreconfigure;
	}

	@Override
	public void setDBNameToPreconfigure(@Nullable final String dbName)
	{
		myDbNameToPreconfigure = dbName;
	}

	@Override
	@NotNull
	public RSpecConfiguration getRSpecConf()
	{
		return myRSpecConf;
	}

	@Override
	public void setRSpecConf(@NotNull final RSpecConfiguration rSpecConf)
	{
		myRSpecConf = rSpecConf;
	}

	@Override
	public boolean isRSpecSupportEnabled()
	{
		final RSpecConfiguration conf = getRSpecConf();
		return conf.enableRSpecSupport() || conf.enableRSpecRailsSupport();
	}

	@Override
	public void setTestsUnitRootPath(@NotNull final String contentRootPath)
	{
		//To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	@Nullable
	public String getRailsApplicationHomeDirRelativePath()
	{
		return myRailsApplicatlionRootRelativePath;
	}

	@Override
	public void setRailsApplicationHomeDirRelativePath(@Nullable final String relativePath)
	{
		myRailsApplicatlionRootRelativePath = relativePath;
	}

	@Override
	@Nullable
	public Sdk getSdk()
	{
		return mySdk;
	}

	@Override
	public void setSdk(@Nullable final Sdk sdk)
	{
		mySdk = sdk;
	}
}
