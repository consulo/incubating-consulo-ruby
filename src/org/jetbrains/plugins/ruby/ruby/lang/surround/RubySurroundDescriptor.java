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

package org.jetbrains.plugins.ruby.ruby.lang.surround;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.surround.surrounders.*;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 4, 2007
 */
public class RubySurroundDescriptor implements SurroundDescriptor {
    @NotNull
    public PsiElement[] getElementsToSurround(@NotNull final PsiFile file, final int startOffset, final int endOffset) {
        final ArrayList<PsiElement> list = new ArrayList<PsiElement>();
        PsiElement first = file.getViewProvider().findElementAt(startOffset);
        PsiElement last = file.getViewProvider().findElementAt(endOffset-1);
        if (first == null || last == null){
            return new PsiElement[]{};
        }
        final PsiElement context = first!=last ? PsiTreeUtil.findCommonContext(first, last) : first.getParent();
        if (context instanceof RCompoundStatement){
            // we find all the elements under RCompoundStatement in given ranges
            while (first.getParent()!=context){
                first = first.getParent();
            }
            while (last.getParent()!=context){
                last = last.getParent();
            }
            while (true) {
                list.add(first);
                if (first == null || first == last){
                    return list.toArray(new PsiElement[list.size()]);
                }
                first = first.getNextSibling();
            }
        } else
        if (context instanceof RExpression){
            return new PsiElement[]{context};
        } else {
            return PsiElement.EMPTY_ARRAY;
        }
    }

    @NotNull
    public Surrounder[] getSurrounders() {
        return new Surrounder[]{
                new RubyBraceSurrounder(),
                new RubyBEGINSurrounder(),
                new RubyENDSurrounder(),
                new RubyIfSurrounder(),
                new RubyWhileSurrounder(),
                new RubyUnlessSurrounder(),
                new RubyBeginEndSurrounder()
        };
    }
}
