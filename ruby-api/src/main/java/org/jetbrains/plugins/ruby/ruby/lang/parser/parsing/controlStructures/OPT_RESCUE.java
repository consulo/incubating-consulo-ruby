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
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.COMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assignments.LHS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assignments.MRHS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.07.2006
 */
public class OPT_RESCUE implements RubyTokenTypes
{

/*
	exc_list	: arg_value
            | mrhs
            | none
            ;
*/

	@NotNull
	private static IElementType parseEXC_LIST(final RBuilder builder)
	{
		IElementType result = MRHS.parse(builder);
		if(result != RubyElementTypes.EMPTY_INPUT)
		{
			return result;
		}
		result = ARG.parse(builder);
		if(result != RubyElementTypes.EMPTY_INPUT)
		{
			return result;
		}

		return RubyElementTypes.EMPTY_INPUT;
	}

	/*
		exc_var		: tASSOC lhs
				| none
				;
	*/
	private static void parseEXC_VAR(final RBuilder builder)
	{
		if(builder.compareAndEat(tASSOC))
		{
			final RMarker marker = builder.mark();
			if(LHS.parse(builder) == RubyElementTypes.EMPTY_INPUT)
			{
				builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
			}
			marker.done(RubyElementTypes.BLOCK_VARIABLES);
		}
	}

/*
    opt_rescue	: kRESCUE exc_list exc_var then
              compstmt
              opt_rescue
            | none
            ;
*/

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();
		if(!builder.compareAndEat(kRESCUE))
		{
			statementMarker.drop();
			return RubyElementTypes.EMPTY_INPUT;
		}

		parseEXC_LIST(builder);

		parseEXC_VAR(builder);

		THEN.parse(builder);

		COMPSTMT.parse(builder, kRESCUE, kELSE, kENSURE, kEND);

		parse(builder);

		statementMarker.done(RubyElementTypes.RESCUE_BLOCK);
		return RubyElementTypes.RESCUE_BLOCK;
	}

}
