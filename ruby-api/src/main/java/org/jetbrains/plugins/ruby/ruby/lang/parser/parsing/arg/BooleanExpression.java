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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg;


import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.BinaryExprParsing;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ParsingMethodWithAssignmentLookup;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.06.2006
 */
class BooleanExpression implements RubyTokenTypes
{

	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		return parseOr(builder);
	}

	/**
	 * Parsing binary expression with operations:  ||
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@Nonnull
	private static IElementType parseOr(final RBuilder builder)
	{
		return parseOrWithLeadAND(builder, builder.mark(), parseAnd(builder));
	}

	/**
	 * Parsing Or with Lead AND parsed
	 *
	 * @param builder Current builder
	 * @param marker  Marker before lead AND
	 * @param result  result of AND parsing
	 * @return result of parsing
	 */
	@Nonnull
	private static IElementType parseOrWithLeadAND(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@Nonnull
			public IElementType parseInner(final RBuilder builder)
			{
				return parseAnd(builder);
			}
		};

		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, tOR, RubyElementTypes.BOOL_BINARY_EXPRESSION);

	}

	/**
	 * Parsing binary expression with operations:  &&
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@Nonnull
	private static IElementType parseAnd(final RBuilder builder)
	{
		return parseAndWithLeadMatch(builder, builder.mark(), MatchingExpression.parse(builder));
	}

	/**
	 * Parsing binary expression with operations:  &&
	 *
	 * @param builder Current builder
	 * @param marker  Marker before lead Match
	 * @param result  result of Match parsing
	 * @return result of parsing
	 */
	@Nonnull
	private static IElementType parseAndWithLeadMatch(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@Nonnull
			public IElementType parseInner(final RBuilder builder)
			{
				return MatchingExpression.parse(builder);
			}
		};

		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, tAND, RubyElementTypes.BOOL_BINARY_EXPRESSION);

	}

	public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		return parseOrWithLeadAND(builder, marker.precede(), parseAndWithLeadMatch(builder, marker.precede(), MatchingExpression.parseWithLeadPRIMARY(builder, marker, result)));
	}
}
