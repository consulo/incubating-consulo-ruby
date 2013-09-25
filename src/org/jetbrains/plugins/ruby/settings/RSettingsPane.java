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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.actions.shortcuts.RegisteredActionNamesPanel;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.UnnamedConfigurable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 16, 2006
 */
class RSettingsPane {
    private JPanel generatedPanel;
    private JTabbedPane settingsTabbedPane;

    private List<UnnamedConfigurable> configurableList =  new ArrayList<UnnamedConfigurable>();

    public JComponent getPanel(){
        settingsTabbedPane.setBackground(generatedPanel.getBackground());

        final GeneralSettingsTab generalTab = new GeneralSettingsTab();
        configurableList.add(generalTab);
        settingsTabbedPane.addTab(RBundle.message("settings.plugin.general.tab.title"),
                                  generalTab.getContentPanel());
        settingsTabbedPane.addTab(RBundle.message("settings.register.shortcut.tab.title"),
                                  new RegisteredActionNamesPanel().getContentPanel());

        return generatedPanel;
    }

    public void apply() throws ConfigurationException {
        for (UnnamedConfigurable configurable : configurableList) {
            configurable.apply();
        }
    }

    public void reset() {
        for (UnnamedConfigurable configurable : configurableList) {
            configurable.reset();
        }
    }

    public boolean isModified() {
        for (UnnamedConfigurable configurable : configurableList) {
            if (configurable.isModified()) {
                return true;
            }
        }
        return  false;
    }
}
