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

package rb.override;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.codeInsight.generation.MemberChooserObject;
import com.intellij.codeInsight.generation.PsiElementMemberChooserObject;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 1, 2007
 */
public class RClassMember extends PsiElementMemberChooserObject implements ClassMember {
    public RClassMember(@NotNull PsiElement psiElement) {
        super(psiElement, RubyPsiUtil.getPresentableName(psiElement), RubyPsiUtil.getIcon(psiElement));
    }

    public MemberChooserObject getParentNodeDelegate() {
        final RPsiElement element = (RPsiElement) getPsiElement();
        final PsiElement parent = element.getParentContainer();
        //noinspection ConstantConditions
        return new RClassMember(parent);
    }
}
