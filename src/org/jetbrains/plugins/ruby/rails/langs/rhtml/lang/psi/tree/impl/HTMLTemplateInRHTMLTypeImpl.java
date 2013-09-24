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

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.StdLanguages;
import com.intellij.lexer.Lexer;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.jsp.jspJava.OuterLanguageElement;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.CharTable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer.BlackAndWhiteLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer.HTMLCuttingLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.impl.OuterRHTMLElementInHTMLImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.TreePatcher;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.04.2007
 */

public class HTMLTemplateInRHTMLTypeImpl extends TemplateWithOuterFragmentsTypeImpl {
    private static final TokenSet TOKENS_TO_MERGE =
            TokenSet.create(RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML,
                            RHTMLTokenTypeEx.TEMPLATE_CHARACTERS_IN_RHTML);

    public HTMLTemplateInRHTMLTypeImpl(final String debugName) {
        super(debugName, StdLanguages.HTML);
    }

    public ASTNode parseContents(final ASTNode chameleon) {
        final PsiFile file = (PsiFile)TreeUtil.getFileElement((TreeElement)chameleon).getPsi();
        PsiFile originalFile = file.getOriginalFile();
        if (originalFile == null) {
            originalFile = file;
        }
        final RHTMLFileViewProvider viewProvider = (RHTMLFileViewProvider)originalFile.getViewProvider();
        final Language templateLanguage = viewProvider.getTemplateDataLanguage();

        final BlackAndWhiteLexer blackAndWhiteLexer = new HTMLCuttingLexer();

        return parseContents(chameleon, templateLanguage, file, blackAndWhiteLexer,
                             TOKENS_TO_MERGE,
                             RHTMLTokenTypeEx.TEMPLATE_CHARACTERS_IN_RHTML,
                             RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML);
    }

    protected TreePatcher createTreePatcher() {
        return new TemplatesTreePatcher(TokenSet.EMPTY);
    }

    protected OuterLanguageElement createOuterElement(final Lexer lexer,
                                                      final CharTable table) {
        return new OuterRHTMLElementInHTMLImpl(RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML,
                                               lexer.getBufferSequence(),
                                               lexer.getTokenStart(),
                                               lexer.getTokenEnd(), table);
    }
}

