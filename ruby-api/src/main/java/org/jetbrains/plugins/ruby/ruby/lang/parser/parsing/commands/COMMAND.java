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
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.controlStructures.Super;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.controlStructures.Yield;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 06.07.2006
 */
public class COMMAND implements RubyTokenTypes
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

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		if(!builder.compare(BNF.tCOMMAND_FIRST_TOKENS))
		{
			return RubyElementTypes.EMPTY_INPUT;
		}

		if(builder.compare(kYIELD))
		{
			IElementType result = Yield.parseWithCommandArgs(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				return result;
			}
		}

		if(builder.compare(kSUPER))
		{
			IElementType result = Super.parseWithCommandArgs(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				return result;
			}
		}

		return parseWithLeadARG(builder, builder.mark(), ARG.parse(builder));
	}

	/**
	 * Parses command with ARG as it`s first element (command_object)
	 *
	 * @param builder Current builder
	 * @param marker  Marker before lead ARG
	 * @param result  ARG parsing result
	 * @return command parsing result
	 */
	@NotNull
	public static IElementType parseWithLeadARG(final RBuilder builder, RMarker marker, final IElementType result)
	{
		if(BNF.COMMAND_OBJECTS.contains(result))
		{
			if(CALL_ARGS.parse(builder) != RubyElementTypes.EMPTY_INPUT)
			{
				marker.done(RubyElementTypes.COMMAND_CALL);

				// Checking for optional cmd_brace_block
				if(builder.compare(tLBRACE))
				{
					marker = marker.precede();
					BRACE_BLOCK.parse(builder);
					marker.done(RubyElementTypes.BLOCK_CALL);
					return RubyElementTypes.BLOCK_CALL;
				}

				return RubyElementTypes.COMMAND_CALL;
			}
		}

		marker.rollbackTo();
		return RubyElementTypes.EMPTY_INPUT;
	}
}

