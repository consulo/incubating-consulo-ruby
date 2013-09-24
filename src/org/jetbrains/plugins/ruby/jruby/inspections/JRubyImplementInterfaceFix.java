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

package org.jetbrains.plugins.ruby.jruby.inspections;

import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import rb.implement.ImplementHandler;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 29, 2008
 */
public class JRubyImplementInterfaceFix implements LocalQuickFix {
    protected Symbol mySymbol;
    protected PsiElement myEndElement;

    public JRubyImplementInterfaceFix(@NotNull final PsiElement endElement, @NotNull final Symbol symbol) {
        myEndElement = endElement;
        mySymbol = symbol;
    }

    @NotNull
    public String getName() {
        return "Implement methods";
    }

    @NotNull
    public String getFamilyName() {
        return "JRuby";
    }

    public void applyFix(@NotNull final Project project, @NotNull final ProblemDescriptor descriptor) {
        final ImplementHandler handler = (ImplementHandler) RubyLanguage.RUBY.getImplementMethodsHandler();
        if (handler != null) {
            // Looking for editor
            Editor editor = null;
            final PsiFile file = myEndElement.getContainingFile();
            final VirtualFile virtualFile = file.getVirtualFile();
            if (virtualFile != null) {
                for (FileEditor fileEditor : FileEditorManager.getInstance(project).getEditors(virtualFile)) {
                    if (fileEditor instanceof TextEditor){
                        editor = ((TextEditor) fileEditor).getEditor();
                        break;
                    }
                }
            }
            handler.execute(editor, project, myEndElement, mySymbol);
        }
    }
}
