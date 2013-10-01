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

package org.jetbrains.plugins.ruby.addins.rspec;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 19, 2008
 */
public class RSpecModuleSettings implements ModuleComponent
{
	protected final Module myModule;

	private RSpecModuleSettingsStorage myStorage;

	public RSpecModuleSettings(final Module module)
	{
		myModule = module;

		myStorage = RSpecModuleSettingsStorage.getInstance(module);
	}

	public static RSpecModuleSettings getInstance(@NotNull final Module module)
	{
		return module.getComponent(RSpecModuleSettings.class);
	}

	@Override
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return RComponents.RSPEC_MODULE_SETTINGS;
	}

	@Override
	public void initComponent()
	{
		// Do nothing
	}

	@Override
	public void disposeComponent()
	{
		// Do nothing
	}

	public boolean shouldUseRSpecTestFramework()
	{
		return getRSpecSupportType() != RSpecSupportType.NONE;
	}

	public RSpecSupportType getRSpecSupportType()
	{
		return myStorage.rSpecSupportType;
	}

	public void setRSpecSupportType(final RSpecSupportType supportType)
	{
		myStorage.rSpecSupportType = supportType;
	}

	@Override
	public void projectOpened()
	{
		//Do nothing
	}

	@Override
	public void projectClosed()
	{
		//Do nothing
	}

	@Override
	public void moduleAdded()
	{
		//Do nothing
	}

	public enum RSpecSupportType
	{
		NONE,
		GEM,
		RAILS_PLUGIN
	}
}