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
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.07.2006
 */
class TernaryExpression implements RubyTokenTypes {
    /**
     * Parsing ternary with lead RANGE already parsed
     * @param builder Current builder
     * @param marker Marker before RANGE
     * @param result result of RANGE
     * @return result of parsing
     */
    private static IElementType parseWithLeadRANGE(final RBuilder builder, RMarker marker, final IElementType result){
// ternary expression parsing
        if (!builder.compare(tQUESTION)) {
            marker.drop();
            return result;
        }
        marker.done(RubyElementTypes.CONDITION);
        marker = marker.precede();

        builder.match(tQUESTION);
        if (ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT) {
            builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
        }
        builder.match(BNF.tCOLONS, ErrorMsg.expected(":"));
        if (ARG.parse(builder) == RubyElementTypes.EMPTY_INPUT) {
            builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
        }
        marker.done(RubyElementTypes.TERNARY_EXPRESSION);
        return RubyElementTypes.TERNARY_EXPRESSION;
    }

    /**
     * Parsing Ternary with lead PRIMARY already parsed
     * @param builder Current builder
     * @param marker Marker before PRIMARY
     * @param result result of PRIMARY
     * @return result of parsing
     */
    public static IElementType parseWithLeadPRIMARY(RBuilder builder, RMarker marker, IElementType result) {
        return parseWithLeadRANGE(builder, marker.precede(), RangeExpression.parseWithLeadPRIMARY(builder, marker, result));
    }
}
