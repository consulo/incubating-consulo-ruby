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


import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */

/*
arg		: lhs '=' arg
		| lhs '=' arg kRESCUE_MOD arg
		| var_lhs tOP_ASGN arg
		| arg tDOT2 arg
		| arg tDOT3 arg
		| arg '+' arg
		| arg '-' arg
		| arg '*' arg
		| arg '/' arg
		| arg '%' arg
		| arg tPOW arg
		| tUMINUS_NUM tINTEGER tPOW arg
		| tUMINUS_NUM tFLOAT tPOW arg
		| tUPLUS arg
		| tUMINUS arg
		| arg '|' arg
		| arg '^' arg
		| arg '&' arg
		| arg tCMP arg
		| arg '>' arg
		| arg tGEQ arg
		| arg '<' arg
		| arg tLEQ arg
		| arg tASSGN arg
		| arg tEQ arg
		| arg tNEQ arg
		| arg tMATCH arg
		| arg tNMATCH arg
		| '!' arg
		| '~' arg
		| arg tLSHFT arg
		| arg tRSHFT arg
		| arg tANDOP arg
		| arg tOROP arg
		| kDEFINED opt_nl  arg
		| arg '?' arg ':' arg
		| primary
		;
*/

public class ARG
{
	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		if(builder.isDEBUG())
		{
			builder.ARG();
		}

		if(!builder.compare(BNF.tARG_FIRST_TOKEN))
		{
			return RubyElementTypes.EMPTY_INPUT;
		}
		return DefinedExpression.parse(builder);
	}


	/**
	 * Parsing ARG with lead PRIMARY already parsed
	 *
	 * @param builder Current builder
	 * @param marker  Marker before PRIMARY
	 * @param result  result of PRIMARY
	 * @return result of ARG parsing
	 */
	public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		return DefinedExpression.parseWithLeadPRIMARY(builder, marker, result);
	}
}
