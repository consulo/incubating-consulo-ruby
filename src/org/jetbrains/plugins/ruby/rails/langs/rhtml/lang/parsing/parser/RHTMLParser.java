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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser;

import static org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType.RHTML_COMMENT_END;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.impl.source.parsing.xml.XmlParsing;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.xml.XmlElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.04.2007
 */
public class RHTMLParser implements PsiParser {
    @NotNull
    public ASTNode parse(final IElementType root, final PsiBuilder builder, LanguageVersion languageVersion) {
        builder.enforceCommentTokens(TokenSet.EMPTY);

        final PsiBuilder.Marker file = builder.mark();
        final RHTMLParsing parsing = new RHTMLParsing(builder);

        parsing.parseDocument();
        file.done(RHTMLElementTypeEx.RHTML_FILE);

        return builder.getTreeBuilt();
    }

    private static class RHTMLParsing extends XmlParsing {
        private int openTagsCount = 0;
        private PsiBuilder myPsiBuilder;

        public RHTMLParsing(final PsiBuilder builder) {
            super(builder);
            myPsiBuilder = builder;
        }

        protected void parseComment() {
            final PsiBuilder.Marker comment = mark();
            advance();
            while (!eof()) {
                final IElementType tt = token();
                if (tt == RHTMLTokenType.RHTML_COMMENT_END) {
                    break;
                }
                advance();
            }
            if (token() == RHTML_COMMENT_END) {
                advance();
            } else {
                mark().error(RBundle.message("rhtml.parsing.named.element.is.not.closed", "comment"));
            }
            comment.done(RHTMLElementType.RHTML_COMMENT_ELEMENT);
        }

        //is comment start token
        protected boolean isCommentToken(final IElementType tt) {
            return tt == RHTMLTokenType.RHTML_COMMENT_START;
        }

        public void parseTagContent() {
            while (!RHTMLPsiUtil.isRubyCodeInjectionEnd(token()) && !eof()) {
                final IElementType tt = token();
                if (RHTMLPsiUtil.isRubyCodeInjectionStart(tt)
                    || RHTMLPsiUtil.isRubyCodeInjectionEnd(tt)) {

                    parseRubyCodeInjection();
                } else if (isCommentToken(tt)) {

                    parseComment();
                } else if (tt == RHTMLTokenType.FLEX_ERROR) {
                    final PsiBuilder.Marker flexError = mark();
                    advance();
                    flexError.error(RBundle.message("rhtml.parsing.flex.error"));
                }
                else {
                    advance();
                }
            }
        }

        public void parseDocument() {
            final PsiBuilder.Marker doc = mark();

            mark().done(XmlElementType.XML_PROLOG);

            while (!eof()) {
                parseTagContent();
            }
            doc.done(XmlElementType.HTML_DOCUMENT);
        }

        private void parseRubyCodeInjection() {
            if (RHTMLPsiUtil.isRubyCodeInjectionStart(token())){
                openTagsCount++;
            }
            final PsiBuilder.Marker tag = mark();

            advance();
            final PsiBuilder.Marker content = mark();

            parseTagContent();

            if (RHTMLPsiUtil.isRubyCodeInjectionEnd(token())) {
                openTagsCount--;
                if (openTagsCount < 0) {
                    openTagsCount = 0;
                    tag.doneBefore(RHTMLElementType.RHTML_XML_TAG, content, RBundle.message("rhtml.parsing.named.element.is.not.closed", "injection"));
                    content.drop();
                    return;
                }
            } else {
                error(RBundle.message("rhtml.parsing.unexpected.end.of.file"));
            }
            content.drop();
            advance();
            tag.done(RHTMLElementType.RHTML_XML_TAG);
        }

        private void error(final String message) {
            myPsiBuilder.error(message);
        }
    }
}
