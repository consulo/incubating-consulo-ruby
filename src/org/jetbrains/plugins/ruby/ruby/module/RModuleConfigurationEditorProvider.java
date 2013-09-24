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

import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.roots.ui.configuration.DefaultModuleConfigurationEditorFactory;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationEditorProvider;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.RubyContentEntriesEditor;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.loadPath.RLoadPathChooser;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.testFrameWork.RTestFrameworkChooser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: oleg, Roman Chernyatchik
 * @date: 18.08.2006
 */
class RModuleConfigurationEditorProvider implements ModuleComponent, ModuleConfigurationEditorProvider {
    @NotNull
    public String getComponentName() {
      return RComponents.RUBY_MODULE_LOCATION_EDITOR_PROVIDER;
    }


    public ModuleConfigurationEditor[] createEditors(ModuleConfigurationState state) {
      final DefaultModuleConfigurationEditorFactory editorFactory = DefaultModuleConfigurationEditorFactory.getInstance();
      final List<ModuleConfigurationEditor> editors = new ArrayList<ModuleConfigurationEditor>();

      editors.add(editorFactory.createClasspathEditor(state));
      editors.add(RubyContentEntriesEditor.createModuleContentRootsEditor(state));
      editors.add(RTestFrameworkChooser.createModuleContentRootsEditor(state));
      editors.add(RLoadPathChooser.createModuleContentRootsEditor(state));

      return editors.toArray(new ModuleConfigurationEditor[editors.size()]);
    }

    public void projectOpened() {}
    public void projectClosed() {}
    public void moduleAdded() {}
    public void initComponent() {}
    public void disposeComponent() {}
}
