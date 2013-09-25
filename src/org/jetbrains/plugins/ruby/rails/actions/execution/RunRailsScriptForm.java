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

package org.jetbrains.plugins.ruby.rails.actions.execution;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;

import javax.swing.*;

public class RunRailsScriptForm extends JDialog {
    private JPanel myContentPane;

    protected RawCommandLineEditor scriptArgsEditor;
    @SuppressWarnings({"UnusedDeclaration"})
    private LabeledComponent<RawCommandLineEditor> scriptArgsComponent;

    protected TextFieldWithBrowseButton scriptPathTextField;
    @SuppressWarnings({"UnusedDeclaration"})
    private LabeledComponent<TextFieldWithBrowseButton> scriptPathComponent;

    private JLabel myDescriptionLabel;

    public RunRailsScriptForm(@NotNull final Project project,
                              @NotNull final String moduleName,
                              @NotNull final VirtualFile moduleScriptFolder) {
        myDescriptionLabel.setText(RBundle.message("rails.actions.execution.run.rails.script.dialog.description.caption", moduleName));

        setContentPane(myContentPane);
        setModal(true);

// adding browse action to script chooser
        String selectScriptTitle = RBundle.message("rails.actions.execution.run.rails.script.dialog.component.script.path.caption.select");
        final FileChooserDescriptor fileChooserDescriptor =
                RubyRunConfigurationUIUtil.addFileChooser(selectScriptTitle, scriptPathTextField, project);
        fileChooserDescriptor.setRoot(moduleScriptFolder);
    }

    public RawCommandLineEditor getArgumentsComponent() {
        return scriptArgsEditor;
    }

    public TextFieldWithBrowseButton getScriptNameComponent() {
        return scriptPathTextField;
    }

    @Override
	public JPanel getContentPane() {
        return myContentPane;
    }

    private void createUIComponents() {
        final String dialogCaption = RBundle.message("rails.actions.execution.run.rails.script.dialog.component.script.args.caption.edit");
        final String text = RBundle.message("rails.actions.execution.run.rails.script.dialog.component.script.args.caption");

        final Ref<RawCommandLineEditor> scriptArgsEditorWrapper = new Ref<RawCommandLineEditor>();
        scriptArgsComponent = RubyRunConfigurationUIUtil.createRawEditorComponent(scriptArgsEditorWrapper, dialogCaption, text);
        scriptArgsEditor = scriptArgsEditorWrapper.get();

        final Ref<TextFieldWithBrowseButton> scriptPathTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
        final String scriptNameLabel = RBundle.message("rails.actions.execution.run.rails.script.dialog.component.script.path.caption");
        scriptPathComponent = RubyRunConfigurationUIUtil.createScriptPathComponent(scriptPathTextFieldWrapper, scriptNameLabel);
        scriptPathTextField = scriptPathTextFieldWrapper.get();
    }
}
