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
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.LayeredIcon;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.railsView.RailsViewContentEntryEditor;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RContentEntryTreeCellRenderer;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 25, 2007
 */
public class RailsContentEntryTreeCellRenderer extends RContentEntryTreeCellRenderer {

    public RailsContentEntryTreeCellRenderer(final RailsContentEntryTreeEditor treeEditor) {
        super(treeEditor);
    }

    protected Icon updateIcon(ContentEntry entry, VirtualFile file, Icon originalIcon, boolean expanded) {
        final Icon firstIcon = super.updateIcon(entry, file, originalIcon, expanded);

         //test folders
        final VirtualFile[] railsViewUserFolders =
                ((RailsViewContentEntryEditor)myTreeEditor.getContentEntryEditor()).getRailsViewUserSourceFolders();

        Icon icon = null;
        for (VirtualFile userFolder : railsViewUserFolders) {
            if (userFolder == null) {
                continue;
            }
            // exclude all subfolders
            if (VfsUtil.isAncestor(userFolder, file, false)) {
                icon = RailsIcons.RAILS_SMALL;
                break;
            }
        }


        if (icon == null) {
            return firstIcon;
        }
        final LayeredIcon itemIcon = new LayeredIcon(2);
        itemIcon.setIcon(firstIcon, 0);
        itemIcon.setIcon(icon, 1, icon.getIconWidth() + 3, 0);
        return itemIcon;
    }
}
