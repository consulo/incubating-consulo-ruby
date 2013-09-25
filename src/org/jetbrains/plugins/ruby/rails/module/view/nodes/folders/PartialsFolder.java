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

import com.intellij.ide.projectView.PresentationData;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNode;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.SimpleFileNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.05.2007
 */
public class PartialsFolder extends FolderNode {
    private static final String PARTIALS_FOLDER = RBundle.message("rails.project.module.view.nodes.partials.presentable");

    public PartialsFolder(final Module module, final VirtualFile folder, final SimpleNode parent) {
        super(module, folder, parent, initPresentationData());
    }

    @Override
	public SimpleNode[] getChildren() {
        final List<SimpleFileNode> children = new ArrayList<SimpleFileNode>();

        final Module module = getModule();
        final List<VirtualFile> partialTempates = ViewsConventions.findPartialViews(getVirtualFile());
        for (VirtualFile tempate : partialTempates) {
            children.add(new SimpleFileNode(module, tempate));
        }

        return children.toArray(new SimpleNode[children.size()]);
    }

    private static PresentationData initPresentationData() {
         return new PresentationData(PARTIALS_FOLDER, PARTIALS_FOLDER,
                                     RailsIcons.RAILS_PARTIALS_OPEN, RailsIcons.RAILS_PARTIALS_CLOSED,
                                     null);
    }
}
