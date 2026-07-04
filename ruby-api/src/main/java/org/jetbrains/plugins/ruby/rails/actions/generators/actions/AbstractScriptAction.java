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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions;

import consulo.content.bundle.Sdk;
import consulo.dataContext.DataContext;
import consulo.language.editor.CommonDataKeys;
import consulo.language.editor.util.IdeView;
import consulo.language.psi.PsiDirectory;
import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.util.IncorrectOperationException;
import consulo.module.Module;
import consulo.ui.annotation.RequiredUIAccess;
import consulo.ui.ex.action.AnAction;
import consulo.ui.ex.action.AnActionEvent;
import consulo.ui.ex.action.AnActionWithSyncUpdate;
import consulo.ui.ex.action.Presentation;
import consulo.ui.image.Image;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 27.11.2006
 */
public abstract class AbstractScriptAction extends AnAction implements AnActionWithSyncUpdate {

    public AbstractScriptAction(@Nullable final String text, @Nullable final String description, @Nullable final Image icon) {
        super(text, description, icon);
    }

    /**
     * @return Caption for Generate dialog.
     */
    protected abstract String getGenerateDialogTitle();

    /**
     * @return Caption for error message.
     */
    protected abstract String getErrorTitle();

    @SuppressWarnings({"UnusedParameters"})
    protected abstract void checkBeforeCreate(@Nonnull final String newName, @Nullable final PsiDirectory directory) throws IncorrectOperationException;

    protected abstract String[] createScriptParameters(final String inputString, final String railsAppHomePath);

    protected abstract boolean validateBeforeInvokeDialog(final Module module);

    protected abstract PsiElement[] invokeDialog(@Nonnull final Module module, @Nullable final PsiDirectory directory);

    @RequiredUIAccess
    @Override
    public void actionPerformed(final AnActionEvent e) {
        final DataContext dataContext = e.getDataContext();

        final IdeView view = e.getData(IdeView.KEY);
        final Module module = e.getData(CommonDataKeys.MODULE);
        final Sdk jdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);

        assert module != null;
        assert jdk != null;

        PsiDirectory dir = view == null ? null : view.getOrChooseDirectory();
        if (dir == null) {
            final PsiFile psiFile = dataContext.getData(CommonDataKeys.PSI_FILE);
            if (psiFile != null) {
                dir = psiFile.getParent();
            }
        }

        if (!validateBeforeInvokeDialog(module)) {
            return;
        }

        final PsiElement[] createdElements = invokeDialog(module, dir);

        if (view != null) {
            for (PsiElement createdElement : createdElements) {
                view.selectElement(createdElement);
            }
        }
    }

    @Override
    public void update(@Nonnull final AnActionEvent e) {
        final Presentation presentation = e.getPresentation();

        final Module module = e.getData(CommonDataKeys.MODULE);
        boolean show = false;
        if (module != null) {
            show = RModuleUtil.getModuleOrJRubyFacetSdk(module) != null;
        }
        AnActionUtil.updatePresentation(presentation, show, show);
    }
}
