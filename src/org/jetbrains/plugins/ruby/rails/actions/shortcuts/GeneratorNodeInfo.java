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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.actions.generators.SerializableGenerator;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 24.03.2007
 */
public class GeneratorNodeInfo extends NodeInfo<SerializableGenerator>{

    public static String getActionId(final String generatorName) {
        return "ruby generator-" + generatorName;
    }

    public static GeneratorNodeInfo createRootNode() {
        final SerializableGenerator generator = new SerializableGenerator(RBundle.message("dialog.register.shortcut.roots.generators"), true, null);
        return new GeneratorNodeInfo(RailsIcons.GENERATORS_ROOT_ICON, generator, true);
    }

    public static GeneratorNodeInfo createGeneratorNode(@NotNull final String name,
                                                        final boolean isGroup,
                                                        final SerializableGenerator parent) {
        final SerializableGenerator generator = new SerializableGenerator(name, isGroup, parent);
        parent.addChild(generator);
        if (isGroup) {
            return new GeneratorNodeInfo(RailsIcons.RAILS_FOLDER_OPENED, RailsIcons.RAILS_FOLDER_CLOSED,
                                         generator, true);
        }
        return new GeneratorNodeInfo(RailsIcons.GENERATOR_ICON, generator, false);
    }

    public String getActionId() {
        return getActionId(this.getData().getName());
    }

    private GeneratorNodeInfo(final Icon icon, final SerializableGenerator generator, final boolean isGroup) {
        this(icon, icon, generator, isGroup);
    }
    private GeneratorNodeInfo(final Icon openIcon, final Icon closedIcon, final SerializableGenerator generator, final boolean isGroup) {
        super(openIcon, closedIcon, generator, isGroup);
    }

    public String toString() {
        return getData().getName();
    }
}
