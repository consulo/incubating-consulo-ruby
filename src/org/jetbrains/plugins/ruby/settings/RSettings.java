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

import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.SerializableGenerator;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.actions.shortcuts.*;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;

import javax.swing.*;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Nov 16, 2006
 */
public class RSettings implements ApplicationComponent, Configurable {
    private RSettingsPane form;

    @Override
	public void initComponent() {
        loadRegisteredData();
    }

    @Override
	public void disposeComponent() {
    }

    @Override
	@NotNull
    public String getComponentName() {
        return RComponents.RSETTINGS;
    }

    @Override
	public String getDisplayName() {
        return RBundle.message("settings.title");
    }

    public Icon getIcon() {
        return RubyIcons.RUBY_LARGE_ICON;
    }

    @Override
	public String getHelpTopic() {
        return null;
    }

    @Override
	public JComponent createComponent() {
        if (form == null) {
            form = new RSettingsPane();
        }
        return form.getPanel();
    }

    @Override
	public boolean isModified() {
        return form == null || form.isModified();
    }

    @Override
	public void apply() throws ConfigurationException {
        if (form != null) {
            // Get data from form to component
            form.apply();
        }
    }

    @Override
	public void reset() {
        if (form != null) {
            // Reset form data from component
            form.reset();
        }
    }

    @Override
	public void disposeUIResources() {
        form = null;
    }

    private void loadRegisteredData() {
        final RubyShortcutsSettings settings = RubyShortcutsSettings.getInstance();
        if (settings.serializableGenerators != null) {
            loadGenerator(settings.serializableGenerators);
        }

        if (settings.serializableRakeTask != null) {
            loadRakeTask(settings.serializableRakeTask);
        }

        //Register default actions in dedault keymap
        final Keymap[] keymaps = KeymapManagerEx.getInstanceEx().getAllKeymaps();
        for (Keymap keymap : keymaps) {
            if (KeymapManager.DEFAULT_IDEA_KEYMAP.equals(keymap.getName())) {
                registerDefaultActions(keymap);
            }
        }
    }

    private void registerDefaultActions(final Keymap keymap) {
        //Generators
        final KeyStroke ctrAltG = KeyStroke.getKeyStroke("ctrl alt G");
        keymap.addShortcut(GeneratorNodeInfo.getActionId("controller"),
                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("C")));
        keymap.addShortcut(GeneratorNodeInfo.getActionId("model"),
                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("M")));
        keymap.addShortcut(GeneratorNodeInfo.getActionId("migration"),
                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("I")));
        keymap.addShortcut(GeneratorNodeInfo.getActionId("scaffold"),
                           new KeyboardShortcut(ctrAltG, KeyStroke.getKeyStroke("S")));

        //RakeTasks
        final KeyStroke ctrAltR = KeyStroke.getKeyStroke("ctrl alt R");
        keymap.addShortcut(RakeTaskNodeInfo.getActionId("db:migrate"),
                           new KeyboardShortcut(ctrAltR, KeyStroke.getKeyStroke("I")));
    }

    private void loadRakeTask(final RakeTask task) {
        if (!task.isGroup()) {
            final String cmd = task.getFullCommand();
            assert cmd != null;
            final String actionId = RakeTaskNodeInfo.getActionId(task.getFullCommand());
            new ShortcutAction(task.getId(),
                               cmd, RailsIcons.RAKE_TASK_ICON,
                               ShortcutsTreeState.RAKE_SUBTREE).registerInKeyMap(actionId);
            return;
        }
        final List<? extends RakeTask> children = task.getSubTasks();
        for (RakeTask child : children) {
            loadRakeTask(child);
        }
    }

    private void loadGenerator(final SerializableGenerator generator) {
        if (!generator.isGroup()) {
            final String actionId = GeneratorNodeInfo.getActionId(generator.getName());
            new ShortcutAction(generator.getName(),
                                   generator.getName(),
                                   RailsIcons.GENERATOR_ICON,
                                   ShortcutsTreeState.GENERATORS_SUBTREE).registerInKeyMap(actionId);
            return;
        }
        final List<SerializableGenerator> children = generator.getChildren();
        for (SerializableGenerator child : children) {
            loadGenerator(child);
        }
    }
}
