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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.ProxyJavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 14, 2008
 */
public class JavaClassReference extends RQualifiedReference
{
	public JavaClassReference(@NotNull final Project project, @NotNull final RPsiElement wholeReference, @Nullable final RPsiElement refObject, @NotNull final PsiElement refValue)
	{
		super(project, wholeReference, refObject, refValue, RReference.Type.COLON_REF, RMethod.JAVA_CLASS);
	}

	@Override
	@NotNull
	protected ResolveResult[] multiResolveInner(final boolean incompleteCode)
	{
		if(((RPsiElementBase) myWholeReference).isClassOrModuleName())
		{
			return ResolveResult.EMPTY_ARRAY;
		}

		// We process only JAVA_CLASS here
		final List<Symbol> symbols = ResolveUtil.resolveToSymbols(myRefObject);
		if(symbols.size() == 1)
		{
			final Ref<PsiElement> ref = new Ref<PsiElement>(null);
			final Symbol symbol = symbols.get(0);
			if(symbol.getType() == Type.JAVA_CLASS)
			{
				ref.set(((JavaSymbol) symbol).getPsiElement());
			}
			if(symbol.getType() == Type.JAVA_PROXY_CLASS)
			{
				//noinspection ConstantConditions
				ref.set(((ProxyJavaSymbol) symbol).getPsiElement());
			}
			final PsiElement element = ref.get();
			if(element != null)
			{
				return new ResolveResult[]{
						new ResolveResult()
						{
							@Override
							@Nullable
							public PsiElement getElement()
							{
								return element;
							}

							@Override
							public boolean isValidResult()
							{
								return true;
							}
						}
				};
			}
		}
		return ResolveResult.EMPTY_ARRAY;
	}
}