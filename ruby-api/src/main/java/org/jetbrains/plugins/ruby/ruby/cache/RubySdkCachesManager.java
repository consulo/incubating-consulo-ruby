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

import com.intellij.ProjectTopics;
import com.intellij.ide.startup.StartupManagerEx;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.impl.DirectoryIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.messages.MessageBusConnection;
import consulo.bundle.SdkTableListener;
import consulo.disposer.Disposable;
import jakarta.inject.Inject;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman.Chernyatchik
 * @date: Jan 23, 2007
 */
public class RubySdkCachesManager implements ProjectComponent, Disposable
{

	private static final Logger LOG = Logger.getInstance(RubySdkCachesManager.class.getName());

	// map with RubyFilesCaches for each sdk
	private Map<Sdk, RubyFilesCache> sdk2RubyFilesCache = new HashMap<Sdk, RubyFilesCache>();

	@NonNls
	private static final String RUBY_CACHE_DIR = "ruby_caches";
	@NonNls
	private static final String RUBY_CACHE_FILE = "sdk";

	private RubyModuleListenerAdapter rModuleListener;
	private ModuleRootListener moduleRootListener;
	private SdkTableListener jdkTableListener;
	private Project myProject;
	private MessageBusConnection myConnection;

	private SymbolsCache symbolsCache;

	@Inject
	public RubySdkCachesManager(@Nonnull final Project project, @Nonnull final DirectoryIndex dirIndex, @Nonnull final PsiManager psiManager)
	{
		myProject = project;

		if(myProject.isDefault())
		{
			return;
		}

		createListeners();

		myConnection = myProject.getMessageBus().connect();

		final StartupManagerEx startupManagerEx = StartupManagerEx.getInstanceEx(myProject);

		//Initialize cahe. Cache should be signed on StartupFileSystemSynchronizer
		startupManagerEx.registerPreStartupActivity(new Runnable()
		{
			@Override
			public void run()
			{
				initSkdCaches(myProject);

				// registering listeners
				myConnection.subscribe(ProjectTopics.MODULES, rModuleListener);
				//SdkTable.getInstance().addListener(jdkTableListener);
				myConnection.subscribe(ProjectTopics.PROJECT_ROOTS, moduleRootListener);
			}
		});
	}

	public void registerSymbolsCache(@Nonnull final SymbolsCache cache)
	{
		symbolsCache = cache;
	}

	private void createListeners()
	{
		rModuleListener = new RubyModuleListenerAdapter()
		{
			@Override
			public void moduleAdded(final Project project, final Module module)
			{
				//rebuild caches for this module
				modulesChanged(project, module);
				rebuildCachesForModules(module);
			}

			@Override
			public void moduleRemoved(final Project project, final Module module)
			{
				//delete caches for this module
				modulesChanged(project, module);
				deleteBuildInCachesForModules(module);
			}
		};

		moduleRootListener = new ModuleRootListener()
		{
			@Override
			public void beforeRootsChange(final ModuleRootEvent event)
			{
				// Do nothing
			}

			@Override
			public void rootsChanged(final ModuleRootEvent event)
			{
				final Project project = (Project) event.getSource();
				if(project != myProject)
				{
					return;
				}

				initAndSetupSkdCaches(project, true);
			}
		};
	}

	private void deleteBuildInCachesForModules(final Module module)
	{
		symbolsCache.clearCaches();
	}

	private void rebuildCachesForModules(final Module module)
	{
		symbolsCache.recreateBuiltInCaches(new Module[]{module});
	}

	/**
	 * @param sdk Sdk to get RFilesStorage
	 * @return FilesCache for sdk
	 */
	@Nullable
	public RubyFilesCache getSdkFilesCache(@Nonnull final Sdk sdk)
	{
		return sdk2RubyFilesCache.get(sdk);
	}

	/**
	 * @param sdk Sdk to get WordsIndex
	 * @return WordsIndex for sdk
	 */
	@Nullable
	public DeclarationsIndex getSdkDeclarationsIndex(@Nullable final Sdk sdk)
	{
		if(sdk == null)
		{
			return null;
		}
		LOG.assertTrue(RubySdkUtil.isKindOfRubySDK(sdk), "Ruby SDK type expected, but was: " + sdk.getName());

		final RubyFilesCache cache = getSdkFilesCache(sdk);
		return cache != null ? cache.getDeclarationsIndex() : null;
	}

	@Override
	public void projectOpened()
	{
		// Do nothing
	}

	private void modulesChanged(@Nonnull final Project project, @Nonnull final Module module)
	{
		if(RModuleUtil.hasRubySupport(module))
		{
			initAndSetupSkdCaches(project, true);
		}
	}

	@Override
	public void projectClosed()
	{
		// unregistering listeners
		//  SdkTable.getInstance().removeListener(jdkTableListener);

		myConnection.disconnect();

		// dispose all the filesCaches
		for(Sdk sdk : sdk2RubyFilesCache.keySet())
		{
			final RubyFilesCache sdkCache = sdk2RubyFilesCache.get(sdk);
			sdkCache.saveCacheToDisk();
		}
	}

	@Override
	@NonNls
	@Nonnull
	public String getComponentName()
	{
		return RComponents.RUBY_SDK_CACHE_MANAGER;
	}


	@Override
	public void initComponent()
	{
		//Do nothing
	}

	@Override
	public void dispose()
	{
		sdk2RubyFilesCache.clear();
	}

	public void initSkdCaches(final Project project)
	{
		initAndSetupSkdCaches(project, false, false);
	}

	public void initAndSetupSkdCaches(final Project project, final boolean runProcessWithProgressSynchronously)
	{
		initAndSetupSkdCaches(project, true, runProcessWithProgressSynchronously);
	}

	protected void initAndSetupSkdCaches(final Project project, final boolean doSetup, final boolean runProcessWithProgressSynchronously)
	{

	}

	public static RubySdkCachesManager getInstance(@Nonnull final Project project)
	{
		return project.getComponent(RubySdkCachesManager.class);
	}

	@Nullable
	public RubyFilesCache getFirstCacheByFile(@Nonnull final VirtualFile file)
	{
		final Sdk sdk = getFirstSdkForFile(file);
		return sdk != null ? getSdkFilesCache(sdk) : null;
	}

	/**
	 * First first sdk, contained file
	 *
	 * @param file Virtual file to search for
	 * @return Sdk or null
	 */
	@Nullable
	public Sdk getFirstSdkForFile(@Nonnull final VirtualFile file)
	{
		final String rootUrl = file.getUrl();
		final boolean isDirectory = file.isDirectory();
		for(Sdk sdk : sdk2RubyFilesCache.keySet())
		{
			final RubyFilesCache cache = getSdkFilesCache(sdk);
			if(cache != null)
			{
				// If we search for directory
				if(isDirectory)
				{
					for(String root : cache.getCacheRootURLs())
					{
						if(rootUrl.startsWith(root))
						{
							return sdk;
						}
					}
				}
				else
				{
					if(cache.containsUrl(rootUrl))
					{
						return sdk;
					}
				}
			}
		}
		return null;
	}

}
