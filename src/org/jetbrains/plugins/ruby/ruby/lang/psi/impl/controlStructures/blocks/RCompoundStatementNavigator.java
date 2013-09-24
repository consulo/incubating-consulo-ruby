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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 10.03.2007
 */
public class RCompoundStatementNavigator {

    @Nullable
    public static RCompoundStatement getByPsiElement(@NotNull final PsiElement element) {
        final PsiElement parent = element.getParent();
        return (parent instanceof RCompoundStatement) ? (RCompoundStatement) parent : null;
    }

    @Nullable
    public static RCompoundStatement getParentCompoundStatement(@NotNull final PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, RCompoundStatement.class);
    }

    @Nullable
    public static RCompoundStatement getNotStrictCompoundStatement(@NotNull final PsiElement element) {
        System.err.println("element: " + element + "text: " + element.getText());
        final RCompoundStatement parent = getByPsiElement(element);
        if (parent!=null){
            return parent;
        }
        final RCompoundStatement next = PsiTreeUtil.getNextSiblingOfType(element, RCompoundStatement.class);
        if (next!=null){
            return next;
        }
        return PsiTreeUtil.getPrevSiblingOfType(element, RCompoundStatement.class);
    }


}
