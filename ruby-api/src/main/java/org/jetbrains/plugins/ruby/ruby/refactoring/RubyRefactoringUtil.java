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

package org.jetbrains.plugins.ruby.ruby.refactoring;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.codeInsight.PsiEquivalenceUtil;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 21, 2007
 */
public class RubyRefactoringUtil
{

	/**
	 * Tries to find all the occurences of given expression
	 *
	 * @param pattern Search pattern
	 * @param context Search context
	 * @return list of occurences
	 */
	@Nonnull
	public static List<PsiElement> getOccurences(@Nonnull final RPsiElement pattern, @Nonnull final RPsiElement context)
	{
		final ArrayList<PsiElement> occurences = new ArrayList<PsiElement>();
		final RubyElementVisitor visitor = new RubyElementVisitor()
		{
			@Override
			public void visitElement(@Nonnull final PsiElement element)
			{
				if(PsiEquivalenceUtil.areElementsEquivalent(pattern, element))
				{
					occurences.add(element);
				}
				element.acceptChildren(this);
			}
		};
		context.acceptChildren(visitor);
		return occurences;
	}

}
