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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;

/**
 * Created by IntelliJ IDEA.
*
* @author: Roman Chernyatchik
* @date: 04.12.2006
*/
public class SimpleGeneratorInputValidator implements InputValidator {
    protected Module myModule;
    protected Project myProject;
    @Nullable
    protected final PsiDirectory myDirectory;
    protected final SimpleGeneratorAction myGeneratorAction;

    public SimpleGeneratorInputValidator(@NotNull final SimpleGeneratorAction generatorAction,
                                         @NotNull final Module module,
                                         @Nullable final PsiDirectory directory) {
        myGeneratorAction = generatorAction;
        myModule = module;
        myDirectory = directory;
        myProject = module.getProject();
    }


    public SimpleGeneratorAction getGeneratorAction() {
        return myGeneratorAction;
    }

    public PsiElement[] getCreatedElements() {
        return new PsiElement[0];
    }

    public boolean canClose(final String inputString) {
        /**
         * Check if input string is valid
         */
        try {
            myGeneratorAction.checkBeforeCreate(inputString, myDirectory);
        }
        catch (IncorrectOperationException e) {
            showErrorDialog(GeneratorsUtil.filterIoExceptionMessage(e.getMessage()));
            return false;
        }
        return true;
    }

    public boolean checkInput(final String inputString) {
        //e.g. for rspec stub generator input can be empty
        return true;
    }

    /**
     * Delegates invokeAction to corresponding <code>SimpleGeneratorAction</code>
     * @param scriptArguments arguments for script
     * @param mainArgument main argument
     */
    public void invokeAction(final String scriptArguments,
                             final String mainArgument) {
        myGeneratorAction.invokeAction(scriptArguments, mainArgument, myModule);
    }

    protected void showErrorDialog(final String message) {
        Messages.showErrorDialog(myProject, message, myGeneratorAction.getErrorTitle());
    }
}
