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

import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import consulo.configurable.ConfigurationException;
import consulo.configurable.UnnamedConfigurable;
import org.jetbrains.plugins.ruby.support.utils.OSUtil;
import consulo.util.lang.Comparing;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: Oct 27, 2007
 */
public class GeneralSettingsTab implements UnnamedConfigurable
{
	private JPanel myContentPane;

	private JCheckBox rubyStacktraceFilterCheckBox;
	private JCheckBox colorModeCheckBox;
	private JCheckBox otherFiltersCheckBox;
	private JCheckBox useRubyProjectViewBox;
	private JTextField myTFAdditioanlPath;
	private JTextPane myTPIdeaPath;

	public Component getContentPanel()
	{
		return myContentPane;
	}

	@Override
	public JComponent createComponent()
	{
		//N/A
		return null;
	}

	@Override
	public boolean isModified()
	{
		final RApplicationSettings settings = RApplicationSettings.getInstance();
		return settings.useConsoleOutputOtherFilters != otherFiltersCheckBox.isSelected() || settings.useConsoleOutputRubyStacktraceFilter != rubyStacktraceFilterCheckBox.isSelected() || settings.useConsoleColorMode != colorModeCheckBox.isSelected() || settings.useRubySpecificProjectView != useRubyProjectViewBox.isSelected() || !Comparing.equal(myTFAdditioanlPath.getText().trim(), settings.additionalEnvPATH);
	}

	@Override
	public void apply() throws ConfigurationException
	{
		final RApplicationSettings settings = RApplicationSettings.getInstance();
		settings.useConsoleOutputOtherFilters = otherFiltersCheckBox.isSelected();
		settings.useConsoleOutputRubyStacktraceFilter = rubyStacktraceFilterCheckBox.isSelected();
		settings.useConsoleColorMode = colorModeCheckBox.isSelected();
		settings.additionalEnvPATH = myTFAdditioanlPath.getText().trim();

		settings.useRubySpecificProjectView = useRubyProjectViewBox.isSelected();
	}

	@Override
	public void reset()
	{
		final RApplicationSettings settings = RApplicationSettings.getInstance();
		rubyStacktraceFilterCheckBox.setSelected(settings.useConsoleOutputRubyStacktraceFilter);
		otherFiltersCheckBox.setSelected(settings.useConsoleOutputOtherFilters);
		colorModeCheckBox.setSelected(settings.useConsoleColorMode);
		useRubyProjectViewBox.setSelected(settings.useRubySpecificProjectView);
		myTFAdditioanlPath.setText(settings.additionalEnvPATH);

		myTPIdeaPath.setText(System.getenv(OSUtil.getPATHenvVariableName()));
	}

	@Override
	public void disposeUIResources()
	{
		//Do nothing
	}
}
