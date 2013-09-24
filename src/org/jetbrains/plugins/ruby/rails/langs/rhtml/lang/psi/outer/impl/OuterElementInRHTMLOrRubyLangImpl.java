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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.impl;

import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.util.CharTable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.OuterElementInRHTMLOrRubyLang;
import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.04.2007
 */
public class OuterElementInRHTMLOrRubyLangImpl extends LeafPsiElement implements OuterElementInRHTMLOrRubyLang {
    public OuterElementInRHTMLOrRubyLangImpl(IElementType type, CharSequence buffer, int startOffset, int endOffset, CharTable table) {
        super(type, buffer, startOffset, endOffset, table);
    }

    public String toString() {
      return "Outer: " + getElementType() + ", characters in RHTML or Ruby lang";
    }

    public void accept(@NotNull final PsiElementVisitor visitor) {
        visitor.visitOuterLanguageElement(this);
    }
}
