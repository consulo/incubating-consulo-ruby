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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method;


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.EXPR;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.basicTypes.VARIABLE;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class Method implements RubyTokenTypes {

/*
        | kDEF fname
          f_arglist
          bodystmt
          kEND
        | kDEF singleton dot_or_colon  fname
          f_arglist
          bodystmt
          kEND
*/

    @NotNull
    public static IElementType parse(final RBuilder builder){
        RMarker statementMarker = builder.mark();

        builder.match(kDEF);

        IElementType definitionType = parseMethodNameAndGetType(builder);

        F_ARGLIST.parse(builder);

        BODYSTMT.parse(builder);

        builder.matchIgnoreEOL(kEND);

        statementMarker.done(definitionType);
        return definitionType;
    }


    //  [singleton dot_or_colon] fname
    @NotNull
    private static IElementType parseMethodNameAndGetType(final RBuilder builder) {
        boolean singletonSeen = false;

        RMarker methodNameMarker = builder.mark();

        IElementType result = parseSingleton(builder);
        if (builder.compare(BNF.tDOT_OR_COLON)){
            if (result==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }
            builder.match(BNF.tDOT_OR_COLON);
            singletonSeen = true;
        } else {
            methodNameMarker.rollbackTo();
            methodNameMarker = builder.mark();
        }

        if (FNAME.parse(builder)==RubyElementTypes.EMPTY_INPUT){
            builder.error(ErrorMsg.expected(RBundle.message("parsing.method.name")));
        }
        methodNameMarker.done(RubyElementTypes.METHOD_NAME);
        return (singletonSeen)? RubyElementTypes.SINGLETON_METHOD :
                    RubyElementTypes.METHOD;
    }

/*
    singleton	: var_ref
            | '('  expr opt_nl ')'
            ;
    var_ref : variable;

*/
    @NotNull
    private static IElementType parseSingleton(final RBuilder builder){
        RMarker statementMarker = builder.mark();

        if (builder.compareAndEat(BNF.tLPARENS)){

            EXPR.parse(builder);
            builder.compareAndEat(tEOL);

            builder.match(tRPAREN);
            statementMarker.done(RubyElementTypes.CLASS_OBJECT);
            return RubyElementTypes.CLASS_OBJECT;

        }

        IElementType result = VARIABLE.parse(builder);
        if (result!=RubyElementTypes.EMPTY_INPUT){
            statementMarker.done(RubyElementTypes.CLASS_OBJECT);
            return RubyElementTypes.CLASS_OBJECT;
        }

        statementMarker.rollbackTo();
        return RubyElementTypes.EMPTY_INPUT;
    }

}
