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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests;

import static com.intellij.openapi.util.io.FileUtil.toSystemDependentName;
import static com.intellij.openapi.util.io.FileUtil.toSystemIndependentName;
import static org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration.TestType;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui.TestCaseClassBrowser;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui.TestMethodBrowser;
import com.intellij.execution.configuration.BrowseModuleValueActionListener;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.RawCommandLineEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.07.2007
 */
@SuppressWarnings({"UnusedDeclaration", "FieldCanBeLocal"})
public class RTestsRunConfigurationForm implements RTestRunConfigurationParams {
    private JPanel myConfigurationPanel;

    private LabeledComponent myTestClassComponent;
    private JRadioButton myAllInFolderRB;
    private JRadioButton myTestClassRB;
    private JRadioButton myTestMethodRB;
    private JRadioButton myTestScriptRB;
    private JLabel myTestTypeComponent;
    private LabeledComponent myWorkingDirComponent;
    private LabeledComponent myRubyArgsComponent;
    private LabeledComponent myTestMethodComponent;
    private LabeledComponent myTestFolderComponent;
    private LabeledComponent myModulesComponent;
    private LabeledComponent myTestScriptComponent;
    private LabeledComponent myTestFileMaskComponent;
    private JCheckBox myUseAlternativeSdkCB;
    private LabeledComponent myAlternativeSdksComponent;
    private JCheckBox myRBDisableInheritanceCheck;

    private TextFieldWithBrowseButton myTestFolderTextField;
    private TextFieldWithBrowseButton myTestScriptTextField;
    private TextFieldWithBrowseButton myTestMethodTextField;
    private TextFieldWithBrowseButton myWorkDirTextField;
    private TextFieldWithBrowseButton myTestClassField;

    private JTextField myTestFileMaskTextField;

    private RawCommandLineEditor myRubyArgsEditor;
    private JComboBox myModulesComboBox;
    private JComboBox myAlternativeSdksComboBox;

    private final Project myProject;
    private final RTestsRunConfiguration myConfiguration;

    //System Environment
    protected EnvironmentVariablesComponent myEnvVariablesComponent;

    public RTestsRunConfigurationForm(@NotNull final Project project,
                                      @NotNull final RTestsRunConfiguration configuration){
        myProject = project;
        myConfiguration = configuration;
        initComponents();
    }

    private void initComponents() {
        RubyRunConfigurationUIUtil.initCommonComponents(myConfiguration, myModulesComboBox, myAlternativeSdksComboBox);

// adding browse action to folder chooser
        String title = RBundle.message("run.configuration.messages.select.folder.path");
        RubyRunConfigurationUIUtil.addFolderChooser(title, myTestFolderTextField, myProject);

// adding browse action to script chooser
        title = RBundle.message("run.configuration.messages.select.ruby.scipt.path");
        RubyRunConfigurationUIUtil.addFileChooser(title, myTestScriptTextField, myProject);

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
        myTestClassRB.addActionListener(testTypeListener);
        myTestMethodRB.addActionListener(testTypeListener);

        RubyRunConfigurationUIUtil.addAlternativeSDKActionListener(myUseAlternativeSdkCB, myAlternativeSdksComponent, myModulesComboBox);
        setShouldUseAlternativeSdk(false);

        myRBDisableInheritanceCheck.addItemListener(new ItemListener() {
            @Override
			public void itemStateChanged(ItemEvent e) {
                myTestScriptComponent.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
            }
        });
    }

    private LabeledComponent createTestClassComponent() {
        myTestClassField = new TextFieldWithBrowseButton();

        LabeledComponent<TextFieldWithBrowseButton> myComponent = new LabeledComponent<TextFieldWithBrowseButton>();
        myComponent.setComponent(myTestClassField);
        myComponent.setText(RBundle.message("run.configuration.tests.dialog.components.class"));

        BrowseModuleValueActionListener classBrowser = new TestCaseClassBrowser(myProject, this);

        classBrowser.setField(myTestClassField);

        return myComponent;
    }

    private LabeledComponent createTestMethodComponent() {
        myTestMethodTextField = new TextFieldWithBrowseButton();

        LabeledComponent<TextFieldWithBrowseButton> myComponent = new LabeledComponent<TextFieldWithBrowseButton>();

        myComponent.setComponent(myTestMethodTextField);
        myComponent.setText(RBundle.message("run.configuration.tests.messages.method.name"));

        BrowseModuleValueActionListener methodBrowser = new TestMethodBrowser(myProject, this);
        methodBrowser.setField(myTestMethodTextField);

        return myComponent;
    }

    private LabeledComponent createWorkDirComponent() {
        myWorkDirTextField = new TextFieldWithBrowseButton();

        LabeledComponent<TextFieldWithBrowseButton> myComponent = new LabeledComponent<TextFieldWithBrowseButton>();
        myComponent.setComponent(myWorkDirTextField);
        myComponent.setText(RBundle.message("run.configuration.messages.working.dir"));

        return myComponent;
    }

    private void createUIComponents() {
        myTestClassComponent = createTestClassComponent();
        myTestMethodComponent = createTestMethodComponent();

        final Ref<TextFieldWithBrowseButton> testsFolderTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
        myTestFolderComponent = RubyRunConfigurationUIUtil.createTestFolderComponent(testsFolderTextFieldWrapper);
        myTestFolderTextField = testsFolderTextFieldWrapper.get();

        final Ref<TextFieldWithBrowseButton> testScriptTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
        myTestScriptComponent = RubyRunConfigurationUIUtil.createScriptPathComponent(testScriptTextFieldWrapper, RBundle.message("run.configuration.messages.script.path"));
        myTestScriptTextField = testScriptTextFieldWrapper.get(); 

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
        final String text = RBundle.message("run.configuration.tests.dialog.components.search.mask");
        myTestFileMaskComponent = RubyRunConfigurationUIUtil.createTestFileMaskComponent(testFileMaskTextFieldWrapper, text);
        myTestFileMaskTextField = testFileMaskTextFieldWrapper.get();

        final Ref<JComboBox> altSdksComboBoxWrapper = new Ref<JComboBox>();
        myAlternativeSdksComponent = RubyRunConfigurationUIUtil.createAlternativeSdksComponent(altSdksComboBoxWrapper);
        myAlternativeSdksComboBox = altSdksComboBoxWrapper.get();
    }

    public JComponent getPanel(){
        return myConfigurationPanel;
    }

    public String getPath(){
        return toSystemIndependentName(myTestFolderTextField.getText().trim());
    }

    @Override
	public String getRubyArgs(){
        return myRubyArgsEditor.getText().trim();
    }

    @Override
	public String getWorkingDirectory(){
        return toSystemIndependentName(myWorkDirTextField.getText().trim());
    }

    @Override
	public Module getModule(){
        final Object selectedObject = myModulesComboBox.getSelectedItem();
        return selectedObject instanceof Module ? (Module)selectedObject : null;
    }

    @Override
	public Sdk getAlternativeSdk(){
        final Object selectedObject = myAlternativeSdksComboBox.getSelectedItem();
        return selectedObject instanceof Sdk ? (Sdk)selectedObject : null;
    }

    @Override
	public boolean shouldUseAlternativeSdk() {
        return myUseAlternativeSdkCB.isSelected();
    }

    @Override
	public void setShouldUseAlternativeSdk(final boolean shouldUse) {
        RubyRunConfigurationUIUtil.setShouldUseAlternSdk(shouldUse, myUseAlternativeSdkCB, myAlternativeSdksComboBox, myModulesComboBox);
    }

    @Override
	public String getTestsFolderPath() {
        return toSystemIndependentName(myTestFolderTextField.getText().trim());
    }

    @Override
	public String getTestScriptPath() {
        return toSystemIndependentName(myTestScriptTextField.getText().trim());
    }

    @Override
	public String getTestQualifiedClassName() {
        return myTestClassField.getText().trim();
    }

    @Override
	public String getTestFileMask() {
        return myTestFileMaskTextField.getText();
    }

    @Override
	public String getTestMethodName() {
        return myTestMethodTextField.getText().trim();
    }

    public void setPath(final String value){
        myTestFolderTextField.setText(toSystemDependentName(TextUtil.getAsNotNull(value)));
    }

    @Override
	public void setRubyArgs(final String value){
        myRubyArgsEditor.setText(value);
    }

    @Override
	public void setWorkingDirectory(final String value){
        myWorkDirTextField.setText(toSystemDependentName(TextUtil.getAsNotNull(value)));
    }

    @Override
	public void setModule(@Nullable final Module module){
        myModulesComboBox.setSelectedItem(module);
    }

    @Override
	public void setAlternativeSdk(@Nullable final Sdk sdk){
        myAlternativeSdksComboBox.setSelectedItem(sdk);
    }

    @Override
	public void setTestsFolderPath(final String path) {
        myTestFolderTextField.setText(toSystemDependentName(TextUtil.getAsNotNull(path)));
    }

    @Override
	public void setTestScriptPath(final String path) {
        myTestScriptTextField.setText(toSystemDependentName(TextUtil.getAsNotNull(path)));
    }

    @Override
	public void setTestQualifiedClassName(final String name) {
        myTestClassField.setText(TextUtil.getAsNotNull(name));
    }

    @Override
	public void setTestFileMask(final String name) {
        myTestFileMaskTextField.setText(TextUtil.getAsNotNull(name));
    }

    @Override
	public void setTestMethodName(final String name) {
        myTestMethodTextField.setText(TextUtil.getAsNotNull(name));
    }

    @Override
	public RTestsRunConfiguration.TestType getTestType() {
        if (myAllInFolderRB.isSelected()) {
            return AbstractRubyRunConfiguration.TestType.ALL_IN_FOLDER;
        } else if (myTestScriptRB.isSelected()) {
            return AbstractRubyRunConfiguration.TestType.TEST_SCRIPT;
        } else if (myTestClassRB.isSelected()) {
            return AbstractRubyRunConfiguration.TestType.TEST_CLASS;
        } else {
            return AbstractRubyRunConfiguration.TestType.TEST_METHOD;
        }
    }

    @Override
	public boolean isInheritanceCheckDisabled() {
        return myRBDisableInheritanceCheck.isSelected();
    }

    @Override
	public void setInheritanceCheckDisabled(final boolean disabled) {
        myRBDisableInheritanceCheck.setSelected(disabled);
    }

    @Override
	public void setTestType(@NotNull final TestType testType) {
        clearTestTypeSettings(testType);

        myRBDisableInheritanceCheck.setSelected(false);
        myTestScriptComponent.setEnabled(true);

        switch (testType) {
            case ALL_IN_FOLDER:
                myAllInFolderRB.setSelected(true);

                myTestFolderComponent.setEnabled(true);
                myTestFileMaskComponent.setEnabled(true);

                myTestScriptComponent.setEnabled(false);
                myTestClassComponent.setEnabled(false);
                myTestMethodComponent.setEnabled(false);
                myRBDisableInheritanceCheck.setEnabled(false);

                break;
            case TEST_SCRIPT:
                myTestScriptRB.setSelected(true);

                myTestScriptTextField.setEnabled(true);

                myTestFolderComponent.setEnabled(false);
                myTestClassComponent.setEnabled(false);
                myTestFileMaskComponent.setEnabled(false);
                myTestMethodComponent.setEnabled(false);
                myRBDisableInheritanceCheck.setEnabled(false);

                break;
            case TEST_CLASS:
                myTestClassRB.setSelected(true);

                myTestClassComponent.setEnabled(true);

                myTestScriptComponent.setEnabled(false);
                myTestFolderComponent.setEnabled(false);
                myTestFileMaskComponent.setEnabled(false);
                myTestMethodComponent.setEnabled(false);
                myRBDisableInheritanceCheck.setEnabled(true);

                break;
            case TEST_METHOD:
                myTestMethodRB.setSelected(true);

                myTestMethodComponent.setEnabled(true);
                myTestClassComponent.setEnabled(true);

                myTestScriptComponent.setEnabled(false);
                myTestFolderComponent.setEnabled(false);
                myTestFileMaskComponent.setEnabled(false);
                myRBDisableInheritanceCheck.setEnabled(true);

                break;
        }

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

    private void clearTestTypeSettings(final TestType testType) {
        setTestsFolderPath("");
        setTestScriptPath("");
        setTestMethodName("");
        setTestQualifiedClassName("");

        final String value = (testType == AbstractRubyRunConfiguration.TestType.ALL_IN_FOLDER
                ? RTestsRunConfiguration.DEFAULT_TESTS_SEARCH_MASK : "");
        setTestFileMask(value);
    }
}
