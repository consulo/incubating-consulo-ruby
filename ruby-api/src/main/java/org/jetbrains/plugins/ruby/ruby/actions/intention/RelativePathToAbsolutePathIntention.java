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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RListOfExpressionsNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RFileUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 04.05.2007
 */
public class RelativePathToAbsolutePathIntention extends RequirePathIntention
{
	private static final String NAME = "RelativePathToAbsolute";
	private static final String TEXT = RBundle.message("ruby.intentions.relative.path.to.absolute");

	@Override
	@NotNull
	public String getFamilyName()
	{
		return NAME;
	}

	@Override
	@NotNull
	public String getText()
	{
		return TEXT;
	}

	@Override
	public boolean isAvailable(@NotNull final Project project, @NotNull final Editor editor, @NotNull final PsiFile psiFile)
	{
		if(!RubyIntentionUtil.isAvailable(editor, psiFile))
		{
			return false;
		}
		final PsiElement psiElement = getElementAt(psiFile, editor);
		if(psiElement != null)
		{
			final PsiElement requireExpr = findRequireRoot(psiElement);
			if(requireExpr != null)
			{
				return !requireExpr.getText().startsWith(RFileUtil.FILE_EXPAND_PATH + "(");
			}
		}
		return false;
	}


	@Override
	public void invoke(@NotNull final Project project, final Editor editor, final PsiFile psiFile) throws IncorrectOperationException
	{
		final PsiElement element = findRequireRoot(getElementAt(psiFile, editor));
		if(element == null)
		{
			return;
		}

		StringBuilder buff = new StringBuilder(RFileUtil.FILE_EXPAND_PATH);
		buff.append("(");
		buff.append(element.getText()).append(")");
		final RPsiElement newElement = RubyPsiUtil.getTopLevelElements(project, buff.toString()).get(0);

		RubyPsiUtil.replaceInParent(element, newElement);
	}

	protected PsiElement findRequireRoot(@NotNull final PsiElement psiElement)
	{
		RListOfExpressions exprList;

		PsiElement element = psiElement;
		while(element != null)
		{
			exprList = null;
			// searches next exprList in PsiTree for current element
			while(exprList == null && element != null)
			{
				exprList = RListOfExpressionsNavigator.getByPsiElement(element);
				element = element.getParent();
			}
			if(element == null)
			{
				return null;
			}

			// Check if element is first argument in require call
			if(exprList.getElements().size() == 1 && isRequireExprList(exprList))
			{
				return exprList.getElements().get(0);
			}

			element = element.getParent();
		}
		return null;
	}
}
