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

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.07.2007
 */
public class RSpecRunConfigurationEditor extends SettingsEditor<RSpecRunConfiguration> {
    private RSpecRunConfigurationForm myForm;


    public RSpecRunConfigurationEditor(final Project project, final RSpecRunConfiguration configuration) {
        myForm = new RSpecRunConfigurationForm(project, configuration);
    }

    protected void resetEditorFrom(final RSpecRunConfiguration config) {
        RSpecRunConfiguration.copyParams(config, myForm);
    }

    protected void applyEditorTo(final RSpecRunConfiguration config) throws ConfigurationException {
        RSpecRunConfiguration.copyParams(myForm, config);
    }

    @NotNull
    protected JComponent createEditor() {
        return myForm.getPanel();
    }

    protected void disposeEditor() {
        myForm = null;
    }
}