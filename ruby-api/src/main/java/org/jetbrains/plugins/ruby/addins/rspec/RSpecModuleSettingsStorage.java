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

import java.util.Map;

import javax.annotation.Nonnull;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleServiceManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 19, 2008
 */
@State(
		name = RComponents.RSPEC_MODULE_SETTINGS_STORAGE,
		storages = {
				@Storage(
						file = "$MODULE_FILE$"
				)
		}
)
public class RSpecModuleSettingsStorage extends SettingsExternalizer implements PersistentStateComponent<Element>
{
	private static final Logger LOG = Logger.getInstance(RSpecModuleSettingsStorage.class.getName());

	public RSpecModuleSettings.RSpecSupportType rSpecSupportType = RSpecModuleSettings.RSpecSupportType.NONE;

	@NonNls
	private static final String RSPEC_SUPPORT_TYPE = "RSPEC_SUPPORT_TYPE";
	@NonNls
	private static final String RSPEC_MODULE_SETTINGS_STORAGE_ID = "RSPEC_MODULE_SETTINGS_STORAGE_ID";

	public static RSpecModuleSettingsStorage getInstance(@Nonnull final Module module)
	{
		return ModuleServiceManager.getService(module, RSpecModuleSettingsStorage.class);
	}

	@Override
	public Element getState()
	{
		final Element element = new Element(getID());

		//writeExternal
		writeOption(RSPEC_SUPPORT_TYPE, rSpecSupportType.toString(), element);

		return element;
	}

	@Override
	public void loadState(@Nonnull final Element elem)
	{
		//readExternal
		final Map<String, String> optionsByName = buildOptionsByElement(elem);

		final String rSpecSupportTypeStr = optionsByName.get(RSPEC_SUPPORT_TYPE);
		if(rSpecSupportTypeStr == null)
		{
			rSpecSupportType = RSpecModuleSettings.RSpecSupportType.NONE;
		}
		else
		{
			try
			{
				rSpecSupportType = RSpecModuleSettings.RSpecSupportType.valueOf(rSpecSupportTypeStr);
			}
			catch(IllegalArgumentException e)
			{
				LOG.warn("RSpec settings: Can't parse RSpecSupportType. Value = " + rSpecSupportTypeStr);

				rSpecSupportType = RSpecModuleSettings.RSpecSupportType.NONE;
			}
		}
	}

	@Override
	public String getID()
	{
		return RSPEC_MODULE_SETTINGS_STORAGE_ID;
	}
}