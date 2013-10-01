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
class ShiftExpression implements RubyTokenTypes
{

	/**
	 * Parsing binary expression with operations:  >>, <<
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		return parseWithLeadMathExpr(builder, builder.mark(), MathExpression.parse(builder));
	}

	/**
	 * Parsing shift expr with lead Math Expr
	 *
	 * @param builder Current builder
	 * @param marker  Marker before lead Math Expr
	 * @param result  result of math Expr parsed
	 * @return result of parsing
	 */
	@NotNull
	public static IElementType parseWithLeadMathExpr(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup()
		{
			@Override
			@NotNull
			public IElementType parseInner(final RBuilder builder)
			{
				return MathExpression.parse(builder);
			}
		};

		return BinaryExprParsing.parseWithLeadOperand(builder, marker, result, parsingMethod, ErrorMsg.EXPRESSION_EXPECTED_MESSAGE, BNF.tSHIFT_OPS, RubyElementTypes.SHIFT_EXPRESSION);
	}

	public static IElementType parseWithLeadPRIMARY(RBuilder builder, RMarker marker, IElementType result)
	{
		return parseWithLeadMathExpr(builder, marker.precede(), MathExpression.parseWithLeadPRIMARY(builder, marker, result));
	}
}
