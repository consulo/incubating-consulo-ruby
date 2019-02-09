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
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 02.07.2006
 */
public class Assignment implements RubyTokenTypes
{
/*
arg		: lhs '=' arg
		| lhs '=' arg kRESCUE_MOD arg
		| var_lhs tOP_ASGN arg
		| primary_value '[' aref_args ']' tOP_ASGN arg
		| primary_value '.' tIDENTIFIER tOP_ASGN arg
		| primary_value '.' tCONSTANT tOP_ASGN arg
		| primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg
		| primary_value tCOLON2 tCONSTANT tOP_ASGN arg
		| tCOLON3 tCONSTANT tOP_ASGN arg
		| backref tOP_ASGN arg
*/

	@Nonnull
	public static IElementType parse(RBuilder builder)
	{
		return parseWithLeadRANGE(builder, builder.mark(), RangeExpression.parse(builder));
	}

	public static IElementType parseWithLeadRANGE(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		if(result == RubyElementTypes.EMPTY_INPUT)
		{
			marker.drop();
			return RubyElementTypes.EMPTY_INPUT;
		}

		// lhs '=' arg
		if(BNF.LHS.contains(result) && builder.compareAndEat(tASSGN))
		{
			if(ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT)
			{
				builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
			}
			marker.done(RubyElementTypes.ASSIGNMENT_EXPRESSION);
			return RubyElementTypes.ASSIGNMENT_EXPRESSION;
		}

		//        | var_lhs tOP_ASGN arg
		//        | primary_value '[' aref_args ']' tOP_ASGN arg
		//        | primary_value '.' tIDENTIFIER tOP_ASGN arg
		//        | primary_value '.' tCONSTANT tOP_ASGN arg
		//        | primary_value tCOLON2 tIDENTIFIER tOP_ASGN arg
		//        | primary_value tCOLON2 tCONSTANT tOP_ASGN arg
		//        | tCOLON3 tCONSTANT tOP_ASGN arg
		//        | backref tOP_ASGN arg
		if(BNF.LHS.contains(result) && builder.compareAndEat(BNF.tOP_ASGNS))
		{
			if(ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT)
			{
				builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
			}
			marker.done(RubyElementTypes.SELF_ASSIGNMENT_EXPRESSION);
			return RubyElementTypes.SELF_ASSIGNMENT_EXPRESSION;
		}

		// otherwise
		return TernaryExpression.parseWithLeadPRIMARY(builder, marker, result);
	}
}
