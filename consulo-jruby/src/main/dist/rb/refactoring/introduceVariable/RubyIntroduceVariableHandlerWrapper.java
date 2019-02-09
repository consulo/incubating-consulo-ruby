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

package rb.refactoring.introduceVariable;

import javax.annotation.Nonnull;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;

import javax.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 21, 2007
 * It`s just a wrapper for JRubyWritten handler 
 */
public class RubyIntroduceVariableHandlerWrapper implements RefactoringActionHandler {
    private RubyIntroduceVariableHandler myJRubyHandler;

    public void setJRubyHandler(@Nonnull final RubyIntroduceVariableHandler handler) {
        myJRubyHandler = handler;
    }

    public RubyIntroduceVariableHandler getJRubyHandler() {
        return myJRubyHandler;
    }

    @Override
	public void invoke(@Nonnull Project project, Editor editor, PsiFile file, @Nullable DataContext dataContext) {
        myJRubyHandler.invoke(project, editor, file, dataContext);
    }

    @Override
	public void invoke(@Nonnull Project project, @Nonnull PsiElement[] elements, @Nullable DataContext dataContext) {
        myJRubyHandler.invokeOutter(project, elements, dataContext);
    }
}
