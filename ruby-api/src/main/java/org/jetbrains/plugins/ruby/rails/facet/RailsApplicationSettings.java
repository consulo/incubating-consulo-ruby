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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 14, 2008
 */
@State(name = "RailsApplicationSettings", storages = @Storage("ruby.xml"))
public class RailsApplicationSettings implements PersistentStateComponent<RailsApplicationSettings>
{
	@Nullable
	public String wizardRailsFacetPreconfigureDBName = null;


	public static RailsApplicationSettings getInstance()
	{
		return ServiceManager.getService(RailsApplicationSettings.class);
	}

	@Override
	public RailsApplicationSettings getState()
	{
		return this;
	}

	@Override
	public void loadState(@Nonnull final RailsApplicationSettings settings)
	{
		wizardRailsFacetPreconfigureDBName = settings.wizardRailsFacetPreconfigureDBName;
	}
}