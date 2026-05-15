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

package org.jetbrains.plugins.ruby.support.utils;

import consulo.application.AccessRule;
import consulo.application.ApplicationManager;
import consulo.application.WriteAction;
import consulo.logging.Logger;
import consulo.ui.ModalityState;
import consulo.util.lang.function.ThrowableRunnable;
import jakarta.annotation.Nonnull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 1, 2007
 */
public class IdeaInternalUtil
{
	private static final Logger LOG = Logger.getInstance(IdeaInternalUtil.class);

	public static void runInsideWriteAction(@Nonnull consulo.util.lang.function.ThrowableRunnable<Exception> runnable)
	{
		try
		{
			WriteAction.run(runnable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void runWriteAction(@Nonnull Runnable runnable)
	{
		ApplicationManager.getApplication().runWriteAction(runnable);
	}

	public static void runInEventDispatchThread(final Runnable runnable, final ModalityState state)
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			runnable.run();
		}
		else
		{
			ApplicationManager.getApplication().invokeAndWait(runnable, state);
		}
	}


	public static void runInEDThreadInWriteAction(@Nonnull final ThrowableRunnable<Exception> runnable, final ModalityState state)
	{
		runInEventDispatchThread(new Runnable()
		{
			@Override
			public void run()
			{
				runInsideWriteAction(runnable);
			}
		}, state);
	}

	public static void runInsideReadAction(@Nonnull final consulo.util.lang.function.ThrowableRunnable<Exception> runnable)
	{
		if(ApplicationManager.getApplication().isUnitTestMode())
		{
			ApplicationManager.getApplication().runReadAction(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						runnable.run();
					}
					catch(Exception e)
					{
						throw new RuntimeException(e);
					}
				}
			});
		}
		else
		{
			try
			{
				AccessRule.read(runnable);
			}
			catch(Exception e)
			{
				LOG.warn(e);
			}
		}
	}
}
