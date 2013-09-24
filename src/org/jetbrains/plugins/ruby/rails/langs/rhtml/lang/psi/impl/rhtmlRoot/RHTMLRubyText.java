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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.tree.ChangeUtil;
import com.intellij.psi.impl.source.tree.Factory;
import com.intellij.psi.impl.source.tree.LeafElement;
import com.intellij.psi.impl.source.tree.SharedImplUtil;
import com.intellij.psi.impl.source.xml.XmlElementImpl;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagChild;
import com.intellij.psi.xml.XmlText;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 16, 2007
 */
public class RHTMLRubyText extends XmlElementImpl implements XmlText {
    public RHTMLRubyText() {
        super(RHTMLElementType.RHTML_RUBY_XML_TEXT);
    }

    public String getValue() {
        return getText();
    }

    public void setValue(String s) throws IncorrectOperationException {
        final LeafElement leaf =
                Factory.createSingleLeafElement(RHTMLTokenType.RUBY_CODE_CHARACTERS,
                                                s, 0, s.length(),
                                                SharedImplUtil.findCharTableByTree(this),
                                                getManager());
        if (getFirstChildNode() != null) {
            ChangeUtil.replaceChild(this, getFirstChildNode(), leaf);
        } else {
            ChangeUtil.addChild(this, leaf, null);
        }
    }

    public XmlElement insertAtOffset(XmlElement element, int physicalOffset) throws IncorrectOperationException {
        throw new IncorrectOperationException("Not supported in RHTMLRubyXmlText");
    }

    public void insertText(String text, int displayOffset) throws IncorrectOperationException {
        final String oldText = getText();
        setValue(oldText.substring(0, displayOffset) + text + oldText.substring(displayOffset));
    }

    public void removeText(int displayStart, int displayEnd) throws IncorrectOperationException {
        final String oldText = getText();
        setValue(oldText.substring(0, displayStart) + oldText.substring(displayEnd));
    }

    public int physicalToDisplay(int offset) {
        return offset;
    }

    public int displayToPhysical(int offset) {
        return offset;
    }

    @Nullable
    public XmlText split(int displayIndex) {
        throw new UnsupportedOperationException("split is not implemented in : " + getClass());
    }

    public XmlTag getParentTag() {
        return (XmlTag) getParent();
    }

    public XmlTagChild getNextSiblingInTag() {
        final PsiElement nextSibling = getNextSibling();
        if (nextSibling instanceof XmlTagChild)
            return (XmlTagChild) nextSibling;
        return null;
    }

    public XmlTagChild getPrevSiblingInTag() {
        final PsiElement prevSibling = getPrevSibling();
        if (prevSibling instanceof XmlTagChild)
            return (XmlTagChild) prevSibling;
        return null;
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        visitor.visitXmlText(this);
    }

    public String toString() {
        return "RHTMLRubyXmlText";
    }
}
