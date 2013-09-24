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

package org.jetbrains.plugins.ruby.rails.langs.rhtml;

import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.MultiplePsiFilesPerDocumentFileViewProvider;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.RHTMLLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.htmlRoot.HTMLRootInRHTMLFileImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rubyRoot.RHTMLRubyFileImpl;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.04.2007
 */
public class RHTMLFileViewProvider extends MultiplePsiFilesPerDocumentFileViewProvider {
    private Set<Language> myViews = null;

    public RHTMLFileViewProvider(final PsiManager manager,
                                     final VirtualFile virtualFile,
                                     final boolean physical) {
        super(manager, virtualFile, physical);
    }

    @NotNull
    public Language getBaseLanguage() {
        return RHTMLLanguage.RHTML;
    }

    @NotNull
    public Language getTemplateDataLanguage() {
        return StdLanguages.HTML;
    }

    @NotNull
    public Set<Language> getRelevantLanguages() {
        if (myViews != null) {
            return myViews;
        }
        Set<Language> views = new HashSet<Language>(4);
        views.add(RHTMLLanguage.RHTML);
        views.add(RubyLanguage.RUBY);
        views.add(StdLanguages.HTML);

        return myViews = views;
    }

    protected FileViewProvider cloneInner(final VirtualFile copy) {
        return new RHTMLFileViewProvider(getManager(), copy, false);
    }

    protected PsiFile createFile(final Language lang) {
        if (lang == RubyLanguage.RUBY) {
            // at current moment original file is used
            // only by RPsiBase.getVirtualFile(). This method can't return null
            final RHTMLRubyFileImpl ruby = new RHTMLRubyFileImpl(this);
            ruby.setOriginalFile(getPsi(RHTMLLanguage.RHTML));
            return ruby;
        } else if (lang == StdLanguages.HTML) {
            //If original file ins't null CssPropertyDescriptor.buildContextPath() leds to NPE
            // in CssShorthandExpandProcessor.processReferences()
            //htmlInRHTMLFile.setOriginalFile(getPsi(RHTMLLanguage.RHTML));
            return new HTMLRootInRHTMLFileImpl(this);
        } else if (lang == RHTMLLanguage.RHTML) {
            final ParserDefinition def = lang.getParserDefinition();
            assert def != null; //not null for RHTML Language
            return def.createFile(this);
        }
        return null;
    }
}
