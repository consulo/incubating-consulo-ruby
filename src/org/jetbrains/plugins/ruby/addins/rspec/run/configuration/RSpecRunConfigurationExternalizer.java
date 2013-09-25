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

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfigurationeExternalizer;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.tests.RTestsRunConfiguration;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 18.07.2007
 */
public class RSpecRunConfigurationExternalizer extends AbstractRubyRunConfigurationeExternalizer {
    private static RSpecRunConfigurationExternalizer myInstance = new RSpecRunConfigurationExternalizer();

    @NonNls public static final String RSPEC_RUN_CONFIG_SETTINGS_ID = "RSPEC_RUN_CONFIG_SETTINGS_ID";

    @NonNls private static final String SPEC_ARGS =   "SPEC_ARGS";
    @NonNls private static final String TESTS_FOLDER_PATH = "TESTS_FOLDER_PATH";
    @NonNls private static final String TEST_SCRIPT_PATH = "TEST_SCRIPT_PATH";
    @NonNls private static final String SPEC_RUNNER_PATH = "SPEC_RUNNER_PATH";
    @NonNls private static final String TEST_FILE_MASK = "TEST_FILE_MASK";
    @NonNls private static final String TEST_TEST_TYPE = "TEST_TEST_TYPE";
    @NonNls private static final String USE_COLOURED_OUTPUT_ENABLED = "USE_COLOURED_OUTPUT_ENABLED";
    @NonNls private static final String RUN_SPECS_SEPARATELY = "RUN_SPECS_SEPARATELY";
    @NonNls private static final String USE_CUSTOM_SPEC_RUNNER = "USE_CUSTOM_SPEC_RUNNER";

    public void writeExternal(final RSpecRunConfiguration config, final Element elem) {
        super.writeExternal(config, elem);

        writeOption(TESTS_FOLDER_PATH,  config.getTestsFolderPath(), elem);
        writeOption(TEST_SCRIPT_PATH,  config.getTestScriptPath(), elem);
        writeOption(SPEC_RUNNER_PATH,  config.getCustomSpecsRunnerPath(), elem);
        writeOption(TEST_FILE_MASK,  config.getTestFileMask(), elem);
        writeOption(TEST_TEST_TYPE,  config.getTestType().toString(), elem);
        writeOption(SPEC_ARGS,    config.getSpecArgs(), elem);

        writeOption(RUN_SPECS_SEPARATELY,  String.valueOf(config.shouldRunSpecSeparately()), elem);
        writeOption(USE_COLOURED_OUTPUT_ENABLED,  String.valueOf(config.shouldUseColoredOutput()), elem);
        writeOption(USE_CUSTOM_SPEC_RUNNER,  String.valueOf(config.shouldUseCustomSpecRunner()), elem);
    }

    public void readExternal(final RSpecRunConfiguration config, final Element elem) {
        super.readExternal(config, elem);

        //noinspection unchecked
        Map<String, String> optionsByName = buildOptionsByElement(elem);

        config.setSpecArgs(optionsByName.get(SPEC_ARGS));
        config.setTestsFolderPath(optionsByName.get(TESTS_FOLDER_PATH));
        config.setTestScriptPath(optionsByName.get(TEST_SCRIPT_PATH));
        config.setCustomSpecsRunnerPath(optionsByName.get(SPEC_RUNNER_PATH));
        config.setTestFileMask(optionsByName.get(TEST_FILE_MASK));
        final String type_value = optionsByName.get(TEST_TEST_TYPE);
        if (type_value != null) {
            config.setTestType(Enum.valueOf(RTestsRunConfiguration.TestType.class,
                                            type_value));
        }

        config.setShouldRunSpecSeparately(Boolean.valueOf(optionsByName.get(RUN_SPECS_SEPARATELY)));
        config.setShouldUseColoredOutput(Boolean.valueOf(optionsByName.get(USE_COLOURED_OUTPUT_ENABLED)));
        config.setShouldUseCustomSpecRunner(Boolean.valueOf(optionsByName.get(USE_CUSTOM_SPEC_RUNNER)));
    }

    public static RSpecRunConfigurationExternalizer getInstance(){
        return myInstance;
    }

    @Override
	public String getID() {
        return RSPEC_RUN_CONFIG_SETTINGS_ID;
    }
}