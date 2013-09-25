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

package org.jetbrains.plugins.ruby.rails.run.configuration.server;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript.RubyRunConfigurationEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.08.2007
 */
public class RailsServerRunConfigurationEditor extends RubyRunConfigurationEditor {
    public RailsServerRunConfigurationEditor(final Project project,
                                         final RubyRunConfiguration runConfiguration) {
        super(project, runConfiguration);

        myForm = new RailsServerConfigurationForm(project, runConfiguration);
    }

    @Override
	protected void resetEditorFrom(final RubyRunConfiguration config) {
        RailsServerRunConfiguration.copyParams((RailsServerRunConfiguration)config,
                                               (RailsServerConfigurationForm)myForm);
    }

    @Override
	protected void applyEditorTo(final RubyRunConfiguration config) throws ConfigurationException {
        RailsServerRunConfiguration.copyParams((RailsServerConfigurationForm)myForm,
                                               (RailsServerRunConfiguration)config);
    }
}
