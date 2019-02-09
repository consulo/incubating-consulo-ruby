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

package org.jetbrains.plugins.ruby.jruby.inspections;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;

/**
 * @author: oleg
 */
public class WrongTopLevelPackageFix implements LocalQuickFix
{

	private static final String JAVA = "Java::";
	protected PsiElement myElement;

	public WrongTopLevelPackageFix(@Nonnull final PsiElement element)
	{
		myElement = element;
	}

	@Override
	@Nonnull
	public String getName()
	{
		return "Add Java:: to package";
	}

	@Override
	@Nonnull
	public String getFamilyName()
	{
		return "JRuby";
	}

	@Override
	public void applyFix(@Nonnull final Project project, @Nonnull final ProblemDescriptor descriptor)
	{
		final String text = myElement.getText();
		final RPsiElement element = RubyPsiUtil.getTopLevelElements(project, JAVA + text).get(0);
		RubyPsiUtil.replaceInParent(myElement, element);
	}
}