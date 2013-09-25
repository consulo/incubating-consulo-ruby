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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.reference.SoftReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCacheListener;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.CacheKey;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.CachedSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 7, 2007
 */
public abstract class AbstractCachedSymbol implements CachedSymbol, RubyFilesCacheListener {
    // full file symbol
    protected FileSymbol myFileSymbol;
    protected final Object LOCK = new Object();

    protected Module myModule;
    protected Sdk mySdk;
    protected Project myProject;

    protected RubyFilesCache[] myCaches;
    private RubyFilesCacheSoftReferenceAdapter myAdapter;
    private CacheKey myKey;
    private Map<CacheKey,SoftReference<CachedSymbol>> myCache;

    public AbstractCachedSymbol(@NotNull final Project project,
                            @Nullable final Module module,
                            @Nullable final Sdk sdk) {
        myProject = project;
        myModule = module;
        mySdk = sdk;
        myCaches = FileSymbolUtil.getCaches(myProject, myModule, mySdk);

        // registering for cache updates
        registerAsCacheListener();
    }

    private void registerAsCacheListener() {
        myAdapter = new RubyFilesCacheSoftReferenceAdapter(this);
        for (RubyFilesCache cache : myCaches) {
            cache.addCacheChangedListener(myAdapter, myProject);
        }
    }

    private void unregisterAsCacheListener() {
        for (RubyFilesCache cache : myCaches) {
            cache.removeCacheChangedListener(myAdapter);
        }
    }

    @Override
	public final void fileRemoved(@NotNull String url) {
        fileChanged(url);
    }

    @Override
	public final void fileUpdated(@NotNull String url) {
        fileChanged(url);
    }

    @Override
	public abstract void fileAdded(@NotNull String url);

    protected abstract void fileChanged(@NotNull String url);

    @Override
	public final boolean isUp2Date() {
        return myFileSymbol!=null;
    }

    @Override
	@Nullable
    public final FileSymbol getUp2DateSymbol(){
        // It`s often operation
        ProgressManager.getInstance().checkCanceled();

        if (!isUp2Date()){
            updateFileSymbol();
        }
        return myFileSymbol;
    }

    protected abstract void updateFileSymbol();

    /**
     * Unregisters as cacheUpdater
     * @throws Throwable
     */
    @Override
	public final void finalize() throws Throwable {
        // unregistering for cache updates
        unregisterAsCacheListener();
        myCache.remove(myKey);
        super.finalize();
    }

    public final void setKey(@NotNull final CacheKey key) {
        myKey = key;
    }

    public final void setMap(@NotNull final Map<CacheKey, SoftReference<CachedSymbol>> softCache) {
        myCache = softCache;
    }
}
