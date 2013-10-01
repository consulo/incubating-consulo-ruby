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

import java.util.Arrays;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.CoreTypes;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RNamedElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 21, 2007
 */
public class RPseudoConstantReference extends RNamedReference
{
	public RPseudoConstantReference(@NotNull RNamedElement element)
	{
		super(element);
	}

	@Override
	@NotNull
	public List<Symbol> multiResolveToSymbols(@Nullable final FileSymbol fileSymbol)
	{
		// kSUPER
		if(RubyTokenTypes.kSUPER.toString().equals(myElement.getText()))
		{
			final RContainer container = myElement.getParentContainer();
			assert container != null;
			final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, container);
			final Symbol context = SymbolUtil.getClassModuleFileSymbol(symbol);
			if(context == null || context.getType() != Type.CLASS)
			{
				return super.multiResolveToSymbols(fileSymbol);
			}
			final Children children = context.getChildren(fileSymbol).getSymbolsOfTypes(Type.SUPERCLASS.asSet());
			// If we have no explicit superclass
			if(!children.hasChildren())
			{
				return Arrays.asList(SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.Object));
			}
			final Symbol superClass = children.getAll().get(0).getLinkedSymbol();
			if(superClass.getType() != Type.NOT_DEFINED)
			{
				return Arrays.asList(superClass);
			}
		}
		return super.multiResolveToSymbols(fileSymbol);
	}
}
