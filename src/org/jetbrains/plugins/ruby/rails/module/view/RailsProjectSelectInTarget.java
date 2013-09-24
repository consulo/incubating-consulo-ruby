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

package org.jetbrains.plugins.ruby.rails.module.view;

import com.intellij.ide.impl.ProjectViewSelectInTarget;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.util.PsiUtil;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

/**
 * Created by IntelliJ IDEA.
 * @author: oleg
 * @date: 15.09.2006
 */
public class RailsProjectSelectInTarget extends ProjectViewSelectInTarget {
    protected RailsProjectSelectInTarget(final Project project) {
        super(project);
    }

    public String toString() {
        return RBundle.message("select.in.rails");
    }

    /**
     * Used by autoscroll from source
     *
     * @param file File to select
     * @return true if we can select
     */
    protected boolean canSelect(PsiFile file) {
         return RailsProjectViewPane.getInstance(myProject).canSelect(file);
    }

    protected boolean canWorkWithCustomObjects() {
        return false;
    }

    public String getMinorViewId() {
        return RailsProjectViewPane.getInstance(myProject).getId();
    }

    public float getWeight() {
        // magic number
        return 5;
    }

    public boolean isSubIdSelectable(String subId, VirtualFile file) {
        return true;
    }

    protected boolean canSelect(PsiFileSystemItem psiFileSystemItem) {
        return true;
    }

    public void select(final PsiElement element, final boolean requestFocus) {
        PsiElement toSelect = getElementToSelect1(element);
        if (toSelect == null) return;
        PsiElement originalElement = toSelect.getOriginalElement();
        final VirtualFile virtualFile = PsiUtil.getVirtualFile(originalElement);
        select(originalElement, virtualFile, requestFocus);
    }

    private static PsiElement getElementToSelect1(final PsiElement element) {
        PsiFile baseRootFile = getBaseRootFile1(element);
        if (baseRootFile == null) return null;
        PsiElement current = element;
        while (current != null) {
            if (current instanceof RContainer) {
                break;
            }
            current = current.getParent();
        }
        return current != null ? current : baseRootFile;
    }

    private static PsiFile getBaseRootFile1(PsiElement element) {
        final PsiFile containingFile = element.getContainingFile();
        if (containingFile == null) {
            return null;
        }

        final FileViewProvider viewProvider = containingFile.getViewProvider();
        return viewProvider.getPsi(viewProvider.getBaseLanguage());
    }
}
