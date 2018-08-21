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

package org.jetbrains.plugins.ruby.ruby.cache.index;

import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 22, 2007
 */
public interface DeclarationsIndex
{
	@NotNull
	public List<RVirtualClass> getClassesByName(@NotNull final String name);

	@NotNull
	public List<RVirtualModule> getModulesByName(@NotNull final String name);

	@NotNull
	public List<RVirtualMethod> getMethodsByName(@NotNull final String name);

	@NotNull
	public List<RVirtualField> getFieldsByName(@NotNull final String name);

	@NotNull
	public List<RVirtualConstant> getConstantsByName(@NotNull final String name);

	@NotNull
	public List<RVirtualGlobalVar> getGlobalVarsByName(@NotNull final String name);

	@NotNull
	public List<RVirtualAlias> getAliasesByName(@NotNull final String name);

	@NotNull
	public List<RVirtualFieldAttr> getFieldAttrsByName(@NotNull final String name);


	/**
	 * Get all classes names
	 *
	 * @return Array of strings or empty array if nothing found
	 */
	@NotNull
	public Collection<String> getAllClassesNames();

	/**
	 * Get all methods names
	 *
	 * @return Array of strings or empty array if nothing found
	 */
	@NotNull
	public Collection<String> getAllMethodsNames();

	/**
	 * Get all modules names
	 *
	 * @return Array of strings or empty array if nothing found
	 */
	@NotNull
	public Collection<String> getAllModulesNames();

	@NotNull
	public Collection<String> getAllFieldsNames();

	@NotNull
	public Collection<String> getAllConstantsNames();

	@NotNull
	public Collection<String> getAllGlobalVarsNames();

	@NotNull
	public Collection<String> getAllAliasesNames();

	@NotNull
	public Collection<String> getAllFieldAttrsNames();

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Files handling operations ////////////////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds all name usages form rFileInfo to index
	 *
	 * @param fileInfo RFileInfo to add
	 */
	public void addFileInfoToIndex(@NotNull final RFileInfo fileInfo);

	/**
	 * Removes all the names usages from fileInfo from index
	 *
	 * @param fileInfo FileInfo to remove
	 */
	public void removeFileInfoFromIndex(@Nullable final RFileInfo fileInfo);


	public void setFileCache(RubyFilesCache myFilesCache);

	/**
	 * Builds index
	 *
	 * @param runProcessWithProgressSynchronously
	 *         If is true update operaiton
	 *         will be run in a background thread and will show a modal progress dialog in
	 *         the main thread while. Otherwise will be run in current thread without
	 *         any modal dialogs..
	 */
	public void build(final boolean runProcessWithProgressSynchronously);
}
