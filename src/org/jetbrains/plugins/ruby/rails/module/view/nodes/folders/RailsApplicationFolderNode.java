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
import com.intellij.ui.treeStructure.SimpleNodeVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: Roman Chernyatchik
 * @date: 12.10.2006
 */
public class RailsApplicationFolderNode extends ControllerSubFolderNode {
    private static final String APPLICATION_NAME = RBundle.message("rails.project.module.view.nodes.appication.presentable");

    public RailsApplicationFolderNode(final Module module, final VirtualFile appControllerRoot) {
        super(module, appControllerRoot, null, initPresentationData());
        init(generateNodeId(getVirtualFileUrl()), getPresentation());
    }

    @NotNull
    public static NodeId generateNodeId(final String appControllersRootUrl) {
        return NodeIdUtil.createForDirectory(appControllersRootUrl, true);
    }
    
    @Override
	public void accept(final SimpleNodeVisitor visitor) {
        if (visitor instanceof RailsNodeVisitor) {
            ((RailsNodeVisitor)visitor).visitControllerNode();
            return;
        }
        super.accept(visitor);
    }

    @Override
	public SimpleNode[] getChildren() {
        List<RailsNode> children = new ArrayList<RailsNode>();
        final Module module = getModule();

        final VirtualFile appControllerFile = ControllersConventions.getApplicationControllerFile(module);
        if (appControllerFile != null) {
            final RubyModuleCachesManager manager = RubyModuleCachesManager.getInstance(module);
            final RubyFilesCache cache = manager.getFilesCache();
            final RFileInfo appContrInfo = cache.getUp2DateFileInfo(appControllerFile);
            if (appContrInfo != null) {
                final List<RVirtualClass> allClasses =
                        RContainerUtil.getTopLevelClasses(appContrInfo.getRVirtualFile());
                for (RVirtualClass rClass : allClasses) {
                    children.add(createClassNode(rClass, appContrInfo));
                }
            }
        }
        return children.toArray(new RailsNode[children.size()]);
    }

    private static PresentationData initPresentationData() {
         return new PresentationData(APPLICATION_NAME, APPLICATION_NAME,
                                     RailsIcons.RAILS_APPlICON_NODES, RailsIcons.RAILS_APPlICON_NODES,
                                     null);
    }

    @Override
	@NotNull
    public RailsProjectNodeComparator.NodeType getType() {
        return RailsProjectNodeComparator.NodeType.SPECIAL_FOLDER;
    }
}
