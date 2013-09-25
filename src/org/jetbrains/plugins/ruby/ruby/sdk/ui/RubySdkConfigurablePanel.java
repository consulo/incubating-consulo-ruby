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

package org.jetbrains.plugins.ruby.ruby.sdk.ui;

import static com.intellij.openapi.util.io.FileUtil.toSystemDependentName;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.EtchedBorder;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 28, 2007
 */
public class RubySdkConfigurablePanel{
    private JPanel mainPanel;

    // ruby interpreter
    private LabeledComponent<JTextPane> myRubyInterpreterComponent;
    private JTextPane myRubyInterpreterPathTP;

    // gems bin folder
    private LabeledComponent myGemsBinFolderComponent;
    protected TextFieldWithBrowseButton myGemsBinFolderTF;

    public RubySdkConfigurablePanel() {

    }

    private void createUIComponents() {
        myRubyInterpreterPathTP = new JTextPane();
        myRubyInterpreterPathTP.setEditable(false);
        myRubyInterpreterPathTP.setEnabled(false);
        myRubyInterpreterPathTP.setBorder(new EtchedBorder());
        myRubyInterpreterComponent = new LabeledComponent<JTextPane>();
        myRubyInterpreterComponent.setComponent(myRubyInterpreterPathTP);
        myRubyInterpreterComponent.setText(RBundle.message("ruby.configuration.interpreter.path"));

        final Ref<TextFieldWithBrowseButton> gemsBinDirComponentWrapper = new Ref<TextFieldWithBrowseButton>();
        final String gemsBinDirChooserTitle = RBundle.message("ruby.configuration.gems.bin.dir.path");
        myGemsBinFolderComponent = RubyRunConfigurationUIUtil.createDirChooserComponent(gemsBinDirComponentWrapper, gemsBinDirChooserTitle);
        myGemsBinFolderTF = gemsBinDirComponentWrapper.get();
        myGemsBinFolderComponent.setToolTipText(RBundle.message("ruby.configuration.gems.bin.dir.path.tooltip"));

		RubyRunConfigurationUIUtil.addFileChooser(RBundle.message("ruby.configuration.gems.bin.dir.select.path"), myGemsBinFolderTF, null);
	}

    public JComponent getPanel() {
        return mainPanel;
    }

    public void setRubyText(String path) {
        myRubyInterpreterPathTP.setText(toSystemDependentName(path));
    }

    public String getGemsBinFolder() {
        final String text = myGemsBinFolderTF.getText().trim();
        return VirtualFileUtil.convertToVFSPathAndNormalizeSlashes(text);
    }

    public void setGemsBinFolder(@NotNull final String path) {
        myGemsBinFolderTF.setText(toSystemDependentName(path));
    }
}
