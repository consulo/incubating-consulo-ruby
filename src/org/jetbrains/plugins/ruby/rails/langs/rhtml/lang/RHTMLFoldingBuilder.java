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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.xml.XmlComment;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.04.2007
 */
public class RHTMLFoldingBuilder implements FoldingBuilder {
    private static final Logger LOG = Logger.getInstance(RHTMLFoldingBuilder.class.getName());

    @NonNls public static final String RHTML_COMMENT_FOLD_TEXT =            "<%#...%>";
    @NonNls private static final String RHTML_SCRIPTLET_FOLD_TEXT =          "<%...%>";
    @NonNls private static final String RHTML_EXPRESSION_FOLD_TEXT =         "<%=...%>";

    @NonNls private static final String XML_COMMENT_OPEN_TAG =               "...";

    private static final TokenSet COLLAPSED_BY_DEFAULT = TokenSet.create(
            RHTMLElementType.RHTML_COMMENT_ELEMENT);

    private static final TokenSet FOLDED_ELEMENTS = TokenSet.create(
                      RHTMLElementType.RHTML_XML_TAG,
                      RHTMLElementType.RHTML_COMMENT_ELEMENT);

    @Override
	@NotNull
    public FoldingDescriptor[] buildFoldRegions(@NotNull ASTNode astNode, @NotNull Document document) {
        List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        gatherDescriptors(astNode, descriptors, document);
        return descriptors.toArray(new FoldingDescriptor[descriptors.size()]);
    }

    private void gatherDescriptors(@NotNull final ASTNode node,
                                   @NotNull final List<FoldingDescriptor> descriptors,
                                   @NotNull final Document document) {
        final IElementType type = node.getElementType();

        if (FOLDED_ELEMENTS.contains(type)) {
            final ASTNode first = node.getFirstChildNode();
            final ASTNode last = node.getLastChildNode();
            if (first != null && last != null && first != last) {
                int startLine = document.getLineNumber(first.getStartOffset());
                int endLine = document.getLineNumber(last.getStartOffset() + last.getTextLength());
                if (startLine != endLine) {
                    descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
                }
            }
        }


        for (ASTNode child : node.getChildren(null)){
            gatherDescriptors(child, descriptors, document);
        }
    }

    @Override
	@Nullable
    public String getPlaceholderText(@NotNull final ASTNode node) {
        final IElementType nodeType = node.getElementType();
        if (nodeType == RHTMLElementType.RHTML_XML_TAG) {
            final ASTNode tagOpen = node.getFirstChildNode();
            if (tagOpen == null) {
                return null;
            }
            final IElementType type = tagOpen.getElementType();
            if (type == RHTMLTokenType.RHTML_SCRIPTLET_START) {
                return RHTML_SCRIPTLET_FOLD_TEXT;
            } else if (type == RHTMLTokenType.RHTML_EXPRESSION_START) {
                return RHTML_EXPRESSION_FOLD_TEXT;
            }
            return null;
        } else if (nodeType == RHTMLElementType.RHTML_COMMENT_ELEMENT) {
            return RHTML_COMMENT_FOLD_TEXT;
        }

        final PsiElement psi = node.getPsi();
        if (psi instanceof XmlComment || psi instanceof XmlTag) {
            return XML_COMMENT_OPEN_TAG;
        }

        LOG.error("Unknown element:" + psi);
        return null;
    }

    @Override
	public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return COLLAPSED_BY_DEFAULT.contains(node.getElementType());
    }
}