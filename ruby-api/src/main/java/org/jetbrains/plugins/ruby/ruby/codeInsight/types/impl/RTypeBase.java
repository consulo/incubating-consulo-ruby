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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 13, 2008
 */
public abstract class RTypeBase implements RType
{
	@Override
	public boolean isTyped()
	{
		return false;
	}

	@Override
	@Nullable
	public String getName()
	{
		return null;
	}

	@Override
	public RType addMessage(@Nonnull final Message message)
	{
		final DuckTypeImpl duckType = new DuckTypeImpl();
		duckType.addMessage(message);
		return RTypeUtil.joinAnd(this, new RDuckTypeImpl(duckType));
	}

	@Override
	public boolean matchesMessage(@Nonnull final Message message)
	{
		for(Message m : getMessagesForName(message.getName()))
		{
			if(m.matchesMessage(message))
			{
				return true;
			}
		}
		return false;
	}
}
