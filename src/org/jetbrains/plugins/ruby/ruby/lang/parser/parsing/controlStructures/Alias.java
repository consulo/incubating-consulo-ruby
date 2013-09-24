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
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.REFS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.SYMBOL;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method.FNAME;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.06.2006
 */
public class Alias implements RubyTokenTypes {
/*
    : kALIAS fitem  fitem
    | kALIAS tGVAR tGVAR
    | kALIAS tGVAR tBACK_REF
    | kALIAS tGVAR tNTH_REF
*/
@NotNull
    public static IElementType parse(final RBuilder builder){
        RMarker statementMarker = builder.mark();
        builder.match(kALIAS);

/*
    | kALIAS tGVAR tGVAR
    | kALIAS tGVAR tBACK_REF
    | kALIAS tGVAR tNTH_REF
*/
        if (builder.compare(tGVAR)){
            builder.parseSingleToken(tGVAR, RubyElementTypes.GLOBAL_VARIABLE);

            if (builder.compare(tGVAR)){
                builder.parseSingleToken(tGVAR, RubyElementTypes.GLOBAL_VARIABLE);
            } else
            if (builder.compare(BNF.tREFS)){
                REFS.parse(builder);
            } else {
                builder.error(ErrorMsg.expected(RBundle.message("parsing.alias.object")));
            }
            statementMarker.done(RubyElementTypes.ALIAS_STATEMENT);
            return RubyElementTypes.ALIAS_STATEMENT;
        }

// kALIAS fitem fitem
        IElementType result = parseFItem(builder);
        if (result!=RubyElementTypes.EMPTY_INPUT){
            if (parseFItem(builder)==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.expected(RBundle.message("parsing.alias.object")));
            }
            statementMarker.done(RubyElementTypes.ALIAS_STATEMENT);
            return RubyElementTypes.ALIAS_STATEMENT;
        }

        builder.error(ErrorMsg.expected(RBundle.message("parsing.alias.object")));
        statementMarker.done(RubyElementTypes.ALIAS_STATEMENT);
        return RubyElementTypes.ALIAS_STATEMENT;
    }

/*
    fitem	: fname
            | symbol
            ;
*/
    private static IElementType parseFItem(final RBuilder builder){
        IElementType result = SYMBOL.parse(builder);
        if (result!=RubyElementTypes.EMPTY_INPUT){
            return result;
        }

        result = FNAME.parse(builder);
        if (result!=RubyElementTypes.EMPTY_INPUT){
            return result;
        }
        return RubyElementTypes.EMPTY_INPUT;
    }

}
