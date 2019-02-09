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


import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 15.06.2006
 */
public class Heredoc implements RubyTokenTypes
{
	private static final TokenSet ENDS = TokenSet.orSet(BNF.tHEREDOC_ENDS, TokenSet.create(tWHITE_SPACE, tEOL));

	@Nonnull
	public static IElementType parse(final RBuilder builder)
	{
		if(builder.compare(tHEREDOC_ID))
		{
			return builder.parseSingleToken(tHEREDOC_ID, RubyElementTypes.HEREDOC_ID);
		}

		final IElementType type = StringParsingUtil.parse(builder, ENDS, BNF.tHEREDOC_TOKENS, false);
		builder.compareAndEat(tEOL);
		builder.compareAndEat(tWHITE_SPACE);
		builder.match(BNF.tHEREDOC_ENDS);
		return type;
	}

}
