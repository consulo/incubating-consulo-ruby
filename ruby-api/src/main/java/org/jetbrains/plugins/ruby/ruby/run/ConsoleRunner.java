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
import com.intellij.execution.Executor;
import com.intellij.execution.ExecutorRegistry;
import com.intellij.execution.executors.DefaultRunExecutor;
import com.intellij.execution.filters.Filter;
import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.actions.CloseAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import consulo.platform.base.icon.PlatformIconGroup;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.ColouredCommandLineState;
import org.jetbrains.plugins.ruby.ruby.run.filters.RStackTraceFilter;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: 02.09.2006
 */
public class ConsoleRunner
{
	private final Project myProject;
	private final String myConsoleTitle;
	private ProcessHandler myProcessHandler;
	private CommandLineArgumentsProvider myArgumentsProvider;
	private final Filter[] myConsoleFilters;
	private final ProcessListener myProcessListener;
	@Nullable
	private final String myWorkingDir;
	private final AnAction[] myUserActions;
	private RunContentDescriptorFactory myDescriptorFactory;

	/**
	 * @param project           Current project
	 * @param processListener   Listener for add.
	 * @param consoleFilters    filter console ouput. If is null filter will not be added.
	 * @param userActions       if these actions are not null, its will be added to console toolbar
	 * @param consoleTitle      Title for console
	 * @param workingDir        Working directory, null to inherit parent add home directory
	 * @param provider          Provides commandline arguments
	 * @param descriptorFactory User Factory for creating non default run content descriptors
	 */
	private ConsoleRunner(@Nonnull final Project project, @Nullable final ProcessListener processListener, @Nullable final Filter[] consoleFilters, @Nullable final AnAction[] userActions, @Nonnull final String consoleTitle, @Nullable final String workingDir, @Nonnull final CommandLineArgumentsProvider provider, @Nullable final RunContentDescriptorFactory descriptorFactory)
	{
		myProject = project;
		myConsoleTitle = consoleTitle;
		myArgumentsProvider = provider;
		myConsoleFilters = consoleFilters;
		myProcessListener = processListener;
		myWorkingDir = workingDir;
		myUserActions = userActions;
		myDescriptorFactory = descriptorFactory;
		init();

	}

	private void init()
	{
		// add holder created
		final String[] command = myArgumentsProvider.getArguments();
		myProcessHandler = ColouredCommandLineState.createOSProcessHandler(Runner.createProcess(myWorkingDir, command), toCommandLine(command));
		if(myProcessListener != null)
		{
			myProcessHandler.addProcessListener(myProcessListener);
		}
		// consoleview creating
		ConsoleView myConsoleView = TextConsoleBuilderFactory.getInstance().createBuilder(myProject).getConsole();
		myConsoleView.setHelpId(myConsoleTitle);

		// add stacktrace filter.(extends default filter)
		myConsoleView.addMessageFilter(new RStackTraceFilter(myProject, myWorkingDir));
		if(myConsoleFilters != null)
		{
			// add other filters
			for(Filter filter : myConsoleFilters)
			{
				myConsoleView.addMessageFilter(filter);
			}
		}
		myConsoleView.attachToProcess(myProcessHandler);
		// Runner creating
		final Executor defaultRunner = ExecutorRegistry.getInstance().getExecutorById(DefaultRunExecutor.EXECUTOR_ID);
		final DefaultActionGroup toolbarActions = new DefaultActionGroup();
		final DefaultActionGroup userActions = new DefaultActionGroup();

		//run content desciptor factory
		final RunContentDescriptorFactory factory = myDescriptorFactory == null ? RunContentDescriptorFactory.DEFAULT : myDescriptorFactory;
		final RunContentDescriptor myDescriptor = factory.createDesc(myConsoleView, myProcessHandler, new ConsolePanel(myConsoleView, toolbarActions, userActions), myConsoleTitle);

		// adding actions
		//user actions
		if(myUserActions != null)
		{
			for(AnAction userAction : myUserActions)
			{
				userActions.add(userAction);
			}
		}
		//rerun
		final Runnable rerun = new Runnable()
		{
			@Override
			public void run()
			{
				//Remove obsolete listener
				if(myProcessListener != null)
				{
					myProcessHandler.removeProcessListener(myProcessListener);
				}
				//Start new add
				init();
				startProcess(true);//todo
			}
		};
		toolbarActions.add(new RerunAction(myConsoleView, rerun));
		//stop
		toolbarActions.add(ActionManager.getInstance().getAction(IdeActions.ACTION_STOP_PROGRAM));
		//cmd line arguments
		toolbarActions.add(new ShowCmdLine());
		//close
		toolbarActions.add(new CloseAction(defaultRunner, myDescriptor, myProject));
		// showing run content
		ExecutionManager.getInstance(myProject).getContentManager().showRunContent(defaultRunner, myDescriptor);
	}

	//    /**
	//     * Runs command in idea Run console
	//     *
	//     * @param project      Current project
	//     * @param command      Command to execute (one command corresponds to one add argument)
	//     * @param workingDir   Working directory, null to inherit parent add home directory
	//     * @param consoleTitle Title for console
	//     */
	//    public static void run(@NotNull final Project project,
	//                           @NotNull final String consoleTitle,
	//                           @Nullable final String workingDir, @NotNull final String ... command) {
	//        run(project, null, consoleTitle, workingDir,
	//            new RubyScriptRunnerArgumentsProvider(command, null, null));
	//    }

	/**
	 * Runs command in idea Run console
	 *
	 * @param project               Current project
	 * @param processListener       Listener for add.
	 * @param consoleFilters        filter console ouput. If is null filter will not be added.
	 * @param userActions           if these actions are not null, its will be added to console toolbar
	 * @param runInBackgroundThread Run operation in background thread and show modal dialog
	 * @param provider              Provides commandline arguments
	 * @param descriptorFactory     User Factory for creating non default run content descriptors
	 * @param workingDir            Working directory, null to inherit parent add home directory
	 * @param consoleTitle          Title for console
	 */
	public static void run(@Nonnull final Project project, @Nullable final ProcessListener processListener, @Nullable final Filter[] consoleFilters, @Nullable final AnAction[] userActions, final boolean runInBackgroundThread, @Nonnull final String consoleTitle, @Nullable final String workingDir, @Nonnull final CommandLineArgumentsProvider provider, @Nullable final RunContentDescriptorFactory descriptorFactory)
	{
		// create runner
		IdeaInternalUtil.runInEventDispatchThread(new Runnable()
		{
			@Override
			public void run()
			{
				// Must be executed in EDT
				final ConsoleRunner runner = new ConsoleRunner(project, processListener, consoleFilters, userActions, consoleTitle, workingDir, provider, descriptorFactory);
				runner.startProcess(runInBackgroundThread);
			}
		}, ModalityState.defaultModalityState());
	}

	public ProcessHandler getProcessHandler()
	{
		return myProcessHandler;
	}

	private void startProcess(final boolean runInBackgroundThread)
	{
		final String title = RBundle.message("progress.title.console.runner.modaldialog.running", myConsoleTitle);
		final Task task = new Task.Backgroundable(myProject, title, true)
		{
			@Override
			public void run(ProgressIndicator indicator)
			{
				if(indicator != null)
				{
					indicator.setText(RBundle.message("progress.backgnd.indicator.title.please.wait", getTitle()));
				}

				myProcessHandler.startNotify();
				myProcessHandler.waitFor();
			}

			@Override
			public void onSuccess()
			{
				final Runnable runnable = new Runnable()
				{
					@Override
					public void run()
					{
						FileDocumentManager.getInstance().saveAllDocuments();
					}
				};
				IdeaInternalUtil.runInEventDispatchThread(runnable, ModalityState.NON_MODAL);
			}
		};

		if(runInBackgroundThread)
		{
			ProgressManager.getInstance().run(task);
		}
		else
		{
			task.run(null);
		}
	}

	private static String toCommandLine(String... command)
	{
		if(command.length > 0)
		{
			command[0] = FileUtil.toSystemDependentName(command[0]);
			return TextUtil.concat(command);
		}
		return TextUtil.EMPTY_STRING;
	}

	private class RerunAction extends AnAction
	{
		private Runnable myRerunTask;

		public RerunAction(final ConsoleView consoleView, Runnable rerun)
		{
			super(RBundle.message("action.rerun"), RBundle.message("action.rerun"), PlatformIconGroup.actionsRerun());

			registerCustomShortcutSet(CommonShortcuts.getRerun(), consoleView.getComponent());
			myRerunTask = rerun;
		}

		@Override
		public void update(AnActionEvent e)
		{
			e.getPresentation().setEnabled(getProcessHandler().isProcessTerminated());
		}

		@Override
		public void actionPerformed(AnActionEvent e)
		{
			myRerunTask.run();
		}
	}

	private class ShowCmdLine extends AnAction
	{

		@SuppressWarnings({"UnresolvedPropertyKey"})
		public ShowCmdLine()
		{
			super(RBundle.message("action.consolerunner.edit.cmdline.text"), RBundle.message("action.consolerunner.edit.cmdline.description"), RubyIcons.RUBY_RUNNER_SHOW_CMDLINE);
		}

		@Override
		public void update(final AnActionEvent e)
		{
			e.getPresentation().setEnabled(getProcessHandler().isProcessTerminated());
		}

		@Override
		public void actionPerformed(final AnActionEvent e)
		{
			// Conversts args array to cmdline
			final String[] args = myArgumentsProvider.getArguments();
			final StringBuilder buff = new StringBuilder();
			for(String arg : args)
			{
				buff.append(arg);
				buff.append(" ");
			}

			final String msg = RBundle.message("action.consolerunner.edit.cmdline.dialog.text");
			final String title = RBundle.message("action.consolerunner.edit.cmdline.dialog.title");
			final String result = Messages.showInputDialog(msg, title, null, buff.toString(), null);
			// Exit on "Cancel"
			if(TextUtil.isEmpty(result))
			{
				return;
			}

			// Disables actions for cmdline params controling
			myArgumentsProvider.disableParametersActions();

			// Parse user cmdline
			final ArrayList<String> userArgsList = new ArrayList<String>();
			final StringTokenizer st = new StringTokenizer(result);
			while(st.hasMoreTokens())
			{
				userArgsList.add(st.nextToken());
			}
			final String[] userArgs = userArgsList.toArray(new String[userArgsList.size()]);

			// Set new arguments provider
			myArgumentsProvider = new RubyScriptRunnerArgumentsProvider(userArgs, null, null);
		}
	}

	//    public class MyRunContentDescriptor extends RunContentDescriptor {
	//
	//        public MyRunContentDescriptor(ExecutionConsole executionConsole, ProcessHandler processHandler, JComponent component, String displayName) {
	//            super(executionConsole, processHandler, component, displayName);
	//        }
	//
	//        public void setAttachedContent(Content content) {
	//            super.setAttachedContent(content);
	//            content.setPinned(true);
	//            content.setCloseable();
	//        }
	//    }
}