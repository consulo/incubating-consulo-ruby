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

package org.jetbrains.plugins.ruby.ruby.lang.search;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;

/**
 * Created by IntelliJ IDEA.
*
* @author: oleg
* @date: Jan 12, 2008
*/
class RubyOccurenceProcessor implements TextOccurenceProcessor {
    private final PsiElement myElement2Search;
    private final String myName;
    private final Processor<PsiReference> myConsumer;

    public RubyOccurenceProcessor(@NotNull final PsiElement element2Search,
                                  @NotNull final String name,
                                  @NotNull final Processor<PsiReference> consumer) {
        myElement2Search = element2Search;
        myName = name;
        myConsumer = consumer;
    }

    @Override
	public boolean execute(final PsiElement element, final int offsetInElement) {
        final PsiReference ref = element.getReference();
        if (ref instanceof RPsiPolyvariantReference && !ref.isReferenceTo(myElement2Search)) {
            final PsiElement refValue = ((RPsiPolyvariantReference) ref).getRefValue();
            // We should search the same variables
            if (myElement2Search instanceof RGlobalVariable && !(refValue instanceof RGlobalVariable)){
                return true;
            }
            if (myElement2Search instanceof RClassVariable && !(refValue instanceof RClassVariable)){
                return true;
            }
            if (myElement2Search instanceof RInstanceVariable && !(refValue instanceof RInstanceVariable)){
                return true;
            }
            final String refName = refValue instanceof PsiNamedElement ? ((PsiNamedElement) refValue).getName() : refValue.getText();
            if (myName.equals(refName)) {
                return myConsumer.process(ref);
            }
        }
        return true;
    }
}
