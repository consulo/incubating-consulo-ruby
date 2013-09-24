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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageType;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageTypeProvider;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.RSymbolNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RAliasNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.RFNameImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RNamedElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 21, 2007
 */
public class RFNameReference extends RNamedReference{
    public RFNameReference(@NotNull RNamedElement element) {
        super(element);
    }

    @NotNull
    protected ResolveResult[] multiResolveInner(boolean incompleteCode) {
        if (((RFNameImpl) myElement).isClassOrModuleName()){
            return new ResolveResult[]{new ResolveResult(){
                @Nullable
                public PsiElement getElement() {
                    RubyUsageTypeProvider.setType(RFNameReference.this, RubyUsageType.DECLARATION);
                    return myElement.getParentContainer();
                }

                public boolean isValidResult() {
                    return true;
                }
            }};
        }
        if (RSymbolNavigator.getSymbolByObject(myElement) !=null ||
                RAliasNavigator.getAliasByOldName(myElement)!=null){
            return super.multiResolveInner(incompleteCode);
        }
        return ResolveResult.EMPTY_ARRAY;
    }
}