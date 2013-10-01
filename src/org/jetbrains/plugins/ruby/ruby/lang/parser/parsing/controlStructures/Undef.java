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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.controlStructures;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.SYMBOL;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method.FNAME;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ListParsingUtil;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.06.2006
 */
public class Undef implements RubyTokenTypes
{
/*
	| kUNDEF undef_list
*/

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();
		builder.match(kUNDEF);

		if(parseUndefList(builder) == RubyElementTypes.EMPTY_INPUT)
		{
			builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
		}

		statementMarker.done(RubyElementTypes.UNDEF_STATEMENT);
		return RubyElementTypes.UNDEF_STATEMENT;
	}
/*
    fitem		: fname
            | symbol
            ;

    undef_list	: fitem
            | undef_list ','  fitem
            ;
*/

	@NotNull
	public static IElementType parseUndefList(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();

		ParsingMethod parsingMethod = new ParsingMethod()
		{
			@Override
			@NotNull
			public IElementType parse(final RBuilder builder)
			{
				IElementType result = FNAME.parse(builder);
				if(result != RubyElementTypes.EMPTY_INPUT)
				{
					return result;
				}
				return SYMBOL.parse(builder);
			}
		};

		if(ListParsingUtil.parseCommaDelimitedExpressions(builder, parsingMethod) == 0)
		{
			builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
		}

		statementMarker.done(RubyElementTypes.LIST_OF_EXPRESSIONS);
		return RubyElementTypes.LIST_OF_EXPRESSIONS;
	}


}
