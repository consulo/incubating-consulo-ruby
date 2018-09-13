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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions;


import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method.BODYSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class Module implements RubyTokenTypes
{
/*
		| kMODULE cpath
          bodystmt
          kEND

*/


	@NotNull
	public static IElementType parse(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();

		builder.match(kMODULE);

		RMarker cpathMarker = builder.mark();
		if(CPATH.parse(builder) == RubyElementTypes.EMPTY_INPUT)
		{
			builder.error(ErrorMsg.expected(RBundle.message("parsing.module.name")));
		}
		cpathMarker.done(RubyElementTypes.MODULE_NAME);


		BODYSTMT.parse(builder);

		builder.matchIgnoreEOL(kEND);
		statementMarker.done(RubyElementTypes.MODULE);
		return RubyElementTypes.MODULE;
	}

}
