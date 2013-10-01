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


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
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
class MatchingExpression implements RubyTokenTypes
{

	/**
	 * Parsing Matching binary expressions
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		return parseLowPriority(builder);
	}


	/**
	 * Parsing binary expression with operations:  <code>BNF.tMATCH_SYMBOLS_LOW_PRIORITY</code>
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseLowPriority(final RBuilder builder)
	{
		return parseLowPriorityWithLeadHigh(builder, builder.mark(), parseHighPriority(builder));
	}

	/**
	 * Parsing binary expression with operations:  <code>BNF.tMATCH_SYMBOLS_LOW_PRIORITY</code> with lead high prior
	 *
	 * @param builder Current builder
	 * @param marker  Marker before High priority match parsed
	 * @param result  result of high priority match parsed
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseLowPriorityWithLeadHigh(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@NotNull
			public IElementType parseInner(final RBuilder builder)
			{
				return parseHighPriority(builder);
			}
		};

		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, BNF.tMATCH_LOW_PRIORITY_OPS, RubyElementTypes.BOOL_MATCHING_EXPRESSION);

	}

	/**
	 * Parsing binary expression with operations:  <code>BNF.tMATCH_SYMBOLS_HIGH_PRIORITY</code>
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseHighPriority(final RBuilder builder)
	{
		return parseHighPriorityWithLeadBitExpr(builder, builder.mark(), BitExpression.parse(builder));
	}

	/**
	 * Parsing binary expression with operations:  <code>BNF.tMATCH_SYMBOLS_HIGH_PRIORITY</code> with Bit expr parsed
	 *
	 * @param builder Current builder
	 * @param marker  marker before Bit expr
	 * @param result  Result of bit expr parsed
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseHighPriorityWithLeadBitExpr(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@NotNull
			public IElementType parseInner(final RBuilder builder)
			{
				return BitExpression.parse(builder);
			}
		};

		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, BNF.tMATCH_HIGH_PRIORITY_OPS, RubyElementTypes.BOOL_MATCHING_EXPRESSION);

	}

	public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		return parseLowPriorityWithLeadHigh(builder, marker.precede(), parseHighPriorityWithLeadBitExpr(builder, marker.precede(), BitExpression.parseWithLeadPRIMARY(builder, marker, result)));
	}
}
