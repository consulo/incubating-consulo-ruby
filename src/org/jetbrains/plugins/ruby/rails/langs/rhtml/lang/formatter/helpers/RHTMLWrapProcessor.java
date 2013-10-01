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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyElementType;
import com.intellij.formatting.Wrap;
import com.intellij.formatting.WrapType;
import com.intellij.lang.ASTNode;
import com.intellij.lang.StdLanguages;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 1, 2007
 */
public class RHTMLWrapProcessor
{
	public static Wrap getWrapForNode(final ASTNode node, @Nullable final FileViewProvider fileViewProvider)
	{
		if(node == null)
		{
			return Wrap.createWrap(Wrap.NONE, true);
		}

		final IElementType nodeType = node.getElementType();

		if((nodeType == RHTMLElementType.RHTML_XML_TAG || nodeType == RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML) && fileViewProvider != null && RHTMLFormatterUtil.isScripletRHTMLXmlTagNode(node))
		{

			final PsiElement psiElement = fileViewProvider.findElementAt(node.getStartOffset() - 1, StdLanguages.HTML);
			final String text = trimEndSpaces(psiElement != null ? psiElement.getText() : null);
			//for html comments
			final boolean shouldIgnoreIndent = text != null && text.endsWith("\n");
			return Wrap.createWrap(shouldIgnoreIndent ? Wrap.NONE : Wrap.ALWAYS, true);
		}

		if(nodeType instanceof RubyElementType)
		{
			Wrap.createWrap(Wrap.NORMAL, true);
		}

		//RHTMLTokenType.RHTML_SEPARATORS.contains(childNodeType)
		//RHTMLTokenType.OMIT_NEW_LINE
		//HTML elements in some cases (in RHTMLBlockGenerator.createAndAddRHTMLHtml)
		//RHTMLElementType.RHTML_XML_TAG:scriplet
		//RHTMLElementType.RHTML_COMMENT_ELEMENT
		//RHTMLFormatterUtil.isHTMLDocomentRootOrProlog(node)
		return Wrap.createWrap(Wrap.NONE, true);
	}

	@Nullable
	private static String trimEndSpaces(@Nullable String text)
	{
		if(text == null)
		{
			return text;
		}
		int i = text.length() - 1;
		for(; i >= 0; i--)
		{
			if(text.charAt(i) != ' ')
			{
				break;
			}
		}
		if(i >= 0)
		{
			text = text.substring(0, i + 1);
		}
		return text;
	}

	public static Wrap createTagAttributesWrap(final WrapType wrapType)
	{
		return Wrap.createWrap(wrapType, false);
	}

	public static Wrap createTagTextWrap(final WrapType wrapType)
	{
		return Wrap.createWrap(wrapType, true);
	}
}
