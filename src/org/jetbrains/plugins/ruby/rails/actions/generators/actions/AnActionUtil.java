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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.01.2007
 */
public class AnActionUtil {
    public static final AnAction[] NO_ACTIONS = new AnAction[0];

    /**
     * Enables, disables, hides or shows action presentation
     * @param presentation action presentation
     * @param visible if presentation should be visible
     * @param enabled if presentation should be enabled
     */
    public static void updatePresentation(@Nullable final Presentation presentation,
                                          final boolean visible,
                                          final boolean enabled) {
        if (presentation == null) {
            return;
        }

        if (presentation.isVisible() != visible) {
            presentation.setVisible(visible);
        }
        if (presentation.isEnabled() != enabled) {
            presentation.setEnabled(enabled);
        }
    }
}
