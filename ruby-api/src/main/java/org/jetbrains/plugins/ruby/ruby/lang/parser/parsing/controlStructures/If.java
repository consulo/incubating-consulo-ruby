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
 * Date: 09.06.2006
 */
public class If implements RubyTokenTypes
{

/*
		| if EXPR THEN
		  COMPSTMT
		  (elsif EXPR THEN COMPSTMT)*
		  [else COMPSTMT]
		  end
*/

	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();

		builder.match(kIF);

		Condition.parse(builder);

		THEN.parse(builder);

		COMPSTMT.parse(builder, kELSE, kELSIF, kEND);


		// elsif
		while(builder.compareIgnoreEOL(kELSIF))
		{
			RMarker elsifMarker = builder.mark();
			builder.match(kELSIF);

			Condition.parse(builder);

			THEN.parse(builder);

			COMPSTMT.parse(builder, kELSE, kELSIF, kEND);
			elsifMarker.done(RubyElementTypes.ELSIF_BLOCK);
		}

		// else
		builder.passEOLs();
		OPT_ELSE.parse(builder);

		builder.matchIgnoreEOL(kEND);
		statementMarker.done(RubyElementTypes.IF_STATEMENT);
		return RubyElementTypes.IF_STATEMENT;

	}

}
