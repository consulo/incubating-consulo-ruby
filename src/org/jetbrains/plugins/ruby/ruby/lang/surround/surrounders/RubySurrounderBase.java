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

package org.jetbrains.plugins.ruby.ruby.lang.surround.surrounders;

import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 5, 2007
 */
public abstract class RubySurrounderBase implements Surrounder {
    public boolean isApplicable(@NotNull final PsiElement[] elements) {
        return elements.length > 0;
    }

    protected String gatherText(PsiElement[] elements) {
        final StringBuffer buffer = new StringBuffer();
        for (PsiElement element : elements) {
            buffer.append(element.getText());
        }
        return buffer.toString();
    }

    @Nullable
    public TextRange surroundElements(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiElement[] elements) throws IncorrectOperationException {
        RPsiElement element = RubyPsiUtil.getTopLevelElements(project, getText(elements)).get(0);

        RubyPsiUtil.addBeforeInParent(elements[0], element);
        RubyPsiUtil.removeElements(elements);

        return getTextRange(element);
    }

    protected abstract TextRange getTextRange(RPsiElement element);

    protected abstract String getText(PsiElement[] elements);

}
