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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleConfigurationEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleConfigurationState;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntriesEditor;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryEditor;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryTreeEditor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
public class RubyContentEntriesEditor extends RContentEntriesEditor {
    public RubyContentEntriesEditor(final Project project, final ModifiableRootModel model) {
        super(project, model);
    }

    public RContentEntryEditor createEditor(final ContentEntry entry, final Module module) {
           return new RubyContentEntryEditor(entry, myModel);
    }

    public RContentEntryTreeEditor createEntryTreeEditor(final Module module) {
        return new RubyContentEntryTreeEditor(module);

    }

    public static ModuleConfigurationEditor createModuleContentRootsEditor(final ModuleConfigurationState state) {
        return new RubyContentEntriesEditor(state.getProject(), state.getRootModel());
    }
}
