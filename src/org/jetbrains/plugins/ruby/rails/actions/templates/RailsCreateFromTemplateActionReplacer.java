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

import com.intellij.ide.fileTemplates.CreateFromTemplateActionReplacer;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.templates.RailsTemplatesLoader;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public class RailsCreateFromTemplateActionReplacer implements CreateFromTemplateActionReplacer {
    @Override
	@Nullable
    public AnAction replaceCreateFromFileTemplateAction(@NotNull final FileTemplate fileTemplate) {
        final String templateName = fileTemplate.getName();
        if (templateName.equals(RailsTemplatesLoader.RHTML_TEMPLATE_NAME)) {
            return new RailsCreateFromTemplateAction(fileTemplate) {
                @Override
				@NotNull
                protected CreateFileFromTemplateDialog createDilog(final Project project, final PsiDirectory dir,
                                                                   final FileTemplate selectedTemplate) {
                    return new RHTMLCreateViewFromTemplateDialog(project, dir, fileTemplate);
                }
            };
        } else if (templateName.equals(RailsTemplatesLoader.RXML_TEMPLATE_NAME)) {
            return new RailsCreateFromTemplateAction(fileTemplate) {
                @Override
				@NotNull
                protected CreateFileFromTemplateDialog createDilog(final Project project, final PsiDirectory dir,
                                                                   final FileTemplate selectedTemplate) {
                    return new RXMLCreateViewFromTemplateDialog(project, dir, fileTemplate);
                }
            };
        }
        return null;
    }

}
