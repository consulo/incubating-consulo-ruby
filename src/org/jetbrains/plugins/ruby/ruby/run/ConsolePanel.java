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

package org.jetbrains.plugins.ruby.ruby.run;

import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * @author: oleg
 * @date: 11.09.2006
 */
class ConsolePanel extends JPanel {
    public ConsolePanel(@NotNull final ExecutionConsole consoleView,
                        @NotNull final ActionGroup toolbarActions) {
        this(consoleView, toolbarActions, null);
    }
    
    public ConsolePanel(@NotNull final ExecutionConsole consoleView,
                        @NotNull final ActionGroup toolbarActions,
                        @Nullable final ActionGroup userActions) {
        super(new BorderLayout());
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, toolbarActions, false).getComponent());
        if (userActions != null) {
            toolbarPanel.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, userActions, false).getComponent(), BorderLayout.EAST);
        }
        add(toolbarPanel, BorderLayout.WEST);
        add(consoleView.getComponent(), BorderLayout.CENTER);
    }
}
