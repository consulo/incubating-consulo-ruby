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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.ForeignLanguageBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLCommentBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLHtmlBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLHtmlTagBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.blocks.RHTMLRubyInjectionBlock;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo.NodeInfo;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo.RHTMLNodeInfo;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTag;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTagNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.support.utils.DebugUtil;
import com.intellij.formatting.Alignment;
import com.intellij.formatting.Block;
import com.intellij.formatting.FormattingModel;
import com.intellij.formatting.FormattingModelBuilder;
import com.intellij.formatting.Indent;
import com.intellij.formatting.Wrap;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageFormatting;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTag;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 11, 2007
 */
public class RHTMLBlockGenerator
{
	private static final Logger LOG = Logger.getInstance(RHTMLBlockGenerator.class.getName());

	protected static final List<Block> EMPTY = Collections.unmodifiableList(new ArrayList<Block>(0));

	public static List<Block> createRHTMLSubBlocks(@Nonnull final ASTNode node, final XmlFormattingPolicy xmlPolicy, final RHTMLBlock rhtmlBlock, @Nonnull final FileViewProvider provider)
	{
		final TextRange nodeBoundsTRange = rhtmlBlock.getTextRange();

		if(node instanceof CompositeElement)
		{
			ChameleonTransforming.transformChildren(node);

			final RCompoundStatement nodeRCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(provider, node);

			final ArrayList<Block> subBlocks = new ArrayList<Block>(5);
			ASTNode childNode = node.getFirstChildNode();
			NodeInfo childNodeInfo = NodeInfo.createRHTMLInfo(node, null, childNode);
			while(childNode != null)
			{
				if(RHTMLFormatterUtil.canBeCorrectBlock(childNode))
				{
					childNodeInfo = processContentChildNode(childNode, childNodeInfo, node, subBlocks, nodeRCmpSt, xmlPolicy, provider, nodeBoundsTRange, null, null);
				}
				else
				{
					childNodeInfo = NodeInfo.createRHTMLInfo(node, childNode, childNode.getTreeNext());
				}
				childNode = NodeInfo.getNextRHTMLNodeByInfo(childNodeInfo);
			}
			return subBlocks;
		}
		return EMPTY;
	}

	@Nonnull
	public static NodeInfo processContentChildNode(@Nonnull final ASTNode childNode, @Nonnull final NodeInfo info, final ASTNode parentNode, final List<Block> subBlocks, final RCompoundStatement nodeRCmpSt, final XmlFormattingPolicy xmlPolicy, @Nonnull final FileViewProvider fileViewProvider, @Nonnull final TextRange parentNodeTextRange, @Nullable final Alignment alignment, @Nullable final Indent rhtmlIndentIfWasCalculated)
	{ //Null if wan;t calced yet
		final TextRange childTextRange = info.getNextNodeTRange();
		LOG.assertTrue(childTextRange != null);

		if(!parentNodeTextRange.intersectsStrict(childTextRange))
		{
			return NodeInfo.createRHTMLInfo(parentNode, childNode, null);
		}

		final IElementType childNodeType = childNode.getElementType();

		if(RHTMLFormatterUtil.isHTMLDocomentRootOrProlog(childNode))
		{
			final Wrap wrap = RHTMLWrapProcessor.getWrapForNode(childNode, fileViewProvider);
			subBlocks.add(new RHTMLBlock(childNode, Indent.getNoneIndent(), wrap, xmlPolicy));
			return NodeInfo.createRHTMLInfo(parentNode, childNode, childNode.getTreeNext());
		}

		final RCompoundStatement childRCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(fileViewProvider, childNode);
		final Indent indent = rhtmlIndentIfWasCalculated != null ? rhtmlIndentIfWasCalculated : RHTMLIndentProcessor.calcRHTMLIndentForChild(childRCmpSt, nodeRCmpSt, parentNode, xmlPolicy, true);

		if(childNodeType == RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML)
		{
			return processHTMLTemplateData(childNode, childTextRange, parentNode, subBlocks, indent, alignment, xmlPolicy, childRCmpSt, parentNodeTextRange, fileViewProvider);
		}

		//rhtml tag for injection
		else if(childNodeType == RHTMLElementType.RHTML_XML_TAG || childNodeType == RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML)
		{
			return processRHTMLInjectionTag(childNode, parentNode, subBlocks, indent, alignment, xmlPolicy, nodeRCmpSt, fileViewProvider);
		}
		else if(childNodeType == RHTMLElementType.RHTML_COMMENT_ELEMENT)
		{
			final PsiElement psiElement = fileViewProvider.findElementAt(childNode.getStartOffset() - 1, HTMLLanguage.INSTANCE);
			final String text = psiElement != null ? psiElement.getText() : null;
			final boolean shouldIgnoreIndent = text != null && text.endsWith("\n");

			final Indent commentIndent = shouldIgnoreIndent ? Indent.getAbsoluteNoneIndent() : indent;
			final Alignment commentAlignment = shouldIgnoreIndent ? null : alignment;
			final Wrap commentWrap = RHTMLWrapProcessor.getWrapForNode(childNode, fileViewProvider);

			subBlocks.add(new RHTMLCommentBlock(childNode, commentIndent, commentAlignment, commentWrap, xmlPolicy));
			return NodeInfo.createRHTMLInfo(parentNode, childNode, childNode.getTreeNext());
		}

		return NodeInfo.createRHTMLInfo(parentNode, childNode, childNode.getTreeNext());
	}

	private static TextRange getHTMLBoundsInRCmpSt(@Nonnull final RCompoundStatement st, @Nonnull final FileViewProvider vProvider)
	{
		final TextRange range = st.getTextRange();

		final int startOffset = range.getStartOffset();
		final int endOffset = range.getEndOffset();


		final PsiElement startRubyCodeInRHTML = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, startOffset, true);

		final int resultStart;
		if(startRubyCodeInRHTML == null)
		{
			resultStart = startOffset;
		}
		else
		{
			final RHTMLRubyInjectionTag startInjection = (RHTMLRubyInjectionTag) ((startRubyCodeInRHTML instanceof RHTMLRubyInjectionTag) ? startRubyCodeInRHTML : RHTMLRubyInjectionTagNavigator.getByPsiElement(startRubyCodeInRHTML));
			if(startInjection != null)
			{
				resultStart = startInjection.getTextRange().getEndOffset();
			}
			else
			{
				resultStart = startOffset;
			}
		}

		final int resultEnd;
		final PsiElement endRubyCodeInRHTML = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, endOffset, true);
		if(endRubyCodeInRHTML == null)
		{
			resultEnd = endOffset;
		}
		else
		{
			final RHTMLRubyInjectionTag enInjectionTag = (RHTMLRubyInjectionTag) ((endRubyCodeInRHTML instanceof RHTMLRubyInjectionTag) ? endRubyCodeInRHTML : RHTMLRubyInjectionTagNavigator.getByPsiElement(endRubyCodeInRHTML));
			if(enInjectionTag != null)
			{
				resultEnd = enInjectionTag.getTextRange().getStartOffset();
			}
			else
			{
				resultEnd = endOffset;
			}
		}

		return new TextRange(resultStart, resultEnd);
	}

	@Nonnull
	public static NodeInfo processHTMLTemplateData(final ASTNode templateNode, @Nonnull final TextRange templateRange, @Nonnull final ASTNode parentNode, @Nonnull final List<Block> subBlocks, @Nonnull final Indent indent, @Nullable final Alignment alignment, @Nonnull final XmlFormattingPolicy xmlPolicy, @Nullable final RCompoundStatement childRCmpSt, @Nonnull final TextRange parentNodeTextRange, @Nonnull final FileViewProvider vProvider)
	{
		final int templateRangeStart = templateRange.getStartOffset();
		final int templateRangeEnd = templateRange.getEndOffset();

		final TextRange boundsTRange;
		if(childRCmpSt == null)
		{
			boundsTRange = parentNodeTextRange;
		}
		else
		{
			LOG.assertTrue(childRCmpSt.getTextLength() != 0, "Unexpected empty Ruby compound....:\nchildRCmpSt = " + childRCmpSt + "\n range = " + childRCmpSt.getTextRange() + "\n Dump: \n" + DebugUtil.psiToString(childRCmpSt.getContainingFile(), false, true));
			boundsTRange = parentNodeTextRange.intersection(getHTMLBoundsInRCmpSt(childRCmpSt, vProvider));
		}
		assert boundsTRange != null;

		final PsiElement htmlPsi = RHTMLFormatterUtil.findUpperHTMLElement(vProvider, templateRangeStart, templateRange);
		LOG.assertTrue(htmlPsi != null);

		final TextRange htmlPsiTRange = htmlPsi.getTextRange();

		if(!boundsTRange.intersectsStrict(htmlPsiTRange))
		{
			//if we have already leaved bounds area
			return NodeInfo.createRHTMLInfo(parentNode, templateNode, null);
		}
		final ASTNode htmlPsiNode = htmlPsi.getNode();
		if(RHTMLFormatterUtil.canBeCorrectBlock(htmlPsiNode))
		{
			final boolean isTag = htmlPsi instanceof XmlTag;
			final boolean isJSEmbeddedContent = RHTMLFormatterUtil.isJSEmbeddedContent(htmlPsi);
			if(RHTMLFormatterUtil.containsTextRange(boundsTRange, htmlPsiTRange))
			{
				//contains whole
				if(isTag || RHTMLFormatterUtil.isTagOrAttrElementOrAttrValue(htmlPsi) || isJSEmbeddedContent)
				{
					//for tags, attributes and attribute values, js
					if(RHTMLFormatterUtil.containsTextRange(templateRange, htmlPsiTRange))
					{
						//if html is in temlate fragement
						if(isJSEmbeddedContent)
						{
							//JS Content
							createAnotherLanguageBlockWrapper(htmlPsi.getLanguage(), htmlPsiNode, subBlocks, indent, xmlPolicy, xmlPolicy.getSettings());
						}
						else
						{
							//other elements
							createAndAddRHTMLHtml(isTag, xmlPolicy, htmlPsi, subBlocks, htmlPsiTRange, indent, alignment, vProvider);
						}
					}
					else
					{
						//html is greater that template, may cover other ruby injections
						final PsiElement lastChild = htmlPsi.getLastChild();
						LOG.assertTrue(lastChild != null, "LastChild here can't be null");

						final ASTNode htmlPsiEndNode = lastChild.getNode();
						assert htmlPsiEndNode != null;

						final RCompoundStatement htmlPsiEndRCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(vProvider, htmlPsiEndNode);

						if(childRCmpSt != htmlPsiEndRCmpSt)
						{
							createAndAddUnClosedRHtml(isTag || isJSEmbeddedContent, subBlocks, xmlPolicy, boundsTRange, htmlPsi, htmlPsiTRange, indent, alignment, vProvider);
							final PsiElement nextPsi;
							assert childRCmpSt != null;
							final PsiElement rhtmlForHtmlCmpStEnd = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, childRCmpSt.getTextRange().getEndOffset(), true);
							nextPsi = rhtmlForHtmlCmpStEnd != null ? RHTMLRubyInjectionTagNavigator.getByPsiElement(rhtmlForHtmlCmpStEnd) : null;
							return NodeInfo.createRHTMLInfo(parentNode, templateNode, nextPsi != null ? nextPsi.getNode() : null);
						}
						//close part of this html container is visible, but covers some ruby injections
						if(isJSEmbeddedContent)
						{
							//JS Content
							createAnotherLanguageBlockWrapper(htmlPsi.getLanguage(), htmlPsiNode, subBlocks, indent, xmlPolicy, xmlPolicy.getSettings());
						}
						else
						{
							//Other
							createAndAddRHTMLHtml(isTag, xmlPolicy, htmlPsi, subBlocks, htmlPsiTRange, indent, alignment, vProvider);
						}

						final int htmlPsiEndOffset = htmlPsiTRange.getEndOffset();

						// we must use "htmlPsiEndOffset - 1" because in case of "<%..%></b><% ..%>" rhtml element at
						// "htmlPsiEndOffset" is "<%" but insted of it we must find "</b>". Tag close element always have more than
						// 1 character so "htmlPsiEndOffset - 1" can't affect anything else.
						final PsiElement htmlEndInRHTML = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, htmlPsiEndOffset - 1, false);
						assert htmlEndInRHTML != null;
						final int htmlEndInRHTMLEnd = htmlEndInRHTML.getTextRange().getEndOffset();

						if(htmlEndInRHTMLEnd == htmlPsiEndOffset)
						{
							// if html tag closing offset equals to some html template data end offset
							final PsiElement nextRHTML = htmlEndInRHTML.getNextSibling();
							final ASTNode nextRHTMLNode = nextRHTML == null ? null : nextRHTML.getNode();
							return NodeInfo.createRHTMLInfo(parentNode, templateNode, nextRHTMLNode);
						}
						//otherwise we should split template
						final PsiElement nextHtml = RHTMLFormatterUtil.findUpperHTMLElement(vProvider, htmlPsiEndOffset, parentNodeTextRange);
						final ASTNode nextHtmlNode = nextHtml == null ? null : nextHtml.getNode();
						final TextRange nextNodeTRange = nextHtmlNode == null ? null : (RHTMLFormatterUtil.safelyCreateTextRange(htmlPsiEndOffset, htmlEndInRHTMLEnd));
						final ASTNode htmlEndInRHTMLNode = htmlEndInRHTML.getNode();
						LOG.assertTrue(htmlEndInRHTMLNode != null, "Can't be null here! " + htmlEndInRHTML);
						return NodeInfo.createTemplateInfo(parentNode, htmlEndInRHTMLNode, nextHtmlNode, nextNodeTRange);
					}
				}
				else
				{
					LOG.assertTrue(RHTMLFormatterUtil.containsTextRange(templateRange, htmlPsiTRange));
					createAndAddRHTMLHtml(false, xmlPolicy, htmlPsi, subBlocks, htmlPsiTRange, indent, alignment, vProvider);
				}
			}
			else
			{
				//intersects
				//element ins't finished
				if(htmlPsi instanceof XmlTag || isJSEmbeddedContent)
				{
					createAndAddUnClosedRHtml(isTag, subBlocks, xmlPolicy, boundsTRange, htmlPsi, htmlPsiTRange, indent, alignment, vProvider);
					LOG.assertTrue(childRCmpSt != null, "Ruby compound statement must != null in case of hiddent html end tag.");

					final TextRange childRCmpStTRange = childRCmpSt.getTextRange();
					final int childRCmpStEndOffset = childRCmpStTRange.getEndOffset();
					LOG.assertTrue(childRCmpStEndOffset >= boundsTRange.getEndOffset(), "Must be true in case of hidden html end tag.");

					final PsiElement endRubyElementCodeInRHTML = RHTMLFormatterUtil.findRHTMLElementByStartOffset(vProvider, childRCmpStEndOffset, true);

					final ASTNode nextNode;
					if(endRubyElementCodeInRHTML == null)
					{
						nextNode = null;
					}
					else
					{
						final RHTMLRubyInjectionTag injectionTag = RHTMLRubyInjectionTagNavigator.getByPsiElement(endRubyElementCodeInRHTML);
						if(injectionTag != null)
						{
							//all is ok
							nextNode = injectionTag.getNode();
						}
						else
						{
							//injection isn't closed
							nextNode = endRubyElementCodeInRHTML.getNode();
						}
					}
					return NodeInfo.createRHTMLInfo(parentNode, templateNode, nextNode);
				}
				else
				{
					LOG.assertTrue(false, "Unexpecteed composite element: " + htmlPsi);
				}
			}
		}

		final int htmlPsiEndOffset = htmlPsiTRange.getEndOffset();
		if(htmlPsiEndOffset < templateRangeEnd)
		{
			final PsiElement nextHTML = RHTMLFormatterUtil.findUpperHTMLElement(vProvider, htmlPsiEndOffset, parentNodeTextRange);
			final ASTNode nextHtmlNode = nextHTML == null ? null : nextHTML.getNode();
			final TextRange nextNodeTRange = nextHtmlNode == null ? null : (RHTMLFormatterUtil.safelyCreateTextRange(htmlPsiEndOffset, templateRangeEnd));
			return NodeInfo.createTemplateInfo(parentNode, templateNode, nextHtmlNode, nextNodeTRange);
		}
		else
		{
			return NodeInfo.createRHTMLInfo(parentNode, templateNode, templateNode.getTreeNext());
		}
	}

	private static void createAnotherLanguageBlockWrapper(final Language childLanguage, final ASTNode child, final List<Block> result, final Indent indent, final XmlFormattingPolicy policy, final CodeStyleSettings settings)
	{
		final PsiElement childPsi = child.getPsi();
		final FormattingModelBuilder builder = LanguageFormatting.INSTANCE.forLanguage(childPsi.getLanguage());
		LOG.assertTrue(builder != null);
		final FormattingModel childModel = builder.createModel(childPsi, settings);
		result.add(new ForeignLanguageBlock(child, policy, childModel.getRootBlock(), indent, 0, child.getTextRange()));
	}

	private static void createAndAddRHTMLHtml(final boolean isTag, final XmlFormattingPolicy xmlPolicy, final PsiElement psiElement, final List<Block> subBlocks, final TextRange textRange, final Indent indent, final Alignment alignment, final FileViewProvider provider)
	{
		final Wrap wrap = RHTMLWrapProcessor.getWrapForNode(psiElement.getNode(), provider);
		final ASTNode node = psiElement.getNode();

		final TextRange textRangeWithoutTrailingWhiteSpaces;
		if(textRange != null)
		{
			final int tRangeStart = textRange.getStartOffset();

			int lastElemInTRangeOffset = textRange.getEndOffset();
			PsiElement lastElemInTRange = provider.findElementAt(lastElemInTRangeOffset - 1, HTMLLanguage.INSTANCE);

			while(lastElemInTRange != null && tRangeStart <= lastElemInTRangeOffset && !RHTMLFormatterUtil.canBeCorrectBlock(lastElemInTRange.getNode()))
			{
				lastElemInTRangeOffset -= lastElemInTRange.getTextLength();
				lastElemInTRange = provider.findElementAt(lastElemInTRangeOffset - 1, HTMLLanguage.INSTANCE);
			}
			LOG.assertTrue(tRangeStart < lastElemInTRangeOffset); //this case must be processed early!
			textRangeWithoutTrailingWhiteSpaces = new TextRange(tRangeStart, lastElemInTRangeOffset);
		}
		else
		{
			textRangeWithoutTrailingWhiteSpaces = null;
		}
		final Block block = isTag ? new RHTMLHtmlTagBlock(node, wrap, alignment, xmlPolicy, indent, provider, textRangeWithoutTrailingWhiteSpaces) : new RHTMLHtmlBlock(node, wrap, alignment, xmlPolicy, indent, provider, textRangeWithoutTrailingWhiteSpaces);
		subBlocks.add(block);
	}

	private static void createAndAddUnClosedRHtml(final boolean isTag, final List<Block> subBlocks, final XmlFormattingPolicy xmlPolicy, final TextRange boundsTRange, final PsiElement htmlPsi, final TextRange htmlPsiTRange, final Indent indent, final Alignment alignment, final FileViewProvider provider)
	{
		//close_tag is hidden by some ruby injection
		//then open tag ins't closed and have influence
		//on all remainig text in block;
		final TextRange tagBounds = new TextRange(htmlPsiTRange.getStartOffset(), boundsTRange.getEndOffset());
		createAndAddRHTMLHtml(isTag, xmlPolicy, htmlPsi, subBlocks, tagBounds, indent, alignment, provider);
	}

	@Nonnull
	private static RHTMLNodeInfo processRHTMLInjectionTag(@Nonnull final ASTNode rhtmlTagNode, final ASTNode parentNode, @Nonnull final List<Block> blockList, @Nonnull final Indent indent, @Nullable final Alignment alignment, @Nonnull final XmlFormattingPolicy xmlPolicy, final RCompoundStatement parentNodeRCmpSt, final FileViewProvider fileViewProvider)
	{
		final Wrap wrap = RHTMLWrapProcessor.getWrapForNode(rhtmlTagNode, fileViewProvider);
		blockList.add(new RHTMLRubyInjectionBlock(rhtmlTagNode, indent, wrap, xmlPolicy, alignment, parentNode, parentNodeRCmpSt));

		return NodeInfo.createRHTMLInfo(parentNode, rhtmlTagNode, rhtmlTagNode.getTreeNext());
	}
}
