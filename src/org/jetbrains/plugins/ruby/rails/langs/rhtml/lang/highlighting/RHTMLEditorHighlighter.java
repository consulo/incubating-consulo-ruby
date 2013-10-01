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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting;

import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import com.intellij.lang.Language;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.editor.colors.EditorColorsScheme;
import com.intellij.openapi.editor.ex.util.LayerDescriptor;
import com.intellij.openapi.editor.ex.util.LayeredLexerEditorHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 16, 2007
 */
public class RHTMLEditorHighlighter extends LayeredLexerEditorHighlighter
{
	private Project myProject;
	private VirtualFile myFile;
	private Language myTemplateLanguage;

	public RHTMLEditorHighlighter(final EditorColorsScheme scheme, final Project project, final VirtualFile file)
	{
		super(SyntaxHighlighterFactory.getSyntaxHighlighter(RHTMLFileType.RHTML, project, file), scheme);

		myProject = project;
		myFile = file;

		final SyntaxHighlighter rubyHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(RubyLanguage.INSTANCE, project, file);

		// "\n" separates lexems if there is no separator
		// <%i = 3%><%a = 6>
		// ruby code : "i = 3\na = 6" instead of "i = 3a = 6"
		final LayerDescriptor rubyLayer = new LayerDescriptor(rubyHighlighter, "\n", RHTMLHighlighter.RHTML_SCRIPTING_BACKGROUND);
		registerLayer(RHTMLTokenType.RUBY_CODE_CHARACTERS, rubyLayer);
	}

	@Override
	protected boolean updateLayers()
	{
		Language templateLanguage = getCurrentTemplateLanguageAndPrefixes();

		//register template language layer (e.g. HTML or maybe Text(for general ERB template))
		if(!Comparing.equal(myTemplateLanguage, templateLanguage))
		{
			unregisterLayer(RHTMLTokenTypeEx.TEMPLATE_CHARACTERS_IN_RHTML);
			myTemplateLanguage = templateLanguage;

			final SyntaxHighlighter templateLanguageHighlighter = SyntaxHighlighterFactory.getSyntaxHighlighter(myTemplateLanguage, myProject, myFile);
			//in this case tempate language is HTML and lexem shouldn't be separator with additional
			//separator, i.e for highlighting "<header<% n %>1>" HTML is interpreted as "<header1>"
			registerLayer(RHTMLTokenTypeEx.TEMPLATE_CHARACTERS_IN_RHTML, new LayerDescriptor(templateLanguageHighlighter, "", null));
			return true;
		}

		return false;
	}


	private Language getCurrentTemplateLanguageAndPrefixes()
	{
		if(myProject != null && getDocument() != null)
		{
			final PsiDocumentManager instance = PsiDocumentManager.getInstance(myProject);
			final PsiFile psiFile = instance.getPsiFile(getDocument());
			if(RHTMLPsiUtil.isInRHTMLFile(psiFile))
			{
				final RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(psiFile);
				assert rhtmlFile != null;
				return rhtmlFile.getViewProvider().getTemplateDataLanguage();
			}
		}

		return StdLanguages.HTML;
	}
}