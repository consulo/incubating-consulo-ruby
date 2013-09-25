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

import com.intellij.codeFormatting.general.FormatterUtil;
import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.formatter.xml.XmlBlock;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlAttributeValue;
import com.intellij.psi.xml.XmlElementType;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLBlockGenerator;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.nodeInfo.NodeInfo;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.spacing.RHTMLSpacingProcessor;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 25, 2007
 */

/**
 * Use this Block only for html psi elements without Ruby injections
 */
public class RHTMLHtmlBlock extends XmlBlock {
    private static final Logger LOG = Logger.getInstance(RHTMLHtmlBlock.class.getName());

    private FileViewProvider myViewProvider;
    private RCompoundStatement myNodeCmpSt;

    public RHTMLHtmlBlock(final ASTNode node,
                          final Wrap wrap, Alignment alignment,
                          final XmlFormattingPolicy policy,
                          final Indent indent,
                          final FileViewProvider provider,
                          final TextRange textRange) {
        super(node, wrap, alignment, policy, indent, textRange);
        myViewProvider = provider;
        myNodeCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(myViewProvider, myNode);
    }

    @Override
	public Spacing getSpacing(Block child1, Block child2) {
        final ASTNode childNode1 = RHTMLFormatterUtil.getNodeByBlockForRHTMLFormatter(child1);
        final ASTNode childNode2 = RHTMLFormatterUtil.getNodeByBlockForRHTMLFormatter(child2);

        final Spacing spacing = RHTMLSpacingProcessor.getSpacing(this, getNode(), childNode1, childNode2, myXmlFormattingPolicy.getSettings());
        return spacing != null
                ? spacing
                : super.getSpacing(child1, child2);
    }

    @Override
	protected List<Block> buildChildren() {
        if (myNode.getElementType() == XmlElementType.XML_ATTRIBUTE_VALUE) {
            if (myNode instanceof CompositeElement) {
                final ArrayList<Block> result = new ArrayList<Block>(5);
                ChameleonTransforming.transformChildren(myNode);
                ASTNode child = myNode.getFirstChildNode();
                while (child != null) {
                    if (!FormatterUtil.containsWhiteSpacesOnly(child) && child.getTextLength() > 0) {
                        //child is html element or RHTML injectiontag
                        child = processChild(result, child, null, null, Indent.getNoneIndent());
                    }
                    if (child != null) {
                        child = child.getTreeNext();
                    }
                }
                return result;
            } else {
                return EMPTY;
            }
        }
        return super.buildChildren();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
	protected
    @Nullable
    ASTNode processChild(List<Block> result, ASTNode child, Wrap wrap, Alignment alignment, Indent indent) {
        final TextRange childTRange = child.getTextRange();
        if (childTRange == null) {
            return null;
        }
        final TextRange nodeTRange = getTextRange();
        if (!RHTMLFormatterUtil.containsTextRange(nodeTRange, childTRange)) {
            final TextRange boundsTRange = nodeTRange.intersection(childTRange);
            if (boundsTRange != null) {
                //fake block for incomplete element
                result.add(new RHTMLBlock(child, indent,  wrap, myXmlFormattingPolicy, boundsTRange, alignment));
            }
            return null;
        }

        final IElementType childNodeType = child.getElementType();
        if (childNodeType instanceof IRHTMLElement) {

            final int childStartOffset = child.getStartOffset();
            final PsiElement rhtmlProjection =
                    RHTMLFormatterUtil.findRHTMLElementByStartOffset(myViewProvider, childStartOffset, true);
            LOG.assertTrue(rhtmlProjection != null);
            final ASTNode rhtmlNode = rhtmlProjection.getNode();

            //noinspection ConstantConditions
            final int rhtmlEnd = rhtmlNode.getTextRange().getEndOffset();
            final NodeInfo info = (rhtmlNode.getElementType() == RHTMLElementType.RHTML_XML_TAG)
                    ? NodeInfo.createRHTMLInfo(myNode, null, rhtmlNode)
                    : NodeInfo.createTemplateInfo(myNode, rhtmlNode, rhtmlNode, new TextRange(childStartOffset, rhtmlEnd));

            final NodeInfo nextNodeInfo = RHTMLBlockGenerator.processContentChildNode(rhtmlNode, info, myNode, result, myNodeCmpSt, myXmlFormattingPolicy, myViewProvider, nodeTRange, alignment, null);
            final ASTNode nextChild = RHTMLHtmlTagBlock.getNextHtmlChild(nextNodeInfo, nodeTRange);
            return nextChild != null
                    ? nextChild.getTreePrev()
                    : null;
        } else if (child instanceof XmlAttributeValue) {
            result.add(new RHTMLHtmlBlock(child, wrap, alignment, myXmlFormattingPolicy, indent, myViewProvider, childTRange));
        } else {
            result.add(new XmlBlock(child, wrap, alignment, myXmlFormattingPolicy, indent, childTRange));
        }

        return child;
    }
}
