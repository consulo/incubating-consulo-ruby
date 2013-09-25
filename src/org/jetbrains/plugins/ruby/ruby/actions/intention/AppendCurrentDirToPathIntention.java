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

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals.RBaseStringNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RListOfExpressionsNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RMathBinExpressionImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RMathBinExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 16.07.2007
 */
public class AppendCurrentDirToPathIntention extends RequirePathIntention {
    private static final String NAME = "AppendCurrentDirToPath";
    private static final String TEXT = RBundle.message("ruby.intentions.append.cur.dir.to.path");

    @Override
	@NotNull
    public String getFamilyName() {
        return NAME;
    }

    @Override
	@NotNull
    public String getText(){
        return TEXT;
    }

    @Override
	public void invoke(@NotNull final Project project, final Editor editor, final PsiFile psiFile) throws IncorrectOperationException {
        final RBaseString myString = RBaseStringNavigator.getByPsiElement(getElementAt(psiFile, editor));
        if (myString == null){
            return;
        }
        StringBuilder buff = new StringBuilder(RFileUtil.FILE_DIRNAME);
        buff.append(" + '");
        final String content = myString.getContent();
        if (!content.startsWith("/")) {
            buff.append('/');
        }
        buff.append(myString.getContent()).append('\'');
        final RPsiElement rPsiElement = RubyPsiUtil.getTopLevelElements(project, buff.toString()).get(0);
        myString.replaceByRMathBinExpression((RMathBinExpressionImpl)rPsiElement);
    }

    @Override
	public boolean isAvailable(@NotNull final Project project,
                               @NotNull final Editor editor,
                               @NotNull final PsiFile psiFile) {
        if (!RubyIntentionUtil.isAvailable(editor, psiFile)){
            return false;
        }
        final PsiElement psiElement = getElementAt(psiFile, editor);
        RBaseString string = RBaseStringNavigator.getByPsiElement(psiElement);
        return string != null && !string.hasExpressionSubstitutions() && canIntent(string);
    }

    protected boolean canIntent(@NotNull final RBaseString string) {
        // If parent is MathBinExpr and string is LeftOperand.
        // e.g :
        //    require 'rpc' + 'fff'
        //    require File.dirname(__FILE__) + '/fff'
        final RMathBinExpressionImpl mathExpr = RMathBinExpressionNavigator.getByRBaseString(string);
        if (mathExpr != null && mathExpr.getLeftOperand() == string) {
            // Math expression should be first operand in require command call
            final RListOfExpressions exprList = RListOfExpressionsNavigator.getByPsiElement(mathExpr);
            //noinspection SimplifiableIfStatement
            if (exprList != null && exprList.getElement(0) == mathExpr) {
                return isRequireExprList(exprList);
            }
            return false;
        }

        //If parent is ListOfEexpressions and string is first expression in the list
        // require 'rpce'
        final RListOfExpressions exprList = RListOfExpressionsNavigator.getByPsiElement(string);
        //noinspection SimplifiableIfStatement
        if (exprList != null && exprList.getElement(0) == string) {
            return isRequireExprList(exprList);
        }
        return false;
    }
}

