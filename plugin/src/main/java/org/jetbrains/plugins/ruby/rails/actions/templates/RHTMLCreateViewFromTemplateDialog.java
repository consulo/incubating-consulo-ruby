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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.ex.FileTypeManagerEx;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 6, 2007
 */
public class RHTMLCreateViewFromTemplateDialog extends CreateFileFromTemplateDialog
{
	//    @NonNls
	//    public static final String PATCHED_HTML_ERB_VIEW_EXTENSION = "html_erb";

	public RHTMLCreateViewFromTemplateDialog(@NotNull final Project project, @NotNull final PsiDirectory directory, @NotNull final FileTemplate template)
	{
		super(project, directory, template);
	}

	@Override
	protected PsiFile createPsiFile(final FileTemplate template, final Project project, final PsiDirectory directory, final String templateText, final String fileName) throws IncorrectOperationException
	{
		final String defaultExt = template.getExtension();
		final FileType fileType = FileTypeManagerEx.getInstanceEx().getFileTypeByExtension(defaultExt);
		assert fileType == RHTMLFileType.INSTANCE;

		final String rhtmlExt = RHTMLFileType.INSTANCE.getDefaultExtension();
		final String erbExt = RHTMLFileType.INSTANCE.getERBExtension();

		String ext = defaultExt;
		if(fileName.endsWith(erbExt))
		{
			ext = erbExt;
		}
		else if(fileName.endsWith(rhtmlExt))
		{
			ext = rhtmlExt;
		}
		//        else if (PATCHED_HTML_ERB_VIEW_EXTENSION.equals(defaultExt)) {
		//            ext = ViewsConventions.HTML_ERB_VIEW_EXTENSION;
		//        }
		return createPsiFile(project, directory, templateText, fileName, ext);
	}
}
