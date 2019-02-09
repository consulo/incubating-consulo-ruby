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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assocs;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 9, 2006
 */
public class ASSOC_OR_ARG implements RubyTokenTypes
{
	@Nonnull
	public static IElementType parseWithLeadArg(final RBuilder builder, final RMarker marker, final IElementType result)
	{
		if(result == RubyElementTypes.EMPTY_INPUT)
		{
			marker.rollbackTo();
			return RubyElementTypes.EMPTY_INPUT;
		}

		if(builder.compareAndEat(tASSOC))
		{
			if(ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT)
			{
				builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
			}
			marker.done(RubyElementTypes.ASSOC);
			return RubyElementTypes.ASSOC;
		}

		marker.drop();
		return result;
	}

	public static IElementType parse(final RBuilder builder)
	{
		return parseWithLeadArg(builder, builder.mark(), ARG.parse(builder));
	}
}
