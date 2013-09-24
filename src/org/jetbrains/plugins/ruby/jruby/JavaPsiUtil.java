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

package org.jetbrains.plugins.ruby.jruby;

import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 17, 2008
 */
public class JavaPsiUtil {

    public static boolean isStaticMethod(@NotNull final PsiMethod method) {
        return method.getModifierList().hasModifierProperty(PsiModifier.STATIC);
    }

    public static boolean isStaticField(@NotNull final PsiField method) {
        final PsiModifierList modifiers = method.getModifierList();
        return modifiers!=null && modifiers.hasModifierProperty(PsiModifier.STATIC);
    }

}
