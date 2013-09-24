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

package org.jetbrains.plugins.ruby.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.impl.storage.ClasspathStorage;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 6, 2007
 */
@State(
  name = RComponents.RMODULE_SETTINGS_STORAGE,
  storages = {
    @Storage(
      id = ClasspathStorage.DEFAULT_STORAGE,
      file = "$MODULE_FILE$"
    )
  }
)
public class RModuleSettingsStorage extends SettingsExternalizer implements PersistentStateComponent<Element>{

    public boolean shouldUseRSpecTestFramework;
    public boolean shouldUseTestUnitFramework;
    public CheckableDirectoriesContainer loadPathDirs = new CheckableDirectoriesContainer();

    @NonNls
    @Deprecated
    private static final String SHOULD_USE_RSPEC_TEST_FRAMEWORK = "SHOULD_USE_RSPEC_TEST_FRAMEWORK";
    @NonNls
    private static final String RMODULE_SETTINGS_STORAGE_ID = "RMODULE_SETTINGS_STORAGE_ID";
    @NonNls
    private static final String SHOULD_USE_TEST_UNIT_TEST_FRAMEWORK = "SHOULD_USE_TEST_UNIT_TEST_FRAMEWORK";

    public static RModuleSettingsStorage getInstance(@NotNull final Module module) {
        return ServiceManager.getService(module, RModuleSettingsStorage.class);
    }

    public Element getState() {
        final Element element = new Element(getID());

//writeExternal
        writeOption(SHOULD_USE_RSPEC_TEST_FRAMEWORK,
                    Boolean.toString(shouldUseRSpecTestFramework),
                    element);
        writeOption(SHOULD_USE_TEST_UNIT_TEST_FRAMEWORK,
                    Boolean.toString(shouldUseTestUnitFramework),
                    element);
        loadPathDirs.writeCheckableDirectores(element, this);

        return element;
    }

    public void loadState(@NotNull final Element elem) {
//readExternal
        final Map<String, String> optionsByName = buildOptionsByElement(elem);

        final String shouldUseTestFrValue = optionsByName.get(SHOULD_USE_RSPEC_TEST_FRAMEWORK);
        shouldUseRSpecTestFramework = shouldUseTestFrValue != null && Boolean.valueOf(shouldUseTestFrValue);

        final String shouldUseTestUnitFrValue = optionsByName.get(SHOULD_USE_TEST_UNIT_TEST_FRAMEWORK);
        shouldUseTestUnitFramework = shouldUseTestUnitFrValue != null && Boolean.valueOf(shouldUseTestUnitFrValue);

        loadPathDirs.loadCheckableDirectores(optionsByName);
    }

    public String getID() {
        return RMODULE_SETTINGS_STORAGE_ID;
    }
}