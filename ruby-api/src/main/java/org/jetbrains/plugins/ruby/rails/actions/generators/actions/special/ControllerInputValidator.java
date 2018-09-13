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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions.special;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorAction;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorInputValidator;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 05.12.2006
 */
public class ControllerInputValidator extends SimpleGeneratorInputValidator
{
	public ControllerInputValidator(@NotNull final SimpleGeneratorAction generatorAction, @NotNull final Module module, @Nullable final PsiDirectory directory)
	{
		super(generatorAction, module, directory);
	}


	@Override
	public boolean checkInput(@NotNull final String inputString)
	{
		if(RailsUtil.isValidRailsFSPath(inputString))
		{
			return true;
		}
		showErrorDialog("\"" + inputString + "\" " + RBundle.message("new.generate.common.error.script.argument.is.not.valid"));
		return false;
	}

	@Nullable
	public String getRelativePath()
	{
		if(myDirectory == null)
		{
			return null;
		}

		final String url = myDirectory.getVirtualFile().getUrl();
		final String path = ControllersConventions.getControllerCorrespondingDir(url, myModule);
		if(path == null)
		{
			return null;
		}
		return ControllersConventions.getRelativePathOfControllerFolder(path, myModule);
	}
}
