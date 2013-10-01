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

package org.jetbrains.plugins.ruby.ruby.module.wizard.ui;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.FacetWizardStep;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RubyModuleBuilder;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 21, 2007
 */
public class TestUnitSourceRootStep extends FacetWizardStep
{
	private Icon myIcon;
	private RubyModuleBuilder mySettingsHolder;
	private String myHelp;
	private final TestSourceRootPanel myForm;

	public TestUnitSourceRootStep(final RubyModuleBuilder settingsHolder, final Icon icon, final String help)
	{
		super();
		myIcon = icon;
		mySettingsHolder = settingsHolder;
		myForm = new TestSourceRootPanel(settingsHolder);
		myHelp = help;
	}

	@Override
	public Icon getIcon()
	{
		return myIcon;
	}

	@Override
	public JComponent getPreferredFocusedComponent()
	{
		return myForm.getPreferredFocusedComponent();
	}

	@Override
	public String getHelpId()
	{
		return myHelp;
	}

	@Override
	public JComponent getComponent()
	{
		return myForm.getContentPane();
	}

	@Override
	public void updateStep()
	{
		myForm.update();
	}

	@Override
	public boolean isStepVisible()
	{
		return mySettingsHolder.isTestUnitSupportEnabled() && !NiiChAVOUtil.isRailsFacetEnabledMagic(getComponent());
	}

	@Override
	public void updateDataModel()
	{
		if(!myForm.shouldSearchInWholeModule())
		{
			mySettingsHolder.setTestsUnitRootPath(myForm.getAbsoluteTestsPath());
		}
	}
}
