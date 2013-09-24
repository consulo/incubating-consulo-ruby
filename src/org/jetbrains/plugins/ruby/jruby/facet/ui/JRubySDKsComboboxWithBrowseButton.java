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

package org.jetbrains.plugins.ruby.jruby.facet.ui;

import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.projectRoots.ui.ProjectJdksEditor;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.ComboboxWithBrowseButton;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.ui.SDKListCellRenderer;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
*
* @author: Roman.Chernyatchik
* @date: 22.07.2008
*/
public class JRubySDKsComboboxWithBrowseButton extends ComboboxWithBrowseButton {
    public JRubySDKsComboboxWithBrowseButton() {
        final JComboBox sdkCombobox = getComboBox();

        sdkCombobox.setRenderer(new SDKListCellRenderer());

        addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Project defaultProject = ProjectManager.getInstance().getDefaultProject();
                ProjectJdksEditor editor = new ProjectJdksEditor(null, defaultProject, JRubySDKsComboboxWithBrowseButton.this);
                editor.show();
                if (editor.isOK()) {
                    final ProjectJdk selectedJdk = editor.getSelectedJdk();
                    rebuildSdksListAndSelectSdk(selectedJdk);
                    if (!JRubySdkType.isJRubySDK(selectedJdk)) {
                        final String title = RBundle.message("jruby.select.sdk");
                        final String msg = RBundle.message("jruby.facet.error.wrong.sdk");
                        Messages.showErrorDialog(JRubySDKsComboboxWithBrowseButton.this, msg, title);
                    }
                }
            }
        });
    }

    public void addComboboxActionListener(final ActionListener l) {
        getComboBox().addActionListener(l);
    }

    public void rebuildSdksListAndSelectSdk(final ProjectJdk selectedSdk) {
        final ProjectJdk[] sdks = JRubySdkUtil.getValidSdks();

        final JComboBox sdksComboBox = getComboBox();
        sdksComboBox.setModel(new DefaultComboBoxModel(sdks));

        if (selectedSdk != null) {
            for (ProjectJdk candidateSdk : sdks) {
                if (candidateSdk != null && candidateSdk.getName().equals(selectedSdk.getName())) {
                    sdksComboBox.setSelectedItem(candidateSdk);
                    return;
                }
            }
        }
        sdksComboBox.setSelectedItem(null);
    }
}
