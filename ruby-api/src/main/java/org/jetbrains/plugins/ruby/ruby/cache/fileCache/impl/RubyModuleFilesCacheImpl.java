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

package org.jetbrains.plugins.ruby.ruby.cache.fileCache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import jakarta.annotation.Nonnull;

import consulo.component.messagebus.MessageBusConnection;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.CacheScannerFilesProvider;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.listeners.RubyPomModelListener;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import consulo.module.event.ModuleListener;
import consulo.module.Module;
import consulo.project.Project;
import consulo.module.content.ModuleFileIndex;
import consulo.module.content.ModuleRootManager;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import consulo.disposer.Disposer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman.Chernyatchik, oleg
 * @date: Jan 25, 2007
 */
public class RubyModuleFilesCacheImpl extends RubyFilesCacheImpl implements RubyModuleFilesCache
{

	protected final Module myModule;

	private RubyPomModelListener myPomModelListener;
	protected ModuleRootManager myModuleRootManager;
	protected List<CacheScannerFilesProvider> myScanProvidersList = new ArrayList<CacheScannerFilesProvider>();

	public RubyModuleFilesCacheImpl(@Nonnull final Module module, @Nonnull final ModuleRootManager manager)
	{
		super(module.getProject(), module.getName());
		myModule = module;
		myModuleRootManager = manager;
		registerScanForFilesProvider(new CacheScannerFilesProvider()
		{
			@Override
			public void scanAndAdd(final String[] rootUrls, final Collection<VirtualFile> files, final ModuleRootManager moduleRootManager)
			{
				RubyVirtualFileScanner.searchRubyFileCacheFiles(moduleRootManager, files);
			}
		});
	}

	@Override
	public void initFileCacheAndRegisterListeners()
	{
		super.initFileCacheAndRegisterListeners();
		registerPomListener();
		registerModuleDeleteListener();
	}

	@Override
	protected void registerDisposer()
	{
		if(JRubyUtil.hasJRubySupport(myModule))
		{
			//noinspection ConstantConditions
			//  Disposer.register(JRubyFacet.getInstance(myModule), this);
		}
		else
		{
			Disposer.register(myModule, this);
		}
	}

	@Override
	public void onClose()
	{
		unregisterPomListener();
		super.onClose();
	}

	@Override
	public List<String> getAllRelativeUrlsForDirectory(@Nullable final VirtualFile directory, final boolean onlyDirectoryFiles)
	{
		if(directory == null)
		{
			return Collections.emptyList();
		}
		assert directory.isDirectory();
		return RubyVirtualFileScanner.getRelativeUrlsForModule(myModuleRootManager, onlyDirectoryFiles, directory);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listeners
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Adds pom model listener to files cache
	 */
	private void registerPomListener()
	{
	   /* final PomModel pomModel = myModule.getPom().getModel();
        myPomModelListener = new RubyPomModelListener(myModule, pomModel) {
            protected synchronized void processEvent(final List<RubyChange> list, final VirtualFile vFile) {
                ProgressManager.getInstance().checkCanceled();
                regenerateFileInfo(vFile);
            }
        };
        pomModel.addModelListener(myPomModelListener, myModule);    */
	}

	private void registerModuleDeleteListener()
	{
		final MessageBusConnection messageBusConnection = myModule.getMessageBus().connect(this);
		messageBusConnection.subscribe(ModuleListener.class, new RubyModuleListenerAdapter()
		{
			@Override
			public void beforeModuleRemoved(final Project project, final Module module)
			{
				if(module == myModule)
				{
					onClose();
				}
			}
		});
	}

	private void unregisterPomListener()
	{
		//myModule.getPom().getModel().removeModelListener(myPomModelListener);
	}

	@Override
	public boolean containsUrl(@Nonnull String url)
	{
		return isInContent(VirtualFileManager.getInstance().findFileByUrl(url));
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Internal functions
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@SuppressWarnings({"unchecked"})
	protected Collection<VirtualFile> scanForFiles(@Nonnull final String[] rootUrls)
	{
		final List<VirtualFile> files = new LinkedList<VirtualFile>();

		for(CacheScannerFilesProvider filesProvider : myScanProvidersList)
		{
			filesProvider.scanAndAdd(rootUrls, files, myModuleRootManager);
		}
		return files;
	}

	@Override
	protected boolean isInContent(@Nullable final VirtualFile file)
	{
		final ModuleFileIndex moduleFileIndex = RubyVirtualFileScanner.getFileIndex(myModuleRootManager);
		return file != null && moduleFileIndex != null && moduleFileIndex.isInContent(file);
	}

	public String toString()
	{
		return super.toString() + " It is Module storage for (" + myModule.toString() + ").";
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Valid Files providers
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@Override
	public void registerScanForFilesProvider(final CacheScannerFilesProvider provider)
	{
		myScanProvidersList.add(provider);
	}

	@Override
	public void unregisterScanForFilesProvider(final CacheScannerFilesProvider provider)
	{
		myScanProvidersList.remove(provider);
	}
}
