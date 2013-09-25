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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.reference.SoftReference;
import com.intellij.util.containers.HashMap;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.RubyComponents;
import org.jetbrains.plugins.ruby.ruby.cache.RubySdkCachesManager;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl.AbstractCachedSymbol;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 17, 2007
 */
public class SymbolsCache implements ProjectComponent {

    // Here we store info about modifiable symbols
    private Map<CacheKey, SoftReference<CachedSymbol>> mySoftCache = new HashMap<CacheKey, SoftReference<CachedSymbol>>();

    // Here we store info about built-in symbols
    private final Map<CacheKey, CachedSymbol> myBuiltInCache = new HashMap<CacheKey, CachedSymbol>();

    private Project myProject;

    @SuppressWarnings({"UnusedDeclaration"})
    @NotNull
    public static SymbolsCache getInstance(@NotNull final Project project) {
        return project.getComponent(SymbolsCache.class);
    }

    public SymbolsCache(@NotNull final Project project,
                        @NotNull final RubySdkCachesManager manager) {
        myProject = project;
        manager.registerSymbolsCache(this);

        //Also we must be sure that RubySdkCachesManager will initialize sdk caches(in post startUp activity)
        //before next activity.
        //So constructor of RubySdkCachesManager must be invoked before this constructor!
        StartupManager.getInstance(myProject).registerPostStartupActivity(new Runnable() {
            @Override
			public void run() {
                recreateAllBuiltInCaches();
            }
        });
    }

    public void clearCaches() {
        myBuiltInCache.clear();

        clearCachesExceptBuiltIn();
    }

    public void clearCachesExceptBuiltIn() {
        mySoftCache.clear();
        LastSymbolStorage.getInstance(myProject).setSymbol(null);
    }

    public void recreateAllBuiltInCaches(){
        recreateBuiltInCaches(RModuleUtil.getAllModulesWithRubySupport(myProject));
    }

    public void recreateBuiltInCaches(final Module[] modules){
        clearCaches();

        final Runnable runnable = new Runnable() {
            @Override
			public void run() {

                // Here we build required caches
                final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
                if (indicator != null) {
                    indicator.setIndeterminate(true);
                }

                // Here we gather info about sdks and builin cache types required
                for (Module module : modules) {
                    final Sdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
                    if (sdk != null){
                        final boolean isJRubyEnabled = JRubyUtil.hasJRubySupport(module);
                        if (RailsFacetUtil.hasRailsSupport(module)) {
                            SymbolCacheUtil.getFileSymbol(getBuiltInCachedSymbol(FileSymbolType.RAILS_BUILT_IN, sdk, isJRubyEnabled));
                        } else if (RModuleUtil.hasRubySupport(module)){
                            SymbolCacheUtil.getFileSymbol(getBuiltInCachedSymbol(FileSymbolType.BUILT_IN, sdk, isJRubyEnabled));
                        }
                    }
                }

            }
        };
        final String title = RBundle.message("cache.symbol.building.builtins.title");
        ProgressManager.getInstance().runProcessWithProgressSynchronously(runnable, title, false, myProject);
  }

    @Override
	public void projectOpened() {
        // N/A
    }

    @Override
	public void projectClosed() {
        mySoftCache.clear();
        myBuiltInCache.clear();
    }

    @Override
	@NonNls
    @NotNull
    public String getComponentName() {
        return RubyComponents.RUBY_SYMBOL_CACHE;
    }

    @Override
	public void initComponent() {
    }

    @Override
	public void disposeComponent() {
    }

    /**
     * Extract modifiable fileSymbol from modifiable cache
     * @param type Type
     * @param url Url for file
     * @param module file module
     * @param sdk sdk for file
     * @param jrubyEnabled is JRuby Enabled
     * @return up2dated FileSymbol
     */
    @Nullable
    public CachedSymbol getModifiableCachedSymbol(@NotNull final FileSymbolType type,
                                                  @Nullable final String url,
                                                  @Nullable final Module module,
                                                  @Nullable final Sdk sdk,
                                                  final boolean jrubyEnabled) {
        final CacheKey key = new CacheKey(type, url, module, sdk, jrubyEnabled);
        SoftReference<CachedSymbol> reference = mySoftCache.get(key);
        CachedSymbol cachedSymbol = reference!=null ? reference.get() : null;
        if (cachedSymbol != null) {
            return cachedSymbol;
        }
        cachedSymbol = CachedSymbolFactory.createCachedSymbol(type, url, myProject, module, sdk, jrubyEnabled);
        if (cachedSymbol == null){
            removeKey(key);
            return null;
        }
        ((AbstractCachedSymbol) cachedSymbol).setKey(key);
        ((AbstractCachedSymbol) cachedSymbol).setMap(mySoftCache);
        reference = new SoftReference<CachedSymbol>(cachedSymbol);
        mySoftCache.put(key, reference);
        return cachedSymbol;
    }

    /**
     * Extract modifiable fileSymbol from modifiable cache
     * @param type Type
     * @param module file module
     * @param sdk sdk for file
     * @param jrubyEnabled is JRuby Enabled
     * @return up2dated FileSymbol
     */
    @Nullable
    public CachedSymbol getCachedSymbol(@NotNull final FileSymbolType type,
                                        @Nullable final Module module,
                                        @Nullable final Sdk sdk,
                                        final boolean jrubyEnabled) {
        return getModifiableCachedSymbol(type, null, module, sdk, jrubyEnabled);
    }

    /**
     * Extract builtin fileSymbol from builtin caches
     * @param type Type of builtin fileSymbol
     * @param sdk sdk for filesymbol
     * @param jrubyEnabled is JRuby Enabled
     * @return up2dated FileSymbol
     */
    @Nullable
    public CachedSymbol getBuiltInCachedSymbol(@NotNull final FileSymbolType type,
                                               @Nullable final Sdk sdk,
                                               final boolean jrubyEnabled) {
        final String cacheKeyUrl = SymbolCacheUtil.getStubUrlByType(type, sdk, jrubyEnabled);
        if (cacheKeyUrl == null) {
            return null;
        }

        final CacheKey key = new CacheKey(type, cacheKeyUrl, null, sdk, jrubyEnabled);
        CachedSymbol builtInCachedSymbol = myBuiltInCache.get(key);

        if (builtInCachedSymbol == null) {
            builtInCachedSymbol = CachedSymbolFactory.createCachedSymbol(type, cacheKeyUrl, myProject, null, sdk, jrubyEnabled);
            if (builtInCachedSymbol == null) {
                return null;
            }
            myBuiltInCache.put(key, builtInCachedSymbol);
        }
        return builtInCachedSymbol;
    }

    public void removeKey(@NotNull final CacheKey key){
        mySoftCache.remove(key);
    }
}