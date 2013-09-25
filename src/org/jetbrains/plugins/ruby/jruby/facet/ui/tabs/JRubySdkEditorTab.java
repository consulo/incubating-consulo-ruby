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

import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetConfiguration;
import org.jetbrains.plugins.ruby.jruby.facet.ui.SelectJRubySdkPane;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
*
* @author: Roman Chernyatchik
* @date: Nov 4, 2007
*/
public class JRubySdkEditorTab extends FacetEditorTab {
    private SelectJRubySdkPane mySdkPaneSelect;
    private JRubyFacetConfiguration myJRubyFacetConfiguration;
    protected FacetEditorContext myEditorContext;

    public JRubySdkEditorTab(@NotNull final JRubyFacetConfiguration jRubyFacetConfiguration,
                             @NotNull final FacetEditorContext editorContext) {
        myJRubyFacetConfiguration = jRubyFacetConfiguration;
        myEditorContext = editorContext;
    }

    @Nls
    public String getDisplayName() {
        return RBundle.message("jruby.settings.tabs.sdk.title");
    }

    public JComponent createComponent() {
        //noinspection ConstantConditions
        mySdkPaneSelect = new SelectJRubySdkPane(myJRubyFacetConfiguration);
        return mySdkPaneSelect.getPanel();
    }

    public boolean isModified() {
        return myJRubyFacetConfiguration.isChanged();
    }

    @Override
	public void apply() throws ConfigurationException {
    }

    @Override
	public void reset() {
        myJRubyFacetConfiguration.setChanged(false);
    }

    public void disposeUIResources() {
        mySdkPaneSelect = null;
    }
}
