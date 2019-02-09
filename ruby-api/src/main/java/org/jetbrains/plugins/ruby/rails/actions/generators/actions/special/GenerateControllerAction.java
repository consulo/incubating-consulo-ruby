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

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.actions.generators.GenerateDialogs;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorAction;
import org.jetbrains.plugins.ruby.rails.presentation.RControllerPresentationUtil;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.11.2006
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class GenerateControllerAction extends SimpleGeneratorAction
{
	@NonNls
	public static final String GENERATOR_CONTROLLER = "controller";

	public GenerateControllerAction()
	{
		this(null, GENERATOR_CONTROLLER);
	}

	public GenerateControllerAction(final String actionName, final String generatorName)
	{
		super(generatorName, actionName != null ? actionName : RBundle.message("new.generate.controller.text"), RBundle.message("new.generate.controller.description"), RControllerPresentationUtil.getIcon());
	}

	@Override
	protected String getGenerateDialogTitle()
	{
		return RBundle.message("new.generate.controller.action.prompt.title");
	}

	@Override
	protected String getErrorTitle()
	{
		return RBundle.message("new.generate.controller.error.title");
	}

	@Override
	protected ControllerInputValidator createValidator(@Nonnull final Module module, @Nullable final PsiDirectory directory)
	{
		return new ControllerInputValidator(this, module, directory);
	}

	@Override
	protected PsiElement[] invokeDialog(@Nonnull final Module module, @Nullable final PsiDirectory directory)
	{
		final ControllerInputValidator validator = createValidator(module, directory);
		GenerateDialogs.showGenerateControllerDialog(module, getGenerateDialogTitle(), validator);
		return validator.getCreatedElements();
	}
}