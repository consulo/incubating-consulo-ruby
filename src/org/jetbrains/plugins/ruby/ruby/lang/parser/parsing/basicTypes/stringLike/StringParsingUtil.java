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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.stringLike;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.STMT;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.REFS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 10.08.2006
 */
class StringParsingUtil implements RubyTokenTypes
{
	/**
	 * All the string like expressions have the same structure. So this is a parsing util,
	 * used in String like objects parsing, i.e. String, Regexp, Words, even Heredoc
	 *
	 * @param builder      Current builder
	 * @param endType      DuckType of end token
	 * @param stringTokens all the string tokens
	 * @return Result of parsing String like object
	 */
	public static IElementType parse(final RBuilder builder, final TokenSet endType, final TokenSet stringTokens)
	{
		return parse(builder, endType, stringTokens, true);
	}

	/**
	 * All the string like expressions have the same structure. So this is a parsing util,
	 * used in String like objects parsing, i.e. String, Regexp, Words, even Heredoc
	 *
	 * @param builder         Current builder
	 * @param endType         DuckType of end token
	 * @param stringTokens    all the string tokens
	 * @param includeEndToken true end token is included
	 * @return Result of parsing String like object
	 */
	public static IElementType parse(final RBuilder builder, final TokenSet endType, final TokenSet stringTokens, final boolean includeEndToken)
	{
		RMarker stringMarker = builder.mark();
		final IElementType stringType = getType(builder.getTokenType());

		while(!builder.compare(endType) && builder.compare(stringTokens))
		{

			if(builder.compare(tSTRING_DBEG))
			{
				RMarker exprMarker = builder.mark();
				builder.match(tSTRING_DBEG);

				STMT.parse(builder);

				builder.matchIgnoreEOL(tSTRING_DEND);
				exprMarker.done(RubyElementTypes.EXPR_SUBTITUTION);
			}
			else

/*
			string_dvar	: tGVAR
                    | tIVAR
                    | tCVAR
                    | backref
                    ;
*/

				if(builder.compare(tSTRING_DVAR))
				{
					RMarker exprInStringMarker = builder.mark();
					builder.match(tSTRING_DVAR);
					if(builder.compare(BNF.tSTRING_DVAR))
					{
						if(builder.compare(BNF.tREFS))
						{
							REFS.parse(builder);
						}
						if(builder.compare(tGVAR))
						{
							builder.parseSingleToken(tGVAR, RubyElementTypes.GLOBAL_VARIABLE);
						}
						if(builder.compare(tIVAR))
						{
							builder.parseSingleToken(tIVAR, RubyElementTypes.INSTANCE_VARIABLE);
						}
						if(builder.compare(tCVAR))
						{
							builder.parseSingleToken(tCVAR, RubyElementTypes.CLASS_VARIABLE);
						}
					}
					else
					{
						builder.error(ErrorMsg.expected(BNF.tSTRING_DVAR, builder));
					}
					exprInStringMarker.done(RubyElementTypes.EXPR_SUBTITUTION);
				}
				else

				{
					builder.compareAndEat(stringTokens);
				}
		}
		if(includeEndToken)
		{
			builder.match(endType);
		}
		stringMarker.done(stringType);
		return stringType;
	}

	/**
	 * @param type DuckType of the beginning token
	 * @return Returns type of a string like object by it`s beginning
	 */
	@NotNull
	private static IElementType getType(final IElementType type)
	{
		if(type == tDOUBLE_QUOTED_STRING_BEG)
		{
			return RubyElementTypes.STRING;
		}
		if(type == tSINGLE_QUOTED_STRING_BEG)
		{
			return RubyElementTypes.NI_STRING;
		}
		if(type == tXSTRING_BEG)
		{
			return RubyElementTypes.X_STRING;
		}
		if(type == tREGEXP_BEG)
		{
			return RubyElementTypes.REGEXP;
		}
		if(type == tWORDS_BEG)
		{
			return RubyElementTypes.NI_WORDS;
		}
		if(type == tQWORDS_BEG)
		{
			return RubyElementTypes.WORDS;
		}
		if(BNF.tHEREDOC_VALUE_BEGINNINGS.contains(type))
		{
			return RubyElementTypes.HEREDOC_VALUE;
		}

		throw new IllegalArgumentException(type + " cannot be the string beginning");
	}
}
