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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RCompoundStatementNavigator;
import com.intellij.formatting.Indent;
import com.intellij.ide.highlighter.XmlFileType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.xml.XmlTag;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 1, 2007
 */
public class RHTMLIndentProcessor
{
	@NotNull
	public static Indent calcHTMLIndentForChild(@NotNull final ASTNode parentNode, @NotNull final XmlFormattingPolicy fPolicy)
	{
		return Indent.getSpaceIndent(calcHTMLIndentSize(parentNode, fPolicy));
	}

	@NotNull
	public static Indent calcRHTMLIndentForChild(@Nullable final RCompoundStatement childRCmpSt, @Nullable final RCompoundStatement parentRCmpSt, @Nullable final ASTNode parentNode, @NotNull final XmlFormattingPolicy fPolicy, final boolean addHtmlIndent)
	{

		int indent_size = 0;
		if(addHtmlIndent && parentNode != null)
		{
			indent_size = calcHTMLIndentSize(parentNode, fPolicy);
		}
		indent_size += calcRubyIndentSize(childRCmpSt, parentRCmpSt, fPolicy.getSettings());

		return Indent.getSpaceIndent(indent_size);
	}

	public static Indent calcRubyIndentForNode(@Nullable final RCompoundStatement childRCmpSt, @Nullable final RCompoundStatement parentRCmpSt, @NotNull final CodeStyleSettings settings)
	{

		return Indent.getSpaceIndent(calcRubyIndentSize(childRCmpSt, parentRCmpSt, settings));
	}

	public static int calcHTMLIndentSize(@NotNull final ASTNode parentNode, final XmlFormattingPolicy fPolicy)
	{
		final PsiElement parentPsi = parentNode.getPsi();
		if(parentPsi instanceof XmlTag)
		{
			if(!fPolicy.indentChildrenOf((XmlTag) parentPsi))
			{
				return 0;
			}
		}
		else
		{
			if(RHTMLFormatterUtil.isHTMLDocomentRootOrProlog(parentNode))
			{
				return 0;
			}
		}
		return getHTMLIndentSize(fPolicy);
	}

	public static int calcRubyIndentSize(@Nullable final RCompoundStatement childRCmpSt, @Nullable final RCompoundStatement parentRCmpSt, @NotNull final CodeStyleSettings settings)
	{

		int indent_count = 0;
		PsiElement current = childRCmpSt;
		while(current != null && current != parentRCmpSt)
		{
			indent_count++;
			current = RCompoundStatementNavigator.getParentCompoundStatement(current);
		}

		return getRHTMLIndentSize(settings, indent_count);
	}

	public static int getRHTMLIndentSize(CodeStyleSettings settings, int indent_count)
	{
		return indent_count * settings.OTHER_INDENT_OPTIONS.INDENT_SIZE;
	}

	public static int getHTMLIndentSize(XmlFormattingPolicy fPolicy)
	{
		return fPolicy.getSettings().getIndentSize(XmlFileType.INSTANCE);
	}

	public static Indent getRTHMLIndent(@NotNull final CodeStyleSettings settings, final int count)
	{
		return Indent.getSpaceIndent(getRHTMLIndentSize(settings, count));
	}
}
