/*
 * Copyright 2000-2007 JetBrains s.r.o.
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

import java.util.Collections;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 14, 2008
 */
public class REmptyType extends RTypeBase implements RType
{

	public static REmptyType INSTANCE = new REmptyType();

	private REmptyType()
	{
	}

	@Override
	@NotNull
	public Set<Message> getMessages()
	{
		return Collections.emptySet();
	}

	@Override
	public Set<Message> getMessagesForName(@Nullable final String name)
	{
		return Collections.emptySet();
	}

	public String toString()
	{
		return "Not typed";
	}
}