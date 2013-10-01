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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.VARIABLE;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ListParsingUtil;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */

class F_ARGS implements RubyTokenTypes
{


/*
f_args	: f_arg ',' f_optarg ',' f_rest_arg opt_f_block_arg
		| f_arg ',' f_optarg opt_f_block_arg
		| f_arg ',' f_rest_arg opt_f_block_arg
		| f_arg opt_f_block_arg
		| f_optarg ',' f_rest_arg opt_f_block_arg
		| f_optarg opt_f_block_arg
		| f_rest_arg opt_f_block_arg
		| f_block_arg
		|  none
        ;

f_args : [f_arg] , [f_optarg] , [f_rest_arg] , [opt_f_block_arg]

f_arg	: var_name (, varname)*

f_optarg	: default_asgn (, default_asgn)*

f_rest_arg	: * [tIDENTIFIER]

f_block_arg	: AMPER tIDENTIFIER

opt_f_block_arg	: [',' f_block_arg]

f_norm_arg	: tCONSTANT
                | tIVAR
                | tGVAR
                | tCVAR
		| tIDENTIFIER
		;
*/

	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		ParsingMethod parsingMethod = new ParsingMethod()
		{
			boolean afterDefAsgn = false;
			boolean afterRestArgs = false;
			boolean afterBlockArg = false;

			@Override
			@NotNull
			public IElementType parse(final RBuilder builder)
			{

				if(afterBlockArg)
				{
					return RubyElementTypes.EMPTY_INPUT;
				}
				// f_restarg
				if(!afterRestArgs && builder.compare(tSTAR))
				{
					afterRestArgs = true;
					return parseRestArg(builder);
				}

				// f_blockarg
				if(!afterBlockArg && builder.compare(tAMPER))
				{
					afterBlockArg = true;
					return parseFBlockArg(builder);
				}
				// f_arg
				if(!afterBlockArg && !afterRestArgs && builder.compare(BNF.tF_NORMARGS))
				{
					RMarker marker = builder.mark();
					if(builder.compare(BNF.tF_NORMARGS))
					{
						VARIABLE.parse(builder);
					}
					// f_optarg
					if(!builder.compareAndEat(tASSGN))
					{
						if(afterDefAsgn)
						{
							builder.error(ErrorMsg.expected(tASSGN));
						}
						marker.done(RubyElementTypes.ARGUMENT);
						return RubyElementTypes.ARGUMENT;
					}
					if(ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT)
					{
						builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
					}
					afterDefAsgn = true;
					marker.done(RubyElementTypes.PREDEFINED_ARGUMENT);
					return RubyElementTypes.PREDEFINED_ARGUMENT;
				}

				return RubyElementTypes.EMPTY_INPUT;
			}
		};

		int count = ListParsingUtil.parseCommaDelimitedExpressions(builder, parsingMethod);
		return (count > 0) ? RubyElementTypes.LIST_OF_EXPRESSIONS : RubyElementTypes.EMPTY_INPUT;
	}

	private static IElementType parseF_NORMARG(RBuilder builder)
	{
		if(builder.compare(BNF.tF_NORMARGS))
		{
			RMarker marker = builder.mark();
			VARIABLE.parse(builder);
			marker.done(RubyElementTypes.ARGUMENT);
			return RubyElementTypes.ARGUMENT;
		}
		return RubyElementTypes.EMPTY_INPUT;
	}

	/**
	 * Parsing block argument, &arg
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	private static IElementType parseFBlockArg(final RBuilder builder)
	{
		RMarker blockMarker = builder.mark();
		builder.match(tAMPER);
		if(builder.compare(BNF.tF_NORMARGS))
		{
			VARIABLE.parse(builder);
		}
		blockMarker.done(RubyElementTypes.BLOCK_ARGUMENT);
		return RubyElementTypes.BLOCK_ARGUMENT;
	}

	/**
	 * Parsing array argument, *arg
	 *
	 * @param builder Current builder
	 * @return result of parsing
	 */
	private static IElementType parseRestArg(final RBuilder builder)
	{
		RMarker starMarker = builder.mark();
		builder.match(tSTAR);
		if(builder.compare(BNF.tF_NORMARGS))
		{
			VARIABLE.parse(builder);
		}
		starMarker.done(RubyElementTypes.ARRAY_ARGUMENT);
		return RubyElementTypes.ARRAY_ARGUMENT;
	}

}
