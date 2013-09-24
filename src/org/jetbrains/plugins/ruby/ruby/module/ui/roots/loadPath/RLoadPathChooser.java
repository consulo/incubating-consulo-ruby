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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots.loadPath;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Ref;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.support.ui.checkableDir.CheckableDirectoriesContainer;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 19, 2008
 */
public class RLoadPathChooser extends ModuleElementsEditor {
    public static final String NAME = RBundle.message("module.settings.dialog.load.path.tab.title");

    private String myModuleName;
    private ModulesProvider myModulesProvider;
    private Ref<CheckableDirectoriesContainer> myLoadPathDirsCopyRef = new Ref<CheckableDirectoriesContainer>(null);

    protected RLoadPathChooser(final Project project,
                               final String moduleName,
                               final ModifiableRootModel model,
                               final ModulesProvider modulesProvider) {
        super(project, model);
        myModuleName = moduleName;
        myModulesProvider = modulesProvider;
    }

    protected JComponent createComponentImpl() {
        final Module module = myModel.getModule();

        final RSupportPerModuleSettings settings = RModuleUtil.getRubySupportSettings(module);
        assert settings != null;

        return RLoadPathChooserUtil.createLoadPathPanel(module, myLoadPathDirsCopyRef, 
                                                        settings.getLoadPathDirs());
    }

    public boolean isModified() {
        final RSupportPerModuleSettings settings = RModuleUtil.getRubySupportSettings(myModel.getModule());
        assert settings != null;

        final CheckableDirectoriesContainer origLoadPathDirs = settings.getLoadPathDirs();
        return super.isModified()
               || RLoadPathChooserUtil.loadPathDirsAreModified(myLoadPathDirsCopyRef.get(), origLoadPathDirs);
    }

    public void saveData() {
        if (isModified() && myLoadPathDirsCopyRef.get() != null) {
            final RSupportPerModuleSettings settings = RModuleUtil.getRubySupportSettings(myModel.getModule());
            assert settings != null;

            settings.setLoadPathDirs(myLoadPathDirsCopyRef.get());

            //Applay new load path to cache
            final Module module = myModel.getModule();
            SymbolsCache.getInstance(module.getProject()).clearCachesExceptBuiltIn();
        }
    }

    @Nls
    public String getDisplayName() {
        return NAME;
    }

    @Nullable
    public Icon getIcon() {
        return RubyIcons.RUBY_MODULE_SETTINGS_LOADPATH;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    public static ModuleConfigurationEditor createModuleContentRootsEditor(final ModuleConfigurationState state) {
        final ModifiableRootModel rootModel = state.getRootModel();
        final Module module = rootModel.getModule();
        final String moduleName = module.getName();
        return new RLoadPathChooser(state.getProject(), moduleName, rootModel, state.getModulesProvider());
    }

}
