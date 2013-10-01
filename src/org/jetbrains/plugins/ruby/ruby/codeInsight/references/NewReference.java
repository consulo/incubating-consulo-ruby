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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.ProxyJavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 16, 2008
 */
public class NewReference extends RQualifiedReference
{
	public NewReference(@NotNull final Project project, @NotNull final RPsiElement wholeReference, @Nullable final RPsiElement refObject, @NotNull final PsiElement refValue)
	{
		super(project, wholeReference, refObject, refValue, RReference.Type.COLON_REF, RMethod.INITIALIZE);
	}

	@Override
	@NotNull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		if(((RPsiElementBase) myWholeReference).isClassOrModuleName())
		{
			return Collections.emptyList();
		}

		// We process only JAVA_CLASS here
		final List<Symbol> symbols = ResolveUtil.resolveToSymbols(myRefObject);
		if(symbols.size() == 1)
		{
			PsiElement element = null;
			final Symbol symbol = symbols.get(0);
			if(symbol.getType() == Type.JAVA_CLASS)
			{
				element = ((JavaSymbol) symbol).getPsiElement();
			}
			if(symbol.getType() == Type.JAVA_PROXY_CLASS)
			{
				element = ((ProxyJavaSymbol) symbol).getPsiElement();
			}
			if(element instanceof PsiClass)
			{
				final ArrayList<Symbol> variants = new ArrayList<Symbol>();
				for(PsiMethod method : ((PsiClass) element).getConstructors())
				{
					variants.add(new JavaSymbol(method, method.getName(), null, Type.JAVA_METHOD));
				}
				return variants;
			}
		}
		return super.multiResolveToSymbols(fileSymbol);
	}
}
