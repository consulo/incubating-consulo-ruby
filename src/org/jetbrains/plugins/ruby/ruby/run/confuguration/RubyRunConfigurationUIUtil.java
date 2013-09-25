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

package org.jetbrains.plugins.ruby.ruby.run.confuguration;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui.SDKListCellRenderer;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkTable;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.ui.RawCommandLineEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 20, 2007
 */
public class RubyRunConfigurationUIUtil {
    public static LabeledComponent createAlternativeSdksComponent(final Ref<JComboBox> alternativeSdksComboBoxWrapper) {
        JComboBox alternativeSdksComboBox = new ComboBox();
        alternativeSdksComboBox.setBorder(BorderFactory.createEtchedBorder());
        alternativeSdksComboBox.setRenderer(new SDKListCellRenderer());

        LabeledComponent<JComboBox> myComponent = new LabeledComponent<JComboBox>();
        myComponent.setComponent(alternativeSdksComboBox);
        myComponent.setLabelLocation(BorderLayout.WEST);
        myComponent.setText(RBundle.message("run.configuration.dialog.components.sdk"));

        alternativeSdksComboBoxWrapper.set(alternativeSdksComboBox);
        return myComponent;
    }

    public static LabeledComponent createDirChooserComponent(final Ref<TextFieldWithBrowseButton> dirTFWrapper,
                                                             final String text) {
        final TextFieldWithBrowseButton dirTextField = new TextFieldWithBrowseButton();
        dirTFWrapper.set(dirTextField);

        LabeledComponent<TextFieldWithBrowseButton> myComponent = new LabeledComponent<TextFieldWithBrowseButton>();
        myComponent.setComponent(dirTextField);
        myComponent.setText(text);

        return myComponent;
    }

    public static LabeledComponent createWorkDirComponent(final Ref<TextFieldWithBrowseButton> workDirTFWrapper) {
        return createDirChooserComponent(workDirTFWrapper, RBundle.message("run.configuration.messages.working.dir"));
    }

    public static LabeledComponent createRubyArgsComponent(final Ref<RawCommandLineEditor> rubyArgsEditorWrapper) {
        final String dialogCaption = RBundle.message("run.configuration.messages.edit.ruby.args");
        final String text = RBundle.message("run.configuration.messages.ruby.args");
        return createRawEditorComponent(rubyArgsEditorWrapper, dialogCaption, text);
    }

    public static LabeledComponent<RawCommandLineEditor> createRawEditorComponent(final Ref<RawCommandLineEditor> rawEditorWrapper,
                                                            final String dialogCaption,
                                                            final String labelTextWithMnemonic) {
        final RawCommandLineEditor rawEditor = new RawCommandLineEditor();
        rawEditorWrapper.set(rawEditor);

        rawEditor.setDialogCaption(dialogCaption);

        LabeledComponent<RawCommandLineEditor> myComponent = new LabeledComponent<RawCommandLineEditor>();
        myComponent.setComponent(rawEditor);
        myComponent.setText(labelTextWithMnemonic);

        return myComponent;
    }

    public static LabeledComponent createModulesComponent(final Ref<JComboBox> modulesComboBoxWrapper) {
        final JComboBox modulesComboBox = new ComboBox();
        modulesComboBoxWrapper.set(modulesComboBox);

        modulesComboBox.setRenderer(new ModuleListCellRenderer());

        LabeledComponent<JComboBox> myComponent = new LabeledComponent<JComboBox>();
        myComponent.setComponent(modulesComboBox);
        myComponent.setText(RBundle.message("run.configuration.messages.select.module"));
        return myComponent;
    }

    public static LabeledComponent createTestFileMaskComponent(final Ref<JTextField> testFileMaskTFWrapper, final String text) {
        final JTextField testFileMaskTextField = new JTextField();
        testFileMaskTFWrapper.set(testFileMaskTextField);

        LabeledComponent<JTextField> myComponent = new LabeledComponent<JTextField>();
        myComponent.setComponent(testFileMaskTextField);
        myComponent.setText(text);

        return myComponent;
    }

    public static LabeledComponent<TextFieldWithBrowseButton> createScriptPathComponent(final Ref<TextFieldWithBrowseButton> testScriptTextFieldWrapper,
                                                                                        final String text) {
        final TextFieldWithBrowseButton testScriptTextField = new TextFieldWithBrowseButton();
        testScriptTextFieldWrapper.set(testScriptTextField);

        LabeledComponent<TextFieldWithBrowseButton> myComponent = new LabeledComponent<TextFieldWithBrowseButton>();
        myComponent.setComponent(testScriptTextField);
        myComponent.setText(text);

        return myComponent;
    }

    public static LabeledComponent createTestFolderComponent(final Ref<TextFieldWithBrowseButton> testsFolderTextFieldWrapper) {
        final TextFieldWithBrowseButton testsFolderTextField = new TextFieldWithBrowseButton();
        testsFolderTextFieldWrapper.set(testsFolderTextField);

        LabeledComponent<TextFieldWithBrowseButton> myComponent = new LabeledComponent<TextFieldWithBrowseButton>();
        myComponent.setComponent(testsFolderTextField);
        myComponent.setText(RBundle.message("run.configuration.messages.folder.path"));

        return myComponent;
    }

    public static void initCommonComponents(final AbstractRubyRunConfiguration myConfiguration,
                                            final JComboBox myModulesComboBox,
                                            final JComboBox myAlternativeSdksComboBox) {
// setting modules
        myModulesComboBox.setModel(new DefaultComboBoxModel(myConfiguration.getModules()));
//setting skds
        final ArrayList<Sdk> foundSdks = new ArrayList<Sdk>();
        final Sdk[] allSdk = SdkTable.getInstance().getAllSdks();
        for (Sdk sdk : allSdk) {
            if (RubySdkUtil.isSDKValid(sdk)) {
                foundSdks.add(sdk);
            }
        }
        myAlternativeSdksComboBox.setModel(new DefaultComboBoxModel(foundSdks.toArray(new Sdk[foundSdks.size()])));
    }

    public static FileChooserDescriptor addFolderChooser(@NotNull final String title,
                                        @NotNull final TextFieldWithBrowseButton textField,
                                        final Project project) {
        final FileChooserDescriptor folderChooserDescriptor =
                FileChooserDescriptorFactory.createSingleFolderDescriptor();
        folderChooserDescriptor.setTitle(title);
        textField.addBrowseFolderListener(title, null, project, folderChooserDescriptor);
        return folderChooserDescriptor;
    }

    public static FileChooserDescriptor addFileChooser(final String title,
                                                       final TextFieldWithBrowseButton textField,
                                                       final Project project) {
        final FileChooserDescriptor fileChooserDescriptor =
                FileChooserDescriptorFactory.createSingleFileNoJarsDescriptor();
        fileChooserDescriptor.setTitle(title);
        textField.addBrowseFolderListener(title, null, project, fileChooserDescriptor);
        return fileChooserDescriptor;
    }

    public static void addAlternativeSDKActionListener(final JCheckBox useAlternativeSdkCB,
                                                       final LabeledComponent alternativeSdksComponent,
                                                       final JComboBox modulesComboBox) {
        useAlternativeSdkCB.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(final ActionEvent e) {
                final boolean useAlternativeSDK = useAlternativeSdkCB.isSelected();
                alternativeSdksComponent.setEnabled(useAlternativeSDK);
                modulesComboBox.setEnabled(!useAlternativeSDK);
            }
        });
    }

    public static void setShouldUseAlternSdk(boolean shouldUse,
                                             final JCheckBox useAlternativeSdkCB,
                                             final JComboBox alternativeSdksComboBox,
                                             final JComboBox myModulesComboBox) {
        useAlternativeSdkCB.setSelected(shouldUse);
        myModulesComboBox.setEnabled(!shouldUse);
        alternativeSdksComboBox.setEnabled(shouldUse);
    }
}
