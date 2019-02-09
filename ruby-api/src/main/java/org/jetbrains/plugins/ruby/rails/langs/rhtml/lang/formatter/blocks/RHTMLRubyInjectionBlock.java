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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLWrapProcessor;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.RubyBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 21, 2007
 */
public class RHTMLRubyInjectionBlock extends RHTMLBlock
{
	private final Indent myCodeIndent;
	private RCompoundStatement myParentRCmpSt;
	private ASTNode myParentNode;

	public RHTMLRubyInjectionBlock(@Nonnull final ASTNode node, @Nullable final Indent indent, @Nullable final Wrap wrap, @Nonnull final XmlFormattingPolicy xmlFormattingPolicy, @Nullable final Alignment alignment, @Nullable final ASTNode parentNode, @Nullable final RCompoundStatement parentRCmpSt)
	{
		super(node, indent, wrap, xmlFormattingPolicy, alignment);

		//indent
		myCodeIndent = calcIndent(getNode());

		myParentRCmpSt = parentRCmpSt;
		myParentNode = parentNode;
	}

	@Override
	@Nonnull
	protected List<Block> buildChildren()
	{
		ChameleonTransforming.transformChildren(myNode);

		final ArrayList<Block> result = new ArrayList<Block>(3);

		final Alignment alignment = Alignment.createAlignment();

		//chilsrent
		ASTNode childNode = myNode.getFirstChildNode();
		while(childNode != null)
		{
			if(RHTMLFormatterUtil.canBeCorrectBlock(childNode))
			{
				final IElementType childNodeType = childNode.getElementType();
				if(RHTMLTokenType.RHTML_SEPARATORS.contains(childNodeType) || childNodeType == RHTMLTokenType.OMIT_NEW_LINE)
				{
					final Wrap wrap = RHTMLWrapProcessor.getWrapForNode(childNode, null);
					result.add(new RHTMLBlock(childNode, Indent.getNoneIndent(), wrap, myXmlFormattingPolicy, null));
				}
				else if(RHTMLTokenType.RUBY_CODE_CHARACTERS == childNodeType)
				{
					//ruby code (injection)
					processRubyCodeCharacters(result, childNode, myXmlFormattingPolicy, alignment, myCodeIndent);
				}

			}
			childNode = childNode.getTreeNext();
		}
		return result;
	}

	private Indent calcIndent(@Nonnull final ASTNode node)
	{
		final ASTNode nodeStart = node.getFirstChildNode();

		Indent indent;
		if(nodeStart != null && RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(nodeStart.getElementType()))
		{
			final IElementType nodeType = nodeStart.getElementType();
			if(nodeType == RHTMLTokenType.RHTML_SCRIPTLET_START)
			{
				// "<% "
				final ASTNode nodeStartNext = nodeStart.getTreeNext();
				if(nodeStartNext != null && nodeStartNext.getElementType() == RHTMLTokenType.OMIT_NEW_LINE)
				{
					// "<%- "
					indent = Indent.getSpaceIndent(4);
				}
				else
				{
					indent = Indent.getSpaceIndent(3);
				}
			}
			else
			{
				// "<%= "
				indent = Indent.getSpaceIndent(4);
			}
		}
		else
		{
			indent = Indent.getNormalIndent();
		}
		return indent;
	}

	@Override
	@Nonnull
	public ChildAttributes getChildAttributes(int newChildIndex)
	{
		return new ChildAttributes(myCodeIndent, null);
	}

	public static void processRubyCodeCharacters(final List<Block> subBlocks, final ASTNode node, final XmlFormattingPolicy xmlPolicy, final Alignment childrenAlignment, final Indent childrenIndent)
	{

		final PsiElement nodePsi = node.getPsi();

		final int startOffset = node.getStartOffset();
		final int endOffset = node.getTextRange().getEndOffset();

		final FileViewProvider viewProvider = nodePsi.getContainingFile().getViewProvider();

		PsiElement curRuby = viewProvider.findElementAt(startOffset);
		while(curRuby != null && curRuby.getParent() != null && curRuby.getParent().getTextRange().getStartOffset() == startOffset)
		{
			curRuby = curRuby.getParent();
		}

		//create blocks for ruby code
		while(curRuby != null && curRuby.getTextRange().getStartOffset() < endOffset)
		{
			final ASTNode currRubyNode = curRuby.getNode();

			if(currRubyNode != null && RHTMLFormatterUtil.canBeCorrectBlock(currRubyNode))
			{
				final TextRange curRubyTRange = curRuby.getTextRange();

				final boolean elementExceedUpperBounds = curRubyTRange.getStartOffset() < startOffset;
				final boolean elementExceedBelowBounds = curRubyTRange.getEndOffset() > endOffset;

				final TextRange patchedTextRange;
				if(elementExceedBelowBounds || elementExceedUpperBounds)
				{
					final int curRubyStart = Math.max(curRubyTRange.getStartOffset(), startOffset);
					final int curRubyEnd = Math.min(curRubyTRange.getEndOffset(), endOffset);
					patchedTextRange = new TextRange(curRubyStart, curRubyEnd);
				}
				else
				{
					patchedTextRange = null;
				}

				final Wrap wrap = RHTMLWrapProcessor.getWrapForNode(currRubyNode, viewProvider);
				final RubyBlock rubyBlock = new RubyBlock(currRubyNode, childrenIndent, wrap, childrenAlignment, xmlPolicy.getSettings(), patchedTextRange);
				if(rubyBlock.getTextRange().getLength() > 0)
				{
					subBlocks.add(rubyBlock);
				}

				if(elementExceedBelowBounds)
				{
					return;
				}
			}
			PsiElement nextRuby = curRuby.getNextSibling();
			if(nextRuby == null)
			{
				PsiElement curParent = curRuby.getParent();
				while(nextRuby == null && curParent != null && !(curParent instanceof PsiFile))
				{
					nextRuby = curParent.getNextSibling();
					curParent = curParent.getParent();
				}

				if(nextRuby != null)
				{
					final ASTNode nextNode = nextRuby.getNode();
					if(nextNode == null || nextNode.getStartOffset() >= endOffset)
					{
						nextRuby = null;
					}
				}
			}
			curRuby = nextRuby;
		}
	}

	@Nullable
	public RCompoundStatement getParentRCmpSt()
	{
		return myParentRCmpSt;
	}

	@Nullable
	public ASTNode getParentNode()
	{
		return myParentNode;
	}
}