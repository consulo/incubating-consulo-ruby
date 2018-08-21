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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.stringLike.Strings;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method.FNAME;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 24.07.2006
 */
public class SYMBOL implements RubyTokenTypes
{

	/*
			symbol		: tSYMBEG sym
					;

			sym		: fname
					| tIVAR
					| tGVAR
					| tCVAR
					| string
					;
	*/
	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		if(builder.compare(tSYMBEG))
		{
			RMarker statementMarker = builder.mark();
			builder.match(tSYMBEG);

			if(builder.compare(BNF.tSTRINGS_BEGINNINGS))
			{
				Strings.parse(builder);
				statementMarker.done(RubyElementTypes.SYMBOL);
				return RubyElementTypes.SYMBOL;
			}

			if(builder.compare(BNF.tVARS))
			{
				VARIABLE.parse(builder);
				statementMarker.done(RubyElementTypes.SYMBOL);
				return RubyElementTypes.SYMBOL;
			}
			IElementType result = FNAME.parse(builder);
			if(result != RubyElementTypes.EMPTY_INPUT)
			{
				statementMarker.done(RubyElementTypes.SYMBOL);
				return RubyElementTypes.SYMBOL;
			}

			builder.error(ErrorMsg.expected(RBundle.message("parsing.symbol.content")));
			statementMarker.done(RubyElementTypes.SYMBOL);
			return RubyElementTypes.SYMBOL;
		}
		return RubyElementTypes.EMPTY_INPUT;
	}

}
