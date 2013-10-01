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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCacheListener;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfoFactory;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFilesStorage;
import org.jetbrains.plugins.ruby.ruby.cache.info.impl.RFilesStorageImpl;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.ide.caches.FileContent;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 06.11.2006
 */
public class RubyFilesCacheImpl implements RubyFilesCache
{

	protected final Project myProject;

	private final static Logger LOG = Logger.getInstance(RubyFilesCacheImpl.class.getName());

	private DeclarationsIndex myIndex;

	private String myCacheDataFilePath;
	private String[] myCacheRootURLs;

	private RFilesStorage myRFilesStorage;

	private List<RubyFilesCacheListener> myCacheChangedListeners = new ArrayList<RubyFilesCacheListener>();

	private final Object LOCK = new Object();

	private boolean wasClosed;
	private String myName;

	public RubyFilesCacheImpl(final Project project, final String name)
	{
		myProject = project;
		myName = name;
	}

	@Override
	public void setCacheFilePath(@NotNull final String dataFilePath)
	{
		this.myCacheDataFilePath = dataFilePath;
	}

	@Override
	public void registerDeaclarationsIndex(@NotNull final DeclarationsIndex wordsIndex)
	{
		myIndex = wordsIndex;
		myIndex.setFileCache(this);
	}

	@Override
	@NotNull
	public DeclarationsIndex getDeclarationsIndex()
	{
		return myIndex;
	}

	@Override
	public void setCacheRootURLs(@NotNull String[] newRoots)
	{
		myCacheRootURLs = newRoots;
	}

	@Override
	public String[] getCacheRootURLs()
	{
		return myCacheRootURLs;
	}

	@Override
	public void setupFileCache(final boolean runProcessWithProgressSynchronously)
	{
		if(myIndex != null)
		{
			myIndex.build(runProcessWithProgressSynchronously);
		}
	}

	public String getName()
	{
		return myName;
	}

	@Override
	public void initFileCacheAndRegisterListeners()
	{
		final ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();
		if(indicator != null)
		{
			indicator.setText(RBundle.message("progress.indicator.title.cache.loading", getName()));
		}
		myRFilesStorage = loadCacheFromDisk();
		if(myRFilesStorage == null)
		{
			myRFilesStorage = new RFilesStorageImpl();
		}

		if(indicator != null)
		{
			indicator.setText("");
		}

		registerAsCacheUpdater();
		addVirtualFileListener();
		registerDisposer();
	}

	protected void registerDisposer()
	{
		Disposer.register(myProject, this);
	}

	@Override
	public void dispose()
	{
		synchronized(LOCK)
		{
			if(wasClosed)
			{
				return;
			}
		}
		try
		{
			wasClosed = true;
			onClose();
		}
		catch(Exception ex)
		{
			//Do nothing.
		}
	}

	protected void onClose()
	{
		synchronized(LOCK)
		{
			wasClosed = true;
			myCacheChangedListeners.clear();
		}
		unregisterAsCacheUpdater();
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// File operations
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


	/**
	 * Returns up2date cached info for file
	 * if forceUpdate is true, then if file info in cache has timestamp older than file, file regenerates automatically
	 *
	 * @param file VirtualFile to get cache for
	 * @return RFileInfo info by the file
	 */
	@Override
	@Nullable
	public RFileInfo getUp2DateFileInfo(@NotNull final VirtualFile file)
	{
		if(myRFilesStorage == null)
		{
			LOG.error("FilesStorage cannot be null. Maybe cache wasn't initialized. Cache: " + toString() + ", file: " + file);
		}

		final RFileInfo fileInfo = myRFilesStorage.getInfoByUrl(file.getUrl());
		return fileInfo != null ? fileInfo : createRFileInfo(file);
	}


	/**
	 * Changes RFileInfo for file according corresponding RFile and fires event
	 * that cache for file was added or updated.
	 *
	 * @param file VirtualFile
	 * @return new RFileInfo for file or null.
	 */
	@SuppressWarnings({"UnusedReturnValue"})
	@Nullable
	protected RFileInfo regenerateFileInfo(@NotNull final VirtualFile file)
	{
		final String url = file.getUrl();
		final boolean wasRemoved = removeRFileInfo(url);
		final RFileInfo fileInfo = createRFileInfo(file);
		if(wasRemoved)
		{
			fireFileUpdated(url);
		}
		else
		{
			if(fileInfo != null)
			{
				fireFileAdded(url);
			}
		}
		return fileInfo;
	}

	/**
	 * Removes RFileInfo and cached data for file
	 *
	 * @param url path for file
	 * @return true if cache contained file or false otherwise
	 */
	protected boolean removeRFileInfo(@NotNull final String url)
	{
		final RFileInfo fileInfo = myRFilesStorage.removeInfoByUrl(url);
		if(myIndex != null)
		{
			myIndex.removeFileInfoFromIndex(fileInfo);
		}
		return fileInfo != null;
	}

	/**
	 * Creates RFileInfo for file according corresponding RFile
	 *
	 * @param file VirtualFile
	 * @return new RFileInfo for file or null.
	 */
	@Nullable
	protected RFileInfo createRFileInfo(@NotNull final VirtualFile file)
	{
		final String url = file.getUrl();
		myRFilesStorage.addUrl(url);
		final RFileInfo fileInfo = RFileInfoFactory.createRFileInfo(myProject, file);
		if(fileInfo == null)
		{
			return null;
		}
		myRFilesStorage.addRInfo(fileInfo);

		if(myIndex != null)
		{
			myIndex.addFileInfoToIndex(fileInfo);
		}
		return fileInfo;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Loading and saving cache to disk
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Tries to load cache data from disk!
	 *
	 * @return RFilesStorage object - if something loaded, null otherwise
	 */
	@Nullable
	private RFilesStorage loadCacheFromDisk()
	{
		final File moduleDataFile = new File(myCacheDataFilePath);
		RFilesStorage storage = null;
		if(!moduleDataFile.exists())
		{
			return null;
		}

		// loading RStoreInfo
		try
		{
			ObjectInputStream ois = null;
			try
			{
				ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(moduleDataFile)));
				final Object data = ois.readObject();
				if(data instanceof RFilesStorage)
				{
					storage = (RFilesStorage) data;
					storage.init(myProject);
				}
			}
			finally
			{
				if(ois != null)
				{
					ois.close();
				}
			}
		}
		catch(ClassNotFoundException e)
		{
			LOG.debug(e);
		}
		catch(IOException e)
		{
			LOG.debug(e);
		}
		return storage;
	}

	/**
	 * Saves serialized cache data to dataFile
	 */
	@Override
	public void saveCacheToDisk()
	{
		final File dataFile = new File(myCacheDataFilePath);
		try
		{
			if(!dataFile.exists())
			{
				dataFile.mkdirs();
			}

			if(!dataFile.exists())
			{
				return;
			}

			if(dataFile.isDirectory())
			{
				dataFile.delete();
			}

			dataFile.createNewFile();
			ObjectOutputStream oos = null;
			try
			{
				oos = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(dataFile)));
				oos.writeObject(myRFilesStorage);
			}
			catch(Exception e)
			{
				LOG.error(e);
			}
			finally
			{
				oos.close();
			}
		}
		catch(IOException e)
		{
			LOG.info(e);
		}
	}

	@Override
	public void removeCacheFile()
	{
		final File dataFile = new File(myCacheDataFilePath);
		try
		{
			if(dataFile.exists())
			{
				final File parentDir = dataFile.getParentFile();
				dataFile.delete();
				parentDir.delete();
			}
		}
		catch(SecurityException e)
		{
			LOG.warn("Cache file [" + dataFile.getPath() + "] wasn't deleted.");
			LOG.warn(e);
		}
	}

	@Override
	public boolean containsUrl(@NotNull final String url)
	{
		return myRFilesStorage.containsUrl(url);
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Internal functions
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	@NotNull
	public List<String> getAllRelativeUrlsForDirectory(@Nullable final VirtualFile directory)
	{
		if(directory == null)
		{
			return Collections.emptyList();
		}
		assert directory.isDirectory();
		return RubyVirtualFileScanner.getRelativeUrls(directory);
	}

	@Override
	@NotNull
	public Set<String> getAllUrls()
	{
		if(myRFilesStorage != null)
		{
			return myRFilesStorage.getAllUrls();
		}
		return Collections.emptySet();
	}

	protected Collection<VirtualFile> scanForFiles(@NotNull final String[] rootUrls)
	{
		VirtualFileManager fManager = VirtualFileManager.getInstance();
		final Set<VirtualFile> filesToAdd = new HashSet<VirtualFile>();
		for(String rootUrl : rootUrls)
		{
			final VirtualFile root = fManager.findFileByUrl(rootUrl);
			if(root != null)
			{
				RubyVirtualFileScanner.addRubyFiles(root, filesToAdd);
			}
		}
		return filesToAdd;
	}

	/**
	 * @param fileOrDir file or directory
	 * @return whether file(or directory) is in cache content directories
	 */
	protected boolean isInContent(@Nullable final VirtualFile fileOrDir)
	{
		if(fileOrDir == null)
		{
			return false;
		}
		final String url = fileOrDir.getUrl();
		for(String rootUrl : myCacheRootURLs)
		{
			if(url.startsWith(rootUrl))
			{
				return true;
			}
		}
		return false;
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// CacheUpdaterFunctionality
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public VirtualFile[] queryNeededFiles(ProgressIndicator progressIndicator)
	{
		final Set<String> urls2Remove = new HashSet<String>(myRFilesStorage.getAllUrls());
		final Collection<VirtualFile> foundFiles = scanForFiles(myCacheRootURLs);
		final Set<VirtualFile> neededFiles = new HashSet<VirtualFile>();

		for(VirtualFile file : foundFiles)
		{
			urls2Remove.remove(file.getUrl());
			if(myRFilesStorage.getFileStatus(file) != RFilesStorage.FileStatus.UP_TO_DATE)
			{
				neededFiles.add(file);
			}
			// Add url to storage even if information will be generated later!!!
			// Needed to retrieve include links in non module cache files (sdk for example)
			myRFilesStorage.addUrl(file.getUrl());
		}

		for(String url : urls2Remove)
		{
			processFileDeleted(url);
		}

		return neededFiles.toArray(new VirtualFile[neededFiles.size()]);
	}

	@Override
	public int getNumberOfPendingUpdateJobs()
	{
		return 0;
	}

	@Override
	public void processFile(@NotNull final FileContent fileContent)
	{
		regenerateFileInfo(fileContent.getVirtualFile());
	}

	@Override
	public void updatingDone()
	{
	}

	@Override
	public void canceled()
	{
	}

	protected void processFileDeleted(@NotNull final String url)
	{
		if(removeRFileInfo(url))
		{
			fireFileRemoved(url);
		}
	}

	private void addVirtualFileListener()
	{
		VirtualFileListener listener = new VirtualFileAdapter()
		{

			@Override
			@SuppressWarnings({"ConstantConditions"})
			public void propertyChanged(final VirtualFilePropertyEvent event)
			{
				if(VirtualFile.PROP_NAME.equals(event.getPropertyName()))
				{
					final VirtualFile file = event.getFile();
					if(processDir(file, event.getParent()))
					{
						return;
					}
					final String oldName = (String) event.getOldValue();
					processFileDeleted(VirtualFileUtil.constructUrl(event.getParent(), oldName));
					processFileAdded(file);
				}
			}

			@Override
			public void contentsChanged(final VirtualFileEvent event)
			{
				final VirtualFile file = event.getFile();
				if(processDir(file, event.getParent()))
				{
					return;
				}
				processFileAdded(file);
			}

			@Override
			public void fileCreated(final VirtualFileEvent event)
			{
				final VirtualFile file = event.getFile();
				if(processDir(file, event.getParent()))
				{
					return;
				}
				processFileAdded(file);
			}

			@Override
			public void fileDeleted(final VirtualFileEvent event)
			{
				final VirtualFile file = event.getFile();
				final VirtualFile parent = event.getParent();
				if(processDir(file, parent))
				{
					return;
				}
				if(parent != null)
				{
					//if parent is removed, then this method will be invoked for parent
					//bug fix for : [RUBY-531] http://www.jetbrains.net/jira/browse/RUBY-531
					processFileDeleted(VirtualFileUtil.constructUrl(parent, file.getName()));
				}
			}

			@Override
			public void fileMoved(final VirtualFileMoveEvent event)
			{
				final VirtualFile file = event.getFile();
				if(processDir(file, event.getParent()))
				{
					return;
				}
				processFileDeleted(VirtualFileUtil.constructUrl(event.getOldParent(), file.getName()));
				processFileAdded(file);
			}

			private void processFileAdded(final VirtualFile file)
			{
				if(RubyVirtualFileScanner.isRubyFile(file) && isInContent(file))
				{

					regenerateFileInfo(file);
				}
			}

			private boolean processDir(@NotNull final VirtualFile file, @Nullable final VirtualFile parent)
			{
				if(file.isDirectory() && isInContent(parent))
				{
					//After internal file creation and deletion cache updater doesn't be invoked
					//Thus we should perform update manually
					forceUpdate();
					return true;
				}
				return false;
			}
		};
		VirtualFileManager.getInstance().addVirtualFileListener(listener, this);
	}

	private void registerAsCacheUpdater()
	{
		StartupManager.getInstance(myProject).registerCacheUpdater(this);
	}

	private void unregisterAsCacheUpdater()
	{
		//  ProjectRootManagerEx.getInstanceEx(myProject).unregisterChangeUpdater(this);
		//  ((VirtualFileManagerEx)VirtualFileManagerEx.getInstance()).unregisterRefreshUpdater(this);
	}

	/**
	 * Used in debug puproses
	 */
	@Override
	public void forceUpdate()
	{
	   /* final FileSystemSynchronizer synchronizer = new FileSystemSynchronizer();
        synchronizer.registerCacheUpdater(this);
        if (!ApplicationManager.getApplication().isUnitTestMode() && myProject.isOpen()) {
            Runnable process = new Runnable() {
                public void run() {
                    synchronizer.execute();
                }
            };
            ProgressManager.getInstance().runProcessWithProgressSynchronously(process, RBundle.message("project.root.change.loading.progress"), false, myProject);
        } else {
            synchronizer.execute();
        }  */
	}

	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Listeners
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void addCacheChangedListener(@NotNull final RubyFilesCacheListener listener, @NotNull final Disposable parentDisposable)
	{
		synchronized(LOCK)
		{
			myCacheChangedListeners.add(listener);
		}
		Disposer.register(parentDisposable, new Disposable()
		{
			@Override
			public void dispose()
			{
				removeCacheChangedListener(listener);
			}
		});
	}

	@Override
	public void removeCacheChangedListener(@NotNull final RubyFilesCacheListener listener)
	{
		synchronized(LOCK)
		{
			myCacheChangedListeners.remove(listener);
		}
	}

	private void fireFileRemoved(@NotNull final String url)
	{
		synchronized(LOCK)
		{
			// To prevent ConcurrentModification of listeners by event handling
			final ArrayList<RubyFilesCacheListener> listeners = new ArrayList<RubyFilesCacheListener>(myCacheChangedListeners);
			for(RubyFilesCacheListener listener : listeners)
			{
				listener.fileRemoved(url);
			}
		}
	}

	private void fireFileAdded(@NotNull final String url)
	{
		synchronized(LOCK)
		{
			// To prevent ConcurrentModification of listeners by event handling
			final ArrayList<RubyFilesCacheListener> listeners = new ArrayList<RubyFilesCacheListener>(myCacheChangedListeners);
			for(RubyFilesCacheListener listener : listeners)
			{
				listener.fileAdded(url);
			}
		}
	}

	private void fireFileUpdated(@NotNull final String url)
	{
		synchronized(LOCK)
		{
			// To prevent ConcurrentModification of listeners by event handling
			final ArrayList<RubyFilesCacheListener> listeners = new ArrayList<RubyFilesCacheListener>(myCacheChangedListeners);
			for(RubyFilesCacheListener listener : listeners)
			{
				listener.fileUpdated(url);
			}
		}
	}

	public String toString()
	{
		final StringBuilder st = new StringBuilder();
		st.append("Ruby Files cache[").append(getName()).append("]. Urls=[");
		for(String url : getAllUrls())
		{
			st.append("\"").append(url).append("\"\n");
		}
		st.append("].");
		return st.toString();
	}
}
