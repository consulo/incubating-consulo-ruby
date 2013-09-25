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

package org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.parser.ParsingMethod;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.arg.Assignment;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 18, 2008
 */
public abstract class ParsingMethodWithAssignmentLookup implements ParsingMethod {
    @NotNull
    public abstract IElementType parseInner(final RBuilder builder);

    @Override
	public final IElementType parse(final RBuilder builder) {
        final RMarker assignMarker = builder.mark();
        final IElementType type = parseInner(builder);
// Lookahead
        if (BNF.LHS.contains(type) && builder.compare(BNF.tASSGNS)){
            return Assignment.parseWithLeadRANGE(builder, assignMarker, type);
        }
        assignMarker.drop();
        return type;
    }
}
