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

package org.jetbrains.plugins.ruby.rails.module.view.nodes;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.treeStructure.SimpleNodeVisitor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitor;
import org.jetbrains.plugins.ruby.rails.module.view.RailsNodeVisitorAdapter;
import org.jetbrains.plugins.ruby.rails.module.view.RailsProjectNodeComparator;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: Roman Chernyatchik
 * @date: 08.10.2006
 */
public class ClassNode extends RailsNode {
    private RVirtualClass myRubyClass;
    private final String parentDirFileUrl;

    private RFileInfo myFileInfo;

    public ClassNode(final Module module, final RVirtualClass rVClass,
                     final RFileInfo fileInfo) {
        super(module);

        myRubyClass = rVClass;
        myFileInfo = fileInfo;
        parentDirFileUrl = fileInfo.getFileDirectoryUrl();

        final ItemPresentation presentation = rVClass.getPresentation();
        init(generateNodeId(rVClass), presentation);
        assert getVirtualFileUrl().equals(fileInfo.getUrl());
    }

    @NotNull
    public static NodeId generateNodeId(final RVirtualClass rVClass) {
        return NodeIdUtil.createForVirtualContainer(rVClass);
    }

     public void accept(final SimpleNodeVisitor visitor) {
        if (visitor instanceof RailsNodeVisitor) {
            ((RailsNodeVisitor)visitor).visitClassNode();
            return;
        }
        super.accept(visitor);

    }

    @Nullable
    public VirtualFile getVirtualFile() {
        return myRubyClass.getVirtualFile();
    }

    public RailsNode[] getChildren() {
        final ArrayList<RailsNode> children = new ArrayList<RailsNode>();
        final Module module = getModule();

        List<RVirtualStructuralElement> methods
                = RContainerUtil.selectVirtualElementsByType(myRubyClass.getVirtualStructureElements(), StructureType.METHOD);
        for (RVirtualElement element : methods) {
            assert element instanceof RVirtualMethod;
            final RVirtualMethod method = (RVirtualMethod) element;
            accept(new RailsNodeVisitorAdapter() {
                public void visitControllerNode() {
                    final String controllerName
                            = ControllersConventions.getControllerNameByClassName(myRubyClass);
                    children.add(new ActionNode(module, method, parentDirFileUrl,
                                                getVirtualFileUrl(),
                                                controllerName));
                }

                public void visitClassNode() {
                    children.add(new MethodNode(module, method, getVirtualFileUrl()));
                }
            });
        }
        final List<RVirtualClass> allClasses = RContainerUtil.getTopLevelClasses(myRubyClass);
        for (final RVirtualClass rClass : allClasses) {
            children.add(new ClassNode(module, rClass, myFileInfo));
        }
        return children.toArray(new RailsNode[children.size()]);
    }

    @NotNull
    public RailsProjectNodeComparator.NodeType getType() {
        return RailsProjectNodeComparator.NodeType.CLASS;
    }

    public RVirtualClass getRubyClass() {
        return myRubyClass;
    }

    protected String getParentDirUrl() {
        return parentDirFileUrl;
    }
}
