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

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ExecutionConsole;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.ui.content.Content;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 13, 2007
 */
public abstract class RunContentDescriptorFactory
{
	public static final RunContentDescriptorFactory DEFAULT = new DefaultFactory();

	public abstract RunContentDescriptor createDesc(final ConsoleView consoleView, final ProcessHandler processHandler, final ConsolePanel consolePanel, final String consoleTitle);

	private static class DefaultFactory extends RunContentDescriptorFactory
	{
		@Override
		public RunContentDescriptor createDesc(ConsoleView consoleView, ProcessHandler processHandler, ConsolePanel consolePanel, String consoleTitle)
		{
			return new RunContentDescriptor(consoleView, processHandler, consolePanel, consoleTitle);
		}
	}

	public static class PinTabsFactory extends RunContentDescriptorFactory
	{
		final List<Content> myContentList = new ArrayList<Content>();

		@Override
		public RunContentDescriptor createDesc(ConsoleView consoleView, ProcessHandler processHandler, ConsolePanel consolePanel, String consoleTitle)
		{
			return new MyDescriptor(consoleView, processHandler, consolePanel, consoleTitle);
		}

		public List<Content> getContentList()
		{
			return myContentList;
		}

		public void unpinAll()
		{
			for(Content content : myContentList)
			{
				content.setPinned(false);
			}
		}

		private class MyDescriptor extends RunContentDescriptor
		{
			public MyDescriptor(ExecutionConsole executionConsole, ProcessHandler processHandler, JComponent component, String displayName)
			{
				super(executionConsole, processHandler, component, displayName);
			}

			@Override
			public void setAttachedContent(Content content)
			{
				super.setAttachedContent(content);
				content.setPinned(true);
				myContentList.add(content);
			}
		}
	}
}
