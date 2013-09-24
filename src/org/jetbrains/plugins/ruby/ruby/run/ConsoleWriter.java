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

import com.intellij.execution.ExecutionManager;
import com.intellij.execution.ExecutionRegistry;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.runners.JavaProgramRunner;
import com.intellij.execution.ui.CloseAction;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 * @author: oleg
 * @date: 11.09.2006
 */
public class ConsoleWriter {
    /**
     * Prints output to Run content console
     * @param project Current project
     * @param consoleTitle Console title text
     * @param out Output to be shown in console
     * @param filters message Filters to be added
     */
    public static void print(@NotNull final Project project,
                             @NotNull final String consoleTitle,
                             @NotNull final Output out,
                             final Filter ... filters) {
        Runnable myRunnable = new Runnable(){
            public void run(){
                ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
                for (Filter filter : filters) {
                    consoleView.addMessageFilter(filter);
                }
                consoleView.setHelpId(consoleTitle);
                consoleView.print(out.getStdout(), ConsoleViewContentType.NORMAL_OUTPUT);
                consoleView.print(out.getStderr(), ConsoleViewContentType.ERROR_OUTPUT);
                DefaultActionGroup toolbarActions = new DefaultActionGroup();
                RunContentDescriptor myDescriptor =
                        new RunContentDescriptor(consoleView, null,
                                new ConsolePanel(consoleView, toolbarActions),
                                consoleTitle);
                JavaProgramRunner defaultRunner = ExecutionRegistry.getInstance().getDefaultRunner();
                toolbarActions.add(new CloseAction(defaultRunner, myDescriptor, project));
                ExecutionManager.getInstance(project).getContentManager().showRunContent(defaultRunner, myDescriptor);
            }
        };
        if (project.isInitialized()){
            myRunnable.run();
        } else {
// If project is not initialized, default runner is not registered!
            StartupManager.getInstance(project).registerPostStartupActivity(myRunnable);
        }
    }
}
