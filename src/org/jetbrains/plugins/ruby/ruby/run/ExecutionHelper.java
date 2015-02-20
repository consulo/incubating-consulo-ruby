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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.ide.errorTreeView.NewErrorTreeViewPanel;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindowId;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.MessageView;
import com.intellij.util.Function;
import com.intellij.util.concurrency.Semaphore;
import com.intellij.util.ui.ErrorTreeView;
import com.intellij.util.ui.MessageCategory;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 4, 2007
 */
public class ExecutionHelper
{
	private static Logger LOG = Logger.getInstance(ExecutionHelper.class.getName());

	public static void showErrors(@NotNull final Project myProject, @NotNull final List<Exception> exceptionList, @NotNull final String tabDisplayName, @Nullable final VirtualFile file)
	{
		if(ApplicationManager.getApplication().isUnitTestMode() && !exceptionList.isEmpty())
		{
			throw new RuntimeException(exceptionList.get(0));
		}
		ApplicationManager.getApplication().invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				if(myProject.isDisposed())
				{
					return;
				}
				if(exceptionList.isEmpty())
				{
					removeContents(null, myProject, tabDisplayName);
					return;
				}

				final RailsErrorViewPanel errorTreeView = new RailsErrorViewPanel(myProject);
				try
				{
					openMessagesView(errorTreeView, myProject, tabDisplayName);
				}
				catch(NullPointerException e)
				{
					final StringBuilder builder = new StringBuilder();
					builder.append("Exceptions occured:");
					for(final Exception exception : exceptionList)
					{
						builder.append("\n");
						builder.append(exception.getMessage());
					}
					Messages.showErrorDialog(builder.toString(), RBundle.message("execution.error.title"));
					return;
				}
				for(final Exception exception : exceptionList)
				{
					String[] messages = new String[]{exception.getMessage()};
					if(messages.length == 0)
					{
						messages = new String[]{RBundle.message("exception.text.unknown.error")};
					}
					errorTreeView.addMessage(MessageCategory.ERROR, messages, file, -1, -1, null);
				}

				ToolWindowManager.getInstance(myProject).getToolWindow(ToolWindowId.MESSAGES_WINDOW).activate(null);
			}
		});
	}

	private static void openMessagesView(@NotNull final RailsErrorViewPanel errorTreeView, @NotNull final Project myProject, @NotNull final String tabDisplayName)
	{
		CommandProcessor commandProcessor = CommandProcessor.getInstance();
		commandProcessor.executeCommand(myProject, new Runnable()
		{
			@Override
			public void run()
			{
				final MessageView messageView = myProject.getComponent(MessageView.class);
				final Content content = ContentFactory.SERVICE.getInstance().createContent(errorTreeView, tabDisplayName, true);
				messageView.getContentManager().addContent(content);
				Disposer.register(content, errorTreeView);
				messageView.getContentManager().setSelectedContent(content);
				removeContents(content, myProject, tabDisplayName);
			}
		}, RBundle.message("command.name.open.error.message.view"), null);
	}

	private static void removeContents(@Nullable final Content notToRemove, @NotNull final Project myProject, @NotNull final String tabDisplayName)
	{
		MessageView messageView = myProject.getComponent(MessageView.class);
		Content[] contents = messageView.getContentManager().getContents();
		for(Content content : contents)
		{
			LOG.assertTrue(content != null);
			if(content.isPinned())
			{
				continue;
			}
			if(tabDisplayName.equals(content.getDisplayName()) && content != notToRemove)
			{
				ErrorTreeView listErrorView = (ErrorTreeView) content.getComponent();
				if(listErrorView != null)
				{
					if(messageView.getContentManager().removeContent(content, true))
					{
						content.release();
					}
				}
			}
		}
	}

	public static class RailsErrorViewPanel extends NewErrorTreeViewPanel
	{
		public RailsErrorViewPanel(final Project project)
		{
			super(project, null);
		}

		@Override
		protected boolean canHideWarningsOrInfos()
		{
			return false;
		}
	}


	public static void executeExternalProcess(@Nullable final Project myProject, @NotNull final OSProcessHandler processHandler, @NotNull final Runner.ExecutionMode mode)
	{
		final String title = mode.getTitle() != null ? mode.getTitle() : RBundle.message("progress.indicator.title.running.please.wait");
		assert title != null;

		final Runnable process;
		if(mode.cancelable())
		{
			process = createCancableExecutionProcess(processHandler, mode.shouldCancelFun());
		}
		else
		{
			if(mode.getTimeout() <= 0)
			{
				process = new Runnable()
				{
					@Override
					public void run()
					{
						processHandler.waitFor();
					}
				};
			}
			else
			{
				process = createTimelimitedExecutionProcess(processHandler, mode.getTimeout());
			}
		}
		if(mode.withModalProgress())
		{
			ProgressManager.getInstance().runProcessWithProgressSynchronously(process, title, mode.cancelable(), myProject);
		}
		else if(mode.inBackGround())
		{
			final Task task = new Task.Backgroundable(myProject, title, mode.cancelable())
			{
				@Override
				public void run(final ProgressIndicator indicator)
				{
					process.run();
				}
			};
			ProgressManager.getInstance().run(task);
		}
		else
		{
			final String title2 = mode.getTitle2();
			final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
			if(indicator != null && title2 != null)
			{
				indicator.setText2(title2);
			}
			process.run();
		}
	}

	private static Runnable createCancableExecutionProcess(final ProcessHandler processHandler, final Function<Object, Boolean> cancelableFun)
	{
		return new Runnable()
		{
			private ProgressIndicator myProgressIndicator;
			private Semaphore mySemaphore = new Semaphore();

			private Runnable myWaitThread = new Runnable()
			{
				@Override
				public void run()
				{
					processHandler.waitFor();
					mySemaphore.up();
				}
			};

			private Runnable myCancelListener = new Runnable()
			{
				@Override
				public void run()
				{
					for(; ; )
					{
						if(myProgressIndicator != null && (myProgressIndicator.isCanceled() || !myProgressIndicator.isRunning()) || (cancelableFun != null && cancelableFun.fun(null)))
						{

							processHandler.destroyProcess();
							mySemaphore.up();
							break;
						}
						else if(myProgressIndicator == null)
						{
							return;
						}
						try
						{
							synchronized(this)
							{
								wait(1000);
							}
						}
						catch(InterruptedException e)
						{
							//Do nothing
						}
					}
				}
			};

			@Override
			public void run()
			{
				myProgressIndicator = ProgressManager.getInstance().getProgressIndicator();
				if(myProgressIndicator != null)
				{
					myProgressIndicator.setText(RBundle.message("progress.indicator.title.please.wait"));
				}

				ApplicationManager.getApplication().executeOnPooledThread(myWaitThread);
				ApplicationManager.getApplication().executeOnPooledThread(myCancelListener);

				mySemaphore.down();
				mySemaphore.waitFor();
			}
		};
	}

	private static Runnable createTimelimitedExecutionProcess(final OSProcessHandler processHandler, final int timeout)
	{
		return new Runnable()
		{
			private Semaphore mySemaphore = new Semaphore();
			private final Object LOCK = new Object();
			private Boolean processedFinished = Boolean.FALSE;

			private Runnable myProcessThread = new Runnable()
			{
				@Override
				public void run()
				{
					processHandler.waitFor();
					synchronized(LOCK)
					{
						processedFinished = Boolean.TRUE;
					}
					mySemaphore.up();
				}
			};

			private Runnable myTimeoutListener = new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						synchronized(this)
						{
							wait(1000 * timeout);
							synchronized(LOCK)
							{
								if(!processedFinished)
								{
									LOG.error("Timeout (" + timeout + " sec) on executing: " + processHandler.getCommandLine());
									processHandler.destroyProcess();
								}
							}
							mySemaphore.up();
						}
					}
					catch(InterruptedException e)
					{
						//Do nothing
					}
				}
			};

			@Override
			public void run()
			{
				ApplicationManager.getApplication().executeOnPooledThread(myProcessThread);
				ApplicationManager.getApplication().executeOnPooledThread(myTimeoutListener);

				mySemaphore.down();
				mySemaphore.waitFor();
			}
		};
	}
}
