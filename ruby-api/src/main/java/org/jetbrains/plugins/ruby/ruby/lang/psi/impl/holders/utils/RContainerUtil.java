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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils;

import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.language.psi.PsiElement;
import consulo.language.psi.PsiFile;
import consulo.language.psi.PsiManager;
import consulo.language.psi.util.PsiTreeUtil;
import consulo.util.lang.ref.Ref;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.AccessModifiersUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks.RCompoundStatementNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.RContainerBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyStructureVisitor;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubySystemCallVisitor;
import consulo.module.Module;
import consulo.virtualFileSystem.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.07.2006
 */
public abstract class RContainerUtil
{
	@Nonnull
	public static List<RStructuralElement> selectElementsByType(@Nonnull List<RStructuralElement> fullList, @Nonnull final StructureType type)
	{
		ArrayList<RStructuralElement> list = new ArrayList<RStructuralElement>();
		for(RStructuralElement element : fullList)
		{
			if(element.getType() == type)
			{
				list.add(element);
			}
		}
		return list;
	}

	@Nonnull
	public static List<RStructuralElement> selectVirtualElementsByType(@Nonnull List<RStructuralElement> fullList, @Nonnull final StructureType type)
	{
		ArrayList<RStructuralElement> list = new ArrayList<RStructuralElement>();
		for(RStructuralElement element : fullList)
		{
			if(element.getType() == type)
			{
				list.add(element);
			}
		}
		return list;
	}


	/**
	 * Searches first class among container children with name
	 * <code>className</code> (search algorithm isn't recursive).
	 *
	 * @param container container, i.e ruby class, module of file
	 * @param className not qualified class name
	 * @return Class element or null.
	 */
	@Nullable
	public static RClass getClassByName(@Nonnull final RContainer container, @Nullable final String className)
	{
		for(RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.CLASS))
		{
			assert element instanceof RClass;
			final RClass rClass = (RClass) element;
			if(rClass.getName().equals(className))
			{
				return rClass;
			}
		}
		return null;
	}

	/**
	 * Searches first class among cached container children with name
	 * <code>className</code> (search algorithm isn't recursive).
	 *
	 * @param container container, i.e cached ruby class, module of file
	 * @param className not qualified class name
	 * @return Class element or null.
	 */
	@Nullable
	public static RClass getVClassByName(@Nonnull final RContainer container, @Nullable final String className)
	{
		for(RStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.CLASS))
		{
			final RClass rClass = (RClass) element;
			if(rClass.getName().equals(className))
			{
				return rClass;
			}
		}
		return null;
	}

	/**
	 * Searches first method(not static then static) among container children with name
	 * <code>methodName</code> (search algorithm isn't recursive).
	 *
	 * @param container  container, i.e ruby class, module of file
	 * @param methodName method name
	 * @return Method element or null.
	 */
	@Nullable
	public static RMethod getMethodByName(@Nonnull final RContainer container, @Nullable final String methodName)
	{
		for(RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.METHOD))
		{
			assert element instanceof RMethod;
			final RMethod rMethod = (RMethod) element;
			if(rMethod.getName().equals(methodName))
			{
				return rMethod;
			}
		}
		for(RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.SINGLETON_METHOD))
		{
			assert element instanceof RMethod;
			final RMethod rMethod = (RMethod) element;
			if(rMethod.getName().equals(methodName))
			{
				return rMethod;
			}
		}
		return null;
	}

	/**
	 * Searches first method among container children with name
	 * <code>methodName</code> (search algorithm isn't recursive).
	 *
	 * @param container  container, i.e ruby class, module of file
	 * @param moduleName not qualified module name
	 * @return Method element or null.
	 */
	@Nullable
	public static RModule getModuleByName(@Nonnull final RContainer container, @Nullable final String moduleName)
	{
		for(RStructuralElement element : selectElementsByType(container.getStructureElements(), StructureType.MODULE))
		{
			assert element instanceof RModule;
			final RModule rModule = (RModule) element;
			if(rModule.getName().equals(moduleName))
			{
				return rModule;
			}
		}
		return null;
	}

	/**
	 * Checks if element has parentContainer of type containerType
	 *
	 * @param containerType type of RContainer
	 * @param element       RPsiElement to check
	 * @return true, if belongs
	 */
	public static boolean belongsToRContainer(@Nonnull final RPsiElement element, @Nonnull final StructureType containerType)
	{
		final RContainer container = PsiTreeUtil.getParentOfType(element, RContainer.class);
		//noinspection SimplifiableIfStatement
		if(container == null)
		{
			return false;
		}
		return container.getType() == containerType;
	}

	@Nonnull
	public static List<RStructuralElement> getStructureElements(@Nonnull final RContainer container)
	{
		final consulo.util.lang.ref.Ref<AccessModifier> scopeAccessModifier = new Ref<AccessModifier>(container.getDefaultChildAccessModifier());
		final List<RStructuralElement> elements = new ArrayList<RStructuralElement>();

		// Adding all the subcontainers with default scope access modifiers
		RubyStructureVisitor myVisitor = new RubyStructureVisitor()
		{

			@Override
			public void visitRCall(RCall rCall)
			{
				if(rCall.getType().isStructureCall())
				{
					elements.add(rCall);
				}
			}

			@Override
			public void visitRAliasStatement(RAliasStatement rAliasStatement)
			{
				elements.add(rAliasStatement);
			}

			@Override
			public void visitContainer(RContainer rContainer)
			{
				((RContainerBase) rContainer).setAccessModifier(scopeAccessModifier.get());
				elements.add(rContainer);
			}

			@Override
			public void visitRIdentifier(RIdentifier rIdentifier)
			{
				// Processing single command statements like private, public, protected, that changes the default container access_attributes
				if(RCompoundStatementNavigator.getByPsiElement(rIdentifier) != null)
				{
					AccessModifier mod = AccessModifiersUtil.getModifierByName(rIdentifier.getText());
					if(mod != AccessModifier.UNKNOWN)
					{
						scopeAccessModifier.set(mod);
					}
				}
			}

		};
		container.acceptChildren(myVisitor);

		RubySystemCallVisitor callVisitor = new RubySystemCallVisitor()
		{
			@Override
			public void visitPublicCall(@Nonnull RCall rCall)
			{
				setAccessModifiers(elements, rCall.getArguments(), AccessModifier.PUBLIC);
			}

			@Override
			public void visitProtectedCall(@Nonnull RCall rCall)
			{
				setAccessModifiers(elements, rCall.getArguments(), AccessModifier.PROTECTED);
			}

			@Override
			public void visitPrivateCall(@Nonnull RCall rCall)
			{
				setAccessModifiers(elements, rCall.getArguments(), AccessModifier.PRIVATE);
			}

		};
		container.acceptChildren(callVisitor);

		return elements;
	}


	@Nullable
	private static RContainerBase getContainerByName(final List<RStructuralElement> list, @Nonnull final String name)
	{
		for(RStructuralElement element : list)
		{
			if(element.getType().isContainer())
			{
				final RContainerBase container = (RContainerBase) element;
				if(name.equals(container.getName()))
				{
					return container;
				}
			}
		}
		return null;
	}

	private static void setAccessModifiers(@Nonnull final List<RStructuralElement> list, @Nonnull final List<RPsiElement> args, final AccessModifier modifier)
	{
		for(RPsiElement arg : args)
		{
			// Symbol processing
			if(arg instanceof RSymbol)
			{
				final PsiElement symbolObject = ((RSymbol) arg).getObject();
				RContainer container = getContainerByName(list, symbolObject.getText());
				if(container != null)
				{
					((RContainerBase) container).setAccessModifier(modifier);
				}
			}
			// String like processing
			if(arg instanceof RStringLiteral && !((RStringLiteral) arg).hasExpressionSubstitutions())
			{
				RContainer container = getContainerByName(list, ((RStringLiteral) arg).getContent());
				if(container != null)
				{
					((RContainerBase) container).setAccessModifier(modifier);
				}
			}
		}
	}

	/**
	 * Returns classes of container and of all it modules and submodules.
	 *
	 * @param container Container for classes.
	 * @return list of RVirtualClasses
	 */
	@Nonnull
	public static List<RClass> getTopLevelClasses(@Nonnull final RContainer container)
	{
		List<RClass> allClasses = new ArrayList<RClass>();
		gatherClasses(container, allClasses);
		return allClasses;
	}

	private static void gatherClasses(@Nonnull final RContainer container, @Nonnull final List<RClass> allClasses)
	{
		for(RStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.CLASS))
		{
			assert element instanceof RClass;
			allClasses.add((RClass) element);
		}
		for(RStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.MODULE))
		{
			assert element instanceof RModule;
			gatherClasses((RModule) element, allClasses);
		}
	}

	public static List<RModule> getTopLevelModules(@Nonnull final RContainer container)
	{
		final ArrayList<RModule> modules = new ArrayList<RModule>();
		for(RStructuralElement element : selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.MODULE))
		{
			assert element instanceof RModule;
			modules.add((RModule) element);
		}
		return modules;
	}


	public static RClass getFirstClassInFile(@Nullable final VirtualFile file, @Nonnull final Module module)
	{
		if(file != null)
		{
			final PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(file);
			if(psiFile instanceof RFile)
			{
				final List<RClass> allClasses = getTopLevelClasses((RFile) psiFile);
				if(!allClasses.isEmpty())
				{
					return allClasses.get(0);
				}
			}
		}
		return null;
	}

	public static RModule getFirstModuleInFile(@Nullable final VirtualFile file, @Nonnull final Module module)
	{
		if(file != null)
		{
			final PsiFile psiFile = PsiManager.getInstance(module.getProject()).findFile(file);
			if(psiFile instanceof RFile)
			{
				final List<RModule> allModules = getTopLevelModules((RFile) psiFile);
				if(!allModules.isEmpty())
				{
					return allModules.get(0);
				}
			}
		}
		return null;
	}
}
