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

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.FirstLastTokensBNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.09.2006
 */
class DefinedExpression implements RubyTokenTypes
{
	/*
		| kDEFINED opt_nl  arg
	*/
	public static IElementType parse(final RBuilder builder)
	{
		final RMarker statementMarker = builder.mark();
		if(builder.compareAndEat(kDEFINED))
		{
			if(builder.compareIgnoreEOL(FirstLastTokensBNF.tARG_FIRST_TOKEN))
			{
				if(ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT)
				{
					builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
				}

				statementMarker.done(RubyElementTypes.DEFINED_STATEMENT);
				return RubyElementTypes.DEFINED_STATEMENT;
			}
		}
		statementMarker.rollbackTo();
		return Assignment.parse(builder);
	}


	/**
	 * Parsing Defined with lead PRIMARY already parsed
	 *
	 * @param builder Current builder
	 * @param marker  Marker before PRIMARY
	 * @param result  result of PRIMARY
	 * @return result of parsing
	 */
	public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		return Assignment.parseWithLeadRANGE(builder, marker, result);
	}
}
