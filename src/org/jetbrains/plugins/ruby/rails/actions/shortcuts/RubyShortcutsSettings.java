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

package org.jetbrains.plugins.ruby.rails.actions.shortcuts;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.plugins.ruby.rails.actions.generators.SerializableGenerator;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTaskSerializableImpl;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 23.03.2007
 */
@State(
        name = "RubyShortcutsSettings",
        storages = {
        @Storage(
                id = "main",
                file = "$APP_CONFIG$/rubysettings.xml"
        )}
)
public class RubyShortcutsSettings implements PersistentStateComponent<RubyShortcutsSettings>{
    public RakeTaskSerializableImpl serializableRakeTask;
    public SerializableGenerator serializableGenerators;

    public static RubyShortcutsSettings getInstance() {
        return ServiceManager.getService(RubyShortcutsSettings.class);
    }

    public RubyShortcutsSettings getState() {
        return this;
    }

    public void loadState(RubyShortcutsSettings shortcutsSettings) {
        serializableRakeTask = shortcutsSettings.serializableRakeTask;
        serializableGenerators = shortcutsSettings.serializableGenerators;

        initParents(serializableRakeTask, null);
        initParents(serializableGenerators, null);
    }

    private void initParents(final SerializableGenerator generator, final SerializableGenerator parentGenerator) {
        generator.setParent(parentGenerator);
        final List<SerializableGenerator> generators = generator.getChildren();
        for (SerializableGenerator child : generators) {
            initParents(child, child);
        }
    }

    private void initParents(final RakeTaskSerializableImpl subTask,
                             final RakeTaskSerializableImpl parentTask) {
        subTask.setParent(parentTask);
        final List<? extends RakeTask> tasks = subTask.getSubTasks();
        for (RakeTask task : tasks) {
            initParents((RakeTaskSerializableImpl)task, subTask);
        }
    }
}