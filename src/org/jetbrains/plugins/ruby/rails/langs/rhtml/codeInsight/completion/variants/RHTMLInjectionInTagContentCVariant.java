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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion.variants;

import com.intellij.codeInsight.completion.CompletionUtil;
import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.xml.XmlTag;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 2, 2007
 */
public class RHTMLInjectionInTagContentCVariant extends CompletionVariant {
    public RHTMLInjectionInTagContentCVariant() {
        super(XmlTag.class, new MyRTHMLInjectionStartFilter());
    }

    private static class MyRTHMLInjectionStartFilter implements ElementFilter {

        @Override
		public boolean isAcceptable(final Object element, final PsiElement context) {
            //noinspection SimplifiableIfStatement
            if (!(element instanceof TreeElement)
                    || !CompletionUtil.DUMMY_IDENTIFIER.trim().equals(((TreeElement) element).getText())) {
                return false;
            }
            return context instanceof XmlTag;
        }

        @Override
		public boolean isClassAcceptable(final Class hintClass) {
            return true;
        }
    }
}
