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

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 29.09.2006
 */
public class RailsTestsFolderNode extends TestsSubFolderNode {
    private static final String TESTS_VIEW_NAME = RBundle.message("rails.project.module.view.nodes.tests.presentable");
    protected static final Icon TEST_ROOT_OPENED = null /*IconSet.getSourceRootIcon(true, true)*/;
    protected static final Icon TEST_ROOT_CLOSED =  null/*IconSet.getSourceRootIcon(true, false)*/;

    public RailsTestsFolderNode(final Module module, final VirtualFile rootDir) {
        super(module, rootDir, null, initPresentationData());
    }

    public void accept(final SimpleNodeVisitor visitor) {
        if (visitor instanceof RailsNodeVisitor) {
            ((RailsNodeVisitor)visitor).visitTestNode();
            return;
        }
        super.accept(visitor);

    }

    @NotNull
    @Override
    public RailsProjectNodeComparator.NodeType getType() {
        return RailsProjectNodeComparator.NodeType.SPECIAL_FOLDER;
    }

    private static PresentationData initPresentationData() {
        return new PresentationData(TESTS_VIEW_NAME, null,
                                    TEST_ROOT_OPENED,
                                    TEST_ROOT_CLOSED,
                                    null);
    }
}

