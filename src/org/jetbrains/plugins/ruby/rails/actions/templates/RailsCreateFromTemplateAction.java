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

package org.jetbrains.plugins.ruby.rails.actions.templates;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
*
* @author: Roman Chernyatchik
* @date: Oct 6, 2007
*/
public abstract class RailsCreateFromTemplateAction extends CreateFromTemplateActionBase {
    private FileTemplate myTemplate;

    public RailsCreateFromTemplateAction(@NotNull final FileTemplate template) {
        super(template.getName(), null, FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(template.getExtension()).getIcon());
        myTemplate = template;
    }

    protected PsiElement invokeDialogAndCreate(Project project, PsiDirectory dir, FileTemplate selectedTemplate) {
        return createDilog(project, dir, selectedTemplate).create();
    }

    @NotNull
    protected abstract CreateFileFromTemplateDialog createDilog(final Project project, final PsiDirectory dir, final FileTemplate selectedTemplate);

    @Nullable
    protected AnAction getReplacedAction(final FileTemplate template) {
        return null;
    }

    protected FileTemplate getTemplate(final Project project, final PsiDirectory dir) {
        return myTemplate;
    }

    public void update(AnActionEvent e) {
        super.update(e);
        Presentation presentation = e.getPresentation();
        boolean isEnabled = canCreateFromTemplate(e, myTemplate);
        presentation.setEnabled(isEnabled);
        presentation.setVisible(isEnabled);
    }
}
