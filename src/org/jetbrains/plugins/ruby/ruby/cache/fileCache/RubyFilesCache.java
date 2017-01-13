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

package org.jetbrains.plugins.ruby.ruby.cache.fileCache;

import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 07.11.2006
 * <p/>
 * Base class for filesCache. Used for module cache and for sdk cache.
 * Basic cache operations, as setupFileCache, refresh, save, get file info.
 */
public interface RubyFilesCache extends /*CacheUpdater, */Disposable
{

	/**
	 * Associate index with given FilesCache
	 *
	 * @param index DeclarationIndex to associate for this fileCache
	 */
	public void registerDeaclarationsIndex(@NotNull final DeclarationsIndex index);

	@NotNull
	public DeclarationsIndex getDeclarationsIndex();

	/**
	 * Saves cache for module
	 */
	public void saveCacheToDisk();

	public void removeCacheFile();

	/**
	 * Setups cache. E.g. rebuilds wordsIndex. Access to PSI elements is allowed.
	 *
	 * @param runProcessWithProgressSynchronously
	 *         If true, initialization runs with attached progress indicator
	 */
	public void setupFileCache(final boolean runProcessWithProgressSynchronously);

	/**
	 * Create storage(or load from disk) and register listeners, such as CacheUpdater, Dsposer, etc. Should be invoked in PreStartupActivity.
	 * Access to PSI elements from this method is deinded!
	 * At first setup file cache path and roots urls. Dont invoke this method befor setup!
	 */
	public void initFileCacheAndRegisterListeners();

	public void setCacheFilePath(@NotNull final String path);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// cache functions
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Returns cached file info
	 *
	 * @param file VirtualFile to get cache for
	 * @return FileInfo cached info associated with file
	 */
	@Nullable
	public RFileInfo getUp2DateFileInfo(@NotNull final VirtualFile file);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Listeners etc
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public void addCacheChangedListener(@NotNull final RubyFilesCacheListener listener, @NotNull final Disposable parent);

	public void removeCacheChangedListener(@NotNull final RubyFilesCacheListener listener);

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//// Files actions
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@NotNull
	public List<String> getAllRelativeUrlsForDirectory(@Nullable final VirtualFile directory);

	@NotNull
	public Set<String> getAllUrls();

	/**
	 * @param url Url to check
	 * @return true if cache contains such an url
	 */
	public boolean containsUrl(@NotNull String url);

	public void setCacheRootURLs(@NotNull final String[] urls);

	public String[] getCacheRootURLs();

	public void forceUpdate();
}
