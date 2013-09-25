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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing;


import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands.COMMAND_CALL;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands.COMMAND_CALL_OR_ARG;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.BinaryExprParsing;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 27.06.2006
 */
public class EXPR implements RubyTokenTypes {

    public static final TokenSet INDICATOR_TOKENS = TokenSet.create(
                tEXCLAMATION,
                kNOT,
                kRETURN,
                kBREAK,
                kNEXT,
                kYIELD,
                kSUPER,
                kDEFINED
    );

/*
expr	: command_call
		| expr kAND expr
		| expr kOR expr
		| kNOT expr
		| '!' command_call
		| arg
		;
*/

    @NotNull
    public static IElementType parse(final RBuilder builder) {
        if (builder.isDEBUG()){
            builder.EXPR();
        }

        if (!builder.compare(BNF.tEXPR_FIRST_TOKENS)){
            return RubyElementTypes.EMPTY_INPUT;
        }
        return parseOr(builder);
    }


    /**
     * Parsing binary expression with operations:  or
     * @param builder Current builder
     * @return result of parsing
     */
    @NotNull
    private static IElementType parseOr(final RBuilder builder) {
        return parseOrWithLeadAnd(builder, builder.mark(), parseAnd(builder));
    }

    /**
     * Parsing or with lead and parsed
     * @param builder Current builder
     * @return result of parsing
     * @param marker Marker before And
     * @param result result of and parsing
     */
    @NotNull
    private static IElementType parseOrWithLeadAnd(final RBuilder builder, final RMarker marker, final IElementType result) {
        ParsingMethod parsingMethod = new ParsingMethod() {
            @Override
			public IElementType parse(final RBuilder builder) {
                return parseAnd(builder);
            }
        };

        return BinaryExprParsing.parseWithLeadOperand(builder,
                marker, result,
                parsingMethod,
                ErrorMsg.EXPRESSION_EXPECTED_MESSAGE,
                kOR,
                RubyElementTypes.BOOL_BINARY_EXPRESSION);
    }


    /**
     * Parsing binary expression with operations: and
     * @param builder Current builder
     * @return result of parsing
     */
    @NotNull
    private static IElementType parseAnd(final RBuilder builder) {
        return parseAndWithLeadNot(builder, builder.mark(), parseNot(builder));
    }

    /**
     * Parsing and with lead not parsed
     * @param builder Current builder
     * @return result of parsing
     * @param marker Marker before Not
     * @param result result of not parsing
     */
    @NotNull
    private static IElementType parseAndWithLeadNot(final RBuilder builder, final RMarker marker, final IElementType result) {
        ParsingMethod parsingMethod = new ParsingMethod() {
            @Override
			public IElementType parse(final RBuilder builder) {
                return parseNot(builder);
            }
        };

        return BinaryExprParsing.parseWithLeadOperand(builder,
                marker, result,
                parsingMethod,
                ErrorMsg.EXPRESSION_EXPECTED_MESSAGE,
                kAND,
                RubyElementTypes.BOOL_BINARY_EXPRESSION);
    }

    /**
     * Parsing not expr
     * @param builder Current builder
     * @return result of parsing
     */
    @NotNull
    private static IElementType parseNot(final RBuilder builder) {
        RMarker statementMarker = builder.mark();
        if (builder.compareAndEat(kNOT)) {
            if (parseNot(builder) == RubyElementTypes.EMPTY_INPUT) {
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }
            statementMarker.done(RubyElementTypes.BOOL_NEGATIVE_EXPRESSION);
            return RubyElementTypes.BOOL_NEGATIVE_EXPRESSION;
        }
        statementMarker.drop();
        return parseSingleExpr(builder);
    }

    /**
     * Parsing not expr
     * @param builder Current builder
     * @return result of parsing
     * @param marker Marker before SingleExpr
     * @param result result of singleExrp
     */
    @NotNull
    private static IElementType parseNotWithLeadSingleExpr(final RBuilder builder, final RMarker marker, final IElementType result) {
        if (result!=RubyElementTypes.EMPTY_INPUT){
            marker.drop();
            return result;
        }
        marker.rollbackTo();
        return RubyElementTypes.EMPTY_INPUT;
    }
    /*
    expr	: command_call
            | expr kAND expr
            | expr kOR expr
            | kNOT expr
            | '!' command_call
            | arg
            ;
    */
    @NotNull
    private static IElementType parseSingleExpr(final RBuilder builder) {
        if (builder.compare(tEXCLAMATION)) {
            RMarker statementMarker = builder.mark();
            builder.match(tEXCLAMATION);

            if (COMMAND_CALL.parse(builder) == RubyElementTypes.EMPTY_INPUT) {
                statementMarker.rollbackTo();
            } else {
                statementMarker.done(RubyElementTypes.BOOL_NEGATIVE_EXPRESSION);
                return RubyElementTypes.BOOL_NEGATIVE_EXPRESSION;
            }
        }

        return COMMAND_CALL_OR_ARG.parse(builder);
    }


    public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result) {
        return parseOrWithLeadAnd(builder, marker.precede(),
                parseAndWithLeadNot(builder, marker.precede(),
                        parseNotWithLeadSingleExpr(builder, marker.precede(),
                                parseSingleExprWithLeadPRIMARY(builder, marker, result))));

    }

    private static IElementType parseSingleExprWithLeadPRIMARY(RBuilder builder, RMarker marker, IElementType result) {
        return COMMAND_CALL_OR_ARG.parseWithLeadPRIMARY(builder, marker, result);
    }
}
