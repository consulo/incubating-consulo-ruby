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

import com.intellij.formatting.*;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLBlockGenerator;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLFormatterUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.RHTMLIndentProcessor;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter.helpers.spacing.RHTMLSpacingProcessor;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 6, 2007
 */
public class RHTMLBlock extends AbstractBlock {

    final protected XmlFormattingPolicy myXmlFormattingPolicy;
    final private Indent myIndent;
    private TextRange myTextRange;
    private RCompoundStatement myNodeCmpSt;
    private FileViewProvider myViewProvider;

    public RHTMLBlock(@NotNull final ASTNode node,
                      @Nullable final Indent indent, @Nullable final Wrap wrap,
                      final XmlFormattingPolicy xmlFormattingPolicy,
                      @Nullable final TextRange textRange,
                      @Nullable final Alignment alignment) {
        super(node, wrap, alignment);
        myIndent = indent;
        myXmlFormattingPolicy = xmlFormattingPolicy;
        myTextRange = textRange == null ? node.getTextRange() : textRange;

        final PsiElement nodePsi = node.getPsi();
        final FileViewProvider provider = nodePsi == null
                ? null
                : nodePsi.getContainingFile().getViewProvider();
        assert provider != null;
        myViewProvider = provider;
        myNodeCmpSt = RHTMLFormatterUtil.getParentRCmpStByRHTMLOrHTMLChildNode(myViewProvider, myNode);
    }

    public RHTMLBlock(@NotNull final ASTNode node,
                      @Nullable final Indent indent, @Nullable final Wrap wrap,
                      final XmlFormattingPolicy xmlFormattingPolicy) {
        this(node,  indent,  wrap, xmlFormattingPolicy, null, null);
    }

    public RHTMLBlock(@NotNull final ASTNode node,
                      @Nullable final Indent indent, @Nullable final Wrap wrap,
                      final XmlFormattingPolicy xmlFormattingPolicy,
                      @Nullable final Alignment alignment) {
        this(node,  indent,  wrap, xmlFormattingPolicy, null, alignment);
    }


    @Override
	@NotNull
    public TextRange getTextRange() {
        return myTextRange;
    }

    @Override
	@Nullable
    public Indent getIndent() {
        return myIndent;
    }

    @Override
	@Nullable
    public Spacing getSpacing(Block child1, Block child2) {
        final ASTNode childNode1 = RHTMLFormatterUtil.getNodeByBlockForRHTMLFormatter(child1);
        final ASTNode childNode2 = RHTMLFormatterUtil.getNodeByBlockForRHTMLFormatter(child2);
        return RHTMLSpacingProcessor.getSpacing(this, getNode(), childNode1,  childNode2, myXmlFormattingPolicy.getSettings());
    }


    @Override
	@NotNull
    public ChildAttributes getChildAttributes(int newChildIndex) {
        Indent indent = Indent.getNoneIndent();
        final List<Block> subBlocks = getSubBlocks();
        if (newChildIndex != 0 && newChildIndex < subBlocks.size()) {
            final Block currBlock = subBlocks.get(newChildIndex);

            final ASTNode curNode = currBlock instanceof AbstractBlock
                    ? ((AbstractBlock) currBlock).getNode()
                    : null;

            final RCompoundStatement childCmpSt;
            if (curNode != null) {
                childCmpSt = RHTMLFormatterUtil.getRCmpStNodeStartOffset(myViewProvider, curNode);
                indent =  RHTMLIndentProcessor.calcRubyIndentForNode(childCmpSt, myNodeCmpSt, myXmlFormattingPolicy.getSettings());
            }
        }

        return new ChildAttributes(indent, null);
    }

    @Override
	public boolean isIncomplete() {
        return isIncomplete(myNode);
    }

    /**
     * @param node Tree node
     * @return true if node is incomplete
     */
    public boolean isIncomplete(@NotNull final ASTNode node) {
        ASTNode lastChild = node.getLastChildNode();
        while (lastChild != null &&
                (lastChild.getPsi() instanceof PsiWhiteSpace || lastChild.getPsi() instanceof PsiComment)) {
            lastChild = lastChild.getTreePrev();
        }
        if (lastChild == null) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (lastChild.getPsi() instanceof PsiErrorElement) {
            return true;
        }
        return isIncomplete(lastChild);
    }

    @Override
	public boolean isLeaf() {
        return myNode.getFirstChildNode() == null;
    }

    public String toString(){
        return "Block at: " + myNode.toString();
    }

    @Override
	@NotNull
    protected List<Block> buildChildren() {
        return RHTMLBlockGenerator.createRHTMLSubBlocks(myNode, myXmlFormattingPolicy, this, myViewProvider);
    }
}
