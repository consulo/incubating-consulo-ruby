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

import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.AbstractRubyRunConfigurationeExternalizer;

import java.util.Map;

public class RubyRunConfigurationExternalizer extends AbstractRubyRunConfigurationeExternalizer {
    private static RubyRunConfigurationExternalizer myInstance = new RubyRunConfigurationExternalizer();

    @NonNls private static final String SCRIPT_PATH = "SCRIPT_PATH";
    @NonNls private static final String SCRIPT_ARGS = "SCRIPT_ARGS";
    @NonNls public static final String RUBY_RUN_CONFIG_SETTINGS_ID = "RUBY_RUN_CONFIG";

    public void writeExternal(final RubyRunConfiguration config, final Element elem) {
        super.writeExternal(config, elem);
        writeOption(SCRIPT_PATH,  config.getScriptPath(), elem);
        writeOption(SCRIPT_ARGS,  config.getScriptArgs(), elem);
    }

    public void readExternal(final RubyRunConfiguration config, final Element elem) {
        super.readExternal(config, elem);

        //noinspection unchecked
        Map<String, String> optionsByName = buildOptionsByElement(elem);

        config.setScriptPath(optionsByName.get(SCRIPT_PATH));
        config.setScriptArgs(optionsByName.get(SCRIPT_ARGS));
    }

    public static RubyRunConfigurationExternalizer getInstance(){
        return myInstance;
    }

    @Override
	public String getID() {
        return RUBY_RUN_CONFIG_SETTINGS_ID;
    }
}