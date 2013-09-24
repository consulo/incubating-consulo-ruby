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

package org.jetbrains.plugins.ruby.ruby.lang.psi;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 14.08.2006
 */
public interface RPsiElement extends PsiElement, RVirtualElement {
    /**
     * Returns child elements in the psi tree
     * @param filter Types of expected child
     * @return PsiElement - child psiElement
     */
    @NotNull
    public List<PsiElement> getChildrenByFilter(IElementType filter);

    /**
     * Returns child element in the psi tree
     * @param filter Types of expected child
     * @param number number
     * @return PsiElement - child psiElement
     */
    @Nullable
    public PsiElement getChildByFilter(TokenSet filter, int number);
    /**
     * Returns child element in the psi tree
     * @param filter Types of expected child
     * @param number number
     * @return PsiElement - child psiElement
     */
    @Nullable
    public PsiElement getChildByFilter(IElementType filter, int number);

    /**
     * Returns children in psiTree left to element of type c
     * @return RElement object if found, null otherwise
     * @param c object of required type
     */
    @NotNull
    public <T extends PsiElement> List<T> getChildrenByType(Class<T> c);

    /**
     * Returns child with given number in psiTree left to element of type c
     * @return RElement object if found, null otherwise
     * @param c object of required type
     * @param number Number of child
     */
    @Nullable
    public <T extends PsiElement> T getChildByType(Class<T> c, int number);

    /**
     * Returns containing container
     * @return RContainer instance - "parent container"
     */

    @Nullable
    public RContainer getParentContainer();
}
