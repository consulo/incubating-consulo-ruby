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

package org.jetbrains.plugins.ruby.rails;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.rails.actions.RailsEditorActionsManager;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacetType;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.util.ActionRunner;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman.Chernyatchik
 * @date: 16.08.2006
 */
public class RailsSupportLoader implements ApplicationComponent
{

	@Override
	@NotNull
	public String getComponentName()
	{
		return RComponents.RAILS_SUPPORT_LOADER;
	}

	@Override
	public void initComponent()
	{
		loadRails();
	}

	@Override
	public void disposeComponent()
	{
	}


	public static void loadRails()
	{
		IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable()
		{
			@Override
			public void run() throws Exception
			{
				RailsEditorActionsManager.registerEditorActions();

				BaseRailsFacetType.load();
			}
		});
	}
}
