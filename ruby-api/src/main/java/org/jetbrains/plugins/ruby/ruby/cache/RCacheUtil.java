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

package org.jetbrains.plugins.ruby.ruby.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import consulo.language.psi.scope.GlobalSearchScope;
import consulo.module.Module;
import consulo.module.content.ProjectRootManager;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index.RubyClassNameIndex;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import consulo.project.Project;
import consulo.util.lang.ref.Ref;
import consulo.virtualFileSystem.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */

/**
 * Utils for class/method/module lookup. Backed by the stub index since the
 * legacy file-based cache is being phased out.
 */
public class RCacheUtil
{
	/**
	 * Search classes with specified name in search scope.
	 *
	 * @param simpleName Class simpleName(not qualified)
	 * @param scope      Search scope. If null, project-wide search scope is used.
	 * @param project    Project
	 * @return Array with ruby classes
	 */
	@Nonnull
	public static RClass[] getClassesByName(@Nonnull final String simpleName, @Nullable final GlobalSearchScope scope, @Nonnull final Project project)
	{
		final GlobalSearchScope searchScope = scope != null ? scope : GlobalSearchScope.allScope(project);
		final Collection<RClass> found = RubyClassNameIndex.find(simpleName, project, searchScope);
		return found.toArray(new RClass[0]);
	}

	/**
	 * Search first class with specified name in search scope.
	 *
	 * @param name    Class name(not qualified)
	 * @param scope   Search scope. If null, project-wide search scope is used.
	 * @param project Project
	 * @return null if nothing was found
	 */
	@Nullable
	public static RClass getFirstClassByName(@Nonnull final String name, @Nullable final GlobalSearchScope scope, @Nonnull final Project project)
	{
		final GlobalSearchScope searchScope = scope != null ? scope : GlobalSearchScope.allScope(project);
		final Collection<RClass> found = RubyClassNameIndex.find(name, project, searchScope);
		return found.isEmpty() ? null : found.iterator().next();
	}

	/**
	 * Searched class with specified name from specified file at first in modules, then in sdk.
	 *
	 * @param className  Class name (not qualified)
	 * @param project    Project
	 * @param sScope     Search scope. If is null all scope will be used.
	 * @param scriptFile Ruby script
	 * @return Ruby class found in the given script file or null
	 */
	@Nullable
	public static RClass getFirstClassByNameInScript(@Nonnull final String className, @Nonnull final Project project, @Nullable final GlobalSearchScope sScope, @Nonnull final VirtualFile scriptFile)
	{
		final GlobalSearchScope scope = sScope != null ? sScope : GlobalSearchScope.allScope(project);
		for(RClass rClass : RubyClassNameIndex.find(className, project, scope))
		{
			if(scriptFile.equals(rClass.getContainingFile().getVirtualFile()))
			{
				return rClass;
			}
		}
		return null;
	}

	/**
	 * Searched test class with specified qualified name from specified file at first in modules, then in sdk.
	 *
	 * @param qualifiedClassName Class name
	 * @param project            Project
	 * @param sScope             Search scope. If is null all scope will be used.
	 * @param scriptFile         Ruby script
	 * @param fSWrapper          if null nothing will happen. If wrapper contains
	 *                           not null value, this value will be used for comparing qualified names, otherwise method
	 *                           will store evaluated light mode symbol.
	 * @return Ruby class or null
	 */
	@Nullable
	public static RClass getClassByNameInScriptInRubyTestMode(@Nonnull final String qualifiedClassName, @Nonnull final Project project, @Nullable final GlobalSearchScope sScope, @Nonnull final VirtualFile scriptFile, @Nullable final Ref<FileSymbol> fSWrapper)
	{
		final GlobalSearchScope scope = sScope != null ? sScope : GlobalSearchScope.allScope(project);
		final String realClassName = RClassPresentationUtil.getNameByQualifiedName(qualifiedClassName);
		for(RClass rClass : RubyClassNameIndex.find(realClassName, project, scope))
		{
			if(scriptFile.equals(rClass.getContainingFile().getVirtualFile()))
			{
				// one file often contains few classes with equal names
				final String qName = RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(rClass, fSWrapper);
				if(qualifiedClassName.equals(qName))
				{
					return rClass;
				}
			}
		}
		return null;
	}

	@Nullable
	public static Module getModuleByFile(@Nonnull final VirtualFile file, @Nonnull final Project project)
	{
		return ProjectRootManager.getInstance(project).getFileIndex().getModuleForFile(file);
	}
}
