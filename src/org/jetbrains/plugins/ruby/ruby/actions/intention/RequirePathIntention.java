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

package org.jetbrains.plugins.ruby.ruby.actions.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall.RCallNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 16.07.2007
 */
public abstract class RequirePathIntention  implements IntentionAction {
    @Override
	public boolean startInWriteAction() {
        return true;
    }

    protected PsiElement getElementAt(PsiFile psiFile, Editor editor) {
        return psiFile.findElementAt(editor.getCaretModel().getOffset());
    }

    protected static boolean isRequireExprList(final RListOfExpressions exprList) {
        final RCall cmdCall = RCallNavigator.getByRListOfExpressions(exprList);
        if (cmdCall != null) {
            final Ref<Boolean> result = new Ref<Boolean>(false);
            
            final RubySystemCallVisitor callVisitor = new RubySystemCallVisitor() {
                @Override
				public void visitRequireCall(@NotNull final RCall rCall) {
                    result.set(true);
                }
            };
            cmdCall.accept(callVisitor);
            return result.get();
        }
        return false;
    }
}
