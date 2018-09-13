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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.RubySdkCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.FileSymbolType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolCacheUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.containers.HashMap;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 11, 2007
 */
public class FileSymbolUtil
{
	private static final Logger LOG = Logger.getInstance(FileSymbolUtil.class.getName());

	/**
	 * Returns the full symbol for file.
	 * It is macros for getFileSymbol(rFile, isLightRubyTestMode) where isLightRubyTestMode is false.
	 *
	 * @param rFile Ruby file to get symbol for
	 * @return The symbol - the lineared sum of file and all required objects
	 */
	@Nullable
	public static FileSymbol getFileSymbol(@NotNull final RFile rFile)
	{
		return getFileSymbol(rFile, false);
	}

	/**
	 * Returns the full symbol for file
	 *
	 * @param rVFile         Pure virtual file to get symbol for(psi will be replaced with it's virtual)
	 * @param isRubyTestMode True if symbol should be evaluated for ruby test mode,
	 *                       in this mode symbol builder process only requires to files form tests folders
	 * @return The symbol - the lineared sum of file and all required objects
	 */
	@Nullable
	public static FileSymbol getFileSymbol(@NotNull final RVirtualFile rVFile, final boolean isRubyTestMode)
	{
		// Check if this action is not stopped
		ProgressManager.getInstance().checkCanceled();
		final RFile rFile;
		final RVirtualFile pureRVFile;
		if(rVFile instanceof RFile)
		{
			rFile = (RFile) rVFile;
			pureRVFile = (RVirtualFile) RVirtualPsiUtil.findVirtualContainer(rFile);
		}
		else
		{
			final RFileInfo info = rVFile.getContainingFileInfo();
			assert info != null; //not null for virtual elements
			pureRVFile = rVFile;
			rFile = (RFile) RVirtualPsiUtil.findPsiByVirtualElement(pureRVFile, info.getProject());
		}

		// Psi file is used to obtain module and project
		LOG.assertTrue(rFile != null, "rFile shouldn`t be null " + rVFile.getContainingFileUrl()); //shouldn't be null
		final VirtualFile file = rFile.getVirtualFile();
		LOG.assertTrue(file != null, "file shouldn`t be null " + rVFile.getContainingFileUrl()); //shouldn't be null

		final Module module = rFile.getModule();
		final Project project = rFile.getProject();
		final boolean jrubyEnabled = rFile.isJRubyEnabled();
		final String url = file.getUrl();

		// Symbol build should work with pure virtual files
		// because it incorrectly works on psi files
		if(pureRVFile == null)
		{
			// can't get from cache in not ruby projects or if no JRuby facet detected
			return null;
		}

		if(isRubyTestMode)
		{
			final Sdk sdk = rFile.getSdk();
			final RubyFilesCache[] caches = FileSymbolUtil.getCaches(project, module, sdk);
			final FileSymbol fileSymbol = new FileSymbol(null, project, jrubyEnabled, caches);
			FileSymbolUtil.process(fileSymbol, url, InterpretationMode.ONLY_TESTS_EXTERNAL, true);
			return fileSymbol;
		}

		//noinspection ConstantConditions
		final Sdk sdk = module != null ? RModuleUtil.getModuleOrJRubyFacetSdk(module) : RubySdkCachesManager.getInstance(project).getFirstSdkForFile(rFile.getVirtualFile());

		final FileSymbol fileSymbol = SymbolCacheUtil.getFileSymbol(SymbolsCache.getInstance(project).getModifiableCachedSymbol(FileSymbolType.MODIFIABLE, url, module, sdk, jrubyEnabled));
		// setting last evaluated symbol
		LastSymbolStorage.getInstance(project).setSymbol(fileSymbol);
		return fileSymbol;
	}

	public static RubyFilesCache[] getCaches(@NotNull final Project project, @Nullable final Module module, @Nullable final Sdk sdk)
	{
		final List<RubyFilesCache> cachesList = RVirtualPsiUtil.getCaches(project, module, sdk);
		return cachesList.toArray(new RubyFilesCache[cachesList.size()]);
	}

	@Nullable
	public static RFileInfo getRFileInfo(@NotNull final String url, @NotNull final RubyFilesCache... caches)
	{
		final VirtualFile vFile = VirtualFileManager.getInstance().findFileByUrl(url);
		if(vFile == null)
		{
			return null;
		}

		// There is no info in caches for non ruby files (*.so for example)
		final RubyFilesCache cacheForFile = RVirtualPsiUtil.getCacheForFile(url, caches);
		if(cacheForFile == null)
		{
			return null;
		}
		return cacheForFile.getUp2DateFileInfo(vFile);
	}

	// Nullable safe operations
	public static void addLoadPath(@Nullable final FileSymbol fileSymbol, @NotNull final String url)
	{
		if(fileSymbol != null)
		{
			fileSymbol.addLoadPathUrl(url);
		}
	}

	public static void process(@Nullable final FileSymbol fileSymbol, @NotNull final String url, @NotNull final InterpretationMode mode, final boolean forceAdd)
	{
		if(fileSymbol != null)
		{
			fileSymbol.process(url, mode, forceAdd);
		}
	}

	@NotNull
	public static Set<String> getUrls(@Nullable final FileSymbol fileSymbol)
	{
		if(fileSymbol != null)
		{
			return fileSymbol.getUrls();
		}
		return Collections.emptySet();
	}
}
