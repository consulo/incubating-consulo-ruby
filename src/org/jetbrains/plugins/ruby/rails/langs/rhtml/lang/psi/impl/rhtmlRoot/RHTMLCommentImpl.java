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

import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.impl.source.xml.XmlElementImpl;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlComment;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlTagChild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 14.04.2007
 */
public class RHTMLCommentImpl  extends XmlElementImpl implements XmlComment {
    public RHTMLCommentImpl() {
        super(RHTMLElementType.RHTML_COMMENT_ELEMENT);
    }

    public IElementType getTokenType() {
        return getElementType();
    }

    public XmlTag getParentTag() {
    if(getParent() instanceof XmlTag) return (XmlTag)getParent();
    return null;
  }

  public XmlTagChild getNextSiblingInTag() {
    if(getParent() instanceof XmlTag) return (XmlTagChild)getNextSibling();
    return null;
  }

  public XmlTagChild getPrevSiblingInTag() {
    if(getParent() instanceof XmlTag) return (XmlTagChild)getPrevSibling();
    return null;
  }

  public void accept(@NotNull PsiElementVisitor visitor) {
    visitor.visitXmlComment(this);
  }
}
