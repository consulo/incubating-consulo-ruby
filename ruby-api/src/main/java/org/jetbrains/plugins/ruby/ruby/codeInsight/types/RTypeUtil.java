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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.jruby.codeInsight.types.JRubyDuckTypeUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Types;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.JavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.ProxyJavaSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolFilter;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolFilterFactory;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl.*;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.Access;
import org.jetbrains.plugins.ruby.ruby.codeInsight.usages.UsageAnalyzer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.presentation.SymbolPresentationUtil;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiJavaPackage;
import com.intellij.psi.PsiMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 24, 2007
 */
public class RTypeUtil
{

	public static RType createTypeBySymbol(@Nullable final FileSymbol fileSymbol, @Nullable final Symbol symbol, @Nonnull final Context context, final boolean inReference)
	{
		if(symbol == null)
		{
			return RType.NOT_TYPED;
		}
		final Type type = symbol.getType();
		if(type == Type.CONSTANT || type == Type.GLOBAL_VARIABLE)
		{
			// Try to find assigned usage
			final RVirtualElement prototype = symbol.getLastVirtualPrototype(fileSymbol);
			if(prototype != null)
			{
				final RPsiElement element = RVirtualPsiUtil.findPsiByVirtualElement(prototype, symbol.getProject());
				if(element != null)
				{
					return createTypeByUsage(fileSymbol, element);
				}
			}

			return RType.NOT_TYPED;
		}
		if(type == Type.MODULE && fileSymbol != null && fileSymbol.isJRubyEnabled())
		{
			final Symbol parent = symbol.getParentSymbol();
			if(parent != null && parent.getType() == Type.FILE && "Java".equals(symbol.getName()))
			{
				return new JavaModuleType(fileSymbol, symbol, context, inReference);
			}
		}
		if(type == Type.FILE || Types.MODULE_OR_CLASS.contains(type) || Types.JAVA.contains(type))
		{
			return new RSymbolTypeImpl(fileSymbol, symbol, context, inReference);
		}
		return RType.NOT_TYPED;
	}

	/**
	 * Creates type by usage of element
	 *
	 * @param fileSymbol
	 * @param element
	 * @return Type if usage of element is assign usage, NOT_TYPE  otherwise
	 */
	private static RType createTypeByUsage(final FileSymbol fileSymbol, @Nonnull final RPsiElement element)
	{
		final TypeInferenceHelper helper = TypeInferenceHelper.getInstance(element.getProject());
		helper.testAndSet(fileSymbol);
		final Access access = UsageAnalyzer.createUsageAccess(element);
		return helper.inferUsageType(access);
	}

	/**
	 * Creates ducktype by symbol
	 *
	 * @param fileSymbol  FileSymbol
	 * @param symbol      Symbol to create ducktype for
	 * @param context     Static or instance context
	 * @param inReference Create duck type for reference or for empty context
	 * @return Ducktype object
	 */
	public static DuckType createDuckTypeBySymbol(@Nullable final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final Context context, final boolean inReference)
	{
		ProgressManager.getInstance().checkCanceled();

		Type type = symbol.getType();
		final Children children = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, context, symbol, null);
		// Adding JRuby Specific children
		if(type == Type.JAVA_PROXY_CLASS)
		{
			final PsiElement element = ((ProxyJavaSymbol) symbol).getPsiElement();
			if(element instanceof PsiClass)
			{
				children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaClass(fileSymbol, (PsiClass) element, context));
			}
		}
		if(type == Type.JAVA_CLASS)
		{
			final PsiElement element = ((JavaSymbol) symbol).getPsiElement();
			assert element instanceof PsiClass;
			children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaClass(fileSymbol, (PsiClass) element, context));
		}
		if(type == Type.JAVA_PACKAGE)
		{
			final PsiElement element = ((JavaSymbol) symbol).getPsiElement();
			assert element instanceof PsiJavaPackage;
			children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaPackage(fileSymbol, (PsiJavaPackage) element));
		}
		if(type == Type.JAVA_METHOD)
		{
			final PsiElement element = ((JavaSymbol) symbol).getPsiElement();
			assert element instanceof PsiMethod;
			children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaMethod(fileSymbol, (PsiMethod) element));
		}
		if(type == Type.JAVA_FIELD)
		{
			final PsiElement element = ((JavaSymbol) symbol).getPsiElement();
			assert element instanceof PsiField;
			children.addChildren(JRubyDuckTypeUtil.getChildrenByJavaField(fileSymbol, (PsiField) element));
		}

		return createDuckTypeByChildren(fileSymbol, children, new HashSet<Symbol>(symbol.getChildren(fileSymbol).getAll()), createFilter(inReference));
	}


	public static SymbolFilter createFilter(final boolean inReference)
	{
		return SymbolFilterFactory.createFilterByTypeSet(inReference ? Types.REFERENCE_AUTOCOMPLETE_TYPES : Types.EMPTY_CONTEXT_AUTOCOMPLETE_TYPES);
	}

	public static DuckType createDuckTypeByChildren(@Nullable final FileSymbol fileSymbol, @Nonnull final Children children, @Nonnull final Set directChildren, @Nonnull final SymbolFilter filter)
	{
		final HashMap<String, Symbol> fieldNames = new HashMap<String, Symbol>();
		final HashMap<String, Symbol> javaFieldNames = new HashMap<String, Symbol>();
		final HashMap<String, Symbol> allNames = new HashMap<String, Symbol>();

		// Here we process fields
		for(Symbol child : children.getSymbolsOfTypes(Types.FIELDS).getAll())
		{
			final String name = child.getName();
			if(!fieldNames.containsKey(name))
			{
				fieldNames.put(name, child);
			}
		}

		// Here we process alternative names of Java methods
		for(Symbol child : children.getSymbolsOfTypes(Type.JAVA_METHOD.asSet()).getAll())
		{
			final String name = ((JavaSymbol) child).getJRubyName();
			if(name != null)
			{
				allNames.put(name, child);
			}
		}

		// Here we process java fields
		for(Symbol child : children.getSymbolsOfTypes(Type.JAVA_FIELD.asSet()).getAll())
		{
			final String name = child.getName();
			if(!javaFieldNames.containsKey(name))
			{
				javaFieldNames.put(name, child);
			}
		}

		// Here we process all others
		for(Symbol child : children.getChildrenByFilter(filter).getAll())
		{
			// We have already handled all fields!
			if(!Types.FIELDS.contains(child.getType()) && !Type.JAVA_FIELD.asSet().contains(child.getType()))
			{
				allNames.put(child.getName(), child);
			}
		}

		final DuckTypeImpl type = new DuckTypeImpl();

		// Add fields
		for(String name : fieldNames.keySet())
		{
			final Symbol child = fieldNames.get(name);
			type.addMessage(createMessageBySymbol(fileSymbol, child, name, directChildren.contains(child)));
		}

		// Add javaFields
		for(String name : javaFieldNames.keySet())
		{
			final Symbol child = javaFieldNames.get(name);
			type.addMessage(createMessageBySymbol(fileSymbol, child, name, directChildren.contains(child)));
		}

		// Add others
		for(String name : allNames.keySet())
		{
			final Symbol child = allNames.get(name);
			type.addMessage(createMessageBySymbol(fileSymbol, child, name, directChildren.contains(child)));
		}
		return type;
	}

	@Nonnull
	private static Message createMessageBySymbol(@Nullable final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final String name, boolean important)
	{
		if(!Types.METHODS.contains(symbol.getType()))
		{
			return new MessageImpl(name, 0, important, symbol);
		}

		final MessageImpl message;
		final int min = RMethodTypeUtil.getMinNumberOfArguments(fileSymbol, symbol);
		final int max = RMethodTypeUtil.getMaxNumberOfArguments(fileSymbol, symbol);
		if(min == max)
		{
			message = new MessageImpl(name, min, important, symbol);
		}
		else
		{
			message = new MessageWithVariousArgsNumberImpl(name, min, max, important, symbol);
		}
		return message;
	}

	public static List<RubyLookupItem> getLookupItemsByType(@Nonnull final RType type, @Nullable final String name, @Nullable final SymbolFilter filter)
	{
		final Collection<Message> messagesForName = type.getMessagesForName(name);
		final ArrayList<RubyLookupItem> items = new ArrayList<RubyLookupItem>();
		for(Message message : messagesForName)
		{
			final boolean important = message.isImportant();
			final String iName = name != null ? name : message.getName();
			final Symbol symbol = message.getSymbol();
			if(symbol != null && (filter == null || filter.accept(symbol)))
			{
				items.add(SymbolPresentationUtil.createRubyLookupItem(symbol, iName, important, message instanceof MultiMessage));
			}
		}
		return items;
	}


	public static boolean equal(@Nonnull final RType type1, @Nonnull final RType type2)
	{
		if(type1.equals(type2))
		{
			return true;
		}
		boolean type1empty = type1 instanceof REmptyType;
		boolean type2empty = type2 instanceof REmptyType;
		if(type1empty || type2empty)
		{
			return type1empty == type2empty;
		}
		return type1.getMessages().equals(type2.getMessages());
	}


	public static Collection<Message> intersection(@Nonnull final Collection<Message> collection1, @Nonnull final Collection<Message> collection2)
	{

		if(collection1.isEmpty())
		{
			return collection2;
		}
		if(collection2.isEmpty())
		{
			return collection1;
		}

		// Here we just iterate over collections to construct name to Message maps
		final Map<String, Message> map1 = new HashMap<String, Message>();
		for(Message message : collection1)
		{
			map1.put(message.getName(), message);
		}
		final Map<String, Message> map2 = new HashMap<String, Message>();
		for(Message Message : collection2)
		{
			map2.put(Message.getName(), Message);
		}

		final ArrayList<Message> result = new ArrayList<Message>();
		final Set<String> names1 = map1.keySet();
		final Set<String> names2 = map2.keySet();
		for(String name : names1)
		{
			if(names2.contains(name))
			{
				final Message message1 = map1.get(name);
				final Message message2 = map2.get(name);
				if(message1.equals(message2))
				{
					result.add(message1);
					continue;
				}
				// add multiMessage
				result.add(new MultiMessage(name, message1.isImportant() && message2.isImportant(), message1, message2));
			}
		}

		return result;
	}

	public static Collection<Message> union(@Nonnull final Collection<Message> collection1, @Nonnull final Collection<Message> collection2)
	{
		if(collection1.isEmpty())
		{
			return collection2;
		}
		if(collection2.isEmpty())
		{
			return collection1;
		}

		// Here we just iterate over collections to construct name to Message maps
		final Map<String, Message> map1 = new HashMap<String, Message>();
		for(Message message : collection1)
		{
			map1.put(message.getName(), message);
		}
		final Map<String, Message> map2 = new HashMap<String, Message>();
		for(Message Message : collection2)
		{
			map2.put(Message.getName(), Message);
		}

		final HashSet<String> names = new HashSet<String>();
		names.addAll(map1.keySet());
		names.addAll(map2.keySet());
		final ArrayList<Message> result = new ArrayList<Message>(names.size());
		for(String name : names)
		{
			final Message message1 = map1.get(name);
			final Message message2 = map2.get(name);
			if(message1 == null)
			{
				result.add(message2);
				continue;
			}
			if(message2 == null)
			{
				result.add(message1);
				continue;
			}
			if(message1.equals(message2))
			{
				result.add(message1);
				continue;
			}

			// add multiMessage
			result.add(new MultiMessage(name, message1.isImportant() && message2.isImportant(), message1, message2));
		}

		return result;
	}


	public static RType joinOr(@Nonnull final RType type1, @Nonnull final RType type2)
	{
		if(type1.equals(type2))
		{
			return type1;
		}
		if(type1 instanceof REmptyType)
		{
			return type2;
		}
		if(type2 instanceof REmptyType)
		{
			return type1;
		}
		// It`s a special logic for duckType join
		if(type2 instanceof RUnionTypeImpl && type1.equals(((RUnionTypeImpl) type2).myType1))
		{
			return type2;
		}
		if(type1 instanceof RUnionTypeImpl && type2.equals(((RUnionTypeImpl) type1).myType1))
		{
			return type1;
		}
		if(type1 instanceof RUnionTypeImpl && type2 instanceof RUnionTypeImpl &&
				((RUnionTypeImpl) type1).myType1 == ((RUnionTypeImpl) type2).myType1)
		{
			return new RUnionTypeImpl(type1, type2);
		}

		// Optimizations
		if(type1 instanceof RJoinTypeImpl && ((RJoinTypeImpl) type1).containsType(type2))
		{
			return type1;
		}
		if(type2 instanceof RJoinTypeImpl && ((RJoinTypeImpl) type2).containsType(type1))
		{
			return type2;
		}

		if(!type1.isTyped() || !type2.isTyped())
		{
			return smartJoinTypes(type1, type2);
		}

		return new RJoinTypeImpl(type1, type2);
	}

	public static RType joinAnd(@Nonnull final RType type1, @Nonnull final RType type2)
	{
		if(type1.equals(type2))
		{
			return type1;
		}
		if(type1 instanceof REmptyType)
		{
			return type2;
		}
		if(type2 instanceof REmptyType)
		{
			return type1;
		}
		return smartJoinTypes(type1, type2);
	}

	private static RType smartJoinTypes(final RType type1, final RType type2)
	{
		if(type1 instanceof RDuckType && type2 instanceof RDuckType)
		{
			return duckAnd(type1, type2);
		}
		if(type1 instanceof RDuckType && type2 instanceof RUnionTypeImpl)
		{
			return mergeDuckAnd((RDuckType) type1, (RUnionTypeImpl) type2);
		}
		if(type2 instanceof RDuckType && type1 instanceof RUnionTypeImpl)
		{
			return mergeDuckAnd((RDuckType) type2, (RUnionTypeImpl) type1);
		}
		return new RUnionTypeImpl(type1, type2);
	}

	private static RType duckAnd(final RType type1, final RType type2)
	{
		DuckTypeImpl result = new DuckTypeImpl();
		final Collection<Message> type1members = type1.getMessages();
		for(Message member : type1members)
		{
			result.addMessage(member);
		}
		for(Message type2member : type2.getMessages())
		{
			if(!type1members.contains(type2member))
			{
				result.addMessage(type2member);
			}
		}
		return new RDuckTypeImpl(result);
	}

	private static RType mergeDuckAnd(final RDuckType type1, final RUnionTypeImpl type2)
	{
		if(type2.myType1 instanceof RDuckType)
		{
			return new RUnionTypeImpl(duckAnd(type1, type2.myType1), type2.myType2);
		}
		if(type2.myType2 instanceof RDuckType)
		{
			return new RUnionTypeImpl(duckAnd(type1, type2.myType2), type2.myType1);
		}
		return new RUnionTypeImpl(type1, type2);
	}

	@Nonnull
	public static RType getBooleanType(@Nullable final FileSymbol fileSymbol)
	{
		final RType trueType = RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.TrueClass), Context.INSTANCE, true);
		final RType falseType = RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.FalseClass), Context.INSTANCE, true);
		return joinOr(trueType, falseType);
	}

}
