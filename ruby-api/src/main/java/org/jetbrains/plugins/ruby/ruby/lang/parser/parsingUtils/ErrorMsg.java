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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils;

import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.PresentableElementType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.06.2006
 */
public class ErrorMsg
{

	private static final String EXPECTED_MESSAGE = RBundle.message("parsing.message.expected");
	private static final String UNEXPECTED_MESSAGE = RBundle.message("parsing.message.unexpected");
	public static final String EXPRESSION_EXPECTED_MESSAGE = expected(RBundle.message("parsing.error.expression"));

	@Nonnull
	public static String expected(String s)
	{
		return EXPECTED_MESSAGE + " " + s;
	}

	@Nonnull
	private static String unexpected(String s)
	{
		return UNEXPECTED_MESSAGE + " " + s;
	}

	@Nonnull
	public static String expected(IElementType type)
	{
		return expected(getPresentableName(type));
	}

	@Nullable
	private static String getPresentableName(final IElementType type)
	{
		if(type instanceof PresentableElementType)
		{
			return ((PresentableElementType) type).getPresentableName();
		}
		return type != null ? type.toString() : "null";
	}

	@Nonnull
	public static String unexpected(IElementType type)
	{
		return unexpected(getPresentableName(type));
	}

	@Nonnull
	public static String expected(final TokenSet types, final RBuilder builder)
	{
		return EXPECTED_MESSAGE + " " + setToString(types, builder);
	}

	@Nonnull
	public static String unexpected(final TokenSet types, final RBuilder builder)
	{
		return UNEXPECTED_MESSAGE + " " + setToString(types, builder);
	}

	@Nonnull
	private static String setToString(@Nonnull final TokenSet set, @Nonnull final RBuilder builder)
	{
		final String cachedString = builder.getErrorCache().get(set);
		if(cachedString != null)
		{
			return cachedString;
		}

		// stringSet used for not to add tokens with similar text, tCOLON2 and tCOLON3 for example!
		final HashSet<String> stringSet = new HashSet<String>();
		final StringBuilder buffer = new StringBuilder();
		for(IElementType myToken : set.getTypes())
		{
			if(!builder.isAcceptibleErrorToken(myToken))
			{
				continue;
			}
			final String tokenText = getPresentableName(myToken);
			if(TextUtil.isEmpty(tokenText))
			{
				continue;
			}
			if(!stringSet.contains(tokenText))
			{
				if(buffer.length() != 0)
				{
					buffer.append(" or ");
				}
				stringSet.add(tokenText);
				buffer.append(tokenText);
			}
		}
		final String errorString = buffer.toString();
		builder.getErrorCache().put(set, errorString);
		return errorString;
	}

}
