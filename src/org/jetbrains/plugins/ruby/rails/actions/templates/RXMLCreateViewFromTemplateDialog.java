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
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.RXMLFileType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public class RXMLCreateViewFromTemplateDialog extends CreateFileFromTemplateDialog {

    public RXMLCreateViewFromTemplateDialog(@NotNull final Project project, @NotNull final PsiDirectory directory, @NotNull final FileTemplate template) {
        super(project, directory, template);
    }

    @Override
	protected PsiFile createPsiFile(final FileTemplate template,
                                    final Project project,
                                    final PsiDirectory directory,
                                    final String templateText,
                                    final String fileName) throws IncorrectOperationException {
        final String defaultExt = template.getExtension();
        final FileType fileType = FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(defaultExt);
        assert fileType == RXMLFileType.RXML;

        final String rxmlExt = RXMLFileType.RXML.getDefaultExtension();
        final String builderExt = RXMLFileType.RXML.getBuilderExtension();

        String ext = defaultExt;
        if (fileName.endsWith(builderExt)) {
            ext = builderExt;
        } else if (fileName.endsWith(rxmlExt)) {
            ext = rxmlExt;
        }
        return createPsiFile(project, directory, templateText, fileName, ext);
    }
}