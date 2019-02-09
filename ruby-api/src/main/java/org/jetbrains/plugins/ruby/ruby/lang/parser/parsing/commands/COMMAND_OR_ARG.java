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

import javax.annotation.Nonnull;

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
 *
 * @author: oleg
 * @date: Oct 16, 2006
 */
public class COMMAND_OR_ARG implements RubyTokenTypes
{

	private static final TokenSet TS_COMMAND_TOKENS = TokenSet.create(kYIELD);

	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
/*
	command	: operation command_args
            | operation command_args cmd_brace_block
            | primary_value '.' operation2 command_args
            | primary_value '.' operation2 command_args cmd_brace_block
            | primary_value tCOLON2 operation2 command_args
            | primary_value tCOLON2 operation2 command_args cmd_brace_block
            | kSUPER command_args
            | kYIELD command_args
            ;
*/
		IElementType result;
		if(builder.compare(TS_COMMAND_TOKENS))
		{
			result = COMMAND.parse(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				return result;
			}
		}

		RMarker marker = builder.mark();
		result = ARG.parse(builder);
		if(!BNF.COMMAND_OBJECTS.contains(result) || !builder.compare(BNF.tCALL_ARG_FIRST_TOKEN))
		{
			marker.drop();
			return result;
		}
		// else ARG can be the first element in command
		return COMMAND.parseWithLeadARG(builder, marker, result);
	}
}
