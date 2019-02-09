/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.jetbrains.plugins.ruby.ruby.inspections.ducktype;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;

/**
 * @author: oleg
 * @date: Jul 1, 2008
 */
public class ExpectedMessages
{
	private Set<String> myRespondsTo = new HashSet<String>();
	private Set<String> myNames = new HashSet<String>();
	private Set<Message> myExpectedMessages = new HashSet<Message>();

	public void addRespondsTo(@Nonnull final String name)
	{
		myRespondsTo.add(name);
	}

	public void addMessage(@Nonnull final Message message)
	{
		final String name = message.getName();
		if(!myRespondsTo.contains(name))
		{
			myNames.add(name);
			myExpectedMessages.add(message);
		}
	}

	public boolean containsName(@Nonnull final String name)
	{
		return myNames.contains(name);
	}

	public Set<Message> getExpectedMessages()
	{
		return myExpectedMessages;
	}
}
