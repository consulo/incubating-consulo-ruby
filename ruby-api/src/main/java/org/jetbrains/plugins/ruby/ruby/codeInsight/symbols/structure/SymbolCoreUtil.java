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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.TypeSet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 15, 2007
 */
class SymbolCoreUtil
{

	public static Symbol find(@NotNull final FileSymbol fileSymbol, @NotNull Symbol anchorSymbol, @NotNull final List<String> path, final boolean global, final boolean forceCreate, final TypeSet typeSet)
	{
		if(global)
		{
			anchorSymbol = fileSymbol.getRootSymbol();
		}
		Symbol symbol = anchorSymbol;

		final int size = path.size();
		for(int index = 0; index < size; index++)
		{
			final String name = path.get(index);
			final TypeSet acceptableTypes = index < size - 1 ? Types.MODULE_OR_CLASS_OR_CONSTANT : typeSet;

			// Search first path in local namespace or in parent namespaces
			if(index == 0)
			{
				// Processing self
				if(!global && RubyTokenTypes.kSELF.toString().equals(name))
				{
					symbol = anchorSymbol;
					while(symbol != null && !new TypeSet(Type.FILE, Type.CLASS, Type.MODULE).contains(symbol.getType()))
					{
						symbol = symbol.getParentSymbol();
					}
				}
				else
				{
					symbol = null;
					final RType type = RTypeUtil.createTypeBySymbol(fileSymbol, anchorSymbol, Context.ALL, false);
					for(Message message : type.getMessagesForName(name))
					{
						final Symbol messageSymbol = message.getSymbol();
						if(messageSymbol != null && acceptableTypes.contains(messageSymbol.getType()))
						{
							symbol = messageSymbol;
						}
					}
					Symbol currentSymbol = anchorSymbol;
					// here we just search in parents upto global namespace
					while(symbol == null && (currentSymbol = currentSymbol.getParentSymbol()) != null)
					{
						symbol = fileSymbol.getChildren(currentSymbol).getSymbolByNameAndTypes(name, acceptableTypes);
					}
					// if still no first element of path found, we just create it in local namespace!
					if(symbol == null)
					{
						if(forceCreate)
						{
							symbol = findOrCreateUndef(fileSymbol, anchorSymbol, name);
						}
						else
						{
							return null;
						}
					}
				}
			}
			else
			{
				assert symbol != null;
				anchorSymbol = symbol;
				symbol = null;
				final RType type = RTypeUtil.createTypeBySymbol(fileSymbol, anchorSymbol, Context.ALL, true);
				for(Message message : type.getMessagesForName(name))
				{
					final Symbol messageSymbol = message.getSymbol();
					if(messageSymbol != null && acceptableTypes.contains(messageSymbol.getType()))
					{
						symbol = messageSymbol;
					}
				}
				if(symbol == null)
				{
					if(forceCreate)
					{
						symbol = findOrCreateUndef(fileSymbol, anchorSymbol, name);
					}
					else
					{
						return null;
					}
				}
			}
		}
		return symbol;
	}

	@NotNull
	public static Symbol create(@NotNull final FileSymbol fileSymbol, @NotNull Symbol anchorSymbol, @NotNull final List<String> path, final boolean global, final Type newType, @NotNull final RVirtualElement prototype)
	{
		final int size = path.size();
		assert size > 0;
		final Symbol parent = find(fileSymbol, anchorSymbol, path.subList(0, size - 1), global, true, Types.MODULE_OR_CLASS);
		return SymbolCoreUtil.add(fileSymbol, parent, new Symbol(fileSymbol, path.get(size - 1), newType, parent, prototype));
	}

	@NotNull
	private static Symbol findOrCreateUndef(@NotNull final FileSymbol fileSymbol, @NotNull Symbol anchorSymbol, @NotNull final String name)
	{
		// try to find not_defined symbol
		final Children children = fileSymbol.getChildren(anchorSymbol);
		Symbol child = children.getSymbolByNameAndTypes(name, Type.NOT_DEFINED.asSet());
		// Change undef state if know exactly type, that needed
		if(child != null)
		{
			return child;
		}
		// try to find not_defined symbol
		child = children.getSymbolByNameAndTypes(name, Type.NOT_DEFINED.asSet());

		if(child == null)
		{
			// create NOT_DEFINED
			child = new Symbol(fileSymbol, name, Type.NOT_DEFINED, anchorSymbol, null);
			fileSymbol.addChild(anchorSymbol, child);
		}
		return child;
	}

	public static Symbol add(@NotNull final FileSymbol fileSymbol, @NotNull final Symbol parent, @NotNull final Symbol child)
	{
		// we add symbols that has no children
		assert !fileSymbol.getChildren(child).hasChildren();

		final String name = child.getName();
		final Type type = child.getType();

		// Adding special symbols without any checkings
		if(name == null)
		{
			fileSymbol.addChild(parent, child);
			return child;
		}

		// if we try to add not defined smth with
		final Children children = fileSymbol.getChildren(parent);
		final Symbol oldChild = children.getSymbolByNameAndTypes(name, type.asSet());

		// If we work with fileds, we just ignore newChild if oldChild exists
		if(Types.FIELDS.contains(type))
		{
			if(oldChild != null)
			{
				return oldChild;
			}
		}
		// we merge only modules, classes and methods
		if(new TypeSet(Type.MODULE, Type.CLASS, Type.INSTANCE_METHOD, Type.CLASS_METHOD).contains(type))
		{
			if(oldChild != null)
			{
				// Here we add prototypes
				for(RVirtualElement element : fileSymbol.getVirtualPrototypes(child).getAll())
				{
					fileSymbol.addPrototype(oldChild, element);
				}
				return oldChild;
			}
		}
		// try to find NOT_DEFINED and change it`s type
		if(new TypeSet(Type.MODULE, Type.CLASS).contains(type))
		{
			final Symbol not_defined = children.getSymbolByNameAndTypes(name, Type.NOT_DEFINED.asSet());
			if(not_defined != null)
			{
				// Here we change the type of not_defined
				not_defined.setType(type);
				// Here we add prototypes
				for(RVirtualElement element : fileSymbol.getVirtualPrototypes(child).getAll())
				{
					fileSymbol.addPrototype(not_defined, element);
				}
				return not_defined;
			}
		}
		// in common case we just add another child
		fileSymbol.addChild(parent, child);
		return child;
	}
}
