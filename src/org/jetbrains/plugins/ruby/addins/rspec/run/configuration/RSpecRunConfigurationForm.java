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

package org.jetbrains.plugins.ruby.addins.rspec.run.configuration;

import static org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration.TestType;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.RawCommandLineEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.07.2007
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class RSpecRunConfigurationForm implements RSpecRunConfigurationParams {
    private JPanel myConfigurationPanel;

    private JRadioButton myAllInFolderRB;
    private JRadioButton myTestScriptRB;
    private JLabel myTestTypeComponent;
    private LabeledComponent myWorkingDirComponent;
    private LabeledComponent myRubyArgsComponent;
    private LabeledComponent mySpecsFolderComponent;
    private LabeledComponent myModulesComponent;
    private LabeledComponent mySpecsScriptComponent;
    private LabeledComponent mySpecsFileMaskComponent;
    private JCheckBox myUseAlternativeSdkGemCB;
    private LabeledComponent myAlternativeSdksComponent;
    private JCheckBox myCBEnableColouredOutput;
    private LabeledComponent mySpecsArgsComponent;
    private LabeledComponent myCustomSpecsRunnerComponent;
    private JCheckBox myCBUseCustomSpecRunner;
    private JCheckBox myCBRunSpecsSeparately;

    private TextFieldWithBrowseButton mySpecsFolderTextField;
    private TextFieldWithBrowseButton mySpecsScriptTextField;
    private TextFieldWithBrowseButton myCustomSpecsRunnerTextField;
    private TextFieldWithBrowseButton myWorkDirTextField;
    private JTextField mySpecsFileMaskTextField;

    private RawCommandLineEditor myRubyArgsEditor;
    private RawCommandLineEditor mySpecsArgsEditor;
    private JComboBox myModulesComboBox;
    private JComboBox myAlternativeSdksComboBox;

    private final Project myProject;
    private final RSpecRunConfiguration myConfiguration;

    //System Environment
    protected EnvironmentVariablesComponent myEnvVariablesComponent;

    public RSpecRunConfigurationForm(@NotNull final Project project,
                                     @NotNull final RSpecRunConfiguration configuration) {
        myProject = project;
        myConfiguration = configuration;
        initComponents();
    }

    private void initComponents() {
        RubyRunConfigurationUIUtil.initCommonComponents(myConfiguration, myModulesComboBox, myAlternativeSdksComboBox);

// adding browse action to folder chooser
        String title = RBundle.message("run.configuration.messages.select.folder.path");
        RubyRunConfigurationUIUtil.addFolderChooser(title, mySpecsFolderTextField, myProject);

// adding browse action to script chooser
        title = RBundle.message("run.configuration.messages.select.ruby.scipt.path");
        RubyRunConfigurationUIUtil.addFileChooser(title, mySpecsScriptTextField, myProject);

// adding browse action to custom spec runner chooser
        title = RBundle.message("rspec.run.configuration.tests.dialog.components.select.spec.custom.runner");
        RubyRunConfigurationUIUtil.addFileChooser(title, myCustomSpecsRunnerTextField, myProject);

//// adding browse action to working dir chooser
        title = RBundle.message("run.configuration.messages.select.working.dir");
        RubyRunConfigurationUIUtil.addFolderChooser(title, myWorkDirTextField, myProject);

        final ActionListener testTypeListener = new ActionListener() {
            @Override
			public void actionPerformed(final ActionEvent e) {
                setTestType(getTestType());
            }
        };

        myAllInFolderRB.addActionListener(testTypeListener);
        myTestScriptRB.addActionListener(testTypeListener);

        RubyRunConfigurationUIUtil.addAlternativeSDKActionListener(myUseAlternativeSdkGemCB, myAlternativeSdksComponent, myModulesComboBox);
        myCBUseCustomSpecRunner.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(final ActionEvent e) {
                final boolean useCustomRunner = shouldUseCustomSpecRunner();
                myCustomSpecsRunnerComponent.setEnabled(useCustomRunner);
            }
        });

        setShouldUseAlternativeSdk(false);
        setShouldUseCustomSpecRunner(false);
    }


    private LabeledComponent createSpecArgsComponent() {
        mySpecsArgsEditor = new RawCommandLineEditor();

        mySpecsArgsEditor.setDialogCaption(RBundle.message("rspec.run.configuration.messages.edit.spec.args"));

        LabeledComponent<RawCommandLineEditor> myComponent = new LabeledComponent<RawCommandLineEditor>();
        myComponent.setComponent(mySpecsArgsEditor);
        myComponent.setText(RBundle.message("rspec.run.configuration.messages.args"));

        return myComponent;
    }

    private void createUIComponents() {
        mySpecsArgsComponent = createSpecArgsComponent();

        final Ref<TextFieldWithBrowseButton> testsFolderTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
        mySpecsFolderComponent = RubyRunConfigurationUIUtil.createTestFolderComponent(testsFolderTextFieldWrapper);
        mySpecsFolderTextField = testsFolderTextFieldWrapper.get();

        final Ref<TextFieldWithBrowseButton> testScriptTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
        final String specFileTitle = RBundle.message("rspec.run.configuration.messages.script.path");
        mySpecsScriptComponent = RubyRunConfigurationUIUtil.createScriptPathComponent(testScriptTextFieldWrapper, specFileTitle);
        mySpecsScriptTextField = testScriptTextFieldWrapper.get();

        final Ref<TextFieldWithBrowseButton> specRunnerScriptTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
        final String specRunnerTitle = RBundle.message("rspec.run.configuration.tests.dialog.components.spec.custom.runner");
        myCustomSpecsRunnerComponent = RubyRunConfigurationUIUtil.createScriptPathComponent(specRunnerScriptTextFieldWrapper, specRunnerTitle);
        myCustomSpecsRunnerComponent.setLabelLocation(BorderLayout.WEST);
        myCustomSpecsRunnerTextField = specRunnerScriptTextFieldWrapper.get();

        final Ref<TextFieldWithBrowseButton> wordDirComponentWrapper = new Ref<TextFieldWithBrowseButton>();
        myWorkingDirComponent = RubyRunConfigurationUIUtil.createWorkDirComponent(wordDirComponentWrapper);
        myWorkDirTextField = wordDirComponentWrapper.get();

        final Ref<RawCommandLineEditor> rubyArgsEditorWrapper = new Ref<RawCommandLineEditor>();
        myRubyArgsComponent = RubyRunConfigurationUIUtil.createRubyArgsComponent(rubyArgsEditorWrapper);
        myRubyArgsEditor = rubyArgsEditorWrapper.get();

        final Ref<JComboBox> modulesComboBoxWrapper = new Ref<JComboBox>();
        myModulesComponent = RubyRunConfigurationUIUtil.createModulesComponent(modulesComboBoxWrapper);
        myModulesComboBox = modulesComboBoxWrapper.get();

        final Ref<JTextField> testFileMaskTextFieldWrapper = new Ref<JTextField>();
        final String text = RBundle.message("rspec.run.configuration.tests.dialog.components.search.mask");
        mySpecsFileMaskComponent = RubyRunConfigurationUIUtil.createTestFileMaskComponent(testFileMaskTextFieldWrapper, text);
        mySpecsFileMaskTextField = testFileMaskTextFieldWrapper.get();

        final Ref<JComboBox> altSdksComboBoxWrapper = new Ref<JComboBox>();
        myAlternativeSdksComponent = RubyRunConfigurationUIUtil.createAlternativeSdksComponent(altSdksComboBoxWrapper);
        myAlternativeSdksComboBox = altSdksComboBoxWrapper.get();
    }

    public JComponent getPanel() {
        return myConfigurationPanel;
    }

    public String getPath() {
        return FileUtil.toSystemIndependentName(mySpecsFolderTextField.getText().trim());
    }

    @Override
	public String getRubyArgs() {
        return myRubyArgsEditor.getText().trim();
    }

    @Override
	public String getWorkingDirectory() {
        return FileUtil.toSystemIndependentName(myWorkDirTextField.getText().trim());
    }

    @Override
	public Module getModule() {
        final Object selectedObject = myModulesComboBox.getSelectedItem();
        return selectedObject instanceof Module ? (Module) selectedObject : null;
    }

    @Override
	public Sdk getAlternativeSdk() {
        final Object selectedObject = myAlternativeSdksComboBox.getSelectedItem();
        return selectedObject instanceof Sdk ? (Sdk) selectedObject : null;
    }

    @Override
	public boolean shouldUseAlternativeSdk() {
        return myUseAlternativeSdkGemCB.isSelected();
    }

    @Override
	public void setShouldUseAlternativeSdk(final boolean shouldUse) {
        RubyRunConfigurationUIUtil.setShouldUseAlternSdk(shouldUse, myUseAlternativeSdkGemCB, myAlternativeSdksComboBox, myModulesComboBox);
    }

    @Override
	public boolean shouldUseCustomSpecRunner() {
        return myCBUseCustomSpecRunner.isSelected();
    }

    @Override
	public void setShouldUseCustomSpecRunner(final boolean shouldUse) {
        myCBUseCustomSpecRunner.setSelected(shouldUse);
        myCustomSpecsRunnerComponent.setEnabled(shouldUse);
    }

    @Override
	public String getTestsFolderPath() {
        return FileUtil.toSystemIndependentName(mySpecsFolderTextField.getText().trim());
    }

    @Override
	public String getTestScriptPath() {
        return FileUtil.toSystemIndependentName(mySpecsScriptTextField.getText().trim());
    }

    @Override
	public String getCustomSpecsRunnerPath() {
        return FileUtil.toSystemIndependentName(myCustomSpecsRunnerTextField.getText().trim());
    }

    @Override
	public String getTestFileMask() {
        return mySpecsFileMaskTextField.getText();
    }

    public void setPath(final String value) {
        mySpecsFolderTextField.setText(FileUtil.toSystemDependentName(TextUtil.getAsNotNull(value)));
    }

    @Override
	public void setRubyArgs(final String value) {
        myRubyArgsEditor.setText(value);
    }

    @Override
	public void setWorkingDirectory(final String value) {
        myWorkDirTextField.setText(FileUtil.toSystemDependentName(TextUtil.getAsNotNull(value)));
    }

    @Override
	public void setModule(@Nullable final Module module) {
        myModulesComboBox.setSelectedItem(module);
    }

    @Override
	public void setAlternativeSdk(@Nullable final Sdk sdk) {
        myAlternativeSdksComboBox.setSelectedItem(sdk);
    }

    @Override
	public void setTestsFolderPath(final String path) {
        mySpecsFolderTextField.setText(FileUtil.toSystemDependentName(TextUtil.getAsNotNull(path)));
    }

    @Override
	public void setTestScriptPath(final String path) {
        mySpecsScriptTextField.setText(FileUtil.toSystemDependentName(TextUtil.getAsNotNull(path)));
    }

    @Override
	public void setCustomSpecsRunnerPath(final String path) {
        myCustomSpecsRunnerTextField.setText(FileUtil.toSystemDependentName((TextUtil.getAsNotNull(path))));
    }


    @Override
	public void setTestFileMask(final String name) {
        mySpecsFileMaskTextField.setText(name);
    }

    @Override
	public TestType getTestType() {
        if (myAllInFolderRB.isSelected()) {
            return TestType.ALL_IN_FOLDER;
        } else {
            return TestType.TEST_SCRIPT;
        }
    }

    @Override
	public boolean shouldUseColoredOutput() {
        return myCBEnableColouredOutput.isSelected();
    }

    @Override
	public void setShouldUseColoredOutput(final boolean disabled) {
        myCBEnableColouredOutput.setSelected(disabled);
    }

    @Override
	public boolean shouldRunSpecSeparately() {
        return myCBRunSpecsSeparately.isSelected();
    }

    @Override
	public void setShouldRunSpecSeparately(final boolean disabled) {
        myCBRunSpecsSeparately.setSelected(disabled);
    }

    @Override
	public void setTestType(@NotNull final TestType testType) {
        clearTestTypeSettings(testType);

        mySpecsScriptComponent.setEnabled(true);

        switch (testType) {
            case ALL_IN_FOLDER:
                myAllInFolderRB.setSelected(true);

                mySpecsFolderComponent.setEnabled(true);
                mySpecsFileMaskComponent.setEnabled(true);

                mySpecsScriptComponent.setEnabled(false);
                myCBRunSpecsSeparately.setEnabled(true);
                break;
            case TEST_SCRIPT:
                myTestScriptRB.setSelected(true);

                mySpecsScriptComponent.setEnabled(true);

                mySpecsFolderComponent.setEnabled(false);
                mySpecsFileMaskComponent.setEnabled(false);
                myCBRunSpecsSeparately.setEnabled(false);
                break;
        }
    }

    private void clearTestTypeSettings(final TestType testType) {
        setTestsFolderPath("");
        setTestScriptPath("");

        final String value = (testType == TestType.ALL_IN_FOLDER
                ? RSpecRunConfiguration.DEFAULT_TESTS_SEARCH_MASK : "");
        setTestFileMask(value);
    }

    @Override
	public void setSpecArgs(final String specArgs) {
        mySpecsArgsEditor.setText(specArgs);
    }

    @Override
	public String getSpecArgs() {
       return mySpecsArgsEditor.getText().trim();
    }

    @Override
	public void setEnvs(final Map<String, String> envs) {
        myEnvVariablesComponent.setEnvs(envs);
    }

    @Override
	public void setPassParentEnvs(final boolean passParentEnvs) {
        myEnvVariablesComponent.setPassParentEnvs(passParentEnvs);
    }

    @Override
	public Map<String, String> getEnvs() {
        return myEnvVariablesComponent.getEnvs();
    }

    @Override
	public boolean isPassParentEnvs() {
        return myEnvVariablesComponent.isPassParentEnvs();
    }
}