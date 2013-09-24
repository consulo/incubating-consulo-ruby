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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.rubyScript;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.JavaProgramRunner;
import com.intellij.execution.runners.RunnerInfo;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfiguration;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUtil;

import java.io.File;

public class RubyRunConfiguration extends AbstractRubyRunConfiguration implements RubyRunConfigurationParams {
    private String myScriptPath = TextUtil.EMPTY_STRING;
    private String myScriptArgs = TextUtil.EMPTY_STRING;

    public RubyRunConfiguration(final Project project, final ConfigurationFactory factory,
                                final String name) {
        super(project, factory, name);
    }

    protected RubyRunConfiguration createInstance() {
        return new RubyRunConfiguration(getProject(), getFactory(), getName());
    }

    public static void copyParams(final RubyRunConfigurationParams fromParams,
                                  final RubyRunConfigurationParams toParams) {
        AbstractRubyRunConfiguration.copyParams(fromParams, toParams);

        toParams.setScriptPath(fromParams.getScriptPath());
        toParams.setScriptArgs(fromParams.getScriptArgs());
    }

    public String getScriptPath() {
        return myScriptPath;
    }

    public void setScriptPath(final String scriptPath) {
        myScriptPath = TextUtil.getAsNotNull(scriptPath);
    }

    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new RubyRunConfigurationEditor(getProject(), this);
    }

    @SuppressWarnings({"deprecation"})
    @Nullable
    public JDOMExternalizable createRunnerSettings(ConfigurationInfoProvider provider) {
        return null;
    }

    @SuppressWarnings({"deprecation"})
    @Nullable
    public SettingsEditor<JDOMExternalizable> getRunnerSettingsEditor(JavaProgramRunner runner) {
        return null;
    }

    public RunProfileState getState(DataContext context,
                                    RunnerInfo runnerInfo,
                                    RunnerSettings runnerSettings,
                                    ConfigurationPerRunnerSettings configurationSettings) throws ExecutionException {

        try {
            validateConfiguration(true);
        } catch (ExecutionException ee) {
            throw ee;
        } catch (Exception e) {
            throw new ExecutionException(e.getMessage(), e);
        }


        return new RubyRunCommandLineState(this, runnerSettings, configurationSettings);
    }

    protected void validateConfiguration(final boolean isExecution) throws Exception {
        RubyRunConfigurationUtil.inspectSDK(this, isExecution);

        // Script inspection
        if (getScriptPath() == null || getScriptPath().trim().length() == 0) {
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("run.configuration.script.not.specified"), isExecution);
        }

        File script = new File(getScriptPath());
        if (!script.exists()){
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("run.configuration.script.not.exists"), isExecution);
        }
        if (!script.isFile()){
            RubyRunConfigurationUtil.throwExecutionOrRuntimeException(RBundle.message("run.configuration.script.is.not.file"), isExecution);
        }

        RubyRunConfigurationUtil.inspectWorkingDirectory(this, isExecution);
    }

    public String getScriptArgs() {
        return myScriptArgs;
    }

    public void setScriptArgs(String myScriptArgs) {
        this.myScriptArgs = TextUtil.getAsNotNull(myScriptArgs);
    }


    public void readExternal(Element element) throws InvalidDataException {
        RubyRunConfigurationExternalizer.getInstance().readExternal(this, element);
    }

    public void writeExternal(Element element) throws WriteExternalException {
        RubyRunConfigurationExternalizer.getInstance().writeExternal(this, element);
    }
}
