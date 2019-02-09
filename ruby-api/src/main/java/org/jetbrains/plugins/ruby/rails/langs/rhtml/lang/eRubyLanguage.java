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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.impl.RHTMLFileHighlighterImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingDocumentModel;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lang.xhtml.XHTMLLanguage;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.HtmlPolicy;
import com.intellij.psi.formatter.xml.ReadOnlyBlock;
import com.intellij.psi.formatter.xml.XmlBlock;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.templateLanguages.TemplateLanguage;
import com.intellij.psi.templateLanguages.TemplateLanguageFileViewProvider;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.04.2007
 */
public class eRubyLanguage extends Language implements TemplateLanguage
{
	public static final eRubyLanguage INSTANCE = new eRubyLanguage();

	public eRubyLanguage()
	{
		super("E-RUBY", "application/x-httpd-eruby");
	}


	@Nonnull
	public SyntaxHighlighter getSyntaxHighlighter(final Project project, final VirtualFile virtualFile)
	{
		return new RHTMLFileHighlighterImpl();
	}

	//TODO  getAnnotator()


	public static Block createRHTMLRoot(final PsiElement element, final CodeStyleSettings settings, final FormattingDocumentModel documentModel)
	{
		final PsiFile file = element.getContainingFile();
		final RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(file);
		assert rhtmlFile != null;

		final TemplateLanguageFileViewProvider rhtmlViewProvider = rhtmlFile.getViewProvider();
		final Language templateLang = rhtmlViewProvider.getTemplateDataLanguage();


		if(templateLang == HTMLLanguage.INSTANCE || templateLang == XHTMLLanguage.INSTANCE)
		{
			final PsiFile psiRoot = rhtmlViewProvider.getPsi(templateLang);
			final ASTNode rootNode = SourceTreeToPsiMap.psiElementToTree(psiRoot);
			return new XmlBlock(rootNode, null, null, new HtmlPolicy(settings, documentModel), null, null);
		}
		else
		{
			return new ReadOnlyBlock(file.getNode());
		}
	}
	//    public static Block createRHTMLRoot(final PsiElement element,
	//                                        final CodeStyleSettings settings,
	//                                        final FormattingDocumentModel documentModel) {
	//        final PsiFile file = element.getContainingFile();
	//        final RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(file);
	//        assert rhtmlFile != null;
	//
	//        final RHTMLFileViewProvider rhtmlViewProvider = rhtmlFile.getViewProvider();
	//        final Language templateLang = rhtmlViewProvider.getTemplateDataLanguage();
	//
	//        //TODO leave only HTML, not XML
	//        if (templateLang == StdLanguages.HTML || templateLang == StdLanguages.XHTML) {
	//            final PsiFile psiRoot = rhtmlViewProvider.getPsi(RHTMLLanguage.RHTML);
	//            final ASTNode rootNode = SourceTreeToPsiMap.psiElementToTree(psiRoot);
	//            return new XmlBlock(rootNode, null, null, new HtmlPolicy(settings, documentModel), null, null);
	//        } else {
	//            return new ReadOnlyBlock(file.getNode());
	//        }
	//    }

}

