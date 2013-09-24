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

package org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;

import java.util.Collection;

/**
 * @author yole
 */
public class ListParamDef extends ParamDef {
    private ParamDef myBaseParamDef;

    public ListParamDef(final ParamDef baseParamDef) {
        myBaseParamDef = baseParamDef;
    }

    @Nullable
    public Collection<RubyLookupItem> getVariants(final ParamContext context) {
        return myBaseParamDef.getVariants(context);
    }

    @Nullable
    public PsiElement resolveReference(final ParamContext context) {
        return myBaseParamDef.resolveReference(context);
    }
}
