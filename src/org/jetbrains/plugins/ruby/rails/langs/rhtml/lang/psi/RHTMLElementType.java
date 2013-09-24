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

import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IRHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.IXMLRHTMLElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.04.2007
 */
public interface RHTMLElementType {
    //RHTML Elements

    // Tag in RHTML Document
    IElementType RHTML_XML_TAG = new IXMLRHTMLElementType("RHTML_XML_TAG");

    // RHTML Comment tag 
    IElementType RHTML_COMMENT_ELEMENT = new IXMLRHTMLElementType("RHTML_COMMENT_ELEMENT");

    // Ruby code in pure RHTML root
    IElementType RHTML_RUBY_XML_TEXT = new IRHTMLElementType("RHTML_RUBY_XML_TEXT");

    // RHTML Document
    IElementType RHTML_DOCUMENT = new IRHTMLElementType("RHTML_DOCUMENT");

//    // <%= ... %>
//    IElementType RHTML_EXPRESSION =
//            RHTMLPsiUtil.createSimpleRubyBlockChameleon("RHTML_EXPRESSION",
//                                               RHTMLTokenType.RHTML_EXPRESSION_START,
//                                               RHTMLTokenType.RHTML_EXPRESSION_END, 3);
//
//    // <% ... %>
//    IElementType RHTML_SCRIPTLET =
//            RHTMLPsiUtil.createSimpleRubyBlockChameleon("RHTML_SCRIPTLET",
//                                               RHTMLTokenType.RHTML_SCRIPTLET_START,
//                                               RHTMLTokenType.RHTML_SCRIPTLET_END, 2);

}