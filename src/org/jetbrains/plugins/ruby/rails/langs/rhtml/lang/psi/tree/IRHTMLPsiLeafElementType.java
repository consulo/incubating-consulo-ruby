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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree;

import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.LeafPsiElementType;
import com.intellij.util.CharTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.RHTMLLanguage;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.outer.impl.OuterElementInRHTMLOrRubyLangImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.PresentableElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 12, 2007
 */
public class IRHTMLPsiLeafElementType extends LeafPsiElementType implements IRHTMLElement, PresentableElementType {
    private String myPresentationText;

    public IRHTMLPsiLeafElementType(@NotNull final String debugName) {
        this(debugName, null);
    }

    public IRHTMLPsiLeafElementType(@NotNull final String debugName,
                                    @Nullable final String presentationText) {
        super(debugName, RHTMLLanguage.RHTML);
        myPresentationText = presentationText != null ? presentationText : toString();
    }

    public PsiElement createLeafNode(final CharSequence text,
                                     final int startOffset, final int endOffset, final CharTable table) {
        return new OuterElementInRHTMLOrRubyLangImpl(this, text, startOffset, endOffset, table);
    }

    public String getPresentableName() {
        return myPresentationText;
    }
}
