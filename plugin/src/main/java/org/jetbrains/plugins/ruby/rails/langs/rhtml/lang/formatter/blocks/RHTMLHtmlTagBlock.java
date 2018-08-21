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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLBlockGenerator;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLIndentProcessor;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLWrapProcessor;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo.NodeInfo;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo.TemplateNodeInfo;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.OuterElementInRHTMLOrRubyLang;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLElement;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.ChildAttributes;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Spacing;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.formatter.xml.AbstractSyntheticBlock;
import com.intellij.psi.formatter.xml.SyntheticBlock;
import com.intellij.psi.formatter.xml.XmlBlock;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.formatter.xml.XmlTagBlock;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlElementType;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlTokenType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 17, 2007
 */
public class RHTMLHtmlTagBlock extends XmlTagBlock
{
	private static final Logger LOG = Logger.getInstance(RHTMLHtmlTagBlock.class.getName());

	private TextRange myTextRange;
	private FileViewProvider myViewProvider;
	private RCompoundStatement myNodeCmpSt;

	public static ASTNode getNextHtmlChild(@NotNull final NodeInfo childNodeInfo, final TextRange textRange)
	{
		ASTNode child;
		child = NodeInfo.getNextNodeByInfo(childNodeInfo);
		if(child instanceof OuterElementInRHTMLOrRubyLang)
		{
			final FileViewProvider vProvider = child.getPsi().getContainingFile().getViewProvider();
			final PsiElement htmlProjection = RHTMLFormatterUtil.findUpperHTMLElement(vProvider, child.getStartOffset(), textRange);
			child = htmlProjection == null ? null : htmlProjection.getNode();
		}
		return child;
	}

	public RHTMLHtmlTagBlock(ASTNode node, Wrap wrap, Alignment alignment, XmlFormattingPolicy policy, Indent indent, final FileViewProvider viewProvider, @Nullable TextRange textRange)
	{
		super(node, wrap, alignment, policy, indent);
		myTextRange = textRange == null ? myNode.getTextRange() : textRange;
		myViewProvider = viewProvider;

		myNodeCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(myViewProvider, myNode);
	}

	@Override
	@NotNull
	public ChildAttributes getChildAttributes(final int newChildIndex)
	{
		final XmlTag tag = getTag();
		if(tag != null && myXmlFormattingPolicy.indentChildrenOf(tag))
		{
			return new ChildAttributes(getTagContentChildIndent(newChildIndex), null);
		}
		else
		{
			return new ChildAttributes(Indent.getNoneIndent(), null);
		}
	}

	@Override
	@NotNull
	public TextRange getTextRange()
	{
		return myTextRange;
	}


	@NotNull
	protected NodeInfo processChildAndGetNext(final List<Block> result, final ASTNode htmlChild, final NodeInfo childNodeInfo, final Wrap wrap, final Alignment alignment, final Indent rhtmlIndent)
	{
		if(htmlChild.getElementType() == XmlElementType.XML_DOCTYPE)
		{
			result.add(new XmlBlock(htmlChild, wrap, alignment, myXmlFormattingPolicy, rhtmlIndent, null)
			{
				@Override
				protected Wrap getDefaultWrap(final ASTNode node)
				{
					final IElementType type = node.getElementType();
					final Wrap wrap = RHTMLWrapProcessor.createTagAttributesWrap(getWrapType(RHTMLHtmlTagBlock.this.myXmlFormattingPolicy.getAttributesWrap()));
					return type == XmlElementType.XML_ATTRIBUTE_VALUE_TOKEN ? wrap : null;
				}
			});
			return NodeInfo.createRHTMLInfo(myNode, htmlChild, htmlChild.getTreeNext());
		}

		final Language language = htmlChild.getElementType().getLanguage();
		if(language == RubyLanguage.INSTANCE || htmlChild.getElementType() instanceof IRHTMLElement || htmlChild instanceof XmlTag || htmlChild instanceof XmlText || htmlChild instanceof CompositeElement)
		{

			//if we should continue template processing
			if(childNodeInfo.isTemplate())
			{
				final ASTNode childNode = ((TemplateNodeInfo) childNodeInfo).getTemplateDataNode();
				return RHTMLBlockGenerator.processContentChildNode(childNode, childNodeInfo, myNode, result, myNodeCmpSt, myXmlFormattingPolicy, myViewProvider, getTextRange(), alignment, rhtmlIndent);
			}

			final int childStartOffset = htmlChild.getStartOffset();
			final PsiElement rhtmlProjection = RHTMLFormatterUtil.findRHTMLElementByStartOffset(myViewProvider, childStartOffset, true);
			LOG.assertTrue(rhtmlProjection != null);
			final ASTNode rhtmlNode = rhtmlProjection.getNode();

			//noinspection ConstantConditions
			final int rhtmlEnd = rhtmlNode.getTextRange().getEndOffset();
			final NodeInfo info = (rhtmlNode.getElementType() == RHTMLElementType.RHTML_XML_TAG) ? NodeInfo.createRHTMLInfo(myNode, null, rhtmlNode) : NodeInfo.createTemplateInfo(myNode, rhtmlNode, rhtmlNode, new TextRange(childStartOffset, rhtmlEnd));

			return RHTMLBlockGenerator.processContentChildNode(rhtmlNode, info, myNode, result, myNodeCmpSt, myXmlFormattingPolicy, myViewProvider, getTextRange(), alignment, rhtmlIndent);
		}
		else
		{
			result.add(new RHTMLHtmlBlock(htmlChild, wrap, alignment, myXmlFormattingPolicy, rhtmlIndent, myViewProvider, htmlChild.getTextRange()));
			return getNextHTMLChildInfo(htmlChild);
		}
	}

	@Override
	@SuppressWarnings({"EmptyMethod"})
	@Nullable
	protected XmlTag getTag()
	{
		return super.getTag();
	}

	@Override
	protected List<Block> buildChildren()
	{
		ChameleonTransforming.transformChildren(myNode);

		ASTNode child = myNode.getFirstChildNode();
		NodeInfo childNodeInfo = NodeInfo.createRHTMLInfo(myNode, null, child);
		//Is using for create syntetics block for childs from the same compoundStatement.
		//It is for correct working of getChildAttributes
		RCompoundStatement prevChildRCmpSt = myNodeCmpSt;

		final Wrap attrWrap = RHTMLWrapProcessor.createTagAttributesWrap(getWrapType(myXmlFormattingPolicy.getAttributesWrap()));
		final XmlTag tag = getTag();
		final Wrap textWrap = RHTMLWrapProcessor.createTagTextWrap(getWrapType(myXmlFormattingPolicy.getTextWrap(tag)));
		final Wrap tagBeginWrap = createTagBeginWrapping(tag);

		final Alignment attrAlignment = Alignment.createAlignment();
		final Alignment textAlignment = Alignment.createAlignment();
		final ArrayList<Block> result = new ArrayList<Block>(3);
		ArrayList<Block> localResult = new ArrayList<Block>(1);

		boolean insideTag = true;
		while(child != null)
		{
			//This check doesn't work on ruby and rails comments whitespaces(only html)
			//but here such elements can't be met
			if(!FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0)
			{

				final Wrap wrap = chooseWrap(child, tagBeginWrap, attrWrap, textWrap);
				final Alignment alignment = chooseAlignment(child, attrAlignment, textAlignment, insideTag);
				final RCompoundStatement childRCmpSt =  //CmpSt for child start offset
						RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(myViewProvider, child);

				final IElementType childType = child.getElementType();
				if(childType == XmlElementType.XML_TAG_END)
				{
					// ..>
					childNodeInfo = processChildAndGetNext(localResult, child, childNodeInfo, wrap, alignment, calcTagContentIndent(insideTag, childRCmpSt));
					if(isDirectAndNotHiddenChild(child, childRCmpSt))
					{
						result.add(createTagDescriptionNode(localResult));
						localResult = new ArrayList<Block>(1);
						insideTag = true;
					}
				}
				else if(childType == XmlElementType.XML_START_TAG_START)
				{
					// <...
					if(isDirectAndNotHiddenChild(child, childRCmpSt))
					{
						if(!localResult.isEmpty())
						{
							result.add(createTagContentNode(localResult, calcTagContentIndent(insideTag, prevChildRCmpSt)));
							localResult = new ArrayList<Block>(1);
						}
						insideTag = false;
					}
					else
					{
						//replace child with parent XmlTag Node if it is start of another tag
						final PsiElement htmlProjection = RHTMLFormatterUtil.findUpperHTMLElement(myViewProvider, child.getStartOffset(), getTextRange());
						child = htmlProjection == null ? null : htmlProjection.getNode();
					}

					final Indent rhtmlIndent = calcTagContentIndent(insideTag, childRCmpSt);
					childNodeInfo = processChildAndGetNext(localResult, child, childNodeInfo, wrap, alignment, rhtmlIndent);
				}
				else if(childType == XmlElementType.XML_END_TAG_START)
				{
					// </...
					if(isDirectAndNotHiddenChild(child, childRCmpSt))
					{
						if(!localResult.isEmpty())
						{
							result.add(createTagContentNode(localResult, calcTagContentIndent(insideTag, prevChildRCmpSt)));
							localResult = new ArrayList<Block>(1);
						}
						insideTag = false;
					}
					final Indent rhtmlIndent = calcTagContentIndent(insideTag, childRCmpSt);
					childNodeInfo = processChildAndGetNext(localResult, child, childNodeInfo, wrap, alignment, rhtmlIndent);
				}
				else if(childType == XmlElementType.XML_EMPTY_ELEMENT_END)
				{
					childNodeInfo = processChildAndGetNext(localResult, child, childNodeInfo, wrap, alignment, calcTagContentIndent(insideTag, childRCmpSt));
					if(isDirectAndNotHiddenChild(child, childRCmpSt))
					{
						result.add(createTagDescriptionNode(localResult));
						localResult = new ArrayList<Block>(1);
					}
				}
				else
				{
					if(prevChildRCmpSt != childRCmpSt)
					{
						if(!localResult.isEmpty())
						{
							result.add(createTagContentNode(localResult, calcTagContentIndent(insideTag, prevChildRCmpSt)));
							localResult = new ArrayList<Block>(1);
						}
						prevChildRCmpSt = childRCmpSt;
					}
					childNodeInfo = processChildAndGetNext(localResult, child, childNodeInfo, wrap, alignment, calcTagContentIndent(insideTag, childRCmpSt));
				}
			}
			else
			{
				//next child info
				childNodeInfo = getNextHTMLChildInfo(child);
			}
			//next child
			child = getNextHtmlChild(childNodeInfo, getTextRange());
		}

		if(!localResult.isEmpty())
		{
			result.add(createTagContentNode(localResult, calcTagContentIndent(insideTag, prevChildRCmpSt)));
		}

		return result;
	}

	@Nullable
	protected Indent getTagContentChildIndent(final int newChildIndex)
	{
		final List<Block> subBlocks = getSubBlocks();
		if(newChildIndex != 0 && newChildIndex < subBlocks.size())
		{
			final Block block = subBlocks.get(newChildIndex);

			if(block instanceof SyntheticBlock)
			{
				final Block curFirstSubBlock = block.getSubBlocks().get(0);
				if(curFirstSubBlock instanceof AbstractBlock)
				{
					// if is tag closing block
					final ASTNode curFirstSubBlockNode = ((AbstractBlock) curFirstSubBlock).getNode();
					final IElementType curFirstSubBlockType = curFirstSubBlockNode.getElementType();

					if(newChildIndex == subBlocks.size() - 1 && curFirstSubBlockType == XmlTokenType.XML_END_TAG_START)
					{

						final Block prev = subBlocks.get(newChildIndex - 1);
						if(prev instanceof SyntheticBlock)
						{
							final List<Block> prevSubBlocks = prev.getSubBlocks();
							final Block prevLastSubBlock = prevSubBlocks.get(prevSubBlocks.size() - 1);
							if(prevLastSubBlock instanceof AbstractBlock)
							{
								//for  "<a></a>" case
								final ASTNode prevLastSubBlockNode = ((AbstractBlock) prevLastSubBlock).getNode();
								if(prevLastSubBlockNode.getElementType() == XmlTokenType.XML_TAG_END)
								{
									return RHTMLIndentProcessor.calcHTMLIndentForChild(prevLastSubBlockNode.getTreeParent(), myXmlFormattingPolicy);
								}
							}
							//other
							return prev.getChildAttributes(prev.getSubBlocks().size() - 1).getChildIndent();
						}
					}
					else if(newChildIndex > 0 && curFirstSubBlock instanceof RHTMLRubyInjectionBlock)
					{
						// next element is rhtml xml tag, i.e <% ... %> or <%= .. %>
						// lets check id this tag is  close part of some ruby compound statement (eg <% end %>  or <% else %>)

						final RCompoundStatement firstSubBlockCmpst = RHTMLFormatterUtil.getRCmpStNodeStartOffset(myViewProvider, curFirstSubBlockNode);
						if(firstSubBlockCmpst != null)
						{
							if(RHTMLFormatterUtil.isRHTMLXmlTagForRubyBlockEnd(curFirstSubBlockNode, firstSubBlockCmpst, myViewProvider))
							{

								final RCompoundStatement parentCmpSt = ((RHTMLRubyInjectionBlock) curFirstSubBlock).getParentRCmpSt();
								final ASTNode parentNode = ((RHTMLRubyInjectionBlock) curFirstSubBlock).getParentNode();

								return RHTMLIndentProcessor.calcRHTMLIndentForChild(firstSubBlockCmpst, parentCmpSt, parentNode, myXmlFormattingPolicy, true);
							}
						}
					}
				}
				return block.getChildAttributes(newChildIndex).getChildIndent();
			}

			final ASTNode node = block instanceof AbstractBlock ? ((AbstractBlock) block).getNode() : null;

			if(node != null)
			{
				final RCompoundStatement childCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(myViewProvider, node);
				return getChildrenIndent(childCmpSt);
			}
		}
		return Indent.getNormalIndent();
	}

	@Override
	public Spacing getSpacing(Block child1, Block child2)
	{
		final XmlTag tag = getTag();
		if(tag == null)
		{
			return createDefaultSpace(true, true);
		}

		//TODO
		final AbstractSyntheticBlock syntheticBlock1 = ((AbstractSyntheticBlock) child1);
		final AbstractSyntheticBlock syntheticBlock2 = ((AbstractSyntheticBlock) child2);

		if(syntheticBlock2.startsWithCDATA() || syntheticBlock1.endsWithCDATA())
		{
			return Spacing.getReadOnlySpacing();
		}

		//        if (syntheticBlock2.isJspTextBlock() || syntheticBlock1.isJspTextBlock()) {
		//            return Spacing.createSafeSpacing(myXmlFormattingPolicy.getShouldKeepLineBreaks(), myXmlFormattingPolicy.getKeepBlankLines());
		//        }
		//        if (syntheticBlock2.isJspxTextBlock() || syntheticBlock1.isJspxTextBlock()) {
		//            return Spacing.createSpacing(0, 0, 1, myXmlFormattingPolicy.getShouldKeepLineBreaks(), myXmlFormattingPolicy.getKeepBlankLines());
		//        }


		if(myXmlFormattingPolicy.keepWhiteSpacesInsideTag(tag))
		{
			return Spacing.getReadOnlySpacing();
		}

		if(myXmlFormattingPolicy.getShouldKeepWhiteSpaces())
		{
			return Spacing.getReadOnlySpacing();
		}

		if(syntheticBlock2.startsWithTag())
		{
			final XmlTag startTag = syntheticBlock2.getStartTag();
			if(myXmlFormattingPolicy.keepWhiteSpacesInsideTag(startTag) && startTag.textContains('\n'))
			{
				return getChildrenIndent(null) != Indent.getNoneIndent() ? Spacing.getReadOnlySpacing() : Spacing.createSpacing(0, 0, 0, true, myXmlFormattingPolicy.getKeepBlankLines());
			}
		}

		boolean saveSpacesBetweenTagAndText = myXmlFormattingPolicy.shouldSaveSpacesBetweenTagAndText() && syntheticBlock1.getTextRange().getEndOffset() < syntheticBlock2.getTextRange().getStartOffset();

		if(syntheticBlock1.endsWithTextElement() && syntheticBlock2.startsWithTextElement())
		{
			return Spacing.createSafeSpacing(myXmlFormattingPolicy.getShouldKeepLineBreaksInText(), myXmlFormattingPolicy.getKeepBlankLines());
		}

		if(syntheticBlock1.endsWithText())
		{ //text</tag
			if(syntheticBlock1.insertLineFeedAfter())
			{
				return Spacing.createDependentLFSpacing(0, 0, tag.getTextRange(), myXmlFormattingPolicy.getShouldKeepLineBreaks(), myXmlFormattingPolicy.getKeepBlankLines());
			}
			if(saveSpacesBetweenTagAndText)
			{
				return Spacing.createSafeSpacing(myXmlFormattingPolicy.getShouldKeepLineBreaks(), myXmlFormattingPolicy.getKeepBlankLines());
			}
			return Spacing.createSpacing(0, 0, 0, myXmlFormattingPolicy.getShouldKeepLineBreaks(), myXmlFormattingPolicy.getKeepBlankLines());

		}
		else if(syntheticBlock1.isTagDescription() && syntheticBlock2.isTagDescription())
		{ //></
			return Spacing.createSpacing(0, 0, 0, myXmlFormattingPolicy.getShouldKeepLineBreaks(), myXmlFormattingPolicy.getKeepBlankLines());
		}
		else if(syntheticBlock2.startsWithText())
		{ //>text
			if(saveSpacesBetweenTagAndText)
			{
				return Spacing.createSafeSpacing(true, myXmlFormattingPolicy.getKeepBlankLines());
			}
			return Spacing.createSpacing(0, 0, 0, true, myXmlFormattingPolicy.getKeepBlankLines());
		}
		else if(syntheticBlock1.isTagDescription() && syntheticBlock2.startsWithTag())
		{
			return Spacing.createSpacing(0, 0, 0, true, myXmlFormattingPolicy.getKeepBlankLines());
		}
		else if(syntheticBlock1.insertLineFeedAfter())
		{
			return Spacing.createSpacing(0, 0, 1, true, myXmlFormattingPolicy.getKeepBlankLines());
		}
		else if(syntheticBlock1.endsWithTag() && syntheticBlock2.isTagDescription())
		{
			return Spacing.createSpacing(0, 0, 0, true, myXmlFormattingPolicy.getKeepBlankLines());
		}
		else
		{
			return createDefaultSpace(true, true);
		}

	}

	/**
	 * RHTML indent for tag content. It is sum of html indent in tag and ruby indent.
	 *
	 * @param insideTag   Is inside tag
	 * @param childRCmpSt Ruby compound statement, that wraps child
	 * @return indent
	 */
	@NotNull
	private Indent calcTagContentIndent(boolean insideTag, @Nullable final RCompoundStatement childRCmpSt)
	{
		Indent indent;
		if(!insideTag)
		{
			indent = Indent.getNoneIndent();
		}
		else
		{
			indent = getChildrenIndent(childRCmpSt);
		}
		return indent;
	}

	private Alignment chooseAlignment(final ASTNode child, final Alignment attrAlignment, final Alignment textAlignment, final boolean insideTag)
	{

		if(!insideTag && child.getElementType() == RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML)
		{
			if(child.getTreeParent() == myNode)
			{
				return attrAlignment;
			}
		}
		return chooseAlignment(child, attrAlignment, textAlignment);
	}

	private Block createTagDescriptionNode(final ArrayList<Block> localResult)
	{
		return new SyntheticBlock(localResult, this, Indent.getNoneIndent(), myXmlFormattingPolicy, null);
	}

	private Block createTagContentNode(@NotNull final ArrayList<Block> localResult, final Indent childIndent)
	{
		return new SyntheticBlock(localResult, this, Indent.getNoneIndent(), myXmlFormattingPolicy, childIndent);
	}

	private NodeInfo getNextHTMLChildInfo(final ASTNode child)
	{
		ASTNode nextChild = child.getTreeNext();
		if(nextChild == null)
		{
			final int childEnd = child.getTextRange().getEndOffset();
			final PsiElement htmlElement = RHTMLFormatterUtil.findUpperHTMLElement(myViewProvider, childEnd, getTextRange());
			nextChild = htmlElement != null ? htmlElement.getNode() : null;
		}

		//go down for the lowest leaf
		if(ifNextHtmlChildOutOfParentBounds(nextChild))
		{
			if(nextChild instanceof CompositeElement)
			{
				nextChild = nextChild.getFirstChildNode();
				while(nextChild instanceof CompositeElement)
				{
					if(!ifNextHtmlChildOutOfParentBounds(nextChild))
					{
						break;
					}
					nextChild = nextChild.getFirstChildNode();
				}
			}
			else
			{
				//finishing...
				nextChild = null;
			}
		}
		return NodeInfo.createRHTMLInfo(myNode, child, nextChild);
	}

	@NotNull
	private Indent getChildrenIndent(@Nullable final RCompoundStatement childRCmpSt)
	{
		if(childRCmpSt == null)
		{
			return RHTMLIndentProcessor.calcHTMLIndentForChild(myNode, myXmlFormattingPolicy);
		}
		return RHTMLIndentProcessor.calcRHTMLIndentForChild(childRCmpSt, myNodeCmpSt, myNode, myXmlFormattingPolicy, true);
	}

	private boolean isDirectAndNotHiddenChild(ASTNode child, RCompoundStatement childRCmpSt)
	{
		return child.getTreeParent() == myNode && childRCmpSt == myNodeCmpSt;
	}

	private boolean ifNextHtmlChildOutOfParentBounds(ASTNode nextChild)
	{
		return nextChild != null && !RHTMLFormatterUtil.containsTextRange(getTextRange(), nextChild.getTextRange());
	}

	//    public boolean insertLineBreakBeforeTag() {
	//      return myXmlFormattingPolicy.insertLineBreakBeforeTag(getTag());
	//    }
	//
	//    public boolean removeLineBreakBeforeTag() {
	//      return myXmlFormattingPolicy.removeLineBreakBeforeTag(getTag());
	//    }
	//
	//    public boolean isTextElement() {
	//      return myXmlFormattingPolicy.isTextElement(getTag());
	//    }

}
