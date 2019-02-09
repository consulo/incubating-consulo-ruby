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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageType;
import org.jetbrains.plugins.ruby.ruby.lang.findUsages.RubyUsageTypeProvider;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.RConstantImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RNamedElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveResult;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 21, 2007
 */
public class RConstantReference extends RNamedReference
{
	public RConstantReference(@Nonnull RNamedElement element)
	{
		super(element);
	}

	@Override
	@Nonnull
	protected ResolveResult[] multiResolveInner(boolean incompleteCode)
	{
		if(((RConstantImpl) myElement).isInDefinition())
		{
			return new ResolveResult[]{
					new ResolveResult()
					{
						@Override
						@Nullable
						public PsiElement getElement()
						{
							RubyUsageTypeProvider.setType(RConstantReference.this, RubyUsageType.DECLARATION);
							return myElement;
						}

						@Override
						public boolean isValidResult()
						{
							return true;
						}
					}
			};
		}
		if(((RConstantImpl) myElement).isClassOrModuleName())
		{
			return new ResolveResult[]{
					new ResolveResult()
					{
						@Override
						@Nullable
						public PsiElement getElement()
						{
							RubyUsageTypeProvider.setType(RConstantReference.this, RubyUsageType.DECLARATION);
							return myElement.getParentContainer();
						}

						@Override
						public boolean isValidResult()
						{
							return true;
						}
					}
			};
		}
		return super.multiResolveInner(incompleteCode);
	}

	@Override
	@Nonnull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		if(((RConstantImpl) myElement).isInDefinition() || ((RConstantImpl) myElement).isClassOrModuleName())
		{
			return Collections.emptyList();
		}
		//noinspection ConstantConditions
		return multiresolveToSymbols(fileSymbol, myElement.getName(), false, Types.MODULE_OR_CLASS_OR_CONSTANT);
	}
}
