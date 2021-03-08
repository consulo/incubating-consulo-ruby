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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.HelpersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 27, 2007
 */
public class RailsSymbolUtil
{

	/**
	 * Adds rails specified symbols for symbol
	 *
	 * @param fileSymbol FileSymbol
	 * @param symbol     Context symbol
	 * @param context    Context
	 * @return Children - result
	 */
	@Nonnull
	public static Children getRailsSpecificSymbols(@Nonnull final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final Context context)
	{

		final Children children = new Children(null);
		final Children railsSymbols = getRailsSymbols(fileSymbol, symbol, context, false);
		if(!railsSymbols.hasChildren())
		{
			return children;
		}
		final HashSet<Symbol> set = new HashSet<Symbol>(children.getAll());
		for(Symbol railsSymbol : railsSymbols.getAll())
		{
			if(!set.contains(railsSymbol))
			{
				children.addSymbol(railsSymbol);
			}
		}
		return children;
	}

	/**
	 * Tries to find rails specific symbol in current namespace or in global namespace.
	 *
	 * @param fileSymbol FileSymbol
	 * @param symbol     Context symbol
	 * @param name       Symbol name
	 * @param context    Context
	 * @param typeSet    List of acceptable types
	 * @return Symbol result of search
	 */
	@Nullable
	public static Symbol findRailsSpecificSymbol(@Nonnull final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final String name, @Nonnull final Context context, @Nonnull final TypeSet typeSet)
	{
		for(Symbol child : getRailsSymbols(fileSymbol, symbol, context, true).getAll())
		{
			if(name.equals(child.getName()) && typeSet.contains(child.getType()))
			{
				return child;
			}
		}
		return null;
	}

	@Nullable
	private static Module getModule(@Nonnull final Project project, @Nullable final RVirtualElement prototype)
	{
		if(prototype == null)
		{
			return null;
		}
		final RPsiElement psiElement = RVirtualPsiUtil.findPsiByVirtualElement(prototype, project);
		if(psiElement == null)
		{
			return null;
		}
		return ModuleUtil.findModuleForPsiElement(psiElement);
	}

	@Nonnull
	private static Children getRailsSymbols(@Nonnull final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final Context context, final boolean forceShowAll)
	{
		final RVirtualElement prototype = symbol.getLastVirtualPrototype(fileSymbol);
		final Module module = getModule(symbol.getProject(), prototype);
		// We ensure, that we`re working in the rails module context
		if(module == null || !RailsFacetUtil.hasRailsSupport(module))
		{
			return Children.EMPTY_CHILDREN;
		}
		if(forceShowAll)
		{
			return SymbolUtil.gatherAllSymbols(fileSymbol);
		}

		// Controller handling
		if(prototype instanceof RVirtualClass)
		{
			final RVirtualClass controllerClass = (RVirtualClass) prototype;
			if(ControllersConventions.isControllerClass(controllerClass, module))
			{
				return getSymbolsForController(fileSymbol, symbol, context);
			}
		}
		// Helper handling
		if(prototype instanceof RVirtualModule)
		{
			final RVirtualModule helperModule = (RVirtualModule) prototype;
			if(HelpersConventions.isHelperModule(helperModule, module))
			{
				return getSymbolsForHelper(fileSymbol, symbol, ControllersConventions.getControllerClassNameByHelper(helperModule), context);
			}
		}
		if(prototype instanceof RVirtualFile)
		{
			final RStructuralElement file = RVirtualPsiUtil.findInPsi(symbol.getProject(), (RVirtualFile) prototype);
			if(file instanceof RFile)
			{
				final VirtualFile viewFile = ((RFile) file).getVirtualFile();
				if(ViewsConventions.isViewFile(viewFile))
				{
					assert viewFile != null;
					return getSymbolsForView(fileSymbol, context, viewFile, module);
				}
			}
		}
		if(Types.MODULE_OR_CLASS.contains(symbol.getType()))
		{
			// TODO: models handling
			return SymbolUtil.gatherAllSymbols(fileSymbol);
		}
		return Children.EMPTY_CHILDREN;
	}

	/**
	 * Returns symbols for controller
	 *
	 * @param fileSymbol FileSymbol
	 * @param symbol     controller symbol
	 * @param context    Context
	 * @return List of rails specific children
	 */
	@Nonnull
	private static Children getSymbolsForController(@Nonnull final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final Context context)
	{
		final Symbol parentSymbol = symbol.getParentSymbol();
		final Children children = new Children(null);
		if(parentSymbol == null)
		{
			return children;
		}
		// ApplicationController
		addApplicationController(fileSymbol, children, context);
		return children;
	}

	/**
	 * Returns symbols for helper
	 *
	 * @param fileSymbol     FileSymbol
	 * @param symbol         helper symbol
	 * @param controllerName own controller name
	 * @param context        Context
	 * @return List of rails specific children
	 */
	@Nonnull
	private static Children getSymbolsForHelper(@Nonnull final FileSymbol fileSymbol, @Nonnull final Symbol symbol, @Nonnull final String controllerName, @Nonnull final Context context)
	{
		final Symbol parentSymbol = symbol.getParentSymbol();
		final Children children = new Children(null);
		if(parentSymbol == null)
		{
			return children;
		}

		// add builtin helpers
		addBuiltinHelpers(fileSymbol, children, context);

		// ApplicationHelper
		addApplicationHelper(fileSymbol, children, context);
		// own controller
		final Symbol ownController = SymbolUtil.findSymbol(fileSymbol, parentSymbol, controllerName, false, Type.CLASS.asSet());
		if(ownController != null)
		{
			addOwnControllerElements(fileSymbol, context, children, ownController);
		}
		return children;
	}

	private static Children getSymbolsForView(@Nonnull final FileSymbol fileSymbol, @Nonnull final Context context, @Nonnull final VirtualFile viewFile, @Nonnull final Module module)
	{
		final Children children = new Children(null);
		// add builtin helpers
		addBuiltinHelpers(fileSymbol, children, context);

		// add application helper
		addApplicationHelper(fileSymbol, children, context);

		// add own controller public methods and instance variables, controller, base_path
		final RVirtualClass controllerVClass = ViewsConventions.getControllerByView(viewFile, module);
		if(controllerVClass == null)
		{
			return children;
		}
		final Symbol controllerSymbol = fileSymbol.getSymbolForContainer(controllerVClass);
		if(controllerSymbol != null)
		{
			addOwnControllerElements(fileSymbol, context, children, controllerSymbol);

			// add own helper
			final String helperName = HelpersConventions.getHelperModuleNameByController(controllerVClass);
			final Symbol parentSymbol = controllerSymbol.getParentSymbol();
			assert parentSymbol != null;
			final Symbol ownHelper = SymbolUtil.findSymbol(fileSymbol, parentSymbol, helperName, false, Type.MODULE.asSet());
			if(ownHelper != null)
			{
				SymbolUtil.addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, ownHelper, null);
			}
		}
		return children;
	}

	private static void addOwnControllerElements(@Nonnull final FileSymbol fileSymbol, @Nonnull final Context context, @Nonnull final Children children, @Nonnull final Symbol controllerSymbol)
	{
		// add controller fields
		final Children controllerElements = SymbolUtil.getAllChildrenWithSuperClassesAndIncludes(fileSymbol, context, controllerSymbol, null);
		final Children fields = controllerElements.getSymbolsOfTypes(Type.INSTANCE_FIELD.asSet());
		children.addChildren(fields);

		// add controller public instance methods
		// TODO[oleg]: add filtering of public methods
		final Children methods = controllerElements.getSymbolsOfTypes(Type.INSTANCE_METHOD.asSet());
		children.addChildren(methods);

		// add attr_internal`s and cattr_accessors
		final Children internals = controllerElements.getSymbolsOfTypes(new TypeSet(Type.ATTR_INTERNAL, Type.CATTR_ACCESSOR));
		children.addChildren(internals);

		// add 'controller' attribute
		children.addSymbol(new Symbol(controllerSymbol.getProject(), "controller", Type.ATTRIBUTE, null, null));
		// add 'base_path' attribute
		children.addSymbol(new Symbol(controllerSymbol.getProject(), "base_path", Type.ATTRIBUTE, null, null));
	}

	private static void addApplicationHelper(@Nonnull final FileSymbol fileSymbol, @Nonnull final Children children, @Nonnull final Context context)
	{
		final Symbol applicationHelper = SymbolUtil.findSymbol(fileSymbol, fileSymbol.getRootSymbol(), HelpersConventions.APPLICATION_HELPER, true, Type.MODULE.asSet());
		if(applicationHelper != null)
		{
			SymbolUtil.addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, applicationHelper, null);
		}
	}

	private static void addApplicationController(@Nonnull final FileSymbol fileSymbol, @Nonnull final Children children, @Nonnull final Context context)
	{
		final Symbol applicationController = SymbolUtil.findSymbol(fileSymbol, fileSymbol.getRootSymbol(), ControllersConventions.APPLICATION_CONTROLLER, true, Type.CLASS.asSet());
		if(applicationController != null)
		{
			SymbolUtil.addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, applicationController, null);
		}
	}

	private static void addBuiltinHelpers(@Nonnull final FileSymbol fileSymbol, @Nonnull final Children children, @Nonnull final Context context)
	{
		final Symbol helpersModule = SymbolUtil.findSymbol(fileSymbol, fileSymbol.getRootSymbol(), HelpersConventions.ACTION_VIEW_PATH, true, Type.MODULE.asSet());
		if(helpersModule != null)
		{
			for(Symbol module : helpersModule.getChildren(fileSymbol).getSymbolsOfTypes(Type.MODULE.asSet()).getAll())
			{
				final String name = module.getName();
				if(name != null && name.endsWith(HelpersConventions.HELPER))
				{
					SymbolUtil.addAllChildrenWithSuperClassesAndIncludes(fileSymbol, children, context, module, null);
				}
			}
		}
	}
}
