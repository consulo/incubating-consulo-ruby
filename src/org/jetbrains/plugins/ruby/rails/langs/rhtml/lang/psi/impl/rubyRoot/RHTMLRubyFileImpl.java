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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rubyRoot;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import static org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementTypeEx.RUBY_DECLARATIONS_IN_RHTML_ROOT;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLRubyFile;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiFileBase;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 02.04.2007
 */
public class RHTMLRubyFileImpl extends RPsiFileBase implements RHTMLRubyFile {
    public RHTMLRubyFileImpl(final FileViewProvider viewProvider) {
        super(RUBY_DECLARATIONS_IN_RHTML_ROOT, RUBY_DECLARATIONS_IN_RHTML_ROOT, viewProvider);
    }

    public Lexer createLexer() {
        return null;
    }

    @NotNull
    public RHTMLFileViewProvider getViewProvider() {
        return (RHTMLFileViewProvider)super.getViewProvider();
    }
     protected boolean isPsiUpToDate(VirtualFile vFile) {
         final FileViewProvider viewProvider = myManager.findViewProvider(vFile);
         assert viewProvider != null;
         return viewProvider.getPsi(RubyLanguage.RUBY) == this;
     }

    public String toString() {
        return "RHTMLRubyFile:" + getName();
    }
}
