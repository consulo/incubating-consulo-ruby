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
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.*;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 28.04.2005
 */
class MathExpression implements RubyTokenTypes{
    private static final TokenSet TS_tPLUS_tMINUS = TokenSet.create(tPLUS, tMINUS);
    private static final TokenSet TS_tSTAR_tDIV_tPERC = TokenSet.create(tMULT, tDIV, tPERC);

    @NotNull
    public static IElementType parse(final RBuilder builder) {
        return parseSum(builder);
    }

    /**
      * Parsing binary expression with operations:  + , -
      * @param builder Current builder
      * @return result of parsing
      */
    @NotNull
    private static IElementType parseSum(final RBuilder builder) {
        return parseSumWithLeadMult(builder, builder.mark(), parseMult(builder));
    }

    /**
      * Parsing sum with lead mult parsed
      * @param builder Current builder
      * @return result of parsing
     * @param marker Marker before Mult
     * @param result result of Mult parsed
      */
    @NotNull
    private static IElementType parseSumWithLeadMult(final RBuilder builder, final RMarker marker, final IElementType result) {
        ParsingMethod parsingMethod = new ParsingMethodWithAssignmentLookup(){
            @Override
			@NotNull
            public IElementType parseInner(final RBuilder builder) {
                return parseMult(builder);
            }
        };

        return BinaryExprParsing.parseWithLeadOperand(builder,
                marker, result,
                parsingMethod,
                ErrorMsg.EXPRESSION_EXPECTED_MESSAGE,
                TS_tPLUS_tMINUS,
                RubyElementTypes.MATH_BINARY_EXPRESSION);

    }

    /**
      * Parsing binary expression with operations:  * , /, %
      * @param builder Current builder
      * @return result of parsing
      */
    @NotNull
    private static IElementType parseMult(final RBuilder builder) {
        return parseMultWithLeadPow(builder, builder.mark(), parsePower(builder));
    }

    /**
      * Parsing mult with lead power
      * @param builder Current builder
      * @return result of parsing
     * @param marker Marker before power
     * @param result result of power parsing
      */
    @NotNull
    private static IElementType parseMultWithLeadPow(final RBuilder builder, final RMarker marker, final IElementType result) {
        ParsingMethod parsingMethod = new ParsingMethod(){
            @Override
			public IElementType parse(final RBuilder builder){
                return parsePower(builder);
            }
        };

        return BinaryExprParsing.parseWithLeadOperand(builder,
                marker, result,
                parsingMethod,
                ErrorMsg.EXPRESSION_EXPECTED_MESSAGE,
                TS_tSTAR_tDIV_tPERC,
                RubyElementTypes.MATH_BINARY_EXPRESSION);

    }


    /**
      * Parsing binary expression with operations:  **
      * @param builder Current builder
      * @return result of parsing
      */
    @NotNull
    private static IElementType parsePower(final RBuilder builder) {
        return parsePowerWithLeadMathTerm(builder, builder.mark(), MathTerm.parse(builder));
    }

    /**
      * Parsing power with lead math term
      * @param builder Current builder
      * @return result of parsing
     * @param marker Marker before math term
     * @param result result of math term parsing
      */
    @NotNull
    private static IElementType parsePowerWithLeadMathTerm(final RBuilder builder, final RMarker marker, final IElementType result) {
        ParsingMethod parsingMethod = new ParsingMethod(){
            @Override
			public IElementType parse(final RBuilder builder){
                return MathTerm.parse(builder);
            }
        };

        return BinaryExprParsing.parseWithLeadOperand(builder,
                marker, result,
                parsingMethod,
                ErrorMsg.EXPRESSION_EXPECTED_MESSAGE,
                tPOW,
                RubyElementTypes.MATH_BINARY_EXPRESSION);

    }

    public static IElementType parseWithLeadPRIMARY(RBuilder builder, RMarker marker, IElementType result) {
        return parseSumWithLeadMult(builder, marker.precede(),
                parseMultWithLeadPow(builder, marker.precede(),
                        parsePowerWithLeadMathTerm(builder, marker.precede(),
                                MathTerm.parseWithLeadPRIMARY(builder, marker, result))));
    }
}