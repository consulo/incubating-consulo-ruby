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

import java.util.Map;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;
import com.intellij.execution.configuration.EnvironmentVariablesComponent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: May 22, 2008
 */
public abstract class AbstractRubyRunConfigurationeExternalizer  extends SettingsExternalizer {
    @NonNls
    private static final String RUBY_ARGS =   "RUBY_ARGS";
    @NonNls
    private static final String WORK_DIR =    "WORK DIR";
    @NonNls private static final String MODULE_NAME = "MODULE_NAME";
    @NonNls private static final String SHOULD_USE_SDK = "SHOULD_USE_SDK";
    @NonNls private static final String ALTERN_SDK_NAME = "ALTERN_SDK_NAME";


    @NonNls public static final String PASS_PARENT_ENVS = "myPassParentEnvs";

    public void writeExternal(final AbstractRubyRunConfiguration config, final Element elem) {
        writeOption(RUBY_ARGS,    config.getRubyArgs(), elem);
        writeOption(WORK_DIR,     config.getWorkingDirectory(), elem);
        writeOption(MODULE_NAME,  config.getModuleName(), elem);

        writeOption(SHOULD_USE_SDK,  String.valueOf(config.shouldUseAlternativeSdk()), elem);
        writeOption(ALTERN_SDK_NAME,  config.getAlternativeSdkName(), elem);

        //env
        writeOption(PASS_PARENT_ENVS,  String.valueOf(config.isPassParentEnvs()), elem);
        EnvironmentVariablesComponent.writeExternal(elem, config.getEnvs());
    }

    public void readExternal(final AbstractRubyRunConfiguration config, final Element elem) {
        //noinspection unchecked
        Map<String, String> optionsByName = buildOptionsByElement(elem);

        config.setRubyArgs(optionsByName.get(RUBY_ARGS));
        config.setWorkingDirectory(optionsByName.get(WORK_DIR));
        config.setModuleName(optionsByName.get(MODULE_NAME));

        config.setAlternativeSdkName(optionsByName.get(ALTERN_SDK_NAME));
        config.setShouldUseAlternativeSdk(Boolean.valueOf(optionsByName.get(SHOULD_USE_SDK)));

        //env
        config.setPassParentEnvs(Boolean.valueOf(optionsByName.get(PASS_PARENT_ENVS)));
        EnvironmentVariablesComponent.readExternal(elem, config.getEnvs());
    }
}
