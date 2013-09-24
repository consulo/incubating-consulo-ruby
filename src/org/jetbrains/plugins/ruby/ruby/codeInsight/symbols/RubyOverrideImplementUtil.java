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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 10, 2008
 */
public class RubyOverrideImplementUtil {
    /*
     * Gathers override virtual elements for given virtual element
     * returns list of RVirtualElement or PsiMethod
     */
    @NotNull
    public static List getOverridenElements(@Nullable final FileSymbol fileSymbol,
                                            @NotNull final Symbol symbol,
                                            @Nullable final RVirtualElement anchorElement) {
        return getOverridenElements(fileSymbol, symbol, anchorElement, getOverridenSymbols(fileSymbol, symbol));
    }

    /*
     * Gathers override virtual elements for given virtual element
     * returns list of RVirtualElement or PsiMethod
     */
    @SuppressWarnings({"unchecked"})
    @NotNull
    public static List getOverridenElements(@Nullable final FileSymbol fileSymbol,
                                            @NotNull final Symbol symbol,
                                            @Nullable final RVirtualElement anchorElement,
                                            @NotNull final List<Symbol> overridenSymbols) {
        final ArrayList elements = new ArrayList();
        // we should add all the prototypes of overriden symbols
        for (Symbol s : overridenSymbols) {
            if (s.getType() == Type.JAVA_METHOD){
                final PsiElement element = ((JavaSymbol) s).getPsiElement();
                assert element instanceof PsiMethod;
                final PsiMethod method = (PsiMethod) element;
                if (method.isValid() && PsiUtil.canBeOverriden(method) && !isAbstract(method)){
                    elements.add(method);
                }
            }
            elements.addAll(s.getVirtualPrototypes(fileSymbol).getAll());
        }

        // and all the prototypes of given symbol before anchorElement
        for (RVirtualElement element : symbol.getVirtualPrototypes(fileSymbol).getAll()) {
            if (element == anchorElement) {
                break;
            }
            elements.add(element);
        }
        return elements;
    }

    /*
     * Gathers implemented Java methods
     */
    @NotNull
    public static List<PsiMethod> getImplementedJavaMethods(@NotNull final List<Symbol> overridenSymbols) {
        final ArrayList<PsiMethod> implemented = new ArrayList<PsiMethod>();
        // we should add all the prototypes of overriden symbols
        for (Symbol s : overridenSymbols) {
            if (s.getType() == Type.JAVA_METHOD){
                final PsiElement element = ((JavaSymbol) s).getPsiElement();
                assert element instanceof PsiMethod;
                final PsiMethod method = (PsiMethod) element;
                if (method.isValid() && isAbstract(method)){
                    implemented.add(method);
                }
            }
        }
        return implemented;
    }

    /**
     * Checks if method is abstract or it`s a member of interface
     * It`s sucks, that rather often everyone has its own method for doing the same things in IDEA!
     * @param method PsiMethod
     * @return true or false
     */
    public static boolean isAbstract(@NotNull final PsiMethod method) {
        return method.getModifierList().hasModifierProperty(PsiModifier.ABSTRACT) || method.getContainingClass().isInterface();
    }

    /*
     * Gathers override symbols for given symbol, e.g. overriden methods for methods
     */
    @NotNull
    public static List<Symbol> getOverridenSymbols(@Nullable final FileSymbol fileSymbol, @NotNull final Symbol symbol) {
        final Type type = symbol.getType();
        final String name = symbol.getName();
        if (name == null) {
            return Collections.emptyList();
        }
        final List<Symbol> overridenSymbols = new ArrayList<Symbol>();
// For methods we look for overriden methods
        if (Types.METHODS_LIKE.contains(type)) {
            final Symbol parent = symbol.getParentSymbol();
            if (parent != null && Types.MODULE_OR_CLASS.contains(parent.getType())) {
                final Children children;
                if (Types.INSTANCE_TYPES.contains(type)){
                    children = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, Context.INSTANCE, parent, symbol);
                } else
                if (Types.STATIC_TYPES.contains(type)){
                    children = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, Context.CLASS, parent, symbol);
                } else {
                    children = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, Context.ALL, parent, symbol);
                }
                for (Symbol method : children.getSymbolsByNameAndTypes(name, Types.METHODS_LIKE).getAll()) {
                    // We add this hack not to tell, that method is overriden by itself
                    if (symbol != method) {
                        overridenSymbols.add(method);
                    }
                }
            }
        }
        return overridenSymbols;
    }

    public static String classMembersToString(@NotNull final List<ClassMember> list){
        final StringBuilder builder = new StringBuilder();
        for (ClassMember member : list) {
            builder.append(member.getText()).append("\n");
        }
        return builder.toString().trim();
    }

}
