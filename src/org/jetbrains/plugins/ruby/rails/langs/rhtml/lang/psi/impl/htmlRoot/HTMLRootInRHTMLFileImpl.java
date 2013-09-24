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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.htmlRoot;

import com.intellij.lang.Language;
import com.intellij.lang.StdLanguages;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiSubstitutor;
import com.intellij.psi.impl.source.html.ScriptSupportUtil;
import com.intellij.psi.impl.source.parsing.ChameleonTransforming;
import com.intellij.psi.impl.source.tree.ChildRole;
import com.intellij.psi.impl.source.tree.CompositeElement;
import com.intellij.psi.impl.source.xml.XmlFileImpl;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.xml.XmlDocument;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.RHTMLFileViewProvider;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementTypeEx;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.04.2007
 */
public class HTMLRootInRHTMLFileImpl extends XmlFileImpl implements HTMLRootInRHTMLFile {
    public HTMLRootInRHTMLFileImpl(final FileViewProvider provider) {
        super(provider,
                RHTMLElementTypeEx.HTML_TEMPLATE_IN_RHTML_ROOT);
    }

    public String toString() {
        return "Html In RHTML File: " + getName();
    }

    protected boolean isPsiUpToDate(VirtualFile vFile) {
        final FileViewProvider viewProvider = myManager.findViewProvider(vFile);
        assert viewProvider != null;
        return viewProvider.getPsi(StdLanguages.HTML) == this;
    }

    @NotNull
    public Language getLanguage() {
        return ((RHTMLFileViewProvider) getViewProvider()).getTemplateDataLanguage();
    }

    public XmlDocument getDocument() {
        CompositeElement treeElement = calcTreeElement();
        ChameleonTransforming.transformChildren(treeElement);
        return (XmlDocument) treeElement.findChildByRoleAsPsiElement(ChildRole.HTML_DOCUMENT);
    }

    @NotNull
    public FileType getFileType() {
        //TODO: investagate LanguageInjector

        // file type used to attach reference provider, etc
        return getViewProvider().getVirtualFile().getFileType();
    }

    public boolean isTemplateDataFile() {
        return true;
    }

    public PsiFile getOriginalFile() {
        final PsiFile original = super.getOriginalFile();
        if (original == null) {
            RHTMLFile rhtmlFile = RHTMLPsiUtil.getRHTMLFileRoot(this);
            final PsiFile rhtmlOriginal = rhtmlFile != null
                    ? rhtmlFile.getOriginalFile()
                    : null;
            if (rhtmlOriginal != null) {
                return rhtmlOriginal.getViewProvider().getPsi(getLanguage());
            }
        }
        return original;
    }

    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull PsiSubstitutor substitutor, PsiElement lastParent, @NotNull PsiElement place) {
        return super.processDeclarations(processor, substitutor, lastParent, place)
                && ScriptSupportUtil.processDeclarations(this, processor, substitutor, lastParent, place);

    }
}

