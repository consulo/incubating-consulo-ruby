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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.highlighting.impl;

import com.intellij.codeInsight.highlighting.BraceMatchingUtil;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.04.2007
 */
public class RHTMLBraceMather extends BraceMatchingUtil.HtmlBraceMatcher {
    public static final int RHTML_INJECTION_TOKENS_GROUP = 23;

    public int getTokenGroup(final IElementType tokenType) {
        if (RHTMLTokenType.RHTML_ALL_INJECTIONS_START_END_TOKENS.contains(tokenType)) {
            return RHTML_INJECTION_TOKENS_GROUP;
        }

        return super.getTokenGroup(tokenType);
    }

     public boolean isPairBraces(IElementType tokenType1, IElementType tokenType2) {
         final boolean result = super.isPairBraces(tokenType1, tokenType2);

         if (result) {
             return true;
         }

         if (tokenType1.getLanguage() == tokenType2.getLanguage()) {
             final PairedBraceMatcher matcher = tokenType1.getLanguage().getPairedBraceMatcher();
             if (matcher != null) {
                 BracePair[] pairs = matcher.getPairs();
                 for (BracePair pair : pairs) {
                     final IElementType lType = pair.getLeftBraceType();
                     final IElementType rType = pair.getRightBraceType();
                     
                     if ((lType == tokenType1 && rType == tokenType2)
                         ||(lType == tokenType2 && rType == tokenType1)) {
                         return true;
                     }
                 }
             }
         }
         return false;
     }
}
