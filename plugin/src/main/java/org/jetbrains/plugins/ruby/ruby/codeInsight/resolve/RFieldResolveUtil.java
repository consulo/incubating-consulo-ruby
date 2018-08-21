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

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubySimpleLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.TypeSet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.RFieldPresentationUtil;
import com.intellij.codeInsight.lookup.LookupValueWithPriority;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 25.04.2007
 */
public class RFieldResolveUtil
{

	/**
	 * Returns all the available fields for this holder
	 *
	 * @param fileSymbol FileSymbol
	 * @param holder     Holder @return array of Strings for autocomplete
	 * @return variants for autocomplete
	 */
	@NotNull
	public static Object[] getVariants(@Nullable final FileSymbol fileSymbol, @NotNull final RVirtualFieldHolder holder)
	{
		final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, holder);
		if(symbol == null)
		{
			return new Object[]{};
		}
		final Children all = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, Context.ALL, symbol, null);

		//noinspection ConstantConditions
		final Children fields = all.getSymbolsOfTypes(Types.FIELDS);

		final List<RubyLookupItem> variants = new ArrayList<RubyLookupItem>();
		for(Symbol fieldSymbol : fields.getAll())
		{
			final RVirtualField field = (RVirtualField) fieldSymbol.getLastVirtualPrototype(fileSymbol);
			//noinspection ConstantConditions
			variants.add(new RubySimpleLookupItem(fieldSymbol.getName(), null, LookupValueWithPriority.NORMAL, false, RFieldPresentationUtil.getIcon(field)));
		}
		return variants.toArray(new Object[variants.size()]);
	}

	@NotNull
	public static List<PsiElement> resolve(@Nullable final FileSymbol fileSymbol, @NotNull final RVirtualFieldHolder holder, @NotNull final String name, @NotNull final TypeSet acceptableTypes)
	{
		final ArrayList<PsiElement> list = new ArrayList<PsiElement>();
		final Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, holder);
		if(symbol == null)
		{
			return list;
		}

		final Symbol fieldSymbol = SymbolUtil.findSymbol(fileSymbol, symbol, name, false, acceptableTypes);
		if(fieldSymbol == null)
		{
			return list;
		}
		for(RVirtualElement prototype : fieldSymbol.getVirtualPrototypes(fileSymbol).getAll())
		{
			final RPsiElement psiElement = RVirtualPsiUtil.findPsiByVirtualElement(prototype, holder.getProject());
			if(psiElement != null)
			{
				list.add(psiElement);
			}
		}
		return list;
	}
}
