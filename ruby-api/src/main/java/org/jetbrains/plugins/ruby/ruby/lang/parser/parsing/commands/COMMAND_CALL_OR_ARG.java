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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 31.07.2006
 */
public class COMMAND_CALL_OR_ARG implements RubyTokenTypes
{
	private static final TokenSet TS_COMMAND_TOKENS = TokenSet.create(kRETURN, kBREAK, kNEXT, kYIELD);

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{

/*
command_call	: command
                | block_command
                | kRETURN call_args
                | kBREAK call_args
                | kNEXT call_args
                ;
*/
		IElementType result;
		if(builder.compare(TS_COMMAND_TOKENS))
		{
			result = COMMAND_CALL.parse(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				return result;
			}
		}

		return parseWithLeadARG(builder, builder.mark(), ARG.parse(builder));
	}

	private static IElementType parseWithLeadARG(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		if(!BNF.COMMAND_OBJECTS.contains(result) || !builder.compare(BNF.tCALL_ARG_FIRST_TOKEN))
		{
			marker.drop();
			return result;
		}
		// else ARG can be the first element in command
		return COMMAND_CALL.parseWithLeadARG(builder, marker, result);
	}

	public static IElementType parseWithLeadPRIMARY(RBuilder builder, RMarker marker, IElementType result)
	{
		return parseWithLeadARG(builder, marker.precede(), ARG.parseWithLeadPRIMARY(builder, marker, result));
	}
}
