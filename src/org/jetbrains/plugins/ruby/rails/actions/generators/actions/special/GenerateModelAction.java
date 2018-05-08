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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorAction;
import org.jetbrains.plugins.ruby.rails.actions.generators.actions.SimpleGeneratorInputValidator;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.module.Module;
import com.intellij.psi.PsiDirectory;
import consulo.awt.TargetAWT;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.11.2006
 */
@SuppressWarnings({"ComponentNotRegistered"})
public class GenerateModelAction extends SimpleGeneratorAction
{
	@NonNls
	public static final String GENERATOR_MODEL = "model";

	public GenerateModelAction()
	{
		this(null);
	}

	public GenerateModelAction(final String actionName)
	{
		super(GENERATOR_MODEL, actionName != null ? actionName : RBundle.message("new.generate.model.text"), RBundle.message("new.generate.model.description"), TargetAWT.to(RailsIcons.RAILS_MODEL_NODE));
	}

	@Override
	protected String getGenerateDialogTitle()
	{
		return RBundle.message("new.generate.model.action.prompt.title");
	}

	@Override
	protected String getErrorTitle()
	{
		return RBundle.message("new.generate.model.error.title");
	}

	@Override
	protected SimpleGeneratorInputValidator createValidator(@NotNull final Module module, @Nullable final PsiDirectory directory)
	{
		return new SimpleGeneratorInputValidator(this, module, directory)
		{
			@Override
			public boolean checkInput(String inputString)
			{
				if(TextUtil.isEmpty(inputString))
				{
					showErrorDialog(RBundle.message("new.generate.common.error.script.arguments.should.be.specified"));
					return false;
				}
				return true;
			}
		};
	}
}
