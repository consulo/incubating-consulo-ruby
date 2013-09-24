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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenTypeEx;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 20.04.2007
 */

public class HTMLCuttingLexer extends BlackAndWhiteLexer {
    public HTMLCuttingLexer() {
        super(new _RHTMLLexer(),
              RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML, null,
              RHTMLTokenTypeEx.RHTML_INJECTION_IN_HTML);
    }

    protected boolean isWhiteData(final IElementType tokenType) {
        return tokenType == RHTMLTokenTypeEx.TEMPLATE_CHARACTERS_IN_RHTML;
    }
}
