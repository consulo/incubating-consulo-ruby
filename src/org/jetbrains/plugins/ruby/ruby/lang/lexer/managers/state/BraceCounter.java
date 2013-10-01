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

package org.jetbrains.plugins.ruby.ruby.lang.lexer.managers.state;

import java.util.Stack;

import com.intellij.openapi.diagnostic.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 23, 2006
 */
public class BraceCounter implements StateComponent
{
	private static final Logger LOG = Logger.getInstance(BraceCounter.class.getName());

	private enum BRACE
	{
		OPEN, CLOSE
	}

	private Stack<BRACE> myBraceStack;

	public BraceCounter()
	{
		this(0);
	}

	public BraceCounter(final int balance)
	{
		assert (balance >= 0);
		myBraceStack = new Stack<BRACE>();
		for(int i = 0; i < balance; i++)
		{
			processOpenBrace();
		}
	}

	public boolean isEmpty()
	{
		return myBraceStack.size() == 0;
	}

	public void processOpenBrace()
	{
		myBraceStack.push(BRACE.OPEN);
	}

	public void processCloseBrace()
	{
		if(myBraceStack.isEmpty())
		{
			return;
		}
		BRACE previous = myBraceStack.peek();
		if(previous == BRACE.OPEN)
		{
			myBraceStack.pop();
		}
		else
		{
			LOG.error("No open brace for this close");
		}
	}
}
