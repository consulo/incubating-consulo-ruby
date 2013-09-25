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

package org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.general;

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 25, 2008
 */
public class GeneralSettingsEditorTab extends FacetEditorTab {
    private BaseRailsFacet myFacet;
    private GeneralSettingsForm myGeneralSettingsForm;

    public GeneralSettingsEditorTab(final BaseRailsFacet facet) {
        myFacet = facet;
        myGeneralSettingsForm = new GeneralSettingsForm(facet.getConfiguration().getRailsApplicationRootPath());
    }

    @Nls
    public String getDisplayName() {
        return RBundle.message("rails.facet.settings.tab.general.title");
    }

    public void onTabEntering() {
        myGeneralSettingsForm.beforeShow(RModuleUtil.getModuleOrJRubyFacetSdk(myFacet.getModule()));
    }

    public JComponent createComponent() {
        return myGeneralSettingsForm.getContentPane();
    }

    public boolean isModified() {
        return false;
    }

    @Override
	public void apply() throws ConfigurationException {
        //Do nothing
    }

    @Override
	public void reset() {
        //Do nothing
    }

    public void disposeUIResources() {
        myGeneralSettingsForm.setClose();
    }
}
