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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.MarkupUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 8, 2007
 */

public class SpecialSymbol extends Symbol
{

	private final Symbol myLinkedSymbol;

	public SpecialSymbol(@Nonnull final FileSymbol fileSymbol, @Nullable String name, @Nullable final Symbol parent, @Nonnull final Symbol symbol, final Type type)
	{
		super(fileSymbol, name, type, parent, null);
		myLinkedSymbol = symbol;
	}

	public SpecialSymbol(@Nonnull final FileSymbol fileSymbol, @Nullable final Symbol parent, @Nonnull final Symbol symbol, final Type type)
	{
		this(fileSymbol, null, parent, symbol, type);
	}

	@Override
	@Nonnull
	public Symbol getLinkedSymbol()
	{
		return myLinkedSymbol;
	}

	@Override
	@SuppressWarnings({"StringConcatenationInsideStringBufferAppend"})
	public String toString(@Nonnull final FileSymbol fileSymbol, boolean useHtml)
	{
		final StringBuilder builder = new StringBuilder();
		builder.append("[" + getId() + "] " + getType() + " ");
		if(myName != null)
		{
			if(useHtml)
			{
				MarkupUtil.appendBold(builder, myName);
			}
			else
			{
				builder.append(myName);
			}
			builder.append(" ");
		}
		if(useHtml)
		{
			MarkupUtil.appendBold(builder, myLinkedSymbol.getName());
			builder.append(" ");
		}
		builder.append("[" + myLinkedSymbol.getId() + "]");
		return builder.toString();
	}

	public String toString()
	{
		return myType + ": [" + myLinkedSymbol.toString() + "]";
	}
}
