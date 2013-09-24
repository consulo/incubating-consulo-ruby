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

package org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.railsView;

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import org.jetbrains.annotations.Nls;
import org.jetbrains.plugins.ruby.RBundle;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 23, 2008
 */
public class RailsViewFoldersEditorTab extends FacetEditorTab {
    private ModuleConfigurationEditor myEditor;

    public RailsViewFoldersEditorTab(final Project project, final ModifiableRootModel modifiableRootModel) {
        myEditor = RailsViewContentEntriesEditor.createModuleContentRootsEditor(project, modifiableRootModel);
    }

    @Nls
    public String getDisplayName() {
        return RBundle.message("rails.facet.settings.tab.railsView.title");
    }

    public JComponent createComponent() {
        return myEditor.createComponent();
    }

    public boolean isModified() {
        return myEditor.isModified();
    }

    public void apply() throws ConfigurationException {
        myEditor.apply();
    }

    public void reset() {
        myEditor.reset();
    }

    public void disposeUIResources() {
        myEditor.disposeUIResources();
    }
}
