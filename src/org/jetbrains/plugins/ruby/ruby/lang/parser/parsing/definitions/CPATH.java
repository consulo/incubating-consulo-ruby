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
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.primary.PRIMARY;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.07.2006
 */
class CPATH  implements RubyTokenTypes {
/*
    cpath	: tCOLON3 cname
            | cname
            | primary_value tCOLON2 cname

    cname		: tIDENTIFIER
		    | tCONSTANT
		    ;

*/
@NotNull
    public static IElementType parse(final RBuilder builder){
    RMarker rollBackMarker = builder.mark();
    boolean colon3Seen = builder.compare(tCOLON3);

    IElementType result = PRIMARY.parse(builder);
    if (result!= RubyElementTypes.EMPTY_INPUT &&
            (colon3Seen ||
                result == RubyElementTypes.CONSTANT ||
                result == RubyElementTypes.IDENTIFIER ||
                result == RubyElementTypes.COLON_REFERENCE)){
        rollBackMarker.drop();
        return result;
    }
    rollBackMarker.rollbackTo();
    return RubyElementTypes.EMPTY_INPUT;
}

}
