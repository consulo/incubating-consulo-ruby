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

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.FacetWizardStep;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RubyModuleBuilder;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.ui.RubySdkChooserPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;

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

    @Override
	public String getHelpId() {
        return myHelp;
    }

    @Override
	public JComponent getPreferredFocusedComponent() {
        return myPanel.getPreferredFocusedComponent();
    }

    @Override
	public JComponent getComponent() {
        return myPanel;
    }


    @Override
	public void updateDataModel() {
        final Sdk sdk = getSdk();
        mySettingsHolder.setSdk(sdk);

        NiiChAVOUtil.putJRubyFacetSdkMagic(myPanel, sdk);

    }

    private Sdk getSdk() {
        return myPanel.getChosenJdk();
    }

    @Override
	public Icon getIcon() {
        return myIcon;
    }

    @Override
	public boolean validate() {
        Sdk jdk = myPanel.getChosenJdk();
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
        if (!jdk.equals(projectRootManager.getSdk())) {
            int result = Messages.showYesNoDialog(
                    RBundle.message("prompt.confirm.default.sdk.change"),
                    RBundle.message("title.default.sdk.change"),
                    Messages.getQuestionIcon()
            );
            if (result == 1) {
                projectRootManager.setSdk(jdk);
            }
        }*/
        return true;
    }
}
