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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.TypeSet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolFilter;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolFilterFactory;
import com.intellij.openapi.progress.ProgressManager;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 15, 2007
 */
public class Children
{
	public static final Children EMPTY_CHILDREN = new Children(null);

	private final List<Symbol> myList = new ArrayList<Symbol>();
	private final Object LOCK = new Object();

	private final Children myBaseChildren;

	public Children(@Nullable final Children children)
	{
		myBaseChildren = children;
	}

	public void addSymbol(@Nonnull final Symbol symbol)
	{
		synchronized(LOCK)
		{
			myList.add(symbol);
		}
	}

	public void addSymbols(@Nonnull final Collection<Symbol> collection)
	{
		for(Symbol symbol : collection)
		{
			addSymbol(symbol);
		}
	}

	public void addChildren(@Nonnull final Children children)
	{
		addSymbols(children.getAll());
	}


	@Nonnull
	public List<Symbol> getAll()
	{
		final ArrayList<Symbol> all = new ArrayList<Symbol>();
		addAll(all);
		return all;
	}

	protected void addAll(@Nonnull final List<Symbol> list)
	{
		ProgressManager.getInstance().checkCanceled();

		if(myBaseChildren != null)
		{
			myBaseChildren.addAll(list);
		}
		synchronized(LOCK)
		{
			list.addAll(myList);
		}
	}

	@Nullable
	public Symbol getSymbolByNameAndTypes(@Nonnull final String name, final TypeSet typeSet)
	{
		// We don`t want to return something NOT_DEFINED, if something better can be found
		Symbol foundSymbol = null;
		for(Symbol symbol : getSymbolsByNameAndTypes(name, typeSet).getAll())
		{
			if(foundSymbol == null || symbol.getType() != Type.NOT_DEFINED)
			{
				foundSymbol = symbol;
			}
		}
		return foundSymbol;
	}

	@Nonnull
	public Children getSymbolsByNameAndTypes(@Nonnull final String name, final TypeSet typeSet)
	{
		return getChildrenByFilter(SymbolFilterFactory.createFilterByNameAndTypeSet(name, typeSet));
	}

	@Nonnull
	public Children getSymbolsOfTypes(final TypeSet typeSet)
	{
		return getChildrenByFilter(SymbolFilterFactory.createFilterByTypeSet(typeSet));
	}

	public boolean hasChildren()
	{
		return !myList.isEmpty() || myBaseChildren != null && myBaseChildren.hasChildren();
	}

	public Children getChildrenByFilter(@Nonnull final SymbolFilter filter)
	{
		final Children children = new Children(null);
		addChildrenByFilter(children, filter);
		return children;
	}

	protected void addChildrenByFilter(@Nonnull final Children children, @Nonnull final SymbolFilter filter)
	{
		ProgressManager.getInstance().checkCanceled();
		if(myBaseChildren != null)
		{
			myBaseChildren.addChildrenByFilter(children, filter);
		}
		synchronized(LOCK)
		{
			for(Symbol symbol : myList)
			{
				if(filter.accept(symbol))
				{
					children.addSymbol(symbol);
				}
			}
		}
	}


}
