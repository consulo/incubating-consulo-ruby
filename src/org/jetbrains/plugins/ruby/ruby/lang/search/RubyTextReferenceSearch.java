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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.PsiSearchHelper;
import com.intellij.psi.search.UsageSearchContext;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.util.Processor;
import com.intellij.util.QueryExecutor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 3, 2007
 */
public class RubyTextReferenceSearch implements QueryExecutor<PsiReference, ReferencesSearch.SearchParameters> {

    public boolean execute(@NotNull final ReferencesSearch.SearchParameters params,
                           @NotNull final Processor<PsiReference> consumer) {
        final PsiElement element2Search = params.getElementToSearch();
        if (element2Search instanceof PsiNamedElement && element2Search instanceof RPsiElement) {
            final String name = ApplicationManager.getApplication().runReadAction(new Computable<String>() {
                public String compute() {
                    return ((PsiNamedElement) element2Search).getName();
                }
            });
            // we don`t search elements with null name
            if (name == null){
                return false;
            }
            // we don`t search local variables
            if (element2Search instanceof RIdentifier){
                final RIdentifier rIdentifier = (RIdentifier) element2Search;
                if (rIdentifier.isParameter() || rIdentifier.isLocalVariable()) {
                    return true;
                }
            }
            final RubyOccurenceProcessor processor = new RubyOccurenceProcessor(element2Search, name, consumer);
            short searchContext = UsageSearchContext.IN_CODE;
            return PsiSearchHelper.SERVICE.getInstance(element2Search.getProject()).
                    processElementsWithWord(processor, params.getEffectiveSearchScope(), name, searchContext, true);
        }
        return true;
    }

}
