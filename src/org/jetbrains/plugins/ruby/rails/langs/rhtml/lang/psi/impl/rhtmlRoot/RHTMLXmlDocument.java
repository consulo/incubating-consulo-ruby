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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.rhtmlRoot;

import com.intellij.lang.ASTNode;
import com.intellij.psi.impl.source.tree.ChildRole;
import com.intellij.psi.impl.source.xml.XmlDocumentImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlProlog;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 14.04.2007
 */

//For full support of RHTMLDocument, see JSP_DOCUMENT, JspXmlDocument and JspParser
public class RHTMLXmlDocument extends XmlDocumentImpl {
    public RHTMLXmlDocument() {
        super(RHTMLElementType.RHTML_DOCUMENT);
    }

    public int getChildRole(ASTNode child) {
        IElementType i = child.getElementType();
        if (i == XML_PROLOG) {
            return ChildRole.XML_PROLOG;
        } else {
            return ChildRole.NONE;
        }
    }

    public XmlProlog getProlog() {
        return (XmlProlog) findElementByTokenType(XML_PROLOG);
    }

    public XmlTag getRootTag() {
        return (XmlTag) findElementByTokenType(HTML_TAG);
    }

    public String toString() {
        return "PsiElement" + "(" + XML_DOCUMENT.toString() + ")";
    }
}
