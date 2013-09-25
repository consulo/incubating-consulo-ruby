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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion;

import com.intellij.codeInsight.completion.CompletionVariant;
import com.intellij.codeInsight.completion.HtmlCompletionData;
import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion.variants.RHTMLInjectionInStringsCVariant;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion.variants.RHTMLInjectionInTagContentCVariant;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.04.2007
 */
public class RHTMLCompletionData extends HtmlCompletionData {
    public static final String RHTML_SCRIPTLET_START = "%";
    public static final String RHTML_EXPRESSION_START = "%=";
    public static final String RHTML_COMMENT_START = "%#";
    public static final String RHTML_SCRIPTLET_WITH_OMIT_START = "%-";
    public static final String RHTML_INJECTION_CLOSE = "%>";
    private String INECTION_START_PREFIX = "<";

    public RHTMLCompletionData() {
        super();
        final String[] inTagCompletionVariants = {
                RHTML_SCRIPTLET_START,
                RHTML_EXPRESSION_START,
                RHTML_SCRIPTLET_WITH_OMIT_START,
                RHTML_COMMENT_START};
        setupAndRegistrVariant(inTagCompletionVariants, new RHTMLInjectionInTagContentCVariant());

        final String[] inStringCompletionVariants = {
                INECTION_START_PREFIX + RHTML_SCRIPTLET_START,
                INECTION_START_PREFIX + RHTML_EXPRESSION_START,
                INECTION_START_PREFIX + RHTML_SCRIPTLET_WITH_OMIT_START,
                INECTION_START_PREFIX + RHTML_COMMENT_START};
        setupAndRegistrVariant(inStringCompletionVariants, new RHTMLInjectionInStringsCVariant());
    }

    private void setupAndRegistrVariant(final String[] completion_variants,
                                        final CompletionVariant variant) {
        variant.addCompletion(completion_variants);
        variant.setInsertHandler(new RHTMLTagInsertHandler());
        registerVariant(variant);
    }

    @Override
	public String findPrefix(final PsiElement insertedElement, final int offset) {
        return RHTMLInjectionInStringsCVariant.ifInStringTokenAfterInjectionStartChar(insertedElement.getNode())
                ? INECTION_START_PREFIX
                : super.findPrefix(insertedElement, offset);
    }
}
