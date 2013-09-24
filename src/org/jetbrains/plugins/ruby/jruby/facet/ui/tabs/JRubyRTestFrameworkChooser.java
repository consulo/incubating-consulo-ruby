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

package org.jetbrains.plugins.ruby.jruby.facet.ui.tabs;

import javax.swing.Icon;
import javax.swing.JComponent;

import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecIcons;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecModuleSettings;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetConfiguration;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.testFrameWork.RORSelectTestFrameworkPanel;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.testFrameWork.TestFrameworkOptions;
import org.jetbrains.plugins.ruby.settings.RSupportPerModuleSettings;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 20, 2007
 */

public class JRubyRTestFrameworkChooser extends FacetEditorTab {
    public JRubyRTestFrameworkChooser(@NotNull final JRubyFacetConfiguration myJRubyFacetConfiguration,
                                      @NotNull final FacetEditorContext myEditorContext) {
        this.myJRubyFacetConfiguration = myJRubyFacetConfiguration;
        this.myEditorContext = myEditorContext;
    }

    private JRubyFacetConfiguration myJRubyFacetConfiguration;
    private FacetEditorContext myEditorContext;

    private TestFrameworkOptions myTestFramworkOptions;

    @Nls
    public String getDisplayName() {
        return RBundle.message("module.settings.dialog.test.framework.tab.title");
    }

    public JComponent createComponent() {
        final Module module = myEditorContext.getModule();
        assert module != null;

        final boolean useRSpec = RSpecModuleSettings.getInstance(module).shouldUseRSpecTestFramework();

        final boolean useTestUnit = myJRubyFacetConfiguration.shouldUseTestUnitTestFramework();
        final RORSelectTestFrameworkPanel panel = new RORSelectTestFrameworkPanel(useRSpec, false, useTestUnit, module, false);
        myTestFramworkOptions = panel;
        return panel.getContentPane();
    }

    @Nullable
    public Icon getIcon() {
        return RSpecIcons.RUN_CONFIGURATION_ICON;
    }

    public boolean isModified() {
        final Module module = myEditorContext.getModule();
        assert module != null;

        if (JRubyFacet.getInstance(module) == null) {
            //New uncommitted JRuby facet
            return true;
        }

        final boolean shouldUseRSpec = RSpecModuleSettings.getInstance(module).shouldUseRSpecTestFramework();
        //noinspection ConstantConditions
        final RSupportPerModuleSettings settings = RModuleUtil.getRubySupportSettings(module);
        assert settings != null;
        final boolean shouldUseTestUnit = settings.shouldUseTestUnitTestFramework();

       // final String oldTestUnitRootUrl = JRubyModuleContentRootManager.getInstance(module).getUnitTestsRootUrl();
      //  final String newTestUnitRootUrl = myTestFramworkOptions.getTestUnitRootUrl();

       /* return myTestFramworkOptions != null
               && !(shouldUseRSpec == myTestFramworkOptions.shouldUseRSpecFramework()
                    && shouldUseTestUnit == myTestFramworkOptions.shouldUseTestUnitFramework()
                    && ((oldTestUnitRootUrl == null && newTestUnitRootUrl == null)
                        || (oldTestUnitRootUrl != null && oldTestUnitRootUrl.equals(newTestUnitRootUrl)))); */
		return false;
    }

    public void apply() throws ConfigurationException {
      /*  final Module module = myEditorContext.getModule();
        assert module != null;

        if (isModified()) {
            final RSpecModuleSettings.RSpecSupportType supportType;
            if (myTestFramworkOptions.shouldUseRSpecFramework()) {
                supportType = myTestFramworkOptions.shouldPreferRSpecPlugin()
                        ? RSpecModuleSettings.RSpecSupportType.RAILS_PLUGIN    //not sure, may be it is wizard and not module setting!
                        : RSpecModuleSettings.RSpecSupportType.GEM;
            } else {
                supportType = RSpecModuleSettings.RSpecSupportType.NONE;
            }
            RSpecModuleSettings.getInstance(module).setRSpecSupportType(supportType);

            final boolean jRubyFacetExists = JRubyFacet.getInstance(module) != null;
            final RSupportPerModuleSettings rubySupportSettings = jRubyFacetExists
                    ? RModuleUtil.getRubySupportSettings(module)
                    //New JRuby Facet, it isn't committed, we can't get it via module
                    : myJRubyFacetConfiguration;
            assert rubySupportSettings != null;
            rubySupportSettings.setShouldUseTestUnitTestFramework(myTestFramworkOptions.shouldUseTestUnitFramework());

            final JRubyModuleContentRootManager rootManager;
            if (jRubyFacetExists) {
                rootManager = JRubyModuleContentRootManager.getInstance(module);
            } else {
                //New JRuby Facet, it isn't committed, we can't get it via module
                final JRubyFacet jRubyFacet = (JRubyFacet) myEditorContext.getFacet();
                assert jRubyFacet != null;

                rootManager = jRubyFacet.getRModuleContentManager();
            }
            rootManager.setUnitTestsRootUrl(myTestFramworkOptions.getTestUnitRootUrl());
        }    */
    }

    public void reset() {
    }

    public void disposeUIResources() {
        myTestFramworkOptions = null;
    }
}