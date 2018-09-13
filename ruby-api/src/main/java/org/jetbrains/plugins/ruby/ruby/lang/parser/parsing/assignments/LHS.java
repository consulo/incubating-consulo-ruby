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


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.primary.PRIMARY;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 29.06.2006
 */
public class LHS
{
	/*
	lhs		: variable
			| primary_value '[' aref_args ']'
			| primary_value '.' tIDENTIFIER
			| primary_value tCOLON2 tIDENTIFIER
			| primary_value '.' tCONSTANT
			| primary_value tCOLON2 tCONSTANT
			| tCOLON3 tCONSTANT
			| backref
			;
	*/
	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		if(!builder.compare(BNF.tLHS_FIRST_TOKEN))
		{
			return RubyElementTypes.EMPTY_INPUT;
		}

		return parseWithLeadPRIMARY(builder, builder.mark(), PRIMARY.parse(builder));
	}

	public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		if(BNF.LHS.contains(result))
		{
			marker.drop();
			return result;
		}
		marker.rollbackTo();
		return RubyElementTypes.EMPTY_INPUT;
	}
}
