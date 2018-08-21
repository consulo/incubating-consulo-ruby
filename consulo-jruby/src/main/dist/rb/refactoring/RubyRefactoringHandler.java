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

package rb.refactoring;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 21, 2007
 */
public interface RubyRefactoringHandler {
    /*
     * Same as RefactoringActionHandler.invoke(@NotNull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext);
     */
    void invoke(@NotNull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext);

    /*
     * Same as RefactoringActionHandler.invokeOutter(@NotNull Project project, @NotNull PsiElement[] elements, @Nullable DataContext dataContext);
     */
    void invokeOutter(@NotNull Project project, @NotNull PsiElement[] elements, @Nullable DataContext dataContext);
}
