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

package org.jetbrains.plugins.ruby.settings;

import static org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions.Option;

import jakarta.annotation.Nonnull;

import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions;
import consulo.project.Project;
import consulo.versionControlSystem.VcsConfiguration;
import consulo.versionControlSystem.VcsShowConfirmationOption;
import consulo.ide.impl.idea.openapi.vcs.VcsShowConfirmationOptionImpl;
import consulo.ide.impl.idea.openapi.vcs.ex.ProjectLevelVcsManagerEx;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.05.2007
 */
public class RProjectUtil
{
	public static boolean isVcsAddSilently(@Nonnull final Project project)
	{
		final VcsShowConfirmationOptionImpl opt = consulo.ide.impl.idea.openapi.vcs.ex.ProjectLevelVcsManagerEx.getInstanceEx(project).getConfirmation(VcsConfiguration.StandardConfirmation.ADD);
		return opt.getValue() == VcsShowConfirmationOption.Value.DO_ACTION_SILENTLY;
	}

	public static boolean isVcsAddNothingSilently(@Nonnull final Project project)
	{
		final consulo.ide.impl.idea.openapi.vcs.VcsShowConfirmationOptionImpl opt = consulo.ide.impl.idea.openapi.vcs.ex.ProjectLevelVcsManagerEx.getInstanceEx(project).getConfirmation(VcsConfiguration.StandardConfirmation.ADD);
		return opt.getValue() == VcsShowConfirmationOption.Value.DO_NOTHING_SILENTLY;
	}

	public static boolean isVcsAddShowConfirmation(@Nonnull final Project project)
	{
		final consulo.ide.impl.idea.openapi.vcs.VcsShowConfirmationOptionImpl opt = consulo.ide.impl.idea.openapi.vcs.ex.ProjectLevelVcsManagerEx.getInstanceEx(project).getConfirmation(VcsConfiguration.StandardConfirmation.ADD);
		return opt.getValue() == VcsShowConfirmationOption.Value.SHOW_CONFIRMATION;
	}

	public static GeneratorOptions getGeneratorsOptions(@Nonnull final Project project)
	{
		final GeneratorOptions options = RProjectSettings.getInstance(project).getGeneratorsOptions();
		final boolean showConfirmation = isVcsAddShowConfirmation(project);

		options.setOption(Option.SVN_SHOW_CONFIRMATION, showConfirmation);

		if(!showConfirmation)
		{
			// set SVN option from Vcs settings
			options.setOption(Option.SVN, isVcsAddSilently(project));
		}
		return options;
	}
}
