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
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RAbstractContentRootPanel;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import org.jetbrains.plugins.ruby.RBundle;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 22, 2008
 */
public class RailsViewContentRootPanel extends RAbstractContentRootPanel {

    protected RailsViewContentRootPanel(final ContentEntry contentEntry, final RailsViewContentEntryEditor editorCallBack) {
        super(contentEntry, editorCallBack);
    }

    protected void addEntryEditorComponents() {
        super.addEntryEditorComponents();

        final java.util.List<ContentFolder> railsViewUserSourceFolders = new ArrayList<ContentFolder>();

               final VirtualFile[] railsViewUserFolders = ((RailsViewContentEntryEditor)myCallBack).getRailsViewUserSourceFolders();
               Arrays.sort(railsViewUserFolders, new VirtualFileUtil.VirtualFilesComparator());
               for (VirtualFile folder : railsViewUserFolders) {
                   railsViewUserSourceFolders.add(new RailsViewUserFolder(folder, myContentEntry));
               }

               if (railsViewUserSourceFolders.size() > 0) {
                   final JComponent railsViewAdditionalComponent = createFolderGroupComponent(RBundle.message("module.paths.rails.view.additional.group"), railsViewUserSourceFolders.toArray(new ContentFolder[railsViewUserSourceFolders.size()]), new Color(0x999900));
                   this.add(railsViewAdditionalComponent, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
               }
    }

    public static class RailsViewUserFolder extends RSourceFolder {
        public RailsViewUserFolder(final VirtualFile folder,
                                   final ContentEntry contentEntry) {
            super(folder, contentEntry);
        }
    }
}
