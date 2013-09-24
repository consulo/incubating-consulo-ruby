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
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.impl.source.tree.ChildRole;
import com.intellij.psi.impl.source.tree.XmlFileElement;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 14.04.2007
 */

public class RHTMLXMLFileElement extends XmlFileElement {
    private static final Logger LOG = Logger.getInstance(RHTMLXMLFileElement.class.getName());

    public RHTMLXMLFileElement(IElementType type) {
        super(type);
    }

    public int getChildRole(ASTNode child) {
        LOG.assertTrue(child.getTreeParent() == this);
        final IElementType type = child.getElementType();
        if (type == HTML_DOCUMENT || type == RHTMLElementType.RHTML_DOCUMENT) {
            return ChildRole.HTML_DOCUMENT;
        } else {
            return ChildRole.NONE;
        }
    }
}
