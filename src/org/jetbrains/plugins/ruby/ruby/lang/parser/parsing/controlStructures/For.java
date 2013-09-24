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


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.COMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.EXPR;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.iterators.BLOCK_VAR;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class For implements RubyTokenTypes {
    /*
            | kFOR block_var kIN  expr_value do
              compstmt
              kEND
    */
    @NotNull
    public static IElementType parse(final RBuilder builder) {
        RMarker statementMarker = builder.mark();

        builder.match(kFOR);

        BLOCK_VAR.parse(builder);

        builder.match(kIN);


        if (EXPR.parse(builder) == RubyElementTypes.EMPTY_INPUT) {
            builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
        }

        DO.parse(builder);

        COMPSTMT.parse(builder, kEND);

        builder.matchIgnoreEOL(kEND);
        statementMarker.done(RubyElementTypes.FOR_STATEMENT);
        return RubyElementTypes.FOR_STATEMENT;
    }

}