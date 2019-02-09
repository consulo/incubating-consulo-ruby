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


import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.TERM;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */
class F_ARGLIST implements RubyTokenTypes
{
/*
f_arglist   : '(' f_args opt_nl ')'
		    | f_args term
		    ;
*/

	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{

		if(builder.compareAndEat(BNF.tLPARENS))
		{
			RMarker funcArgsMarker = builder.mark();
			F_ARGS.parse(builder);
			funcArgsMarker.done(RubyElementTypes.FUNCTION_ARGUMENT_LIST);
			builder.passEOLs();
			builder.match(tRPAREN);
			return RubyElementTypes.FUNCTION_ARGUMENT_LIST;
		}

		RMarker commandArgsMarker = builder.mark();
		F_ARGS.parse(builder);
		commandArgsMarker.done(RubyElementTypes.COMMAND_ARGUMENT_LIST);
		TERM.parse(builder);
		return RubyElementTypes.COMMAND_ARGUMENT_LIST;
	}

}
