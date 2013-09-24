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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser.rubyInjections;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLElement;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilderImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.04.2007
 */
public class RHTMLRubyBuilder extends RBuilderImpl {
    public RHTMLRubyBuilder(@NotNull final PsiBuilder psiBuilder) {
        super(psiBuilder);
    }

    public void error(@NotNull String error) {
        super.error("[RUBY] "+ error);
    }

    public boolean isAcceptibleErrorToken(IElementType myToken) {
        return myToken instanceof RubyElementType || myToken instanceof IRHTMLElement;
    }
}
