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

package org.jetbrains.plugins.ruby.ruby.actions.intention;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import com.intellij.codeInsight.intention.IntentionManager;
import com.intellij.openapi.components.ProjectComponent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 25, 2006
 */
class RubyIntentionActionsManager implements ProjectComponent
{
	private static final String RUBY_INTENTIONS = RBundle.message("ruby.intentions");

	@Override
	public void projectOpened()
	{
		IntentionManager.getInstance().registerIntentionAndMetaData(new RelativePathToAbsolutePathIntention(), RUBY_INTENTIONS);
		IntentionManager.getInstance().registerIntentionAndMetaData(new AppendCurrentDirToPathIntention(), RUBY_INTENTIONS);
	}

	@Override
	public void projectClosed()
	{
	}

	@Override
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return RComponents.RUBY_INTENTION_ACTIONS_MANAGER;
	}

	@Override
	public void initComponent()
	{
	}

	@Override
	public void disposeComponent()
	{
	}
}
