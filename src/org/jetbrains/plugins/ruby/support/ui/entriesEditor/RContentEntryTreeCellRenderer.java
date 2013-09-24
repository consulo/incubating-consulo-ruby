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

import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.fileChooser.FileElement;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ExcludeFolder;
import com.intellij.openapi.roots.ui.configuration.IconSet;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 20, 2007
 */

public abstract class RContentEntryTreeCellRenderer extends NodeRenderer {
    protected final RContentEntryTreeEditor myTreeEditor;

    public RContentEntryTreeCellRenderer(final RContentEntryTreeEditor treeEditor) {
        myTreeEditor = treeEditor;
    }

    public void customizeCellRenderer(final JTree tree, final Object value,
                                      final boolean selected, final boolean expanded,
                                      final boolean leaf, final int row, final boolean hasFocus) {

        super.customizeCellRenderer(tree, value, selected, expanded, leaf, row, hasFocus);

        final RContentEntryEditor contentEntryEditor = myTreeEditor.getContentEntryEditor();
        if (contentEntryEditor == null) {
            return;
        }

        final DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
        if (!(node.getUserObject() instanceof NodeDescriptor)) {
            return;
        }

        final NodeDescriptor descriptor = (NodeDescriptor)node.getUserObject();
        final Object element = descriptor.getElement();
        if (element instanceof FileElement) {
            final VirtualFile file = ((FileElement)element).getFile();
            if (file != null && file.isDirectory()) {
                final ContentEntry contentEntry = contentEntryEditor.getContentEntry();
                setIcon(updateIcon(contentEntry, file, getIcon(), expanded));
            }
        }
    }

    protected Icon updateIcon(final ContentEntry entry, final VirtualFile file,
                             final Icon originalIcon, final boolean expanded) {
        //exclude folder
        final ExcludeFolder[] excludeFolders = entry.getExcludeFolders();
        for (ExcludeFolder excludeFolder : excludeFolders) {
            final VirtualFile f = excludeFolder.getFile();
            if (f == null) {
                continue;
            }
            // exclude all subfolders
            if (VfsUtil.isAncestor(f, file, false)) {
                return IconSet.getExcludeIcon(expanded);
            }
        }
        return originalIcon;
    }
}