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

import javax.swing.SwingUtilities;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ActionRunner;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 1, 2007
 */
public class IdeaInternalUtil
{
	private static final Logger LOG = Logger.getInstance(IdeaInternalUtil.class.getName());

	public static void runInsideWriteAction(@NotNull ActionRunner.InterruptibleRunnable runnable)
	{
		try
		{
			ActionRunner.runInsideWriteAction(runnable);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static void runWriteAction(@NotNull Runnable runnable)
	{
		ApplicationManager.getApplication().runWriteAction(runnable);
	}

	public static void runInEventDispatchThread(final Runnable runnable, final ModalityState state)
	{
		try
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
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}


	public static void runInEDThreadInWriteAction(@NotNull final ActionRunner.InterruptibleRunnable runnable, final ModalityState state)
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

	public static void runInsideReadAction(@NotNull final ActionRunner.InterruptibleRunnable runnable)
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
				ActionRunner.runInsideReadAction(runnable);
			}
			catch(Exception e)
			{
				LOG.warn(e);
			}
		}
	}
}
