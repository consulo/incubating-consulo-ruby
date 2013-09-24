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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter.codeHighlighting;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.ruby.lang.highlighter.RubyHighlightUtil;
import com.intellij.codeHighlighting.TextEditorHighlightingPass;
import com.intellij.codeInsight.daemon.impl.CollectHighlightsUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 02.02.2007
 */
 public abstract class AbstractRubyHighlighterPass extends TextEditorHighlightingPass {
    protected final Project myProject;
    protected final PsiFile myFile;
    protected final Editor myEditor;

    protected final int myStartOffset;
    protected final int myEndOffset;
    private final List<PsiElement> myElements;

    public AbstractRubyHighlighterPass(@NotNull final Project project,
                                       @NotNull final PsiFile psiFile,
                                       @NotNull final Editor editor,
                                       final boolean updateVisible,
                                       final int passId) {
        super(project, editor.getDocument());
        myFile = psiFile;
        myEditor = editor;
        myProject = project;
        
        setId(passId);
        if (updateVisible) {
            final TextRange visibleRange = RubyHighlightUtil.getVisibleRange(myEditor);
            myStartOffset = visibleRange.getStartOffset();
            myEndOffset = visibleRange.getEndOffset();
        } else {
            myStartOffset = 0;
            myEndOffset = psiFile.getTextLength();
        }
        myElements = collectElementsInRange(psiFile, myStartOffset, myEndOffset);
    }

    @NotNull
    protected List<PsiElement> collectElementsInRange(@NotNull final PsiFile psiFile,
                                                      final int startOffset, final int endOffset) {
        return psiFile instanceof RHTMLFile ?
				CollectHighlightsUtil.getElementsInRange(((RHTMLFile) psiFile).getInnerRubyFile(), startOffset, endOffset, false):
				CollectHighlightsUtil.getElementsInRange(psiFile, startOffset, endOffset, false);
    }

    protected List<PsiElement> getElementsInRange(){
        return myElements;
    }
}