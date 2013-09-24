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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.FacetWizardStep;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RubyModuleBuilder;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.ui.RubySdkChooserPanel;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * @author: oleg
 * @date: 16.08.2006
 */
public class RubySdkSelectStep extends FacetWizardStep {
    protected final RubySdkChooserPanel myPanel;
    protected final RubyModuleBuilder mySettingsHolder;

    private Icon myIcon;
    private String myHelp;

    public RubySdkSelectStep(@NotNull final RubyModuleBuilder settingsHolder,
                             @Nullable final Icon icon,
                             @Nullable  final String helpId,
                             @Nullable final Project project) {
        super();
        myIcon = icon;
        mySettingsHolder = settingsHolder;
        myPanel = new RubySdkChooserPanel(project);
        myHelp = helpId;
    }

    public String getHelpId() {
        return myHelp;
    }

    public JComponent getPreferredFocusedComponent() {
        return myPanel.getPreferredFocusedComponent();
    }

    public JComponent getComponent() {
        return myPanel;
    }


    public void updateDataModel() {
        final ProjectJdk sdk = getSdk();
        mySettingsHolder.setSdk(sdk);

        NiiChAVOUtil.putJRubyFacetSdkMagic(myPanel, sdk);
        NiiChAVOUtil.revalidateRailsFacetSettings(myPanel);
    }

    private ProjectJdk getSdk() {
        return myPanel.getChosenJdk();
    }

    public Icon getIcon() {
        return myIcon;
    }

    public boolean validate() {
        ProjectJdk jdk = myPanel.getChosenJdk();
        if (jdk==null){
            int result = Messages.showYesNoDialog(
                    RBundle.message("sdk.error.no.sdk.prompt.messge.confirm.without.sdk"),
                    RBundle.message("sdk.select.prompt.title"),
                    Messages.getWarningIcon()
            );
            return result == DialogWrapper.OK_EXIT_CODE;
        }
        if (!RubySdkUtil.isKindOfRubySDK(jdk)) {
            Messages.showErrorDialog(
                    RBundle.message("sdk.error.prompt.message.sdk.not.valid"),
                    RBundle.message("sdk.select.prompt.title")
            );
            return false;
        }
/* try to set selected SDK as Project default SDK
        final ProjectRootManager projectRootManager = ProjectRootManager.getInstance(myProject);
        if (!jdk.equals(projectRootManager.getProjectJdk())) {
            int result = Messages.showYesNoDialog(
                    RBundle.message("prompt.confirm.default.sdk.change"),
                    RBundle.message("title.default.sdk.change"),
                    Messages.getQuestionIcon()
            );
            if (result == 1) {
                projectRootManager.setProjectJdk(jdk);
            }
        }*/
        return true;
    }
}
