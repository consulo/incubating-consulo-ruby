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


import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.TokenBNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */
class DO implements RubyTokenTypes
{
	private static final TokenSet TS_DO = TokenSet.orSet(TokenSet.orSet(TokenSet.create(kDO_COND), BNF.tCOLONS), BNF.tTERM_TOKENS);

	/*
	do		: term
			| ':'
			| kDO_COND
			;
	*/
	public static void parse(final RBuilder builder)
	{
		// It`s a hack for RHTML
		if(builder.compare(TokenBNF.tOUTER_ELEMENTS))
		{
			return;
		}
		if(builder.compareAndEat(TS_DO))
		{
			builder.passEOLs();
			return;
		}

		builder.error(ErrorMsg.expected(TS_DO, builder));
	}
}
