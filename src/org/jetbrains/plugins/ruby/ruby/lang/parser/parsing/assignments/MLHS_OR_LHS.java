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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.assignments;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 9, 2006
 */
public class MLHS_OR_LHS implements RubyTokenTypes {
    /**
     * Parses MLHS or LHS object
     * @param builder Current builder
     * @return result of parsing
     */
    public static IElementType parse(final RBuilder builder){
        if (builder.compare(tLPAREN)){
            return MLHS.parse(builder);
        }
        return parseWithLeadLHS(builder, builder.mark(), LHS.parse(builder));
    }


    /**
     * Parses MLHS_OR_LHS with lead PRIMARY parsed
     * @param builder Current builder
     * @param marker Marker before PRIMARY parsed
     * @param result result of PRIMARY parsing
     * @return result of parsing
     */
    public static IElementType parseWithLeadPRIMARY(final RBuilder builder, final RMarker marker, final IElementType result){
        return parseWithLeadLHS(builder, marker.precede(), LHS.parseWithLeadPRIMARY(builder, marker, result));
    }

    /**
     * Parses MLHS_OR_LHS with lead LHS parsed
     * @param builder Current builder
     * @param marker Marker before LHS parsed
     * @param result result of LHS parsing
     * @return result of parsing
     */
    public static IElementType parseWithLeadLHS(final RBuilder builder, final RMarker marker, final IElementType result){
        if (result!=RubyElementTypes.EMPTY_INPUT && builder.compare(tCOMMA)){
            return MLHS.parseWithLeadLHS(builder, marker);
        }
        if (result!=RubyElementTypes.EMPTY_INPUT){
            marker.drop();
            return result;
        }
        marker.rollbackTo();
        return MLHS.parse(builder);
    }
}
