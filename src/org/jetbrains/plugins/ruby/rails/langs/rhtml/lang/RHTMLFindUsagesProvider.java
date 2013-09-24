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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.Language;
import com.intellij.lang.cacheBuilder.SimpleWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.lang.findUsages.LanguageFindUsages;
import com.intellij.lang.xml.XmlFindUsagesProvider;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.04.2007
 */
public class RHTMLFindUsagesProvider extends XmlFindUsagesProvider {

  @Nullable
  public WordsScanner getWordsScanner() {
    return new SimpleWordsScanner();
  }

  public boolean canFindUsagesFor(final @NotNull PsiElement psiElement) {
    if (super.canFindUsagesFor(psiElement)) {
        return true;
    }

    final Language lang = psiElement.getLanguage();
    if (lang instanceof eRubyLanguage) {
        return false;
    }
    final FindUsagesProvider delegateProvider = LanguageFindUsages.INSTANCE.forLanguage(lang);
    return delegateProvider.canFindUsagesFor(psiElement);
  }

  @NotNull
  public String getType(@NotNull PsiElement element) {
    final String supertype = super.getType(element);
    if (supertype != null) {
        return supertype;
    }
    final Language lang = element.getLanguage();
    if (lang instanceof eRubyLanguage) {
        return "";
    }
    final FindUsagesProvider delegateProvider = LanguageFindUsages.INSTANCE.forLanguage(lang);
    return delegateProvider != null ? delegateProvider.getType(element)
                                    : "";
  }

  @NotNull
  public String getDescriptiveName(@NotNull PsiElement element) {
    final String supertext = super.getDescriptiveName(element);
    if (supertext != null) {
        return supertext;
    }

    final Language lang = element.getLanguage();
    if (lang instanceof eRubyLanguage) {
        return "";
    }
    final FindUsagesProvider delegateProvider = LanguageFindUsages.INSTANCE.forLanguage(lang);
    return delegateProvider != null ? delegateProvider.getDescriptiveName(element)
                                    : "";
  }

  @NotNull
  public String getNodeText(@NotNull PsiElement element, boolean useFullName) {
    final String supertext = super.getNodeText(element, useFullName);
    if (supertext != null) {
        return supertext;
    }

    final Language lang = element.getLanguage();
    if (lang instanceof eRubyLanguage) {
        return "";
    }
    final FindUsagesProvider delegateProvider = LanguageFindUsages.INSTANCE.forLanguage(lang);
    return delegateProvider != null ? delegateProvider.getNodeText(element, useFullName)
                                    : "";
  }
}
