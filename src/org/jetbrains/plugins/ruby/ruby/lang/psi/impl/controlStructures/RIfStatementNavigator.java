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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.OuterElementInRHTMLOrRubyLang;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RIfStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 22, 2007
 */
public class RIfStatementNavigator {
    @Nullable public static RIfStatement getByOuter(@NotNull final OuterElementInRHTMLOrRubyLang outer)  {
        final PsiElement parent = outer.getParent();
        return (parent instanceof RIfStatement) ? (RIfStatement) parent : null;
    }

    @Nullable
    public static RCompoundStatement getTrueBlock(@NotNull final RIfStatement rIfStatement) {
        final PsiElement trueBlock =
                RubyPsiUtil.getChildByFilter(rIfStatement, RubyElementTypes.COMPOUND_STATEMENT, 0);
        return (trueBlock instanceof RCompoundStatement) ? (RCompoundStatement) trueBlock : null;
    }
}

