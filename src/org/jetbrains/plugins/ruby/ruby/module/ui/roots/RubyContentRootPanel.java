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
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.support.ui.entriesEditor.RAbstractContentRootPanel;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, Eugene Zhuravlev
 * @date: Aug 21, 2007
 */

public class RubyContentRootPanel extends RAbstractContentRootPanel {
    private static final Color TESTS_COLOR = new Color(0x008C2E);
    private static final Color EXCLUDED_COLOR = new Color(0x992E00);

    protected void addEntryEditorComponents() {
        super.addEntryEditorComponents();

        final java.util.List<ContentFolder> testSources = new ArrayList<ContentFolder>();
        final java.util.List<ContentFolder> excluded = new ArrayList<ContentFolder>();

        final VirtualFile[] testSourceFolders = ((RubyContentEntryEditor)myCallBack).getTestSourceFolders();
        Arrays.sort(testSourceFolders, new VirtualFileUtil.VirtualFilesComparator());
        for (VirtualFile folder : testSourceFolders) {
            testSources.add(new RTestSourceFolder(folder, myContentEntry));
        }

        final ExcludeFolder[] excludeFolders = myContentEntry.getExcludeFolders();
        for (final ExcludeFolder excludeFolder : excludeFolders) {
            if (!excludeFolder.isSynthetic()) {
                excluded.add(excludeFolder);
            }
        }

        if (testSources.size() > 0) {
            final JComponent testSourcesComponent = createFolderGroupComponent(RBundle.message("module.paths.test.sources.group"), testSources.toArray(new ContentFolder[testSources.size()]), TESTS_COLOR);
            this.add(testSourcesComponent, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        }

        if (excluded.size() > 0) {
            final JComponent excludedComponent = createFolderGroupComponent(RBundle.message("module.paths.excluded.group"), excluded.toArray(new ContentFolder[excluded.size()]), EXCLUDED_COLOR);
            this.add(excludedComponent, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
        }
    }

    public RubyContentRootPanel(final ContentEntry contentEntry, final RubyContentEntryEditor editorCallBack) {
        super(contentEntry, editorCallBack);

    }
    public static class RTestSourceFolder extends RSourceFolder {

        public RTestSourceFolder(final VirtualFile folder,
                                 final ContentEntry contentEntry) {
            super(folder, contentEntry);
        }
    }
}