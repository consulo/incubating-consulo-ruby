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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.codeInsight.completion.variants;

import consulo.language.editor.impl.internal.completion.CompletionUtil;
import consulo.language.editor.impl.internal.completion.CompletionVariant;
import consulo.language.impl.ast.TreeElement;
import consulo.language.psi.PsiElement;
import consulo.language.psi.filter.ElementFilter;
import consulo.xml.language.psi.XmlTag;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 2, 2007
 */
public class RHTMLInjectionInTagContentCVariant extends CompletionVariant
{
	public RHTMLInjectionInTagContentCVariant()
	{
		super(XmlTag.class, new MyRTHMLInjectionStartFilter());
	}

	private static class MyRTHMLInjectionStartFilter implements ElementFilter
	{

		@Override
		public boolean isAcceptable(final Object element, final PsiElement context)
		{
			//noinspection SimplifiableIfStatement
			if(!(element instanceof TreeElement) || !CompletionUtil.DUMMY_IDENTIFIER.trim().equals(((TreeElement) element).getText()))
			{
				return false;
			}
			return context instanceof XmlTag;
		}

		@Override
		public boolean isClassAcceptable(final Class hintClass)
		{
			return true;
		}
	}
}
