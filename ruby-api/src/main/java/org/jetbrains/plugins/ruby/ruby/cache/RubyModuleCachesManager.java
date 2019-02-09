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

import javax.annotation.Nonnull;

import com.intellij.openapi.module.Module;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.CacheScannerFilesProvider;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 22, 2007
 */
public class RubyModuleCachesManager extends BaseModuleCachesManager
{
	private static final RubyModuleCachesManager ourDummyInstance = new RubyModuleCachesManager();

	public static RubyModuleCachesManager getInstance(@Nonnull final Module module)
	{
//		if(JRubyUtil.hasJRubySupport(module))
//		{
//			final JRubyFacet facet = JRubyFacet.getInstance(module);
//			assert facet != null;
//			return facet.getRubyModuleCachesManager();
//		}
//		return module.getComponent(RubyModuleCachesManager.class);
		return ourDummyInstance;
	}

	public void initComponent()
	{

//		final Project project = myModule.getProject();
//		final Runnable initAction = new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				myModuleFilesCache.initFileCacheAndRegisterListeners();
//			}
//		};
//		final Runnable setupAction = new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				registerDeclarationsIndesAndInitFilesCache(myModule, myModuleFilesCache);
//			}
//		};
//
//		final boolean projectIsOpened = project.isOpen();
//
//		myModuleFilesCache = new RubyModuleFilesCacheImpl(myModule, myModuleRootManager);
//		myModuleFilesCache.setCacheFilePath(generateCacheFilePath());
//		myModuleFilesCache.setCacheRootURLs(ModuleRootManager.getInstance(myModule).getContentRootUrls());
//
//		if(projectIsOpened)
//		{
//			//FacetDetector works in pre or startup activities.
//			//so here we cant register preStartupActivity
//			initAction.run();
//		}
//		else
//		{
//			StartupManagerEx.getInstanceEx(project).registerPreStartupActivity(initAction);
//		}
//		StartupManager.getInstance(project).runWhenProjectIsInitialized(setupAction);
	}

	/**
	 * Invoke it only after RubyModuleCachesManager.initComponent()
	 *
	 * @param provider Provides files for caches. By default all module's ruby files are included.
	 */
	public void registerScanForFilesProvider(final CacheScannerFilesProvider provider)
	{
		//myModuleFilesCache.registerScanForFilesProvider(provider);
	}

	public void unregisterScanForFilesProvider(final CacheScannerFilesProvider provider)
	{
		//myModuleFilesCache.unregisterScanForFilesProvider(provider);
	}

	public static void registerDeclarationsIndesAndInitFilesCache(@Nonnull final Module module, @Nonnull final RubyFilesCache myModuleFilesCache)
	{
//		final Project project = module.getProject();
//
//		final DeclarationsIndex index = new DeclarationsIndexImpl(project);
//		// Associating wordsIndex with moduleFilesCache
//		myModuleFilesCache.registerDeaclarationsIndex(index);
//
//		//This code runs Action immediatly if project is initialized, otherwise registers post startup activiti
//		//if we add new module to existing project, action will run immediatly
//		StartupManager.getInstance(project).runWhenProjectIsInitialized(new Runnable()
//		{
//			@Override
//			public void run()
//			{
//				myModuleFilesCache.setupFileCache(true);
//			}
//		});
	}
}
