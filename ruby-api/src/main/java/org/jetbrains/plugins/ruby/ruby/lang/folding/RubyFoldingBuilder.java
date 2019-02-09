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

package org.jetbrains.plugins.ruby.ruby.lang.folding;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgumentList;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RClassObject;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;

public class RubyFoldingBuilder implements FoldingBuilder
{

	@NonNls
	private static final String CONTAINER_FOLD_TEXT = " ... end";
	@NonNls
	private static final String HEREDOC_FOLD_TEXT = "heredoc ...";
	@NonNls
	private static final String BRACE_CODE_BLOCK_FOLD_TEXT = "{ ... }";
	@NonNls
	private static final String DO_CODE_BLOCK_FOLD_TEXT = "do ... end";
	@NonNls
	private static final String END_MARKER_FOLD_TEXT = "__END__";

	@NonNls
	private static final String IF_STATEMENT_FOLD_TEXT = "if ... end";
	@NonNls
	private static final String UNLESS_STATEMENT_FOLD_TEXT = "unless ... end";
	@NonNls
	private static final String WHILE_STATEMENT_FOLD_TEXT = "while ... end";
	@NonNls
	private static final String BEGIN_END_FOLD_TEXT = "begin ... end";
	@NonNls
	private static final String LBEGIN_FOLD_TEXT = "BEGIN {...}";
	@NonNls
	private static final String LEND_FOLD_TEXT = "END {...}";

	private static final TokenSet COLLAPSED_BY_DEFAULT = TokenSet.create(RubyTokenTypes.tEND_MARKER);

	private static final TokenSet FOLDED_ELEMENTS = TokenSet.create(RubyElementTypes.MODULE, RubyElementTypes.CLASS, RubyElementTypes.OBJECT_CLASS, RubyElementTypes.METHOD, RubyElementTypes.SINGLETON_METHOD,

			RubyElementTypes.HEREDOC_VALUE,

			RubyElementTypes.DO_CODE_BLOCK, RubyElementTypes.BRACE_CODE_BLOCK,

			RubyTokenTypes.tEND_MARKER,

			RubyElementTypes.IF_STATEMENT, RubyElementTypes.UNLESS_STATEMENT, RubyElementTypes.WHILE_STATEMENT, RubyElementTypes.BEGIN_END_BLOCK_STATEMENT, RubyElementTypes.LBEGIN_STATEMENT, RubyElementTypes.LEND_STATEMENT);

	@Override
	@Nonnull
	public FoldingDescriptor[] buildFoldRegions(@Nonnull ASTNode astNode, @Nonnull Document document)
	{
		List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
		gatherDescriptors(astNode, descriptors);
		return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
	}

	private void gatherDescriptors(@Nonnull final ASTNode node, @Nonnull final List<FoldingDescriptor> descriptors)
	{
		final IElementType type = node.getElementType();

		if(FOLDED_ELEMENTS.contains(type))
		{
			descriptors.add(new FoldingDescriptor(node, getFoldingTextRange(node)));
		}


		for(ASTNode child : node.getChildren(null))
		{  //TODO OuterElements
			gatherDescriptors(child, descriptors);
		}
	}

	@Nonnull
	private TextRange getFoldingTextRange(@Nonnull final ASTNode node)
	{
		final PsiElement psiElement = node.getPsi();
		final int endOffset = node.getTextRange().getEndOffset();
		if(psiElement instanceof RContainer)
		{
			// class <<smth
			if(psiElement instanceof RObjectClass)
			{
				final RObjectClass rObjectClass = (RObjectClass) psiElement;
				final RClassObject classObject = rObjectClass.getObject();
				if(classObject != null)
				{
					return new TextRange(getVisibleTextOffset(classObject), endOffset);
				}
				else
				{
					final PsiElement shiftElem = rObjectClass.getChildByFilter(RubyTokenTypes.tLSHFT, 0);
					assert shiftElem != null;
					return new TextRange(shiftElem.getTextRange().getEndOffset(), endOffset);
				}
			}
			// method handling
			if(psiElement instanceof RMethod)
			{
				final RMethod method = (RMethod) psiElement;
				final PsiElement rparen = method.getChildByFilter(RubyTokenTypes.tRPAREN, 0);
				if(rparen != null)
				{
					return new TextRange(getVisibleTextOffset(rparen), endOffset);
				}
				final RArgumentList argumentList = method.getArgumentList();
				if(argumentList != null)
				{
					return new TextRange(getVisibleTextOffset(argumentList), endOffset);
				}
			}
			final RName name = ((RPsiElement) psiElement).getChildByType(RName.class, 0);
			if(name != null)
			{
				return new TextRange(getVisibleTextOffset(name), endOffset);
			}
			else
			{
				// if no name is found, i.e. class ... end
				final PsiElement firstChild = psiElement.getFirstChild();
				assert firstChild != null;
				return new TextRange(node.getStartOffset() + firstChild.getTextLength(), endOffset);
			}
		}
		return node.getTextRange();
	}

	/**
	 * Calculates visible end text offset of psiElement
	 *
	 * @param element psiElement
	 * @return visible text offset
	 */
	private int getVisibleTextOffset(@Nonnull final PsiElement element)
	{
		return element.getTextRange().getStartOffset() + element.getText().trim().length();
	}

	@Override
	@Nullable
	public String getPlaceholderText(@Nonnull final ASTNode node)
	{
		final IElementType type = node.getElementType();
		final PsiElement psiElement = node.getPsi();
		if(psiElement instanceof RContainer)
		{
			return CONTAINER_FOLD_TEXT;
		}
		if(type == RubyElementTypes.HEREDOC_VALUE)
		{
			return HEREDOC_FOLD_TEXT;
		}

		if(type == RubyElementTypes.DO_CODE_BLOCK)
		{
			return DO_CODE_BLOCK_FOLD_TEXT;
		}

		if(type == RubyElementTypes.BRACE_CODE_BLOCK)
		{
			return BRACE_CODE_BLOCK_FOLD_TEXT;
		}

		if(type == RubyTokenTypes.tEND_MARKER)
		{
			return END_MARKER_FOLD_TEXT;
		}

		if(type == RubyElementTypes.IF_STATEMENT)
		{
			return IF_STATEMENT_FOLD_TEXT;
		}

		if(type == RubyElementTypes.UNLESS_STATEMENT)
		{
			return UNLESS_STATEMENT_FOLD_TEXT;
		}

		if(type == RubyElementTypes.WHILE_STATEMENT)
		{
			return WHILE_STATEMENT_FOLD_TEXT;
		}
		if(type == RubyElementTypes.BEGIN_END_BLOCK_STATEMENT)
		{
			return BEGIN_END_FOLD_TEXT;
		}
		if(type == RubyElementTypes.LBEGIN_STATEMENT)
		{
			return LBEGIN_FOLD_TEXT;
		}
		if(type == RubyElementTypes.LEND_STATEMENT)
		{
			return LEND_FOLD_TEXT;
		}
		return null;
	}

	@Override
	public boolean isCollapsedByDefault(@Nonnull ASTNode node)
	{
		return COLLAPSED_BY_DEFAULT.contains(node.getElementType());
	}
}
