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

package org.jetbrains.plugins.ruby.support.ui.entriesEditor;

import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ContentFolder;
import com.intellij.openapi.roots.ExcludeFolder;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.EventDispatcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Eugene Zhuravlev, Roman Chernyatchik
 * @date: Aug 20, 2007
 */
public abstract class RContentEntryEditor {

    private final ContentEntry myContentEntry;
    private final ModifiableRootModel myRootModel;

    private boolean myIsSelected;

    private RAbstractContentRootPanel myContentRootPanel;
    private JPanel myMainPanel;

    protected final EventDispatcher<RContentEntryEditorListener> myEventDispatcher;

    public abstract void apply();    
    public abstract boolean isModified();
    public abstract void deleteContentFolder(final ContentEntry contentEntry,
                                    final ContentFolder folder);
    protected abstract void loadRoots();

    public RContentEntryEditor(final ContentEntry contentEntry,
                               final ModifiableRootModel rootModel) {
        myContentEntry = contentEntry;
        myRootModel = rootModel;
        myEventDispatcher = EventDispatcher.create(RContentEntryEditorListener.class);
    }

    /**
     * Must be called in implementations
     */
    protected void init() {
        loadRoots();
        apply();

        myMainPanel = new JPanel(new BorderLayout());
        myMainPanel.setOpaque(false);
        myMainPanel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                myEventDispatcher.getMulticaster().editingStarted();
            }

            public void mouseEntered(MouseEvent e) {
                if (!myIsSelected) {
                    highlight(true);
                }
            }

            public void mouseExited(MouseEvent e) {
                if (!myIsSelected) {
                    highlight(false);
                }
            }
        });
        setSelected(false);
        update();
    }

    @SuppressWarnings({"UnusedReturnValue"})
    public ExcludeFolder addExcludeFolder(VirtualFile file) {
        try {
            boolean isExplodedDirectory = isExplodedDirectory(file);
            if (isExplodedDirectory) {
                myRootModel.setExcludeExplodedDirectory(true);
                return null;
            } else {
                return myContentEntry.addExcludeFolder(file);
            }
        }
        finally {
            myEventDispatcher.getMulticaster().folderExcluded(file);
            update();
        }
    }

    public void addContentEntryEditorListener(RContentEntryEditorListener listener) {
        myEventDispatcher.addListener(listener);
    }

    public JComponent getComponent() {
        return myMainPanel;
    }

    public ContentEntry getContentEntry() {
        return myContentEntry;
    }

    public ExcludeFolder getExcludeFolder(VirtualFile file) {
        if (myContentEntry == null) {
            return null;
        }
        final ExcludeFolder[] excludeFolders = myContentEntry.getExcludeFolders();
        for (final ExcludeFolder excludeFolder : excludeFolders) {
            final VirtualFile f = excludeFolder.getFile();
            if (f == null) {
                continue;
            }
            if (f.equals(file)) {
                return excludeFolder;
            }
        }
        return null;
    }


    public boolean isExcluded(final VirtualFile file) {
        return getExcludeFolder(file) != null;
    }

    public boolean isUnderExcludedDirectory(final VirtualFile file) {
        if (myContentEntry == null) {
            return false;
        }
        final ExcludeFolder[] excludeFolders = myContentEntry.getExcludeFolders();
        for (ExcludeFolder excludeFolder : excludeFolders) {
            final VirtualFile excludedDir = excludeFolder.getFile();
            if (excludedDir == null) {
                continue;
            }
            if (VfsUtil.isAncestor(excludedDir, file, true)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isUrlsListsModified(final Set<String> oldUrls,
                                               final Set<VirtualFile> newFiles) {
        if (oldUrls.size() != newFiles.size()) {
            return true;
        }

        for (VirtualFile file : newFiles) {
            if (!oldUrls.contains(file.getUrl())) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public void navigateFolder(final ContentEntry contentEntry,
                               final ContentFolder contentFolder) {
        final VirtualFile file = contentFolder.getFile();
        if (file != null) { // file can be deleted externally
            myEventDispatcher.getMulticaster().navigationRequested(this, file);
        }
    }

    public void removeExcludeFolder(ExcludeFolder excludeFolder) {
        final VirtualFile file = excludeFolder.getFile();
        try {
            if (isExplodedDirectory(file)) {
                myRootModel.setExcludeExplodedDirectory(false);
            }
            if (!excludeFolder.isSynthetic()) {
                myContentEntry.removeExcludeFolder(excludeFolder);
            }
        }
        finally {
            myEventDispatcher.getMulticaster().folderIncluded(file);
            update();
        }
    }

    public void removeContentEntryEditorListener(RContentEntryEditorListener listener) {
        myEventDispatcher.removeListener(listener);
    }

    public void setSelected(boolean isSelected) {
        if (myIsSelected != isSelected) {
            highlight(isSelected);
            myIsSelected = isSelected;
        }
    }

    protected abstract RAbstractContentRootPanel createContentRootPanel(final ContentEntry contentEntry, final RContentEntryEditor rContentEntryEditor);

    public void update() {
        if (myContentRootPanel != null) {
            myMainPanel.remove(myContentRootPanel);
        }
        myContentRootPanel = createContentRootPanel(myContentEntry, this);
        myContentRootPanel.setSelected(myIsSelected);
        myMainPanel.add(myContentRootPanel, BorderLayout.CENTER);
        myMainPanel.revalidate();
    }

    private void highlight(boolean selected) {
        if (myContentRootPanel != null) {
            myContentRootPanel.setSelected(selected);
        }
    }

    private boolean isExplodedDirectory(VirtualFile file) {
        final VirtualFile explodedDir = myRootModel.getExplodedDirectory();
        if (explodedDir != null) {
            if (explodedDir.equals(file)) {
                return true;
            }
        }
        return false;
    }
}