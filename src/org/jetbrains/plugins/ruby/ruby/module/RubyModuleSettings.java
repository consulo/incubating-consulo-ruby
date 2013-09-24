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

package org.jetbrains.plugins.ruby.ruby.module;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.RubyComponents;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.settings.RModuleSettingsStorage;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 6, 2007
 */
public class RubyModuleSettings implements RSupportPerModuleSettings, ModuleComponent {
    protected final Module myModule;
    
    private RModuleSettingsStorage myRModuleSettingsStorage;

    public RubyModuleSettings(final Module module,
                              final RModuleSettingsStorage rModuleStorage) {
        myModule = module;

        myRModuleSettingsStorage = RModuleSettingsStorage.getInstance(module);
    }

    public void initComponent() {
        // Do nothing
    }

    public void disposeComponent() {
        // Do nothing
    }

    @NotNull
    public CheckableDirectoriesContainer getLoadPathDirs() {
        return myRModuleSettingsStorage.loadPathDirs;
    }

    public void setLoadPathDirs(@NotNull final CheckableDirectoriesContainer loadPathDirs) {
        myRModuleSettingsStorage.loadPathDirs = loadPathDirs;
    }

    public boolean shouldUseTestUnitTestFramework() {
        return myRModuleSettingsStorage.shouldUseTestUnitFramework;
    }

    public void setShouldUseTestUnitTestFramework(final boolean shouldUse) {
        myRModuleSettingsStorage.shouldUseTestUnitFramework = shouldUse;
    }

    public static RubyModuleSettings getInstance(@NotNull final Module module) {
        return module.getComponent(RubyModuleSettings.class);
    }

    public void projectOpened() {
        //Do nothing
    }

    public void projectClosed() {
        //Do nothing
    }

    public void moduleAdded() {
        //Do nothing
    }

    @NonNls
    @NotNull
    public String getComponentName() {
        return RubyComponents.RUBY_MODULE_SETTINGS;
    }
}
