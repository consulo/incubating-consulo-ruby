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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi;

import com.intellij.lang.Language;
import com.intellij.lang.StdLanguages;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.RHTMLLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.04.2007
 */
public class RHTMLPsiUtil {
    @Nullable
    public static RHTMLFile getRHTMLFileRoot(final PsiElement element) {
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) return null;

        final FileViewProvider viewProvider = containingFile.getViewProvider();
        final PsiFile psiFile = viewProvider.getPsi(viewProvider.getBaseLanguage());
        return psiFile instanceof RHTMLFile ? (RHTMLFile)psiFile : null;
    }

    @Nullable
    public static RFile getRubyFileRoot(final PsiElement element) {
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) return null;

        final FileViewProvider viewProvider = containingFile.getViewProvider();
        final PsiFile psiFile = viewProvider.getPsi(RubyLanguage.RUBY);
        return psiFile instanceof RFile ? (RFile)psiFile : null;
    }

    @Nullable
    public static XmlFile getHTMLFileRoot(final PsiElement element) {
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) return null;

        final FileViewProvider viewProvider = containingFile.getViewProvider();
        final PsiFile psiFile = viewProvider.getPsi(StdLanguages.HTML);
        return psiFile instanceof XmlFile ? (XmlFile)psiFile : null;
    }

    public static boolean isRubyCodeInjectionStart(final IElementType tokenType) {
        return RHTMLTokenType.RHTML_SEPARATORS_STARTS.contains(tokenType);
    }

    public static boolean isRubyCodeInjectionEnd(final IElementType tokenType) {
        return RHTMLTokenType.RHTML_SEPARATORS_ENDS.contains(tokenType);
    }


    public static boolean isRubyContext(final PsiElement position) {
        return PsiTreeUtil.getContextOfType(position, RHTMLRubyFile.class, false) != null;
    }

/*
    public static IChameleonElementType createSimpleRubyBlockChameleon(@NonNls final String debugName,
                                                                       final IElementType start,
                                                                       final IElementType end,
                                                                       final int startLength) {
        return new IChameleonElementType(debugName, RHTMLLanguage.RHTML) {
            public ASTNode parseContents(ASTNode chameleon) {
                return parseSimpleRubyBlock(chameleon, start, end, startLength);
            }
        };
    }

    public static ASTNode parseSimpleRubyBlock(final ASTNode chameleon,
                                               final IElementType start,
                                               final IElementType end,
                                               final int startLength) { //start element lenght
        final CharSequence chars = ((CharTableBasedLeafElementImpl)chameleon).getInternedText();
        final CharTable charTableByTree = SharedImplUtil.findCharTableByTree(chameleon);
        final FileElement treeElement =
                new DummyHolder(((TreeElement)chameleon).getManager(),
                                null,
                                charTableByTree).getTreeElement();
        TreeUtil.addChildren(treeElement,
                             Factory.createLeafElement(start, chars, 0,
                                                       Math.min(startLength, chars.length()),
                                                       charTableByTree));
        int omit_at_start = 0;
        int omit_at_end = 0;
        final RHTMLRubyText rubyText = new RHTMLRubyText();
        
        // Completed rhtml block : {START}[omit_at_start][ruby_code]%>
        if (chars.length() >= startLength ) {
            // check OMIT_NEW_LINE modifier "-" after start, e.g <%- or <%=
            if (chars.charAt(startLength) == '-') {
                TreeUtil.addChildren(rubyText,
                                     Factory.createLeafElement(RHTMLTokenType.OMIT_NEW_LINE,
                                     chars,
                                     startLength,
                                     1,
                                     charTableByTree));
                omit_at_start = 1;
            }
        }

        if (chars.length() > startLength + 1
            && chars.charAt(chars.length() - 1) == '>'
            && chars.charAt(chars.length() - 2) == '%') {

            TreeUtil.addChildren(treeElement, rubyText);

            // if exist text between START element and %>
            if (chars.length() > startLength + 2) {    // 2 = "%>".length
                // check OMIT_NEW_LINE modifier "-" before end, e.g -%> or -%%> etc
                if (chars.charAt(startLength) == '-') {
                    TreeUtil.addChildren(rubyText,
                                         Factory.createLeafElement(RHTMLTokenType.OMIT_NEW_LINE,
                                         chars,
                                         chars.length() - 3,
                                         1,
                                         charTableByTree));
                    omit_at_end = 1;
                }

                // ruby code
                TreeUtil.addChildren(rubyText,
                                     Factory.createLeafElement(RHTMLTokenType.RUBY_CODE_CHARACTERS,
                                                               chars,
                                                               startLength + omit_at_start,
                                                               chars.length() - 2 - omit_at_end,
                                                               charTableByTree));
            }
            TreeUtil.addChildren(treeElement,
                                 Factory.createLeafElement(end, chars,
                                                           chars.length() - 2, chars.length(),
                                                           charTableByTree));
        } else if (chars.length() > 2) {
            // it is incomplete block, e.g.: {START}[omit_at_start][ruby_code]EOF
            TreeUtil.addChildren(treeElement, rubyText);
            TreeUtil.addChildren(rubyText,
                                 Factory.createLeafElement(RHTMLTokenType.RUBY_CODE_CHARACTERS,
                                                           chars,
                                                           startLength + omit_at_start,
                                                           chars.length(),
                                                           charTableByTree));
        }

        return treeElement.getFirstChildNode();
  }
*/

    public static boolean isInRHTMLFile(final PsiElement element) {
        if (element == null) {
            return false;
        }
        final PsiFile psiFile = element.getContainingFile();
        if (psiFile == null) {
            return false;
        }
        final Language language = psiFile.getViewProvider().getBaseLanguage();
        return language == RHTMLLanguage.RHTML;
    }
}
