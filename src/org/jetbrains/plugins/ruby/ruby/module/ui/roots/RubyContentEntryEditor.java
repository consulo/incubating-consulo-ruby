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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots;

import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ContentFolder;
import com.intellij.openapi.roots.ExcludeFolder;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryEditor;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.ruby.roots.RModuleContentRootManager;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
public class RubyContentEntryEditor extends RContentEntryEditor {
    private Set<VirtualFile> myTestFolders = new HashSet<VirtualFile>();
    protected final RModuleContentRootManager myManager;


    public RubyContentEntryEditor(final ContentEntry contentEntry,
                                  final ModifiableRootModel rootModel) {
        super(contentEntry, rootModel);

        myManager = RModuleUtil.getModuleContentManager(rootModel.getModule());

        init();
    }

    protected RubyContentRootPanel createContentRootPanel(final ContentEntry contentEntry, final RContentEntryEditor contentEntryEditor) {
        return new RubyContentRootPanel(contentEntry, (RubyContentEntryEditor)contentEntryEditor);
    }

    public void addTestSourceFolder(final VirtualFile folder) {
        myTestFolders.add(folder);
        myEventDispatcher.getMulticaster().folderAdded(folder);
        update();
    }

    public void apply() {
        saveExistentTestRoots();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void deleteContentFolder(final ContentEntry contentEntry,
                                    final ContentFolder folder) {
        if (folder instanceof RubyContentRootPanel.RTestSourceFolder) {
            removeTestSourceFolder(folder.getFile());
        } else if (folder instanceof ExcludeFolder) {
            removeExcludeFolder((ExcludeFolder)folder);
        }
    }

    public VirtualFile getTestSourceFolder(VirtualFile file) {
        return myTestFolders.contains(file) ? file : null;
    }

    public VirtualFile[] getTestSourceFolders() {
        return myTestFolders.toArray(new VirtualFile[myTestFolders.size()]);
    }

    public boolean isTestSource(final VirtualFile file) {
        return getTestSourceFolder(file) != null;
    }

    public boolean isModified() {
        final Set<String> testUrls = myManager.getTestUnitFolderUrls();

        return isUrlsListsModified(testUrls, myTestFolders);
    }

    public void removeTestSourceFolder(final VirtualFile folder) {
        myTestFolders.remove(folder);
        myEventDispatcher.getMulticaster().folderRemoved(folder);
        update();
    }

    protected void loadRoots() {
        loadExistentTestRoots();
    }

    private void loadExistentTestRoots() {
        final Set<String> testRootUrls = myManager.getTestUnitFolderUrls();
        final VirtualFileManager vFManager = VirtualFileManager.getInstance();
        for (String url : testRootUrls) {
            final VirtualFile file = vFManager.findFileByUrl(url);
            if (file != null) {
                myTestFolders.add(file);
            }
        }
    }

    private void saveExistentTestRoots() {
        final LinkedList<String> testUrls = new LinkedList<String>();
        for (VirtualFile folder : myTestFolders) {
            testUrls.add(folder.getUrl());
        }
        myManager.setTestUnitFolderUrls(testUrls);
    }
}
