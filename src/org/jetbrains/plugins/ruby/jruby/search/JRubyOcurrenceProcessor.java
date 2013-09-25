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

package org.jetbrains.plugins.ruby.jruby.search;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.TextOccurenceProcessor;
import com.intellij.util.Processor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 12, 2008
 */
public class JRubyOcurrenceProcessor implements TextOccurenceProcessor{
    private PsiMethod myMethod;
    private String myName;
    private Processor<PsiReference> myPsiReferenceProcessor;
    private boolean myShouldResolve;

    public JRubyOcurrenceProcessor(@NotNull final PsiMethod method,
                                       @NotNull final String name,
                                       @NotNull final Processor<PsiReference> psiReferenceProcessor,
                                       final boolean shouldResolve) {
        myMethod = method;
        myName = name;
        myPsiReferenceProcessor = psiReferenceProcessor;
        myShouldResolve = shouldResolve;
    }

    @Override
	public boolean execute(PsiElement element, int offsetInElement) {
        final PsiReference ref = element.getReference();
        if (ref instanceof RPsiPolyvariantReference) {
            if (myShouldResolve && ref.isReferenceTo(myMethod) || !myShouldResolve && !ref.isReferenceTo(myMethod)) {
                final PsiElement refValue = ((RPsiPolyvariantReference) ref).getRefValue();
                final String refName = refValue instanceof PsiNamedElement ? ((PsiNamedElement) refValue).getName() : refValue.getText();
                if (myName.equals(refName)) {
                    return myPsiReferenceProcessor.process(ref);
                }
            }
        }
        return true;
    }
}
