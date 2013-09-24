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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl;

import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.application.ex.ApplicationManagerEx;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.xml.breadcrumbs.BreadcrumbsLoaderComponentImpl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;

public class RFileImpl extends RPsiFileBase implements RFile {
    @NotNull private Language myLanguage;
    @NotNull private ParserDefinition myParserDefinition;

    public RFileImpl(final FileViewProvider viewProvider) {
        super(viewProvider);
        initLanguage(RubyFileType.RUBY.getLanguage());

        // For debug mode
        // This enables bread crumbs loader for ruby files.
        if (ApplicationManagerEx.getApplicationEx().isInternal()) {
            putUserData(BreadcrumbsLoaderComponentImpl.BREADCRUMBS_SUITABLE_FILE, new Object());
        }
    }

    @NotNull
    public FileType getFileType() {
        return RubyFileType.RUBY;
    }

    public String toString(){
        return "Ruby file";
    }

    @NotNull
    public final Language getLanguage() {
        return myLanguage;
    }

    public final Lexer createLexer() {
        return myParserDefinition.createLexer(getProject());
    }

//////////////////////////// PsiFileBase /////////////////////////////

    private void initLanguage(final Language language) {
        myLanguage = language;
        final ParserDefinition parserDefinition = language.getParserDefinition();
        if (parserDefinition == null) {
            throw new RuntimeException("PsiFileBase: language.getParserDefinition() returned null.");
        }
        myParserDefinition = parserDefinition;
        final IFileElementType nodeType = parserDefinition.getFileNodeType();
        init(nodeType, nodeType);
    }

}