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

package org.jetbrains.plugins.ruby.rails.run.configuration.server;

import static org.jetbrains.plugins.ruby.rails.run.configuration.server.RailsServerRunConfiguration.RailsEnvironmentType;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.ExternalRailsSettings;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationForm;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.RawCommandLineEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.08.2007
 */
@SuppressWarnings({
		"UnusedDeclaration",
		"FieldCanBeLocal"
})
public class RailsServerConfigurationForm extends RubyRunConfigurationForm implements RailsServerRunConfigurationParams
{
	private JPanel generatedPanel1;

	private LabeledComponent scriptComponent1;
	private LabeledComponent scriptArgsComponent1;
	private LabeledComponent workingDirComponent1;
	private LabeledComponent rubyArgsComponent1;
	private LabeledComponent modulesComponent1;

	private LabeledComponent myRailsServerComponent;
	private LabeledComponent myPort;
	private LabeledComponent myIPAddr;

	private JRadioButton myUseAnyFreePortRadioButton;
	private JRadioButton myChoosePortManuallyRB;
	private LabeledComponent myRailsEnvironment;

	private EnvironmentVariablesComponent myEnvVariablesComponent1;

	private JTextField myIPAddrField;
	private JTextField myPortField;
	private JComboBox myRailsServerComboBox;
	private JComboBox myRailsEnvironmentComboBox;

	public RailsServerConfigurationForm(@NotNull final Project project, @NotNull final RubyRunConfiguration configuration)
	{
		super(project, configuration);

		initComponents1();
	}

	private void initComponents1()
	{
		// It's awful hack for LAZY and DRY people. Sorry =)
		//
		//noinspection BoundFieldAssignment
		generatedPanel = generatedPanel1;

		super.initComponents();

		// group the radio buttons
		final ButtonGroup portBG = new ButtonGroup();
		portBG.add(myChoosePortManuallyRB);
		portBG.add(myUseAnyFreePortRadioButton);

		myModulesComboBox.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				final Module module = getModule();
				if(module == null)
				{
					scriptPathTextField.setText("");
					workDirTextField.setText("");
				}
				else
				{
					final String text = RailsServerRunConfiguration.getServerScriptPathByModule(module);
					scriptPathTextField.setText(FileUtil.toSystemDependentName(TextUtil.getAsNotNull(text)));
					final String wd = RailsServerRunConfiguration.getRailsWorkDirByModule(module);
					workDirTextField.setText(FileUtil.toSystemDependentName(TextUtil.getAsNotNull(wd)));
				}
			}
		});
		scriptPathTextField.setEnabled(false);
		workDirTextField.setEnabled(false);

		myChoosePortManuallyRB.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				myPort.setEnabled(isChoosePortManually());
			}
		});
		myUseAnyFreePortRadioButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(final ActionEvent e)
			{
				myPort.setEnabled(isChoosePortManually());
			}
		});

		myRailsServerComboBox.setModel(new DefaultComboBoxModel(ExternalRailsSettings.getRailsServersTypes()));
		myRailsEnvironmentComboBox.setModel(new DefaultComboBoxModel(RailsEnvironmentType.values()));

		myRailsServerComboBox.setSelectedItem(RailsServerRunConfiguration.DEFAULT_SERVER);
		myRailsEnvironmentComboBox.setSelectedItem(RailsEnvironmentType.DEVELOPMENT);
	}

	private void createUIComponents()
	{
		final Ref<TextFieldWithBrowseButton> scriptTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
		scriptComponent1 = scriptComponent = RubyRunConfigurationUIUtil.createScriptPathComponent(scriptTextFieldWrapper, RBundle.message("run.configuration.messages.script.path"));
		scriptPathTextField = scriptTextFieldWrapper.get();

		scriptArgsComponent1 = scriptArgsComponent = createScriptArgsComponent();

		final Ref<TextFieldWithBrowseButton> wordDirComponentWrapper = new Ref<TextFieldWithBrowseButton>();
		workingDirComponent1 = workingDirComponent = RubyRunConfigurationUIUtil.createWorkDirComponent(wordDirComponentWrapper);
		workDirTextField = wordDirComponentWrapper.get();

		final Ref<RawCommandLineEditor> rubyArgsEditorWrapper = new Ref<RawCommandLineEditor>();
		rubyArgsComponent1 = rubyArgsComponent = RubyRunConfigurationUIUtil.createRubyArgsComponent(rubyArgsEditorWrapper);
		rubyArgsEditor = rubyArgsEditorWrapper.get();

		final Ref<JComboBox> modulesComboBoxWrapper = new Ref<JComboBox>();
		modulesComponent1 = modulesComponent = RubyRunConfigurationUIUtil.createModulesComponent(modulesComboBoxWrapper);
		myModulesComboBox = modulesComboBoxWrapper.get();

		myRailsServerComponent = createServersComponent();
		myRailsEnvironment = createEnvornmentsComponent();
		myPort = createPortComponent();
		myIPAddr = createIPAddrComponent();
	}

	private LabeledComponent createIPAddrComponent()
	{
		myIPAddrField = new JTextField();
		LabeledComponent<JTextField> myComponent = new LabeledComponent<JTextField>();
		myComponent.setComponent(myIPAddrField);
		myComponent.setText(RBundle.message("run.configuration.server.dialog.ip"));

		return myComponent;
	}

	private LabeledComponent createPortComponent()
	{
		myPortField = new JTextField();

		LabeledComponent<JTextField> myComponent = new LabeledComponent<JTextField>();
		myComponent.setComponent(myPortField);
		myComponent.setLabelLocation(BorderLayout.WEST);
		myComponent.setText(RBundle.message("run.configuration.server.dialog.port"));

		return myComponent;
	}

	private LabeledComponent createServersComponent()
	{
		myRailsServerComboBox = new ComboBox();
		myRailsServerComboBox.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if(value != null)
				{
					setText(ExternalRailsSettings.getRailsServersTitlesByType((String) value));
				}
				return this;
			}
		});

		LabeledComponent<JComboBox> myComponent = new LabeledComponent<JComboBox>();
		myComponent.setComponent(myRailsServerComboBox);
		myComponent.setText(RBundle.message("run.configuration.server.dialog.server"));
		return myComponent;
	}

	private LabeledComponent createEnvornmentsComponent()
	{
		myRailsEnvironmentComboBox = new ComboBox();
		myRailsEnvironmentComboBox.setRenderer(new DefaultListCellRenderer()
		{
			@Override
			public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus)
			{
				super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
				if(value != null)
				{
					setText(((RailsEnvironmentType) value).getParamName());
				}
				return this;
			}
		});

		LabeledComponent<JComboBox> myComponent = new LabeledComponent<JComboBox>();
		myComponent.setComponent(myRailsEnvironmentComboBox);
		myComponent.setText(RBundle.message("run.configuration.server.dialog.environment"));
		return myComponent;
	}

	@Override
	protected LabeledComponent createScriptArgsComponent()
	{
		final LabeledComponent myComponent = super.createScriptArgsComponent();
		myComponent.setText(RBundle.message("run.configuration.server.args"));
		return myComponent;
	}

	@Override
	@NotNull
	public String getIPAddr()
	{
		return myIPAddrField.getText().trim();
	}

	@Override
	public void setIPAddr(final String ip)
	{
		myIPAddrField.setText(ip);
	}

	@Override
	public String getPort()
	{
		return myPortField.getText().trim();
	}

	@Override
	public void setPort(final String port)
	{
		myPortField.setText(port);
	}

	@Override
	public boolean isChoosePortManually()
	{
		return myChoosePortManuallyRB.isSelected();
	}

	@Override
	public void setChoosePortManually(final boolean choosePortManually)
	{
		if(choosePortManually)
		{
			myChoosePortManuallyRB.doClick();
		}
		else
		{
			myUseAnyFreePortRadioButton.doClick();
		}
	}

	@Override
	@NotNull
	public String getServerType()
	{
		return (String) myRailsServerComboBox.getSelectedItem();
	}

	@Override
	public void setServerType(final String type)
	{
		myRailsServerComboBox.setSelectedItem(type);
	}

	@Override
	public void setRailsEnvironmentType(@NotNull final RailsServerRunConfiguration.RailsEnvironmentType type)
	{
		myRailsEnvironmentComboBox.setSelectedItem(type);
	}

	@Override
	@NotNull
	public RailsServerRunConfiguration.RailsEnvironmentType getRailsEnvironmentType()
	{
		final Object selectedObject = myRailsEnvironmentComboBox.getSelectedItem();
		return ((RailsServerRunConfiguration.RailsEnvironmentType) selectedObject);
	}

	@Override
	public Map<String, String> getEnvs()
	{
		return myEnvVariablesComponent1.getEnvs();
	}

	@Override
	public void setEnvs(@NotNull final Map<String, String> envs)
	{
		myEnvVariablesComponent1.setEnvs(envs);
	}
}
