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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assignments;


import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 02.07.2006
 */
class MLHS_ITEM implements RubyTokenTypes
{
	/*
	mlhs_item    : mlhs_node
			| tLPAREN mlhs_entry ')'
			;
	*/
	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		if(builder.compare(tLPAREN))
		{
			RMarker marker = builder.mark();
			builder.match(tLPAREN);
			IElementType result = parseMLHS_ENTRY(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				builder.match(tRPAREN);
				marker.done(RubyElementTypes.EXPRESSION_IN_PARENS);
				return RubyElementTypes.EXPRESSION_IN_PARENS;
			}
			marker.rollbackTo();
		}

		// mlhs_node == lhs
		return LHS.parse(builder);
	}

/*
		 mlhs_entry    : mlhs_basic
                 | tLPAREN mlhs_entry ')'
                 ;
*/

	private static IElementType parseMLHS_ENTRY(final RBuilder builder)
	{
		if(builder.compare(tLPAREN))
		{
			RMarker marker = builder.mark();
			builder.match(tLPAREN);
			IElementType result = parseMLHS_ENTRY(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				builder.match(tRPAREN);
				marker.done(RubyElementTypes.EXPRESSION_IN_PARENS);
				return RubyElementTypes.EXPRESSION_IN_PARENS;
			}
			marker.rollbackTo();
		}
		return MLHS.parseMLHS_BASIC(builder);
	}
}
