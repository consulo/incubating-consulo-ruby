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

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.plugins.ruby.addins.rspec.RSpecApplicationSettings;
import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.FacetWizardStep;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RubyModuleBuilder;
import consulo.awt.TargetAWT;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 21, 2007
 */
public class SelectTestFrameworkStep extends FacetWizardStep
{
	private Image myIcon;
	private RubyModuleBuilder mySettingsHolder;
	private String myHelp;
	private final SelectTestFrameworkPanel myForm;

	public SelectTestFrameworkStep(final RubyModuleBuilder settingsHolder, final Image icon, final String help)
	{
		super();
		myIcon = icon;
		mySettingsHolder = settingsHolder;
		myHelp = help;

		final RSpecApplicationSettings settings = RSpecApplicationSettings.getInstance();

		final boolean shouldUseRSpecFramework = settings.wizardRubyShouldUseRSpecFramework;
		final boolean shouldUseTestUnit = settings.wizardRubyShouldUseTestUnitFramework;

		settingsHolder.enableTestUnitSupport(shouldUseTestUnit);
		settingsHolder.enableRSpecSupport(shouldUseRSpecFramework);


		myForm = new SelectTestFrameworkPanel(new ItemListener()
		{
			@Override
			public void itemStateChanged(final ItemEvent e)
			{
				fireStateChanged();
			}
		}, shouldUseTestUnit, shouldUseRSpecFramework);
	}

	@Override
	public Icon getIcon()
	{
		return TargetAWT.to(myIcon);
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
		updateDataModel();
	}

	@Override
	public void onStepLeaving()
	{
		RSpecApplicationSettings.getInstance().wizardRubyShouldUseRSpecFramework = myForm.shouldUseRSpecFramework();
	}

	@Override
	public void updateDataModel()
	{
		mySettingsHolder.enableRSpecSupport(myForm.shouldUseRSpecFramework());
		mySettingsHolder.enableTestUnitSupport(myForm.shouldUseTestUnitFramework());
	}

	@Override
	public boolean isStepVisible()
	{
		return super.isStepVisible() && !NiiChAVOUtil.isRailsFacetEnabledMagic(getComponent());
	}
}