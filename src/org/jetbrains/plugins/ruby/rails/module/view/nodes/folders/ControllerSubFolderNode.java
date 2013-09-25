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
import org.jetbrains.plugins.ruby.rails.module.view.nodes.ClassNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.ControllerClassNode;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.10.2006
 */
public class ControllerSubFolderNode extends FolderNode {
    public ControllerSubFolderNode(final Module module, final VirtualFile dir,
                                   final SimpleNode parent) {
        super(module, dir, parent);
    }


    public ControllerSubFolderNode(final Module module, final VirtualFile dir,
                                   final SimpleNode parent, final PresentationData data) {
        super(module, dir, parent, data);
    }


    @Override
	protected void processNotDirectoryFile(final List<RailsNode> nodes, final VirtualFile file,
                                           final String url) {
        if (ControllersConventions.isApplicationControllerFile(file, getModule())) {
            return;
        }
        super.processNotDirectoryFile(nodes, file, url);
    }

    @Override
	protected ClassNode createClassNode(final RVirtualClass rClass,
                                       final RFileInfo rFileInfo) {
        final Module module = getModule();

        if (ControllersConventions.isControllerClass(rClass, module)) {
            return new ControllerClassNode(module, rClass, rFileInfo);
        }
        return new ClassNode(module, rClass, rFileInfo);
    }
}
