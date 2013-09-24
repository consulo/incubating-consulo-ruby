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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.iterators;


import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.commands.CALL_ARGS;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 06.07.2006
 */
public class BLOCK_COMMAND {
    /*
        block_command	: block_call
                | block_call '.' operation2 command_args
                | block_call tCOLON2 operation2 command_args
                ;

    */
    @NotNull
    public static IElementType parse(final RBuilder builder) {
        return parseWithLeadBlockCall(builder, builder.mark(), BLOCK_CALL.parse(builder));
    }

    /**
     * Parses block_command with leading Block_call parsed.
     * @param builder Current builder
     * @param marker Marker just before block_call
     * @param result Result of block_call parsing
     * @return result of block_command parsing
     */
    private static IElementType parseWithLeadBlockCall(final RBuilder builder, final RMarker marker, final IElementType result){
/*
    | block_call '.' operation2 command_args
    | block_call tCOLON2 operation2 command_args
*/
        if (result != RubyElementTypes.DOT_REFERENCE &&
                result != RubyElementTypes.COLON_REFERENCE) {
            marker.drop();
            return result;
        }

        if (CALL_ARGS.parse(builder) == RubyElementTypes.EMPTY_INPUT) {
            marker.drop();
            return result;
        }
        marker.done(RubyElementTypes.COMMAND_CALL);
        return RubyElementTypes.COMMAND_CALL;
    }


    /**
     * Parses block_command with leading command already parsed.
     * @param builder Current builder
     * @param marker Marker just before command parsed
     * @param result Result of command parsing
     * @return result of block_command parsing
     */
    public static IElementType parseWithLeadCOMMAND(final RBuilder builder, RMarker marker, IElementType result) {
        return parseWithLeadBlockCall(builder, marker.precede(), BLOCK_CALL.parseWithLeadCOMMAND(builder, marker, result));
    }
}
