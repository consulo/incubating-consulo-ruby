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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg;


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.primary.PRIMARY;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.06.2006
 */
class MathTerm implements RubyTokenTypes {


    /**
      * Parsing math term, i.e. -mt, +mt, !mt, ~mt or primary
      * @param builder Current builder
      * @return result of parsing
      */
    @NotNull
    public static IElementType parse(final RBuilder builder) {
// plus
        if (builder.compare(tUPLUS)) {
            RMarker statementMarker= builder.mark();
            builder.match(tUPLUS);

            if(parse(builder)==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }

            statementMarker.done(RubyElementTypes.UNARY_EXPRESSION);
            return RubyElementTypes.UNARY_EXPRESSION;
        }

// minus
        if (builder.compare(tUMINUS)) {
            RMarker statementMarker = builder.mark();
            builder.match(tUMINUS);

            if(parse(builder)==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }

            statementMarker.done(RubyElementTypes.NEGATIVE_EXPRESSION);
            return RubyElementTypes.NEGATIVE_EXPRESSION;
        }

// exclamation
        if (builder.compare(tEXCLAMATION)) {
            RMarker statementMarker = builder.mark();
            builder.match(tEXCLAMATION);

            if(parse(builder)==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }

            statementMarker.done(RubyElementTypes.BOOL_NEGATIVE_EXPRESSION);
            return RubyElementTypes.BOOL_NEGATIVE_EXPRESSION;
        }

// tilde
        if (builder.compare(tTILDE)) {
            RMarker statementMarker = builder.mark();
            builder.match(tTILDE);

            if(parse(builder)==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }

            statementMarker.done(RubyElementTypes.UNARY_EXPRESSION);
            return RubyElementTypes.UNARY_EXPRESSION;
        }

        return PRIMARY.parse(builder);
    }


    public static IElementType parseWithLeadPRIMARY(RBuilder builder, RMarker marker, IElementType result) {
        if (result!=RubyElementTypes.EMPTY_INPUT){
            marker.drop();
            return result;
        } else {
            marker.rollbackTo();
            return RubyElementTypes.EMPTY_INPUT;
        }
    }
}
