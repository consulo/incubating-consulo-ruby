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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLPsiLeafElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 31.03.2007
 */
public interface RHTMLTokenType extends XmlTokenType {
//Outer tokens
    IElementType TEMPLATE_CHARACTERS_IN_RHTML =    new IRHTMLPsiLeafElementType("TEMPLATE_CHARACTERS_IN_RHTML");//template characters in rhtml lang, i.e. pure html code
    IElementType RUBY_CODE_CHARACTERS =            new IRHTMLPsiLeafElementType("RUBY_CODE_CHARACTERS"); //ruby characters in rhtml lang

//Pure RHTML tokens
    //injection modifiers
    IElementType OMIT_NEW_LINE =                   new IRHTMLElementType("OMIT_NEW_LINE", "-");  // "-" omit new line rhtml tag modifier

    IElementType RHTML_SCRIPTLET_START =           new IRHTMLElementType("<%");
    IElementType RHTML_SCRIPTLET_END =             new IRHTMLElementType("%>");

    IElementType RHTML_EXPRESSION_START =          new IRHTMLElementType("<%=");
    IElementType RHTML_EXPRESSION_END =            new IRHTMLElementType("%>");

    IElementType RHTML_COMMENT_START =             new IRHTMLElementType("RHTML_COMMENT_START", "<%#");
    IElementType RHTML_COMMENT_END =               new IRHTMLElementType("RHTML_COMMENT_END");
    IElementType RHTML_COMMENT_CHARACTERS =        new IRHTMLElementType("RHTML_COMMENT_CHARACTERS", "%>");
    IElementType RHTML_COMMENT_EOL =               new IRHTMLElementType("RHTML_COMMENT_EOL");  // "\n" or "\r" or "\r\n" - EOL in rhtml comment characters
    IElementType RHTML_COMMENT_WSL =               new IRHTMLElementType("RHTML_COMMENT_WSL");  // whitespaces line in rhtml comment characters


//For flex lexer debugging
    // for flex lexer debugging
    public IElementType FLEX_ERROR =               new IRHTMLElementType("FLEX_ERROR");

//Helpers
    // All comments tokens
    public TokenSet RHTML_ALL_COMMENT_TOKENS = TokenSet.create(
            RHTML_COMMENT_START,
            RHTML_COMMENT_CHARACTERS,
            RHTML_COMMENT_END,
            RHTML_COMMENT_EOL,
            RHTML_COMMENT_WSL
    );

    // Start separators for non comment injections
    public TokenSet RHTML_SEPARATORS_STARTS = TokenSet.create(
            RHTML_EXPRESSION_START,
            RHTML_SCRIPTLET_START
    );
    // Ends separators for non comment injections
    public TokenSet RHTML_SEPARATORS_ENDS = TokenSet.create(
            RHTML_EXPRESSION_END,
            RHTML_SCRIPTLET_END
    );

    // Ends separators for non comment injections
    public TokenSet RHTML_ALL_SEPARATORS_ENDS = TokenSet.create(
            RHTML_EXPRESSION_END,
            RHTML_SCRIPTLET_END,
            RHTML_COMMENT_END
    );

    // Separators(start/end) for rhtml scriplets
    TokenSet RHTML_SEPARATORS = TokenSet.create(
            RHTML_SCRIPTLET_START,
            RHTML_SCRIPTLET_END,

            RHTML_EXPRESSION_START,
            RHTML_EXPRESSION_END,

            RHTML_COMMENT_START,
            RHTML_COMMENT_END,

            FLEX_ERROR
    );

    // Start/end elements for brace matcher
    TokenSet RHTML_ALL_INJECTIONS_START_END_TOKENS = TokenSet.create(
            RHTML_SCRIPTLET_START,
            RHTML_SCRIPTLET_END,

            RHTML_EXPRESSION_START,
            RHTML_EXPRESSION_END,

            RHTML_COMMENT_START,
            RHTML_COMMENT_END
    );

    TokenSet RHTML_WHITE_SPECE_TOKENS = TokenSet.create(
            RHTML_COMMENT_EOL,
            RHTML_COMMENT_WSL
    );
}
