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

package org.jetbrains.plugins.ruby.ruby.ri;

import com.intellij.openapi.diagnostic.Logger;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman.Chernyatchik
 * @date: Nov 8, 2006
 */
class RDocSettingsExternalizer extends SettingsExternalizer {
    private static final Logger LOG = Logger.getInstance(RDocSettingsExternalizer.class.getName());

    private static RDocSettingsExternalizer myInstance = new RDocSettingsExternalizer();

    @NonNls
    private static final String USE_DEFAULTS = "DEFAULTS";

    @NonNls
    private static final String RDOC_SETTINGS_ID = "RUBY_DOC";

    public void writeExternal(@NotNull final RDocSettings settings,
                              @NotNull final Element elem) {
        boolean doUseDefaults = settings.doUseDefaults();
        writeOption(USE_DEFAULTS, Boolean.toString(doUseDefaults), elem);

        settings.getDocDirs().writeCheckableDirectores(elem, this);
    }

    public void readExternal(@NotNull final RDocSettings settings,
                             @NotNull final Element elem) {
        try {
            //noinspection unchecked
            final Map<String, String> optionsByName = buildOptionsByElement(elem);

            final String useDefaults = optionsByName.get(USE_DEFAULTS);
            settings.setUseDefaults(Boolean.valueOf(useDefaults));
            
            settings.getDocDirs().loadCheckableDirectores(optionsByName);
        } catch (Exception e) {
            // ignore. something was saved incorrectly
            LOG.warn(e);
        }
    }

    public static RDocSettingsExternalizer getInstance() {
        return myInstance;
    }

    @Override
	public String getID() {
        return RDOC_SETTINGS_ID;
    }
}
