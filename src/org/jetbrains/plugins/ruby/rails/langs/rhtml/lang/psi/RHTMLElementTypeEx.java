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

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.RHTMLLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.impl.HTMLTemplateInRHTMLTypeImpl;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.impl.RubyDeclarationsInRHTMLTypeImpl;
import com.intellij.psi.tree.IFileElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.04.2007
 */
public interface RHTMLElementTypeEx {
    //Roots

    //RHTML root of rhtml file
    IFileElementType RHTML_FILE = new IFileElementType("RHTML_FILE", RHTMLLanguage.INSTANCE);

    //HTML root of rhtml file
    IFileElementType HTML_TEMPLATE_IN_RHTML_ROOT = new HTMLTemplateInRHTMLTypeImpl("HTML_TEMPLATE_IN_RHTML_ROOT");

    //Ruby root of rhtml file
    IFileElementType RUBY_DECLARATIONS_IN_RHTML_ROOT = new RubyDeclarationsInRHTMLTypeImpl("RUBY_DECLARATIONS_IN_RHTML_ROOT");
}
