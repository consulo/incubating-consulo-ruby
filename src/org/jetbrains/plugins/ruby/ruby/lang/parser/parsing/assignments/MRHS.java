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


import com.intellij.openapi.util.Ref;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands.CALL_ARGS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ListParsingUtil;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 02.07.2006
 */
public class MRHS implements RubyTokenTypes {
    /*
    mrhs		: args ',' arg_value
            | args ',' tSTAR arg_value
            | tSTAR arg_value
            ;
    args = arg (, arg)*
    */
    public static IElementType parse(final RBuilder builder) {
        final Ref<Boolean> starSeen = new Ref<Boolean>(false);
        ParsingMethod parsignMethod = new ParsingMethod() {
            @NotNull
            public IElementType parse(final RBuilder builder) {

                if (starSeen.get()) {
                    return RubyElementTypes.EMPTY_INPUT;
                }

                if (builder.compare(tSTAR)) {
                    starSeen.set(true);
                    return CALL_ARGS.parseArrayToArguments(builder);
                }

                return ARG.parse(builder);
            }
        };

        RMarker statementMarker = builder.mark();
        int count = ListParsingUtil.parseCommaDelimitedExpressions(builder, parsignMethod);

        if (count >= 2 || starSeen.get()) {
            statementMarker.done(RubyElementTypes.LIST_OF_EXPRESSIONS);
            return RubyElementTypes.MRHS;
        } else {
            statementMarker.rollbackTo();
            return RubyElementTypes.EMPTY_INPUT;
        }
    }
}
