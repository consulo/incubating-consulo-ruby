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

package org.jetbrains.plugins.ruby.rails.langs.yaml;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 11, 2008
 */
public class YAMLSyntaxHighlighter extends SyntaxHighlighterBase implements YAMLTokenTypes {

    private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

    static {
        ATTRIBUTES.put(SCALAR_TEXT_TYPE, YAMLHighlighter.SCALAR_TEXT);
        ATTRIBUTES.put(SCALAR_STRING_TYPE, YAMLHighlighter.SCALAR_STRING);
        ATTRIBUTES.put(SCALAR_DSTRING_TYPE, YAMLHighlighter.SCALAR_DSTRING);
        ATTRIBUTES.put(SCALAR_LIST_TYPE, YAMLHighlighter.SCALAR_LIST);
        ATTRIBUTES.put(SCALAR_VALUE_TYPE, YAMLHighlighter.SCALAR_VALUE);
        ATTRIBUTES.put(SCALAR_KEY_TYPE, YAMLHighlighter.SCALAR_KEY);
        ATTRIBUTES.put(VALUE_TYPE, YAMLHighlighter.VALUE);
        ATTRIBUTES.put(COMMENT_TYPE, YAMLHighlighter.COMMENT);
    }


    @NotNull
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        return pack(ATTRIBUTES.get(tokenType));
    }

    @NotNull
    public Lexer getHighlightingLexer() {
        return new YAMLLexer();
    }
}
