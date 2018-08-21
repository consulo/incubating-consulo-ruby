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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeVariable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RNamedElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 21, 2007
 */
public class RIdentifierReference extends RNamedReference
{
	public RIdentifierReference(@NotNull RNamedElement element)
	{
		super(element);
	}

	@Override
	@NotNull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		final RIdentifier identifier = (RIdentifier) myElement;
		final ScopeVariable scopeVariable = identifier.getScopeVariable();
		if(scopeVariable != null)
		{
			return Collections.singletonList(scopeVariable.createSymbol());
		}

		// constructor handling
		if(RMethod.NEW.equals(myElement.getName()))
		{
			final RClass rClass = RubyPsiUtil.getContainingRClass(myElement);
			if(rClass != null)
			{
				final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, rClass);
				if(symbol != null)
				{
					return symbol.getChildren(fileSymbol).getSymbolsByNameAndTypes(RMethod.INITIALIZE, Type.CLASS_METHOD.asSet()).getAll();
				}
			}
		}

		return super.multiResolveToSymbols(fileSymbol);
	}
}
