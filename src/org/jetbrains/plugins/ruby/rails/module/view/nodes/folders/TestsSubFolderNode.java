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

package org.jetbrains.plugins.ruby.rails.module.view.nodes.folders;

import java.util.List;

import javax.swing.Icon;

import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.SimpleFileNode;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.10.2006
 */
public class TestsSubFolderNode extends FolderNode {
    protected static final Icon TESTS_OPENED =null/* IconSet.getSourceFolderIcon(true, true)*/;
    protected static final Icon TESTS_CLOSED = null/*IconSet.getSourceFolderIcon(true, false)/*/;

    public TestsSubFolderNode(final Module module, final VirtualFile dir,
                              final SimpleNode parent) {
        super(module, dir, parent, initPresentationData(dir.getName()));
    }

    public TestsSubFolderNode(final Module module, final VirtualFile dir,
                              final SimpleNode parent, final PresentationData data) {
        super(module, dir, parent, data);
    }

    @Override
	protected void processNotDirectoryFile(final List<RailsNode> nodes,
                                           final VirtualFile file, final String url) {
        if (RubyVirtualFileScanner.isRubyFile(file)) {
            super.processNotDirectoryFile(nodes, file, url);
        } else if (RailsUtil.isYMLFile(file.getName())) {
            nodes.add(new SimpleFileNode(getModule(), file));
        }
    }

    private static PresentationData initPresentationData(final String name) {
        return new PresentationData(name, name,
                                    TESTS_OPENED,
                                    TESTS_CLOSED,
                                    null);
    }
}
