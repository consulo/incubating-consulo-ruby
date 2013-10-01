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

package org.jetbrains.plugins.ruby.ruby.run.confuguration.tests;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualSingletonMethod;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.roots.RubyModuleRootUtil;
import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.Ref;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class RTestUnitUtil
{
	public static final String TEST_METHOD_NAME_PREFIX = "test";

	/**
	 * Recursively checks if user class inherits Test::Unit::TestCase class
	 *
	 * @param rVClassSymbol Symbol for user class
	 * @param fileSymbol    FileSymbol
	 * @return True if inherits.
	 */
	public static boolean isUserUnitTestCase(@NotNull final Symbol rVClassSymbol, @NotNull final FileSymbol fileSymbol)
	{
		if(isBaseUnitTestCase(rVClassSymbol))
		{
			return true;
		}

		//Some times super class may have no virtual prototypes
		//e.g. class A < Test::Unit::TestCase;end; whrere
		// we ignore "test/unit" require.
		final Children superClassSymbols = rVClassSymbol.getChildren(fileSymbol).getSymbolsOfTypes(Type.SUPERCLASS.asSet());
		for(Symbol superClassSymbol : superClassSymbols.getAll())
		{
			if(isUserUnitTestCase(superClassSymbol.getLinkedSymbol(), fileSymbol))
			{
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if class is unit testcase class (Test::Unit::TestCase)
	 *
	 * @param rVClassSymbol Class
	 * @return True if class is Test::Unit::TestCase.
	 */

	public static boolean isBaseUnitTestCase(@NotNull final Symbol rVClassSymbol)
	{
		final String name = rVClassSymbol.getName();
		if(Comparing.equal(RailsConstants.TEST_CASE_CLASS_NAME, name))
		{
			final Symbol unitModuleSymbol = rVClassSymbol.getParentSymbol();
			if(unitModuleSymbol != null && Comparing.equal(RailsConstants.UNIT_MODULE_NAME, unitModuleSymbol.getName()))
			{

				final Symbol testUnitModuleSymb = unitModuleSymbol.getParentSymbol();
				return testUnitModuleSymb != null && Comparing.equal(RailsConstants.TEST_MODULE_NAME, testUnitModuleSymb.getName());
			}
		}
		return false;
	}

	/**
	 * Checks if class inherits form Test::Unit::TestCase using light test mode symbols.
	 * If ruby class isn't direct descendant of Test::Unit::TestCase, all other
	 * checks will be made in context of TEST folders of current project(speed optimization).
	 *
	 * @param rClass            Virtual(cache) or Psi Ruby class
	 * @param fileSymbolWrapper if null nothing will happen. If wrapper contains
	 *                          not null value, this value will be used for check, otherwise method
	 *                          will store evaluated light mode symbol.
	 * @return true if class is valid testcase class
	 */
	public static boolean isClassUnitTestCase(@NotNull final RVirtualClass rClass, @Nullable final Ref<FileSymbol> fileSymbolWrapper)
	{
		// 1. lets check direct ancestor without symbol cashe
		final String fullName;
		if(rClass instanceof RClass)
		{
			//TODO it's awfull style...
			final RSuperClass rSuperClass = ((RClass) rClass).getPsiSuperClass();
			fullName = rSuperClass == null ? null : rSuperClass.getText();
		}
		else
		{
			final RVirtualName vSuperClass = rClass.getVirtualSuperClass();
			fullName = vSuperClass == null ? null : vSuperClass.getFullName();
		}
		if(RailsConstants.TEST_UNIT_TESTCASE.equals(fullName))
		{
			return true;
		}

		// 2. we consider only classes form TEST folder (optimization)
		final String url = rClass.getContainingFileUrl();
		if(!RubyModuleRootUtil.isUnderTestUnitRoot(rClass.getProject(), url))
		{
			return false;
		}

		// 3. check ancestors in light test mode
		final Pair<Symbol, FileSymbol> pair = SymbolUtil.getSymbolByContainerRubyTestMode(rClass, fileSymbolWrapper);
		return pair != null && pair.first != null && isUserUnitTestCase(pair.first, pair.second);
	}

	/**
	 * Checks if method name starts with prefix "test"
	 *
	 * @param method Some ruby method
	 * @return is method test method
	 */
	public static boolean hasValidTestNameAndNotSingleton(@NotNull final RVirtualMethod method)
	{
		final String methodName = method.getName();
		return methodName.startsWith(TEST_METHOD_NAME_PREFIX) && methodName.length() > TEST_METHOD_NAME_PREFIX.length() && !(method instanceof RVirtualSingletonMethod);
	}

	/**
	 * Check if another unit test cases exeis in file.
	 *
	 * @param currentContainer RClass, given unit test case.
	 * @param rFile            Ruby File
	 * @return True if exist.
	 */
	public static boolean checkForAnotherTestCases(@Nullable final RContainer currentContainer, @NotNull final RFile rFile)
	{
		final List<RVirtualClass> classes = RContainerUtil.getTopLevelClasses(rFile);
		final Ref<FileSymbol> fileSymbolWrapper = new Ref<FileSymbol>();

		for(RVirtualClass vClass : classes)
		{
			// Really all classes are "psi", not "virtual".
			if(currentContainer == vClass || !(vClass instanceof RClass))
			{
				continue;
			}
			// if exist another test case in script
			if(isClassUnitTestCase(vClass, fileSymbolWrapper))
			{
				return true;
			}
		}
		return false;
	}
}