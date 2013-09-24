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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots.testFrameWork;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecIcons;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings;
import static org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings.RSpecSupportType;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 20, 2007
 */

public class RTestFrameworkChooser extends ModuleElementsEditor {
    public static final String NAME = RBundle.message("module.settings.dialog.test.framework.tab.title");
    public static final Icon ICON = RSpecIcons.RUN_CONFIGURATION_ICON;

    private String myModuleName;
    private ModulesProvider myModulesProvider;
    private TestFrameworkOptions myTestFramworkOptions;

    protected RTestFrameworkChooser(final Project project,
                                    final String moduleName, final ModifiableRootModel model,
                                    final ModulesProvider modulesProvider) {
        super(project, model);
        myModuleName = moduleName;
        myModulesProvider = modulesProvider;
    }

    protected JComponent createComponentImpl() {
        final Module module = getModule();

        final RSupportPerModuleSettings rubySupportSettings = RModuleUtil.getRubySupportSettings(module);
        final boolean useTestUnitFramework = rubySupportSettings != null && rubySupportSettings.shouldUseTestUnitTestFramework();

        final RSpecModuleSettings settings = RSpecModuleSettings.getInstance(module);
        final boolean useRSpecFramework = settings.shouldUseRSpecTestFramework();
        if (RailsFacetUtil.hasRailsSupport(module)) {
            final boolean preferRSpecSplugin = settings.getRSpecSupportType() == RSpecSupportType.RAILS_PLUGIN;

            final RORSelectTestFrameworkPanel panel = new RORSelectTestFrameworkPanel(useRSpecFramework, preferRSpecSplugin, useTestUnitFramework, module, true);
            myTestFramworkOptions = panel;
            return panel.getContentPane();
        } else if (RModuleUtil.hasRubySupport(module)) {

            final RORSelectTestFrameworkPanel panel = new RORSelectTestFrameworkPanel(useRSpecFramework, false, useTestUnitFramework, module, false);
            myTestFramworkOptions = panel;
            return panel.getContentPane();
        }
        return null;
    }

    public boolean isModified() {
        if (super.isModified()) {
            return true;
        }

        final Module module = getModule();
        return isModuleModified(module);
    }

    private boolean isModuleModified(final Module module) {
        if (RailsFacetUtil.hasRailsSupport(module)) {
            final RSpecSupportType rSpecSupportType = RSpecModuleSettings.getInstance(module).getRSpecSupportType();

            final boolean preferRailsPlugin = rSpecSupportType == RSpecSupportType.RAILS_PLUGIN;
            final boolean useRSpecFramework = rSpecSupportType != RSpecSupportType.NONE;

            final RSupportPerModuleSettings rubySupportSettings = RModuleUtil.getRubySupportSettings(module);
            assert rubySupportSettings != null;
            final boolean shouldUseTestUnit = rubySupportSettings.shouldUseTestUnitTestFramework();

            return myTestFramworkOptions != null
                   && !(preferRailsPlugin == myTestFramworkOptions.shouldPreferRSpecPlugin()
                       && useRSpecFramework == myTestFramworkOptions.shouldUseRSpecFramework()
                       && shouldUseTestUnit == myTestFramworkOptions.shouldUseTestUnitFramework());
        } else if (RubyUtil.isRubyModuleType(module)) {
            final boolean useRSpecFramework = RSpecModuleSettings.getInstance(module).shouldUseRSpecTestFramework();

            final RSupportPerModuleSettings rubySupportSettings = RModuleUtil.getRubySupportSettings(module);
            assert rubySupportSettings != null;
            final boolean shouldUseTestUnit = rubySupportSettings.shouldUseTestUnitTestFramework();

            return myTestFramworkOptions != null
                   && !(useRSpecFramework == myTestFramworkOptions.shouldUseRSpecFramework()
                       && shouldUseTestUnit == myTestFramworkOptions.shouldUseTestUnitFramework());
        }
        return false;
    }

    public void saveData() {
        final Module module = getModule();
        if (!isModuleModified(module)) {
            return;
        }

        final RSpecModuleSettings settings = RSpecModuleSettings.getInstance(module);

        final RSpecModuleSettings.RSpecSupportType supportType;
        if (RailsFacetUtil.hasRailsSupport(module)) {
            if (!myTestFramworkOptions.shouldUseRSpecFramework()) {
                supportType = RSpecSupportType.NONE;
            } else {
                supportType = myTestFramworkOptions.shouldPreferRSpecPlugin()
                                ? RSpecSupportType.RAILS_PLUGIN
                                : RSpecSupportType.GEM;
            }
        } else if (RubyUtil.isRubyModuleType(module)) {
            supportType = myTestFramworkOptions.shouldUseRSpecFramework()
                                ? RSpecSupportType.GEM
                                : RSpecSupportType.NONE;
        } else {
            supportType = RSpecSupportType.NONE;
        }
        settings.setRSpecSupportType(supportType);
        final RSupportPerModuleSettings rubySupportSettings = RModuleUtil.getRubySupportSettings(module);
        assert rubySupportSettings != null;
        rubySupportSettings.setShouldUseTestUnitTestFramework(myTestFramworkOptions.shouldUseTestUnitFramework());        
    }

    @Nls
    public String getDisplayName() {
        return NAME;
    }

    @Nullable
    public Icon getIcon() {
        return ICON;
    }

    @Nullable
    @NonNls
    public String getHelpTopic() {
        return null;
    }

    private Module getModule() {
        return myModulesProvider.getModule(myModuleName); //TODO module model?
    }

    public static ModuleConfigurationEditor createModuleContentRootsEditor(final ModuleConfigurationState state) {
        final ModifiableRootModel rootModel = state.getRootModel();
        final Module module = rootModel.getModule();
        final String moduleName = module.getName();
        return new RTestFrameworkChooser(state.getProject(), moduleName, rootModel, state.getModulesProvider());
    }

}
