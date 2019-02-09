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


import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.COMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class While implements RubyTokenTypes
{

	/*
			| kWHILE  expr_value do
			  compstmt
			  kEND
	*/
	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();

		builder.match(kWHILE);

		Condition.parse(builder);
		DO.parse(builder);

		COMPSTMT.parse(builder, kEND);

		builder.matchIgnoreEOL(kEND);
		statementMarker.done(RubyElementTypes.WHILE_STATEMENT);
		return RubyElementTypes.WHILE_STATEMENT;
	}
}
