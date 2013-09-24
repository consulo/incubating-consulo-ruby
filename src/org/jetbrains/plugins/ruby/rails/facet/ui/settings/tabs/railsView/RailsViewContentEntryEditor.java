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

package org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.railsView;

import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ContentFolder;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.plugins.ruby.rails.module.view.RailsViewFoldersManager;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryEditor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
public class RailsViewContentEntryEditor  extends RContentEntryEditor {
    private Set<VirtualFile> myRailsViewUserSourceFolders; //we can't initialize it because super constructor use this value via RContentEntryEditor.
    protected final RailsViewFoldersManager myManager;

    public RailsViewContentEntryEditor(final ContentEntry contentEntry, final ModifiableRootModel rootModel) {
        super(contentEntry, rootModel);

        myManager = RailsViewFoldersManager.getInstance(rootModel.getModule());

        init();
    }

    public void apply() {
        saveExistentRailsViewUserRoots();
    }

    public boolean isModified() {
        return isUrlsListsModified(myManager.getRailsViewUserFolderUrls(),
                                   getMyRailsViewUserFolders());
    }

    protected void loadRoots() {
        loadRailsViewUserRoots();
    }

    private void loadRailsViewUserRoots() {
        final Set<String> testRootUrls = myManager.getRailsViewUserFolderUrls();
        final VirtualFileManager vFManager = VirtualFileManager.getInstance();
        for (String url : testRootUrls) {
            final VirtualFile file = vFManager.findFileByUrl(url);
            if (file != null) {
                getMyRailsViewUserFolders().add(file);
            }
        }
    }

    private void saveExistentRailsViewUserRoots() {
        final LinkedList<String> testUrls = new LinkedList<String>();
        for (VirtualFile folder : getMyRailsViewUserFolders()) {
            testUrls.add(folder.getUrl());
        }
        myManager.setRailsViewUserFolderUrls(testUrls);
    }

    public boolean isRailsViewAdditionalSource(final VirtualFile file) {
        return getRailsViewAdditionalSourceFolder(file) != null;
    }

    public VirtualFile getRailsViewAdditionalSourceFolder(VirtualFile file) {
        return getMyRailsViewUserFolders().contains(file) ? file : null;
    }

    public VirtualFile[] getRailsViewUserSourceFolders() {
        return getMyRailsViewUserFolders().toArray(new VirtualFile[getMyRailsViewUserFolders().size()]);
    }


    public void addRailsViewAdditionalSourceFolder(final VirtualFile folder) {
        getMyRailsViewUserFolders().add(folder);
        myEventDispatcher.getMulticaster().folderAdded(folder);
        update();
    }

    public void removeRailsViewAdditionalSourceFolder(final VirtualFile folder) {
        getMyRailsViewUserFolders().remove(folder);
        myEventDispatcher.getMulticaster().folderRemoved(folder);
        update();
    }

    public void removeRailsViewAdditionalFolder(final VirtualFile folder) {
        getMyRailsViewUserFolders().remove(folder);
        myEventDispatcher.getMulticaster().folderRemoved(folder);
        update();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void deleteContentFolder(ContentEntry contentEntry, ContentFolder folder) {
        if (folder instanceof RailsViewContentRootPanel.RailsViewUserFolder) {
            removeRailsViewAdditionalFolder(folder.getFile());
        }
    }

    @SuppressWarnings({"deprecation"})
    private Set<VirtualFile> getMyRailsViewUserFolders() {
        if (myRailsViewUserSourceFolders == null) {
            myRailsViewUserSourceFolders = new HashSet<VirtualFile>();
        }
        return myRailsViewUserSourceFolders;
    }

    protected RailsViewContentRootPanel createContentRootPanel(final ContentEntry contentEntry, final RContentEntryEditor rContentEntryEditor) {
        return new RailsViewContentRootPanel(contentEntry, this);
    }
}
