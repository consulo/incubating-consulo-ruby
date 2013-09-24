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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions;


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.EXPR;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.TERM;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.definitions.method.BODYSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.ErrorMsg;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class Class implements RubyTokenTypes {
/*
        | kCLASS tLSHFT expr
          term
          bodystmt
          kEND
        | kCLASS cpath superclass
          bodystmt
          kEND

superclass	: term
		| '<' expr_value term
		| error term


*/
@NotNull
    public static IElementType parse(final RBuilder builder){
        RMarker statementMarker = builder.mark();
        IElementType classType = RubyElementTypes.CLASS;

        builder.match(kCLASS);



// objectClass
        if (builder.compareAndEat(tLSHFT)){
            RMarker singletonMarker = builder.mark();

            if (EXPR.parse(builder)==RubyElementTypes.EMPTY_INPUT){
                builder.error(ErrorMsg.EXPRESSION_EXPECTED_MESSAGE);
            }
            singletonMarker.done(RubyElementTypes.CLASS_OBJECT);
            classType = RubyElementTypes.OBJECT_CLASS;
        } else {
            final RMarker nameMarker = builder.mark();
            if (CPATH.parse(builder)==RubyElementTypes.EMPTY_INPUT) {
                builder.error(ErrorMsg.expected(RBundle.message("parsing.class.name")));
            }
            nameMarker.done(RubyElementTypes.CLASS_NAME);
// superclass
            if (builder.compareAndEat(tLT)) {
                RMarker superClassMarker = builder.mark();
                if (EXPR.parse(builder)==RubyElementTypes.EMPTY_INPUT){
                    builder.error(ErrorMsg.expected(RBundle.message("parsing.singleton")));
                }
                superClassMarker.done(RubyElementTypes.SUPER_CLASS);
            }
        }

        TERM.parse(builder);

        BODYSTMT.parse(builder);

        builder.matchIgnoreEOL(kEND);
        statementMarker.done(classType);
        return classType;
    }





}
