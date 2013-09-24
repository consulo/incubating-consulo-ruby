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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.impl;

import javax.swing.Icon;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer.BlackAndWhiteLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.TreePatcher;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.SingleRootFileViewProvider;
import com.intellij.psi.impl.source.DummyHolder;
import com.intellij.psi.impl.source.PsiFileImpl;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.tree.FileElement;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.templateLanguages.OuterLanguageElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.util.CharTable;
import com.intellij.util.LocalTimeCounter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.04.2007
 */
public abstract class TemplateWithOuterFragmentsTypeImpl extends IFileElementType {
    protected TemplateWithOuterFragmentsTypeImpl(final @NonNls String debugName, final Language language) {
        super(debugName, language);
    }

    /**
     *
     * @param chameleon Node
     * @param templateLang Template templateLang
     * @param file File for <code>chameleon</code> node
     * @param blackAndWhiteLexer Lexer returns lexems for template's and outer's fragments
     * @param tokensToMerge If <code>blackAndWhiteLexer</code> does'n merge lexems special Merge adapter uses for it.
     * @param temlateFragment Lexem for template fragment
     * @param outerFragment Lexem for outer fragement
     * @return parsed tree for template templateLang with inserted outer elements
     */
      protected ASTNode parseContents(final ASTNode chameleon,
                                      @NotNull final Language templateLang,
                                      @NotNull final PsiFile file,
                                      @NotNull final BlackAndWhiteLexer blackAndWhiteLexer,
                                      @NotNull final TokenSet tokensToMerge,
                                      @NotNull final IElementType temlateFragment,
                                      @NotNull final IElementType outerFragment) {

        final CharTable table = SharedImplUtil.findCharTableByTree(chameleon);
        final FileElement treeElement =
                new DummyHolder(((TreeElement)chameleon).getManager(),
                                null, table).getTreeElement();
        final CharSequence chars = ((CharTableBasedLeafElementImpl)chameleon).getInternedText();

        final StringBuilder templateText
                = createTemplateText(chars, blackAndWhiteLexer, temlateFragment);

        final PsiFile templateFile = createFromText(templateLang, templateText, file.getManager());

        final TreeElement parsed = ((PsiFileImpl)templateFile).calcTreeElement();
        ChameleonTransforming.transformChildren(parsed, false);

        final Lexer lexer = new MergingLexerAdapter(blackAndWhiteLexer,
                                                    tokensToMerge);
        lexer.start(chars, 0, chars.length(), 0);
        insertOuters(parsed, lexer, table, outerFragment);

        if (parsed != null) {
            TreeUtil.addChildren(treeElement, parsed.getFirstChildNode());
        }
        treeElement.clearCaches();
        treeElement.subtreeChanged();
        return treeElement.getFirstChildNode();
    }

    protected abstract TreePatcher createTreePatcher();
    private void insertOuters(final TreeElement root, final Lexer lexer,
                              final CharTable table,
                              final IElementType outerFragment) {
        TreePatcher patcher = createTreePatcher();

        int treeOffset = 0;
        LeafElement leaf = TreeUtil.findFirstLeaf(root);
        while (lexer.getTokenType() != null) {
            IElementType tt = lexer.getTokenType();
            if (tt == outerFragment) {
                while (leaf != null && (treeOffset < lexer.getTokenStart()
                        || (treeOffset == lexer.getTokenStart() && leaf.getTextLength() == 0))) {
                    treeOffset += leaf.getTextLength();
                    if (treeOffset > lexer.getTokenStart()) {
                        if (leaf instanceof ChameleonElement) {
                            final ASTNode transformed = ChameleonTransforming.transform(leaf);
                            treeOffset -= leaf.getTextLength();
                            leaf = TreeUtil.findFirstLeaf(transformed);
                            continue;
                        }
                        leaf = patcher.split(leaf, leaf.getTextLength() - (treeOffset - lexer.getTokenStart()), table);
                        treeOffset = lexer.getTokenStart();
                    }
                    leaf = (LeafElement)TreeUtil.nextLeaf(leaf);
                }

                if (leaf == null) break;

                final OuterLanguageElement newLeaf = createOuterElement(lexer, table);

                patcher.insert(leaf.getTreeParent(), leaf, newLeaf);
                leaf.getTreeParent().subtreeChanged();
                leaf = (LeafElement)newLeaf;
            }
            lexer.advance();
        }

        if (lexer.getTokenType() != null) {
            assert lexer.getTokenType() == outerFragment;
            final OuterLanguageElement newLeaf = createOuterElement(lexer, table);
            TreeUtil.addChildren((CompositeElement)root, (TreeElement)newLeaf);
            ((CompositeElement)root).subtreeChanged();
        }
    }

    /**
     *
     * @param lexer  Lexer with current state.
     * @param table CharTable
     * @return OuterLanguageElement. Also obj must implement LeafElement
     */
    protected abstract OuterLanguageElement createOuterElement(final Lexer lexer,
                                                               final CharTable table);

    /**
     * Removes all outer data from buffer.
     * @param buf Text
     * @param blackAndWhiteLexer Lexer returns lexems for template's and outer's fragments
     * @param temlateFragment Lexem for template data (see blackAndWhiteLexer)
     * @return Tempate text without outer fragments.
     */
    protected StringBuilder createTemplateText(final CharSequence buf,
                                              final BlackAndWhiteLexer blackAndWhiteLexer,
                                              @NotNull final IElementType temlateFragment) {
        StringBuilder result = new StringBuilder(buf.length());
        blackAndWhiteLexer.start(buf, 0, buf.length(), 0);

        while (blackAndWhiteLexer.getTokenType() != null) {
            if (blackAndWhiteLexer.getTokenType() == temlateFragment) {
                result.append(buf, blackAndWhiteLexer.getTokenStart(), blackAndWhiteLexer.getTokenEnd());
            }

            blackAndWhiteLexer.advance();
        }

        return result;
    }

    private PsiFile createFromText(final Language language, CharSequence text, PsiManager manager) {
        @NonNls
        final LightVirtualFile virtualFile = new LightVirtualFile("foo", new LanguageFileType(language) {

            @NotNull
            @NonNls
            public String getDefaultExtension() {
                return "";
            }

            @NotNull
            @NonNls
            public String getDescription() {
                return "fake for language" + language.getID();
            }

            @Nullable
            public Icon getIcon() {
                return null;
            }

            @NotNull
            @NonNls
            public String getName() {
                return language.getID();
            }
        }, text, LocalTimeCounter.currentTime());

        FileViewProvider viewProvider = new SingleRootFileViewProvider(manager, virtualFile, false) {
            @NotNull
            public Language getBaseLanguage() {
                return language;
            }
        };

        return viewProvider.getPsi(language);
    }

}
