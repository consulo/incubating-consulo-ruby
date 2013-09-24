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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.MarkupUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 19.10.2007
 */
public class PsiElementSymbol extends Symbol{
    private final PsiElement myPsiElement;

    public PsiElementSymbol(@NotNull final PsiElement element,
                            @NotNull final String name,
                            final Type type){
        super(element.getProject(), name, type, null, null);
        myPsiElement = element;
    }

    @SuppressWarnings({"StringConcatenationInsideStringBufferAppend"})
    public String toString(@NotNull final FileSymbol fileSymbol, boolean useHtml) {
        final StringBuilder builder = new StringBuilder();
        builder.append("[" + getId() + "] " + getType() +  " ");
        if (useHtml){
            MarkupUtil.appendBold(builder, myPsiElement.toString());
        } else {
            builder.append(myPsiElement);
        }
        return builder.toString();
    }

    @NotNull
    public PsiElement getPsiElement() {
        return myPsiElement;
    }
}
