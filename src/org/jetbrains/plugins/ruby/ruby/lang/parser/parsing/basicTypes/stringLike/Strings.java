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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.stringLike;


import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 07.06.2006
 */
public class Strings implements RubyTokenTypes{
    private static final TokenSet TS_STRING_BEGS =
            TokenSet.create(tDOUBLE_QUOTED_STRING_BEG, tSINGLE_QUOTED_STRING_BEG);
    private static final TokenSet tSTRING_ENDS = TokenSet.create(tSTRING_END);

    /**
     * String block parsing , i.e.  "string1" "string2"
     * @param builder Current builder
     * @return Result of parsing
     */

    @NotNull
    public static IElementType parse(final RBuilder builder) {

        RMarker statementMarker = builder.mark();
        IElementType result = parseSingleString(builder);
        while (builder.compare(TS_STRING_BEGS)) {
            parseSingleString(builder);
            result = RubyElementTypes.STRINGS;
        }

        if (result == RubyElementTypes.STRINGS) {
            statementMarker.done(RubyElementTypes.STRINGS);
            return RubyElementTypes.STRINGS;
        }

        statementMarker.drop();
        return result;
    }


    /**
     * Parsing single string literal
     * @param builder Current builder
     * @return Result of parsing
     */

    @NotNull
    private static IElementType parseSingleString(final RBuilder builder) {
        return StringParsingUtil.parse(builder, tSTRING_ENDS, BNF.tSTRING_TOKENS);
    }
}
