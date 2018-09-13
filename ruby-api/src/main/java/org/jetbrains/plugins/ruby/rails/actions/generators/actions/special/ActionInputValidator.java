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
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorInputValidator;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryImpl;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 27.12.2006
 */
public class ActionInputValidator extends SimpleGeneratorInputValidator
{
	public DataContext myDataContext;
	public VirtualFile myFile;

	public ActionInputValidator(@NotNull final GenerateActionAction generatorAction, @NotNull final Module module, @Nullable final VirtualFile file, @Nullable final DataContext dataContext)
	{
		super(generatorAction, module, new PsiDirectoryImpl((PsiManagerImpl) PsiManager.getInstance(module.getProject()), file));
		myDataContext = dataContext;
		myFile = file;
	}

	@Override
	public boolean checkInput(@NotNull final String actionName)
	{
		if(ControllersConventions.isValidActionName(actionName))
		{
			return true;
		}
		showErrorDialog(RBundle.message("popup.generate.action.error.script.argument.is.not.valid", actionName, ControllersConventions.toValidActionName(actionName)));
		return false;
	}

	@Nullable
	public String getRelativePath()
	{
		if(myFile == null)
		{
			return TextUtil.EMPTY_STRING;
		}
		return ControllersConventions.getControllerFullName(myModule, myFile);
	}


	@Override
	public void invokeAction(final String scriptArguments, final String mainArgument)
	{
		((GenerateActionAction) myGeneratorAction).invokeAction(scriptArguments, mainArgument, myModule, myDataContext);
	}
}
