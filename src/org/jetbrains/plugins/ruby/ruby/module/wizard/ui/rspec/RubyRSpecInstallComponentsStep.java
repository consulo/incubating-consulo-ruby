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

package org.jetbrains.plugins.ruby.ruby.module.wizard.ui.rspec;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RRModuleBuilder;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 5, 2007
 */
public class RubyRSpecInstallComponentsStep extends ModuleWizardStep {

    protected final RubyRSpecInstallComponentsForm myPanel;
    private Icon myIcon;
    private String myHelp;
    private RRModuleBuilder myBuilder;
    private Project myProject;

    public RubyRSpecInstallComponentsStep(final RRModuleBuilder builder,
                                          final Icon icon,
                                          final String helpId,
                                          final Project project) {
        super();
        myIcon = icon;
        myBuilder = builder;
        myPanel = new RubyRSpecInstallComponentsForm(builder);
        myHelp = helpId;
        myProject = project;
    }

    public String getHelpId() {
        return myHelp;
    }

    public JComponent getComponent() {
        return myPanel.getContentPane();
    }


    public void updateDataModel() {
        // Do nothing
    }

    public Icon getIcon() {
        return myIcon;
    }

    public void updateStep() {
        myPanel.initBeforeShow();
    }

    public boolean isStepVisible() {
        if (!myBuilder.isRSpecSupportEnabled() || !NiiChAVOUtil.isRailsFacetEnabledMagic(getComponent())) {
            return false;
        }

        final ProjectJdk sdk = myBuilder.getSdk();
        //noinspection SimplifiableIfStatement
        return sdk != null;
    }

    public boolean validate() throws ConfigurationException {
        final ProjectJdk sdk = myBuilder.getSdk();
        if (!RSpecUtil.checkIfRSpecGemExists(sdk)) {
            final String msg = RBundle.message("module.settings.dialog.select.test.spec.ruby.please.install.rspec.gem.text");
            final String title = RBundle.message("module.settings.dialog.select.test.spec.ruby.please.install.rspec.gem.title");
            return Messages.showYesNoDialog(myProject, msg, title, UIUtil.getWarningIcon()) == DialogWrapper.OK_EXIT_CODE;
        }
        return true;
    }
}


