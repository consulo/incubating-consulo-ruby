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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests;

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfigurationeExternalizer;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 18.07.2007
 */
public class RTestRunConfigurationExternalizer extends AbstractRubyRunConfigurationeExternalizer {
    private static RTestRunConfigurationExternalizer myInstance = new RTestRunConfigurationExternalizer();

    @NonNls public static final String RTEST_RUN_CONFIG_SETTINGS_ID = "RTEST_RUN_CONFIG_SETTINGS_ID";

    @NonNls private static final String TESTS_FOLDER_PATH = "TESTS_FOLDER_PATH";
    @NonNls private static final String TEST_SCRIPT_PATH = "TEST_SCRIPT_PATH";
    @NonNls private static final String TEST_CLASS_NAME = "TEST_CLASS_NAME";
    @NonNls private static final String TEST_FILE_MASK = "TEST_FILE_MASK";
    @NonNls private static final String TEST_METHOD_NAME = "TEST_METHOD_NAME";
    @NonNls private static final String TEST_TEST_TYPE = "TEST_TEST_TYPE";
    @NonNls private static final String INHERITANCE_CHECK_DISABLED = "INHERITANCE_CHECK_DISABLED";

    public void writeExternal(final RTestsRunConfiguration config, final Element elem) {
        super.writeExternal(config, elem);

        writeOption(TESTS_FOLDER_PATH,  config.getTestsFolderPath(), elem);
        writeOption(TEST_SCRIPT_PATH,  config.getTestScriptPath(), elem);
        writeOption(TEST_CLASS_NAME,  config.getTestQualifiedClassName(), elem);
        writeOption(TEST_FILE_MASK,  config.getTestFileMask(), elem);
        writeOption(TEST_METHOD_NAME,  config.getTestMethodName(), elem);
        writeOption(TEST_TEST_TYPE,  config.getTestType().toString(), elem);

        writeOption(INHERITANCE_CHECK_DISABLED,  String.valueOf(config.isInheritanceCheckDisabled()), elem);
    }

    public void readExternal(final RTestsRunConfiguration config, final Element elem) {
        super.readExternal(config, elem);

        //noinspection unchecked
        Map<String, String> optionsByName = buildOptionsByElement(elem);

        config.setTestsFolderPath(optionsByName.get(TESTS_FOLDER_PATH));
        config.setTestScriptPath(optionsByName.get(TEST_SCRIPT_PATH));
        config.setTestQualifiedClassName(optionsByName.get(TEST_CLASS_NAME));
        config.setTestFileMask(optionsByName.get(TEST_FILE_MASK));
        config.setTestMethodName(optionsByName.get(TEST_METHOD_NAME));
        final String type_value = optionsByName.get(TEST_TEST_TYPE);
        if (type_value != null) {
            config.setTestType(Enum.valueOf(RTestsRunConfiguration.TestType.class,
                                            type_value));
        }

        config.setInheritanceCheckDisabled(Boolean.valueOf(optionsByName.get(INHERITANCE_CHECK_DISABLED)));
    }

    public static RTestRunConfigurationExternalizer getInstance(){
        return myInstance;
    }

    @Override
	public String getID() {
        return RTEST_RUN_CONFIG_SETTINGS_ID;
    }
}