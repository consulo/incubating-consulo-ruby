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

import com.intellij.ProjectTopics;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleFileIndex;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.pom.PomModel;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.CacheScannerFilesProvider;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.listeners.RubyPomModelListener;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.ruby.pom.RubyChange;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman.Chernyatchik, oleg
 * @date: Jan 25, 2007
 */
public class RubyModuleFilesCacheImpl extends RubyFilesCacheImpl implements RubyModuleFilesCache {

    protected final Module myModule;

    private RubyPomModelListener myPomModelListener;
    protected ModuleRootManager myModuleRootManager;
    protected List<CacheScannerFilesProvider> myScanProvidersList = new ArrayList<CacheScannerFilesProvider>();

    public RubyModuleFilesCacheImpl(@NotNull final Module module, @NotNull final ModuleRootManager manager) {
        super(module.getProject(), module.getName());
        myModule = module;
        myModuleRootManager = manager;
        registerScanForFilesProvider(new CacheScannerFilesProvider() {
            public void scanAndAdd(final String[] rootUrls,
                                   final Collection<VirtualFile> files,
                                   final ModuleRootManager moduleRootManager) {
                RubyVirtualFileScanner.searchRubyFileCacheFiles(moduleRootManager, files);
            }
        });
    }

    public void initFileCacheAndRegisterListeners() {
        super.initFileCacheAndRegisterListeners();
        registerPomListener();
        registerModuleDeleteListener();
    }

    protected void registerDisposer() {
        if (JRubyUtil.hasJRubySupport(myModule)) {
            //noinspection ConstantConditions
            Disposer.register(JRubyFacet.getInstance(myModule), this);
        } else {
            Disposer.register(myModule, this);
        }
    }

    public void onClose() {
        unregisterPomListener();
        super.onClose();
    }

    public List<String> getAllRelativeUrlsForDirectory(@Nullable final VirtualFile directory,
                                                       final boolean onlyDirectoryFiles) {
        if (directory == null){
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
    private void registerPomListener() {
        final PomModel pomModel = myModule.getPom().getModel();
        myPomModelListener = new RubyPomModelListener(myModule, pomModel) {
            protected synchronized void processEvent(final List<RubyChange> list, final VirtualFile vFile) {
                ProgressManager.getInstance().checkCanceled();
                regenerateFileInfo(vFile);
            }
        };
        pomModel.addModelListener(myPomModelListener, myModule);
    }

    private void registerModuleDeleteListener() {
        final MessageBusConnection messageBusConnection =
                myModule.getMessageBus().connect(this);
        messageBusConnection.subscribe(ProjectTopics.MODULES, new RubyModuleListenerAdapter() {
            public void beforeModuleRemoved(final Project project,
                                            final Module module) {
                if (module == myModule) {
                    onClose();
                }
            }
        });
    }

    private void unregisterPomListener(){
        myModule.getPom().getModel().removeModelListener(myPomModelListener);
    }

    public boolean containsUrl(@NotNull String url) {
        return isInContent(VirtualFileManager.getInstance().findFileByUrl(url));
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Internal functions
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @SuppressWarnings({"unchecked"})
    protected Collection<VirtualFile> scanForFiles(@NotNull final String[] rootUrls) {
        final List<VirtualFile> files = new LinkedList<VirtualFile>();

        for (CacheScannerFilesProvider filesProvider : myScanProvidersList) {
             filesProvider.scanAndAdd(rootUrls, files, myModuleRootManager);
        }
        return files;
    }

    protected boolean isInContent(@Nullable final VirtualFile file) {
        final ModuleFileIndex moduleFileIndex = RubyVirtualFileScanner.getFileIndex(myModuleRootManager);
        return file != null && moduleFileIndex != null && moduleFileIndex.isInContent(file);
    }

    public String toString() {
        return super.toString() + " It is Module storage for ("+myModule.toString()+"). Module type = " + myModule.getModuleType();
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
// Valid Files providers
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public void registerScanForFilesProvider(final CacheScannerFilesProvider provider) {
        myScanProvidersList.add(provider);
    }

    public void unregisterScanForFilesProvider(final CacheScannerFilesProvider provider) {
        myScanProvidersList.remove(provider);
    }
}
