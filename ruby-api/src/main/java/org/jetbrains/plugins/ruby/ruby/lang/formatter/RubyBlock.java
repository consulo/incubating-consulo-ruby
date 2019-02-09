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

package org.jetbrains.plugins.ruby.ruby.lang.formatter;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.indent.RIndentCOMPSTMT;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.processors.RubyIndentProcessor;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.processors.RubySpacingProcessor;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.processors.RubyWrapProcessor;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.templateLanguages.OuterLanguageElement;

public class RubyBlock implements Block, RubyTokenTypes
{
	final private ASTNode myNode;
	final private Indent myIndent;
	private Wrap myWrap;

	final private CodeStyleSettings mySettings;

	private List<Block> mySubBlocks = null;
	private TextRange myTextRange;
	private TextRange myPatchedTextRange; //is used only for injected ruby code in template context
	private Alignment myAlignment;

	/**
	 * For ruby injections in templates. (e.g RHTML)
	 *
	 * @param node             AST node
	 * @param indent           indent
	 * @param wrap             wrap
	 * @param alignment        Alignment
	 * @param settings         settigs
	 * @param patchedTextRange patched range or null if patch range isn't necessary
	 */
	public RubyBlock(@Nonnull final ASTNode node, @Nonnull final Indent indent, @Nullable final Wrap wrap, @Nullable final Alignment alignment, final CodeStyleSettings settings, @Nullable final TextRange patchedTextRange)
	{

		myNode = node;
		myIndent = indent;
		myWrap = wrap;
		mySettings = settings;
		myAlignment = alignment;

		final TextRange nodeTextRange = node.getTextRange();

		myPatchedTextRange = patchedTextRange;
		if(myPatchedTextRange != null)
		{
			myTextRange = nodeTextRange.intersection(patchedTextRange);
		}
		else
		{
			myTextRange = nodeTextRange;
		}
		if(myTextRange == null)
		{
			myTextRange = new TextRange(0, -1);
		}
	}

	public RubyBlock(@Nonnull final ASTNode node, @Nonnull final Indent indent, @Nullable final Wrap wrap, final CodeStyleSettings settings)
	{
		this(node, indent, wrap, null, settings, null);
	}


	@Nonnull
	public ASTNode getNode()
	{
		return myNode;
	}


	@Override
	@Nonnull
	public TextRange getTextRange()
	{
		return myTextRange;
	}

	@Override
	@Nonnull
	public List<Block> getSubBlocks()
	{
		if(mySubBlocks == null)
		{
			mySubBlocks = generateSubBlocks();
		}
		return mySubBlocks;
	}

	@Override
	@Nullable
	public Wrap getWrap()
	{
		return myWrap;
	}


	@Override
	@Nullable
	public Indent getIndent()
	{
		return myIndent;
	}


	@Override
	@Nullable
	public Alignment getAlignment()
	{
		return myAlignment;
	}

	@Override
	@Nullable
	public Spacing getSpacing(@Nullable final Block child1, @Nonnull final Block child2)
	{
		final ASTNode childNode1 = RHTMLFormatterUtil.getNodeByBlockForRubyFormatter(child1);
		final ASTNode childNode2 = RHTMLFormatterUtil.getNodeByBlockForRubyFormatter(child2);
		return RubySpacingProcessor.getSpacing(childNode1, childNode2, mySettings);
	}


	@Override
	@Nonnull
	public ChildAttributes getChildAttributes(final int newChildIndex)
	{
		final PsiElement psiParent = getNode().getPsi();

		// RFile
		if(psiParent instanceof RFile)
		{
			return new ChildAttributes(Indent.getNoneIndent(), null);
		}


		// Normal indent handling
		if(psiParent instanceof RIndentCOMPSTMT || psiParent instanceof RContainer)
		{
			return new ChildAttributes(Indent.getNormalIndent(), null);
		}

		// after BNF.tCONTINAUTION_INDENT continuation Indent needed
		if(newChildIndex > 0)
		{
			ASTNode node = getNode().getChildren(null)[newChildIndex];
			if(BNF.tCONTINUATION_INDENT.contains(node.getElementType()))
			{
				return new ChildAttributes(Indent.getContinuationIndent(), null);
			}
		}
		// No indent by default
		return new ChildAttributes(Indent.getNoneIndent(), null);
	}

	@Override
	public boolean isIncomplete()
	{
		return isIncomplete(myNode);
	}


	@Override
	public boolean isLeaf()
	{
		return isLeaf(myNode);
	}

	private boolean isLeaf(@Nonnull final ASTNode node)
	{
		return node.getFirstChildNode() == null;
	}

	private List<Block> generateSubBlocks()
	{
		final ArrayList<Block> subBlocks = new ArrayList<Block>();

		// Get all the children with null filter
		ASTNode children[] = myNode.getChildren(null);
		int childNumber = children.length;
		if(childNumber == 0)
		{
			return subBlocks;
		}

		final int patchedEndOffset = myPatchedTextRange == null ? -1 : myPatchedTextRange.getEndOffset();

		ASTNode prevChildNode = null;
		for(int i = 0; i < childNumber; i++)
		{
			final ASTNode childNode = children[i];
			if(canBeCorrectBlock(childNode))
			{
				if(myPatchedTextRange != null && childNode.getStartOffset() >= patchedEndOffset)
				{
					return subBlocks;
				}

				final Wrap wrap = RubyWrapProcessor.getChildWrap(this, childNode, childNumber, i);
				final Indent indent = RubyIndentProcessor.getChildIndent(this, prevChildNode, childNode);
				final RubyBlock rubyBlock = new RubyBlock(childNode, indent, wrap, null, mySettings, myPatchedTextRange);
				if(rubyBlock.getTextRange().getLength() > 0)
				{
					subBlocks.add(rubyBlock);
				}
				prevChildNode = childNode;
			}
		}
		return subBlocks;
	}


	/**
	 * @param node Tree node
	 * @return true if node is incomplete
	 */
	private static boolean isIncomplete(@Nonnull final ASTNode node)
	{
		ASTNode lastChild = node.getLastChildNode();
		while(lastChild != null && (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment))
		{
			lastChild = lastChild.getTreePrev();
		}
		if(lastChild == null)
		{
			return false;
		}

		//noinspection SimplifiableIfStatement
		if(lastChild.getPsi() instanceof PsiErrorElement)
		{
			return true;
		}
		return isIncomplete(lastChild);
	}

	/**
	 * @param node Tree node
	 * @return true, if the current node can be block node, else otherwise
	 */
	private static boolean canBeCorrectBlock(@Nonnull final ASTNode node)
	{
		return (node.getText().trim().length() > 0) && (!(node.getPsi() instanceof OuterLanguageElement));
	}

	public String toString()
	{
		return "Block at: " + myNode.toString();
	}
}
