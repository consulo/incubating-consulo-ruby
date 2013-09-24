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
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.filters.ElementFilter;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.xml.XmlTokenType;
import org.jetbrains.plugins.ruby.addins.jsSupport.JavaScriptIntegrationUtil;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 2, 2007
 */
public class RHTMLInjectionInStringsCVariant extends CompletionVariant {
    public RHTMLInjectionInStringsCVariant() {

        super(PsiElement.class, new MyRTHMLInjectionStartFilter());
    }

    private static class MyRTHMLInjectionStartFilter implements ElementFilter {
        public boolean isAcceptable(final Object element, final PsiElement context) {
            final ASTNode node = context.getNode();
            return ifInStringTokenAfterInjectionStartChar(node);
        }

        public boolean isClassAcceptable(final Class hintClass) {
            return true;
        }
    }
    public static boolean ifInStringTokenAfterInjectionStartChar(final ASTNode node) {
        final IElementType nodeType = node != null ? node.getElementType() : null;
        final String nodeText = node != null ? node.getText() : null;
        if ((isJSStringContentNode(nodeType) || nodeType == XmlTokenType.XML_ATTRIBUTE_VALUE_TOKEN)
                && nodeText != null) {
            final int index = nodeText.indexOf(CompletionUtil.DUMMY_IDENTIFIER);
            if (index != -1 && index > 0) {
                return nodeText.charAt(index -1) == '<';
            }
        }
        return false;
    }

    public static boolean isJSStringContentNode(final IElementType nodeType) {
        return RApplicationSettings.getInstance().isJsSupportEnabled()
                && JavaScriptIntegrationUtil.isJSStringContentNode(nodeType);
    }

}
