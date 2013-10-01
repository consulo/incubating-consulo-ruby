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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLHtmlBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLRubyInjectionBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.OuterRHTMLElementInHTML;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.FormattingModelProvider;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.FormattingDocumentModelImpl;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 11, 2007
 */
public class RHTMLFormattingModelBuilder implements FormattingModelBuilder
{
	private static final Logger LOG = Logger.getInstance(RHTMLFormattingModelBuilder.class.getName());

	@Override
	@NotNull
	public FormattingModel createModel(@NotNull final PsiElement element, final CodeStyleSettings settings)
	{
		final PsiFile psiFile = element.getContainingFile();

		final FormattingDocumentModelImpl documentModel = FormattingDocumentModelImpl.createOn(psiFile);
		final XmlFormattingPolicy rhtmlPolicy = new RHTMLPolicy(settings, documentModel);

		//noinspection ConstantConditions
		final ASTNode node = element.getNode();

		assert node != null;
		assert psiFile != null;

		final FileViewProvider vProvider = psiFile.getViewProvider();

		final Block block;
		if(node.getElementType() == XmlElementType.HTML_DOCUMENT)
		{
			final PsiElement rhtmlElement = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, node.getStartOffset(), true);
			assert rhtmlElement != null; //it is RHTML Document

			final ASTNode rhmtlElementNode = rhtmlElement.getNode();
			assert rhmtlElementNode != null; //it is RHTML Element, it's node isn't null!

			block = new RHTMLBlock(rhmtlElementNode, Indent.getNoneIndent(), null, rhtmlPolicy);
		}
		else if(vProvider instanceof RHTMLFileViewProvider && (element instanceof XmlAttribute || element instanceof XmlAttributeValue))
		{
			block = new RHTMLHtmlBlock(node, null, null, rhtmlPolicy, Indent.getNoneIndent(), vProvider, element.getTextRange());
		}
		else if(element instanceof OuterRHTMLElementInHTML)
		{
			final PsiElement rhtmlProjection = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, node.getStartOffset(), true);
			assert rhtmlProjection != null;

			final ASTNode rhtmlNode = rhtmlProjection.getNode();

			assert rhtmlNode != null;
			LOG.assertTrue(rhtmlNode.getElementType() == RHTMLElementType.RHTML_XML_TAG, "RHTML injection expected, but was: " + rhtmlNode.getElementType());
			block = new RHTMLRubyInjectionBlock(rhtmlNode, Indent.getNoneIndent(), null, rhtmlPolicy, null, null, null);
		}
		else
		{
			block = new RHTMLBlock(node, Indent.getAbsoluteNoneIndent(), null, rhtmlPolicy);
		}
		return FormattingModelProvider.createFormattingModelForPsiFile(psiFile, block, settings);
	}

	@Nullable
	@Override
	public TextRange getRangeAffectingIndent(PsiFile psiFile, int i, ASTNode astNode)
	{
		return null;
	}
}
