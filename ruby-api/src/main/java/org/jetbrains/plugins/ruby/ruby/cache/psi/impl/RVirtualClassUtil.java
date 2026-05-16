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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class RVirtualClassUtil
{
	/**
	 * Retruns list of classes, that contain direct ancestor of given class
	 *
	 * @param rVClassSymbol Given ruby class
	 * @param fileSymbol    FileSymbol
	 * @return List o ruby classes.
	 */
	@Nonnull
	public static List<RClass> getVirtualSuperClasses(@Nonnull final Symbol rVClassSymbol, @Nonnull final FileSymbol fileSymbol)
	{
		final List<RClass> superClasses = new ArrayList<RClass>();
		final Children superClassSymbols = rVClassSymbol.getChildren(fileSymbol).getSymbolsOfTypes(Type.SUPERCLASS.asSet());
		for(Symbol superClassSymbol : superClassSymbols.getAll())
		{
			final List<RPsiElement> superVirtPrototypes = superClassSymbol.getLinkedSymbol().getVirtualPrototypes(fileSymbol).getAll();
			for(RPsiElement superVirtPrototype : superVirtPrototypes)
			{
				if(superVirtPrototype instanceof RClass)
				{
					superClasses.add((RClass) superVirtPrototype);
				}
			}
		}
		return superClasses;
	}

	/**
	 * Search all methods form class and it's superclasses.
	 *
	 * @param symbol     Class or module symbol
	 * @param fileSymbol FileSymbol
	 * @param context    Context
	 * @return all metods
	 */
	@Nonnull
	public static RMethod[] getAllMethods(@Nullable final Symbol symbol, @Nonnull final FileSymbol fileSymbol, @Nonnull final Context context)
	{
		final Children children = symbol != null ? SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, context, symbol, null) : new Children(null);
		final Children methodSymbols = children.getSymbolsOfTypes(Types.METHODS);
		final List<RMethod> methods = new ArrayList<RMethod>();
		for(Symbol mehodSymb : methodSymbols.getAll())
		{
			//noinspection unchecked
			final List<RPsiElement> prototypes = mehodSymb.getVirtualPrototypes(fileSymbol).getAll();
			for(RPsiElement prototype : prototypes)
			{
				methods.add((RMethod) prototype);
			}
		}
		return methods.toArray(new RMethod[methods.size()]);
	}

	@Nonnull
	public static RMethod[] getAllMethodsWithName(@Nullable final Symbol symbol, @Nonnull final FileSymbol fileSymbol, @Nonnull final String name, @Nonnull final Context context)
	{
		final Children children = symbol != null ? SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, context, symbol, null) : new Children(null);
		final Children methodSymbols = children.getSymbolsByNameAndTypes(name, Types.METHODS);
		final List<RMethod> methods = new ArrayList<RMethod>();
		for(Symbol mehodSymb : methodSymbols.getAll())
		{
			//noinspection unchecked
			final List<RPsiElement> prototypes = mehodSymb.getVirtualPrototypes(fileSymbol).getAll();
			for(RPsiElement prototype : prototypes)
			{
				methods.add((RMethod) prototype);
			}
		}
		return methods.toArray(new RMethod[methods.size()]);
	}
}
