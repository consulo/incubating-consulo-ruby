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

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.07.2006
 */
public class BinaryExprParsing implements RubyTokenTypes
{

	/**
	 * Parsing (leftOperand Operation rightOperand) Operation ...
	 *
	 * @param builder              Current builder
	 * @param parseOperandMethod   method used parsing each of single operands
	 * @param errorOperandNotFound Error which generates when no operand found, but it must exist
	 * @param operations           Set of the operation tokens
	 * @param statementType        The result type of expression
	 * @return Result of parsing
	 */
	public static IElementType parse(final RBuilder builder, final ParsingMethod parseOperandMethod, final String errorOperandNotFound, final TokenSet operations, final IElementType statementType)
	{
		return parseWithLeadOperand(builder, builder.mark(), parseOperandMethod.parse(builder), parseOperandMethod, errorOperandNotFound, operations, statementType);
	}

	/**
	 * Parsing (leftOperand Operation rightOperand) Operation ...
	 *
	 * @param builder              Current builder
	 * @param parseOperandMethod   method used parsing each of single operands
	 * @param errorOperandNotFound Error which generates when no operand found, but it must exist
	 * @param operation            type of operationToken
	 * @param statementType        The result type of expression
	 * @return Result of parsing
	 */
	public static IElementType parse(final RBuilder builder, final ParsingMethod parseOperandMethod, final String errorOperandNotFound, final IElementType operation, final IElementType statementType)
	{

		return parseWithLeadOperand(builder, builder.mark(), parseOperandMethod.parse(builder), parseOperandMethod, errorOperandNotFound, operation, statementType);
	}

	/**
	 * Parsing (leftOperand Operation rightOperand) Operation ...
	 *
	 * @param builder              Current builder
	 * @param parseOperandMethod   method used parsing each of single operands
	 * @param errorOperandNotFound Error which generates when no operand found, but it must exist
	 * @param operations           Set of the operation tokens
	 * @param statementType        The result type of expression
	 * @param marker               Marker before first operand
	 * @param result               result of First operand parsing
	 * @return Result of parsing
	 */
	public static IElementType parseWithLeadOperand(final RBuilder builder, RMarker marker, IElementType result, final ParsingMethod parseOperandMethod, final String errorOperandNotFound, final TokenSet operations, final IElementType statementType)
	{
		if(result == RubyElementTypes.EMPTY_INPUT)
		{
			marker.drop();
			return RubyElementTypes.EMPTY_INPUT;
		}
		while(builder.compareAndEat(operations))
		{
			if(parseOperandMethod.parse(builder) == RubyElementTypes.EMPTY_INPUT)
			{
				builder.error(errorOperandNotFound);
			}
			marker.done(statementType);
			marker = marker.precede();
			result = statementType;
		}
		marker.drop();
		return result;
	}

	/**
	 * Parsing (leftOperand Operation rightOperand) Operation ...
	 *
	 * @param builder              Current builder
	 * @param parseOperandMethod   method used parsing each of single operands
	 * @param errorOperandNotFound Error which generates when no operand found, but it must exist
	 * @param operation            type of operationToken
	 * @param statementType        The result type of expression
	 * @param marker               Marker before first operand
	 * @param result               result of First operand parsing
	 * @return Result of parsing
	 */
	public static IElementType parseWithLeadOperand(final RBuilder builder, RMarker marker, IElementType result, final ParsingMethod parseOperandMethod, final String errorOperandNotFound, final IElementType operation, final IElementType statementType)
	{
		if(result == RubyElementTypes.EMPTY_INPUT)
		{
			marker.drop();
			return RubyElementTypes.EMPTY_INPUT;
		}
		while(builder.compareAndEat(operation))
		{
			if(parseOperandMethod.parse(builder) == RubyElementTypes.EMPTY_INPUT)
			{
				builder.error(errorOperandNotFound);
			}
			marker.done(statementType);
			marker = marker.precede();
			result = statementType;
		}
		marker.drop();
		return result;
	}

}
