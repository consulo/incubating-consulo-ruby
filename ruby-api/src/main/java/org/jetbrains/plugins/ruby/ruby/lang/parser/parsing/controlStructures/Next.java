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
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands.CALL_ARGS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 10.07.2006
 */
public class Next implements RubyTokenTypes
{

	@Nonnull
	public static IElementType parseWithCallArgs(final RBuilder builder)
	{
		RMarker statementMarker = builder.mark();
		builder.match(kNEXT);
		final boolean braceSeen = builder.compareAndEat(tfLPAREN);
		if(CALL_ARGS.parse(builder) == RubyElementTypes.EMPTY_INPUT)
		{
			statementMarker.rollbackTo();
			return RubyElementTypes.EMPTY_INPUT;
		}
		if(braceSeen)
		{
			builder.match(tRPAREN);
		}
		statementMarker.done(RubyElementTypes.NEXT_STATEMENT);
		return RubyElementTypes.NEXT_STATEMENT;

	}
}
