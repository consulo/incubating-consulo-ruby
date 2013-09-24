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

package org.jetbrains.plugins.ruby.addins.jsSupport;

import com.intellij.lang.javascript.JSTokenTypes;
import com.intellij.lang.javascript.psi.impl.JSEmbeddedContentImpl;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 6, 2007
 */
public class JavaScriptIntegrationUtil {
    public static boolean isJSEmbeddedContent(final PsiElement htmlPsi) {
        return htmlPsi instanceof JSEmbeddedContentImpl;
    }

    public static boolean isJSStringContentNode(final IElementType nodeType) {
        return nodeType == JSTokenTypes.STRING_LITERAL;
    }
}
