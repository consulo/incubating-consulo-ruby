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
import com.intellij.openapi.roots.ui.configuration.IconSet;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.LayeredIcon;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryTreeCellRenderer;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryTreeEditor;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
public class RubyContentEntryTreeCellRenderer extends RContentEntryTreeCellRenderer {
    public RubyContentEntryTreeCellRenderer(final RContentEntryTreeEditor treeEditor) {
        super(treeEditor);
    }

    protected Icon updateIcon(final ContentEntry entry, final VirtualFile file,
                              final Icon originalIcon, final boolean expanded) {

        final Icon firstIcon = super.updateIcon(entry, file, originalIcon, expanded);

        //test folders
        final VirtualFile[] testFolders =
                ((RubyContentEntryEditor)myTreeEditor.getContentEntryEditor()).getTestSourceFolders();

        final Icon testIcon = getTestFolderIcon(file, expanded, testFolders);

        if (testIcon == null) {
            return firstIcon;
        }

        if (firstIcon == originalIcon) {
            return testIcon;
        }

        final LayeredIcon itemIcon = new LayeredIcon(2);
        itemIcon.setIcon(firstIcon, 0);
        itemIcon.setIcon(testIcon, 1, testIcon.getIconWidth() + 3, 0);
        return itemIcon;
    }

    private Icon getTestFolderIcon(final VirtualFile file, boolean expanded,
                                   final VirtualFile[] testFolders) {
        Icon originalIcon = null;

        for (VirtualFile f : testFolders) {
            if (f.equals(file)) {
                //mark test root
                return IconSet.getSourceRootIcon(true, expanded);
            }
        }

        VirtualFile currentRoot = null;
        for (VirtualFile testFolder : testFolders) {
            if (VfsUtil.isAncestor(testFolder, file, true)) {
                if (currentRoot != null && VfsUtil.isAncestor(testFolder, currentRoot, false)) {
                    continue;
                }
                originalIcon = IconSet.getSourceFolderIcon(true, expanded);
                currentRoot = testFolder;
            }
        }

        return originalIcon;
    }
}
