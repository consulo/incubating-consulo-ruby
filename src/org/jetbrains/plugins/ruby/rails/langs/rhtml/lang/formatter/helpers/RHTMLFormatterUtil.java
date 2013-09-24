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

import com.intellij.formatting.Block;
import com.intellij.lang.ASTNode;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.formatter.common.AbstractBlock;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.addins.jsSupport.JavaScriptIntegrationUtil;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.eRubyLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTag;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot.RHTMLRubyInjectionTagNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.RubyBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RCompoundStatementNavigator;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 18, 2007
 */
public class RHTMLFormatterUtil {
    private static RApplicationSettings myRApplicationSettings;

    static boolean checkIfInParentClosingTagEnd(@NotNull final ASTNode parentNode,
                                                @Nullable final PsiElement htmlPsi) {
        if (htmlPsi != null) {
            final ASTNode node = htmlPsi.getNode();
            return (node != null && node.getElementType() == XmlElementType.XML_END_TAG_START
                && node.getTreeParent() == parentNode);
        }
        return false;
    }

    static TextRange safelyCreateTextRange(final int startOffset, final int endOffset) {
        return startOffset < endOffset ? new TextRange(startOffset, endOffset) : null;
    }

    /**
     * @param node Tree node
     * @return true, if the current node can be block node, else otherwise
     */
    public static boolean canBeCorrectBlock(@Nullable final ASTNode node) {
        return node != null && node.getText().trim().length() > 0;
    }

    public static boolean containsTextRange(final TextRange container, final TextRange range) {
        return (container.getStartOffset() <= range.getStartOffset()
                && container.getEndOffset() >= range.getEndOffset());
    }

    public static ASTNode getNodeByBlockForRHTMLFormatter(final Block child1) {
        if (child1 instanceof AbstractBlock) {
            return ((AbstractBlock)child1).getNode();
        } else if (child1 instanceof RubyBlock) {
            return ((RubyBlock)child1).getNode();
        }
        return null;
    }
    
    public static ASTNode getNodeByBlockForRubyFormatter(final Block child1) {
        if (child1 instanceof RubyBlock) {
            return ((RubyBlock)child1).getNode();
        } else if (child1 instanceof AbstractBlock) {
            return ((AbstractBlock)child1).getNode();
        }
        return null;
    }

    /**
     * Calculates the first ruby RCompoundStatement that contains node
     * @param provider FileView provider
     * @param node Given node
     * @return Found RCompoundStatement or null.
     */
    @Nullable
    public static RCompoundStatement getParentRCmpStByRHTMLOrHTMLChildNode(@NotNull final FileViewProvider provider,
                                                                           @NotNull final ASTNode node) {
        final RCompoundStatement cmpSt = getRCmpStNodeStartOffset(provider, node);
        if (cmpSt == null) {
            return null;
        }

        // cmpSt is Ruby Element so it is in ruby code of some rhtml injection element
        // ruby code is a "middle" part of rhtml injection =)
        return isRHTMLXmlTagForRubyBlockEnd(node, cmpSt, provider)
                ? RCompoundStatementNavigator.getParentCompoundStatement(cmpSt)
                : cmpSt;
    }

    @Nullable
    public static RCompoundStatement getRCmpStNodeStartOffset(@NotNull final FileViewProvider provider,
                                                              @NotNull final ASTNode node) {
        final int boundsStart = node.getStartOffset();
        final PsiElement rubyPsi = provider.findElementAt(boundsStart, RubyLanguage.RUBY);

        if (rubyPsi == null) {
            return null;
        }

        if (rubyPsi instanceof RCompoundStatement) {
            return (RCompoundStatement)rubyPsi;
        }

        return RCompoundStatementNavigator.getByPsiElement(rubyPsi);
    }

    /**
     * Searches first highest XmlTag or XmlAttribute or XmlAttributeValue by given starting offset. If bounds text range ins't null
     * and no tag corresponds to given offset method will return highest XmlElement in bounds text range.
     * If highest element is XmlText or XmlDocument then firstChild will be returned!
     * @param provider File View provider
     * @param startOffset Start offset
     * @param boundsTRange Bounds text range or null
     * @return Found PsiElement or null
     */
    @Nullable
    public static PsiElement findUpperHTMLElement(@NotNull final FileViewProvider provider,
                                                       final int startOffset,
                                                       @Nullable final TextRange boundsTRange) {
        PsiElement htmlPsi = provider.findElementAt(startOffset, StdLanguages.HTML);
        PsiElement htmlPsiParent = (htmlPsi != null ? htmlPsi.getParent() : null);
        TextRange htmlParentRange = (htmlPsiParent != null ? htmlPsiParent.getTextRange() : null);
        //goes up until  XmlTag or XmlAttribute or XmlAttributeValue starts in startOffset and in geiven bounds
        while (!(htmlPsi instanceof XmlText
                 || htmlPsi instanceof XmlDocument
                 // attributs, tags, js_embedded_content
                 || isRegisteredContainerElement(htmlPsi))

                && htmlParentRange != null && htmlParentRange.getStartOffset() == startOffset
                //bounds condition
                && (boundsTRange == null
                    || (isRegisteredContainerElement(htmlPsiParent))
                    || containsTextRange(boundsTRange, htmlParentRange))) {
            htmlPsi = htmlPsi.getParent();
            htmlPsiParent = (htmlPsi != null ? htmlPsi.getParent() : null);
            htmlParentRange = (htmlPsiParent != null ? htmlPsiParent.getTextRange() : null);
        }
        if (htmlPsi instanceof XmlText || htmlPsi instanceof XmlDocument) {
            htmlPsi = htmlPsi.getFirstChild();
        }
        if (htmlPsi != null && htmlPsi instanceof XmlProlog) {
            htmlPsi = htmlPsi.getNextSibling();
        }
        final TextRange htmlPsiRange = htmlPsi != null ? htmlPsi.getTextRange() : null;
        if (boundsTRange != null && htmlPsiRange != null && !boundsTRange.intersectsStrict(htmlPsiRange)) {
            return null;
        }
        return htmlPsi;
    }

    /**
     * Searches RHTML Element at offset. If searh highest flag is set, method tries to search
     * the widest(highest in tree) container with such start offset. If container is XmlDocument method will stop 
     * @param vProvider File view provider
     * @param startOffset Start Offset
     * @param searchHighest Search highest container flag
     * @return Found RHTML Element
     */
    public static PsiElement findRHTMLElementByStartOffset(final FileViewProvider vProvider, final int startOffset,
                                                           final boolean searchHighest) {
        PsiElement psiElement = vProvider.findElementAt(startOffset, eRubyLanguage.INSTANCE);
        if (searchHighest && psiElement != null) {
            PsiElement psiParent = psiElement.getParent();
            while (psiParent != null
                    && !(psiParent instanceof XmlDocument)
                    && psiParent.getTextRange().getStartOffset() == startOffset) {
                psiElement = psiParent;
                psiParent = psiParent.getParent();
            }
        }
        return psiElement;
    }

    public static boolean isRHTMLXmlTagForRubyBlockEnd(@NotNull final ASTNode rhtmlTagNode,
                                                     @NotNull RCompoundStatement nodeCmpSt,
                                                     @NotNull final FileViewProvider vProvider) {
        final PsiElement cmpStEndInRHTML =
                    findRHTMLElementByStartOffset(vProvider, nodeCmpSt.getTextRange().getEndOffset() - 1, false);
        if (cmpStEndInRHTML != null) {
            //If rhtmlTagNode is compound statement closing element (e.g. <% end %>)
            //we should patch cmpSt and return its parent!!!!!
            final RHTMLRubyInjectionTag injectionTag =
                    RHTMLRubyInjectionTagNavigator.getByPsiElement(cmpStEndInRHTML);
            final ASTNode injectionTagNode = injectionTag != null ? injectionTag.getNode() : null;
            return injectionTagNode != null && injectionTagNode.getTextRange().equals(rhtmlTagNode.getTextRange());
        }
        return false;
    }

    public static boolean isRegisteredContainerElement(final PsiElement htmlPsi) {
        return isTagOrAttrElementOrAttrValue(htmlPsi)
                || isSpecialElement(htmlPsi);
    }

    public static boolean isHTMLDocomentRootOrProlog(final ASTNode parentNode) {
        final IElementType type = parentNode.getElementType();
        return type == XmlElementType.HTML_DOCUMENT ||  type == XmlElementType.XML_PROLOG;
    }

    public static boolean isTagOrAttrElementOrAttrValue(final PsiElement htmlPsi) {
        return htmlPsi instanceof XmlTag
                || htmlPsi instanceof XmlAttribute
                || htmlPsi instanceof XmlAttributeValue
                || htmlPsi instanceof XmlDoctype;
    }

    public static boolean isSpecialElement(final PsiElement htmlPsi) {
        return isJSEmbeddedContent(htmlPsi);
    }

    /**
     * @param rhtmlTagNode RHTMLElementType.RHTML_XML_TAG node
     * @return if tag is scriplet tag
     */
    public static boolean isScripletRHTMLXmlTagNode(@NotNull final ASTNode rhtmlTagNode) {
        final ASTNode firstChildNode = rhtmlTagNode.getFirstChildNode();
        return firstChildNode != null && firstChildNode.getElementType() == RHTMLTokenType.RHTML_SCRIPTLET_START;
    }

    static boolean isJSEmbeddedContent(final PsiElement htmlPsi) {
        if (myRApplicationSettings == null) {
            myRApplicationSettings = RApplicationSettings.getInstance();
        }
        return (myRApplicationSettings.isJsSupportEnabled()) && JavaScriptIntegrationUtil.isJSEmbeddedContent(htmlPsi);
    }
}
