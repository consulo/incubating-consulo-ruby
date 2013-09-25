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

package rb.implement;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 30, 2008
 */
public interface ImplementHandler extends LanguageCodeInsightActionHandler {

    /*
     * Creates list of classMembers to implement
     */
    public List<ClassMember> create_implement_members(@NotNull final Symbol class_symbol);

    public void execute(@Nullable Editor editor, @NotNull Project project, @Nullable PsiElement element, @Nullable Symbol symbol);
}
