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

import consulo.execution.ExecutionManager;
import consulo.execution.action.CloseAction;
import consulo.execution.executor.DefaultRunExecutor;
import consulo.execution.executor.Executor;
import consulo.execution.executor.ExecutorRegistry;
import consulo.execution.ui.RunContentDescriptor;
import consulo.execution.ui.console.ConsoleView;
import consulo.execution.ui.console.ConsoleViewContentType;
import consulo.execution.ui.console.Filter;
import consulo.execution.ui.console.TextConsoleBuilderFactory;
import consulo.project.Project;
import consulo.project.startup.StartupManager;
import consulo.ui.ex.action.DefaultActionGroup;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 11.09.2006
 */
public class ConsoleWriter
{
	/**
	 * Prints output to Run content console
	 *
	 * @param project      Current project
	 * @param consoleTitle Console title text
	 * @param out          Output to be shown in console
	 * @param filters      message Filters to be added
	 */
	public static void print(@Nonnull final Project project, @Nonnull final String consoleTitle, @Nonnull final Output out, final Filter... filters)
	{
		Runnable myRunnable = new Runnable()
		{
			@Override
			public void run()
			{
				ConsoleView consoleView = TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
				for(Filter filter : filters)
				{
					consoleView.addMessageFilter(filter);
				}
				consoleView.setHelpId(consoleTitle);
				consoleView.print(out.getStdout(), ConsoleViewContentType.NORMAL_OUTPUT);
				consoleView.print(out.getStderr(), ConsoleViewContentType.ERROR_OUTPUT);
				DefaultActionGroup toolbarActions = new DefaultActionGroup();
				RunContentDescriptor myDescriptor = new RunContentDescriptor(consoleView, null, new ConsolePanel(consoleView, toolbarActions), consoleTitle);
				Executor defaultRunner = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
				toolbarActions.add(new CloseAction(defaultRunner, myDescriptor, project));
				ExecutionManager.getInstance(project).getContentManager().showRunContent(defaultRunner, myDescriptor);
			}
		};
		if(project.isInitialized())
		{
			myRunnable.run();
		}
		else
		{
			// If project is not initialized, default runner is not registered!
			StartupManager.getInstance(project).registerPostStartupActivity(myRunnable);
		}
	}
}
