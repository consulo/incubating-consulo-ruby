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

package org.jetbrains.plugins.ruby.ruby.module;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RubyModuleBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.module.ModuleType;

public class RubyModuleType extends ModuleType<RubyModuleBuilder> implements ApplicationComponent
{
	@NonNls
	private static final String RUBY_MODULE = "RUBY_MODULE";

	public RubyModuleType()
	{
		//super(RUBY_MODULE);
	}

	@NotNull
	public static RubyModuleType getInstance()
	{
		return ApplicationManager.getApplication().getComponent(RubyModuleType.class);
	}

	@Override
	@NonNls
	@NotNull
	public String getComponentName()
	{
		return RComponents.RUBY_MODULE_TYPE;
	}

	@Override
	public void initComponent()
	{
		// ModuleTypeManager.getInstance().registerModuleType(this);
	}

	@Override
	public void disposeComponent()
	{
	}

   /* public ModuleWizardStep[] createWizardSteps(final WizardContext ctx, final RubyModuleBuilder builder,
												final ModulesProvider provider) {
        ArrayList<ModuleWizardStep> steps = new ArrayList<ModuleWizardStep>();

        steps.add(new RubySdkSelectStep(builder, RubyIcons.RUBY_ADD_MODULE, null, ctx.getProject()));
        steps.add(new SupportForFrameworksStep(builder));

        steps.add(new SelectTestFrameworkStep(builder, RubyIcons.RUBY_ADD_MODULE, null));
        steps.add(new RubyRSpecInstallComponentsStep(builder, RubyIcons.RUBY_ADD_MODULE, null, ctx.getProject()));
        steps.add(new TestUnitSourceRootStep(builder, RubyIcons.RUBY_ADD_MODULE, null));

        return steps.toArray(new ModuleWizardStep[steps.size()]);
    }     */


	public RubyModuleBuilder createModuleBuilder()
	{
		return new RubyModuleBuilder();
	}


	public String getName()
	{
		return RBundle.message("module.ruby.title");
	}

	public String getDescription()
	{
		return RBundle.message("module.ruby.description");
	}


	public Icon getBigIcon()
	{
		return RubyIcons.RUBY_MODULE_BIG;
	}


	public Icon getNodeIcon(boolean isOpened)
	{
		return isOpened ? RubyIcons.RUBY_MODULE_OPENED : RubyIcons.RUBY_MODULE_CLOSED;
	}
}

