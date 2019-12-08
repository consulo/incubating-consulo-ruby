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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.CacheScannerFilesProvider;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCacheListener;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualAlias;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualConstant;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualFieldAttr;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualGlobalVar;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 29.04.2007
 */
@Deprecated
public abstract class BaseModuleCachesManager
{
	protected Module myModule;

	protected RubyModuleFilesCache myModuleFilesCache = new RubyModuleFilesCache()
	{
		@Override
		public List<String> getAllRelativeUrlsForDirectory(@Nullable VirtualFile directory, boolean onlyDirectoryFiles)
		{
			return null;
		}

		@Override
		public void registerScanForFilesProvider(CacheScannerFilesProvider provider)
		{

		}

		@Override
		public void unregisterScanForFilesProvider(CacheScannerFilesProvider provider)
		{

		}

		@Override
		public void registerDeaclarationsIndex(@Nonnull DeclarationsIndex index)
		{

		}

		@Nonnull
		@Override
		public DeclarationsIndex getDeclarationsIndex()
		{
			return new DeclarationsIndex()
			{
				@Nonnull
				@Override
				public List<RVirtualClass> getClassesByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualModule> getModulesByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualMethod> getMethodsByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualField> getFieldsByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualConstant> getConstantsByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualGlobalVar> getGlobalVarsByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualAlias> getAliasesByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public List<RVirtualFieldAttr> getFieldAttrsByName(@Nonnull String name)
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllClassesNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllMethodsNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllModulesNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllFieldsNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllConstantsNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllGlobalVarsNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllAliasesNames()
				{
					return Collections.emptyList();
				}

				@Nonnull
				@Override
				public Collection<String> getAllFieldAttrsNames()
				{
					return Collections.emptyList();
				}

				@Override
				public void addFileInfoToIndex(@Nonnull RFileInfo fileInfo)
				{

				}

				@Override
				public void removeFileInfoFromIndex(@Nullable RFileInfo fileInfo)
				{

				}

				@Override
				public void setFileCache(RubyFilesCache myFilesCache)
				{

				}

				@Override
				public void build(boolean runProcessWithProgressSynchronously)
				{

				}
			};
		}

		@Override
		public void saveCacheToDisk()
		{

		}

		@Override
		public void removeCacheFile()
		{

		}

		@Override
		public void setupFileCache(boolean runProcessWithProgressSynchronously)
		{

		}

		@Override
		public void initFileCacheAndRegisterListeners()
		{

		}

		@Override
		public void setCacheFilePath(@Nonnull String path)
		{

		}

		@Nullable
		@Override
		public RFileInfo getUp2DateFileInfo(@Nonnull VirtualFile file)
		{
			return null;
		}

		@Override
		public void addCacheChangedListener(@Nonnull RubyFilesCacheListener listener, @Nonnull Disposable parent)
		{

		}

		@Override
		public void removeCacheChangedListener(@Nonnull RubyFilesCacheListener listener)
		{

		}

		@Nonnull
		@Override
		public List<String> getAllRelativeUrlsForDirectory(@Nullable VirtualFile directory)
		{
			return Collections.emptyList();
		}

		@Nonnull
		@Override
		public Set<String> getAllUrls()
		{
			return Collections.emptySet();
		}

		@Override
		public boolean containsUrl(@Nonnull String url)
		{
			return false;
		}

		@Override
		public void setCacheRootURLs(@Nonnull String[] urls)
		{

		}

		@Override
		public String[] getCacheRootURLs()
		{
			return new String[0];
		}

		@Override
		public void forceUpdate()
		{

		}

		@Override
		public void dispose()
		{

		}
	};


	@Nonnull
	public RubyModuleFilesCache getFilesCache()
	{
		return myModuleFilesCache;
	}

	@Nonnull
	public DeclarationsIndex getDeclarationsIndex()
	{
		return myModuleFilesCache.getDeclarationsIndex();
	}
}
