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

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaResolveUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.*;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.PsiElementSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageType;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageTypeProvider;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.presentation.SymbolPresentationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 20, 2007
 */
public class ResolveUtil {
    /**
     * Adds set of virtualPrototypes to resolve list
     * @param fileSymbol FileSymbol
     * @param project Current project
     * @param list resolve list to add to
     * @param symbol Symbol to add to Resolveresults
     */
    public static void addVariants(@Nullable final FileSymbol fileSymbol,
                                   @NotNull final Project project,
                                   @NotNull final List<ResolveResult> list,
                                   @NotNull final Symbol symbol) {

// Java Symbol
        if (Types.JAVA.contains(symbol.getType())){
            add(list, ((JavaSymbol) symbol).getPsiElement());
        }
// Local variable
        if (Type.LOCAL_VARIABLE.asSet().contains(symbol.getType())){
            add(list, ((PsiElementSymbol) symbol).getPsiElement());
        }

        for (RVirtualElement element : SymbolPresentationUtil.getPrototypesToShow(fileSymbol, symbol)) {
// JRuby Specific!
            if (element instanceof RVirtualImportJavaClass){
                for (RVirtualName name : ((RVirtualImportJavaClass) element).getNames()) {
                    add(list, JavaResolveUtil.getPackageOrClass(project, name.getPath()));
                }
            }
            if (element instanceof RVirtualIncludeJavaClass){
                String qualifiedName = ((RVirtualIncludeJavaClass) element).getQualifiedName();
                if (qualifiedName!=null){
                // Hack for RVirtualIncludePackage
                    if (element instanceof RVirtualIncludeJavaPackage){
                        qualifiedName += '.' + symbol.getName();
                    }
                    add(list, JavaResolveUtil.getPackageOrClass(project, qualifiedName));
                }
            }
            add(list, RVirtualPsiUtil.findPsiByVirtualElement(element, project));
        }
    }

    private static void add(@NotNull final List<ResolveResult> list, final PsiElement psiElement) {
        if (psiElement!=null){
            list.add(new ResolveResult(){
                @Nullable
                public PsiElement getElement() {
                    return psiElement;
                }

                public boolean isValidResult() {
                    return true;
                }
            });
        }
    }

    /**
     * Resolves element to the set of symbols
     * @param element Element to resolve
     * @return The list of symbols
     */
    public static List<Symbol> resolveToSymbols(@Nullable final PsiElement element) {
        if (element == null) {
            return Collections.emptyList();
        }
        final PsiReference ref = element.getReference();
        if (ref instanceof RPsiPolyvariantReference){
            FileSymbol fileSymbol = null;
            if (element instanceof RFile){
                fileSymbol = ((RFile) element).getFileSymbol();
            } else
            if (element instanceof RPsiElementBase){
                fileSymbol = ((RPsiElementBase) element).forceFileSymbolUpdate();
            }
             return ((RPsiPolyvariantReference) ref).multiResolveToSymbols(fileSymbol);
        }
        return Collections.emptyList();
    }


    @Nullable
    public static PsiElement resolvePolyVarReference(@NotNull final RPsiPolyvariantReference ref){
        final ResolveResult[] results = ref.multiResolve(false);
        return results.length==1 ? results[0].getElement() : null;
    }

    @Nullable
    public static Symbol resolveToSymbol(@Nullable final FileSymbol fileSymbol,
                                         @Nullable final PsiReference ref){
        if (ref instanceof RPsiPolyvariantReference){
            final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(fileSymbol);
            return symbols.size() == 1 ? symbols.get(0) : null;
        }
        return null;
    }

    public static boolean isReferenceTo(@NotNull final PsiReference reference, final PsiElement element){
        if (reference instanceof PsiPolyVariantReference){
            for (ResolveResult result : ((PsiPolyVariantReference) reference).multiResolve(true)) {
                if (result.getElement() == element){
                    return true;
                }
            }
        } else
        if (reference.resolve() == element){
            return true;
        }
        RubyUsageTypeProvider.setType(reference, RubyUsageType.TEXT_MATCHED);
        return false;
    }

    @NotNull
    public static List<PsiElement> multiResolve(@NotNull final PsiElement element) {
        final PsiReference ref = element.getReference();
        if (ref instanceof PsiPolyVariantReference){
            final ResolveResult[] results = ((PsiPolyVariantReference) ref).multiResolve(false);
            final ArrayList<PsiElement> result = new ArrayList<PsiElement>(results.length);
            for (ResolveResult resolveResult : results) {
                result.add(resolveResult.getElement());
            }
            return result;
        }

        final PsiElement res = ref!=null ? ref.resolve() : null;
        return res!=null ? Collections.singletonList(res) : Collections.<PsiElement>emptyList();
    }
}
