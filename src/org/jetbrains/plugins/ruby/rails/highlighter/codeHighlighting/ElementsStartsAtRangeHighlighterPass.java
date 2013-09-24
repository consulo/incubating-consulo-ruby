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

package org.jetbrains.plugins.ruby.rails.highlighter.codeHighlighting;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.codeInsight.RCodeInsightUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting.AbstractRubyHighlighterPass;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 30, 2007
 */
public abstract class ElementsStartsAtRangeHighlighterPass extends AbstractRubyHighlighterPass {
    public ElementsStartsAtRangeHighlighterPass(@NotNull final Project project, @NotNull final PsiFile psiFile, @NotNull final Editor editor, final boolean updateVisible, final int passId) {
        super(project, psiFile, editor, updateVisible, passId);
    }

    @NotNull
    protected List<PsiElement> collectElementsInRange(@NotNull final PsiFile psiFile,
                                                      final int startOffset, final  int endOffset) {
        final PsiFile file =  psiFile instanceof RHTMLFile
            ? ((RHTMLFile) psiFile).getInnerRubyFile()
            : psiFile;

        return RCodeInsightUtil.getElementsStartsInRange(file, startOffset, endOffset, false);
    }
}
