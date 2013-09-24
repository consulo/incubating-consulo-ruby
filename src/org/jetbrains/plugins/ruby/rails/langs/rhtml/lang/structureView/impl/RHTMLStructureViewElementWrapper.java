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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.structureView.impl;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLPsiUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 21.05.2007
 */
//TODO Refactor with JSP
public class RHTMLStructureViewElementWrapper <V extends PsiElement> implements StructureViewTreeElement {
    private final StructureViewTreeElement myTreeElement;
    private final RHTMLFile myRHTMLFile;

    public RHTMLStructureViewElementWrapper(@NotNull StructureViewTreeElement treeElement, @NotNull RHTMLFile rhtmlFile) {
        myTreeElement = treeElement;
        myRHTMLFile = rhtmlFile;
    }

    public V getValue() {
        //noinspection unchecked
        return (V)myTreeElement.getValue();
    }

    public StructureViewTreeElement[] getChildren() {
        TreeElement[] baseChildren = myTreeElement.getChildren();
        List<StructureViewTreeElement> result = new ArrayList<StructureViewTreeElement>();
        for (TreeElement element : baseChildren) {
            StructureViewTreeElement wrapper = new RHTMLStructureViewElementWrapper((StructureViewTreeElement)element, myRHTMLFile);

            result.add(wrapper);
        }
        return result.toArray(new StructureViewTreeElement[result.size()]);
    }

    public ItemPresentation getPresentation() {
        return myTreeElement.getPresentation();
    }

    public void navigate(final boolean requestFocus) {
        Navigatable navigatable = getNavigatableInRHTMLFile();
        navigatable.navigate(requestFocus);
    }

    private Navigatable getNavigatableInRHTMLFile() {
        PsiElement element = (PsiElement)myTreeElement.getValue();
        int offset = element.getTextRange().getStartOffset();
        final RHTMLFile rhtmlRoot = RHTMLPsiUtil.getRHTMLFileRoot(myRHTMLFile);
        assert rhtmlRoot != null;

        PsiElement rhtmlElement = rhtmlRoot.findElementAt(offset);
        while (true) {
            if (rhtmlElement == null || rhtmlElement.getTextRange().getStartOffset() != offset)
                break;
            if (rhtmlElement instanceof Navigatable) {
                return (Navigatable)rhtmlElement;
            }
            rhtmlElement = rhtmlElement.getParent();
        }
        return null;
    }

    public boolean canNavigate() {
        return getNavigatableInRHTMLFile() != null;
    }

    public boolean canNavigateToSource() {
        return canNavigate();
    }
}

