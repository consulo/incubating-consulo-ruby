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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RMultiAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RSelfAssignmentExpression;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 16, 2007
 */
public class RAssignmentExpressionNavigator {
    @Nullable
    public static RAssignmentExpression getAssignmentByLeftPart(@NotNull final PsiElement element){
        PsiElement parent = element.getParent();
        if (parent instanceof RAssignmentExpression &&
                !(parent instanceof RSelfAssignmentExpression) &&
                ((RAssignmentExpression) parent).getObject() == element) {
            return (RAssignmentExpression)parent;
        }

        if (parent instanceof RListOfExpressions) {
            PsiElement grandPa = parent.getParent();
            if (grandPa instanceof RMultiAssignmentExpression &&
                    ((RAssignmentExpression) grandPa).getObject() == parent) {
                return (RAssignmentExpression) grandPa;
            }
        }

        return null;
    }
}
