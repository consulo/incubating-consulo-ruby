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
class BitExpression implements RubyTokenTypes
{

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		return parseBitOr(builder);
	}

	/**
	 * Parsing binary expression with operations:  | ^
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseBitOr(final RBuilder builder)
	{
		return parseBitOrWithLeadBitAnd(builder, builder.mark(), parseBitAnd(builder));
	}

	/**
	 * Parsing binary or with lead bit and
	 *
	 * @param builder Current builder
	 * @param marker  Marker before lead BitAnd
	 * @param result  result of bit And parsing
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseBitOrWithLeadBitAnd(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@NotNull
			public IElementType parseInner(final RBuilder builder)
			{
				return parseBitAnd(builder);
			}
		};
		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, BNF.tBIT_OR_OPS, RubyElementTypes.BIT_EXPRESSION);
	}

	/**
	 * Parsing binary expression with operations:  &
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseBitAnd(final RBuilder builder)
	{
		return parseBitAndWithLeadShift(builder, builder.mark(), ShiftExpression.parse(builder));
	}

	/**
	 * Parsing bit and with lead Shift
	 *
	 * @param builder Current builder
	 * @param marker  Marker before lead ShiftExpr
	 * @param result  result of ShiftExpr parsing
	 * @return result of parsing
	 */
	@NotNull
	private static IElementType parseBitAndWithLeadShift(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@NotNull
			public IElementType parseInner(final RBuilder builder)
			{
				return ShiftExpression.parse(builder);
			}
		};

		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, tBIT_AND, RubyElementTypes.BIT_EXPRESSION);
	}

	public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		return parseBitOrWithLeadBitAnd(builder, marker.precede(), parseBitAndWithLeadShift(builder, marker.precede(), ShiftExpression.parseWithLeadPRIMARY(builder, marker, result)));
	}
}
