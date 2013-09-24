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

package org.jetbrains.plugins.ruby.rails.module.view;

import com.intellij.ProjectTopics;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.StatusBarProgress;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.ui.treeStructure.LazyTreeBuilder;
import com.intellij.ui.treeStructure.PatchedDefaultMutableTreeNode;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsProjectRootNode;
import org.jetbrains.plugins.ruby.rails.nameConventions.ViewsConventions;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCacheListener;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import java.awt.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * @author: Roman Chernyatchik
 * @date: 27.09.2006
 */
public class RailsProjectTreeBuilder extends LazyTreeBuilder {
    private final Project myProject;
    private final ProjectFileIndex myFileIndex;

    public RailsProjectTreeBuilder(final JTree tree,
                                  final Project project) {
        super(tree,
              (DefaultTreeModel)tree.getModel(),
              new RailsProjectTreeStructure(new RailsProjectRootNode(project)),
              null);
        myProject = project;
        myFileIndex =  ProjectRootManager.getInstance(myProject).getFileIndex();
        setNodeDescriptorComparator(new RailsProjectNodeComparator());

        // Listener for files in RubyFileCache
        project.getMessageBus().connect(this).subscribe(ProjectTopics.MODULES, new RubyModuleListenerAdapter() {
            public void moduleAdded(final Project project, final Module module) {
                processModuleAdded(module);
            }
        });

        // Add listeners to for already opened modules
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            processModuleAdded(module);
        }

        // Listener for not RubyFilesCache files
        VirtualFileManager.getInstance().addVirtualFileListener(new MyVirtualFileListener(), this);

        initRootNode();
    }

    private void processModuleAdded(Module module) {
        if (!RailsFacetUtil.hasRailsSupport(module)) {
            return;
        }
        final MyRubyFilesCacheListener listener = new MyRubyFilesCacheListener(module);
        RubyModuleCachesManager.getInstance(module).getFilesCache().addCacheChangedListener(listener, module);
    }

    public boolean isAlwaysShowPlus(NodeDescriptor nodeDescriptor) {
      return ((SimpleNode) nodeDescriptor).isAlwaysShowPlus();
    }

    public boolean isAutoExpandNode(NodeDescriptor nodeDescriptor) {
      return ((SimpleNode) nodeDescriptor).isAutoExpandNode();
    }

    public void performUpdate() {
        if (EventQueue.isDispatchThread()) {
            myUpdater.performUpdate();
        }
    }

    public void runAfterUpdate(final Runnable runnable) {
        myUpdater.runAfterUpdate(runnable);
    }

    public final void updateFromRoot() {
      updateFromRoot(false);
    }

    public void updateFromRoot(boolean rebuild) {
      if (rebuild) {
        cleanUpStructureCaches();
      }

      if (EventQueue.isDispatchThread()) {
        RailsProjectTreeBuilder.super.updateFromRoot();
      } else {
        ApplicationManager.getApplication().invokeLater(new Runnable() {
          public void run() {
            RailsProjectTreeBuilder.super.updateFromRoot();
          }
        });
      }
    }

    public void updateNode(DefaultMutableTreeNode node) {
        super.updateNode(node);
    }

    protected final void addUpdateFromRoot() {
        addSubtreeToUpdate(myRootNode);
    }

    protected final DefaultMutableTreeNode createChildNode(final NodeDescriptor childDescr) {
      return new RailsPatchedDefaultMutableTreeNode(childDescr);
    }

    @NotNull
    protected ProgressIndicator createProgressIndicator() {
        return new StatusBarProgress();
    }

    protected final Project getProject() {
        return myProject;
    }

    protected Object getTreeStructureElement(NodeDescriptor nodeDescriptor) {
        return nodeDescriptor;
    }

    protected boolean addSubtreeToUpdate(@Nullable final VirtualFile file,
                                         @NotNull final String fileUrl,
                                         @NotNull final Module module) {
        NodeId[] infos = NodeIdUtil.getRailsTreeNodeParentID(file, fileUrl, module);
        boolean hasDatatoUpdate = false;
        if (infos == null) {
            return false;
        } else {
            for (NodeId info : infos) {
                hasDatatoUpdate = myUpdater.addSubtreeToUpdateByElement(info);
            }
        }
        
        return hasDatatoUpdate;
    }

    private void cleanUpStructureCaches() {
      if (!(myTreeStructure instanceof SimpleTreeStructure)) {
          return;
      }
      ((SimpleTreeStructure)myTreeStructure).clearCaches();
    }

    private class MyRubyFilesCacheListener implements RubyFilesCacheListener {
        private final Module myModule;
        private VirtualFileManager myVirtualFileManager;


        public MyRubyFilesCacheListener(final Module module) {
            myModule = module;
            myVirtualFileManager = VirtualFileManager.getInstance();
        }

        public void fileAdded(@NotNull final String url) {
            processChanges(url);
        }

        public void fileRemoved(@NotNull final String url) {
            processChanges(url);
        }

        public void fileUpdated(@NotNull final String url) {
            processChanges(url);
        }

        private void processChanges(@NotNull final String url) {
            final VirtualFile file = myVirtualFileManager.findFileByUrl(url);
            if (addSubtreeToUpdate(file, url, myModule)) {
                performUpdate();
            }
        }
    }

    private class RailsPatchedDefaultMutableTreeNode
            extends PatchedDefaultMutableTreeNode {

        public RailsPatchedDefaultMutableTreeNode(final NodeDescriptor childDescr) {
            super(childDescr);
        }

        @SuppressWarnings({"unchecked"})
        public void insert(final MutableTreeNode newChild, final int childIndex) {
            if (!allowsChildren || newChild == null || isNodeAncestor(newChild)) {
                super.insert(newChild, childIndex);
                return;
            }

            MutableTreeNode oldParent = (MutableTreeNode)newChild.getParent();

            if (oldParent != null) {
                oldParent.remove(newChild);
            }
            newChild.setParent(this);
            if (children == null) {
                children = new Vector();
            }
            if (children.size() < childIndex) {
                children.setSize(childIndex + 10);
            }
            children.insertElementAt(newChild, childIndex);
        }
    }

    private class MyVirtualFileListener extends VirtualFileAdapter {
        public void contentsChanged(final VirtualFileEvent event) {
            fileChanged(event.getFile().getName(), event.getParent());
            performUpdate();
        }

        public void fileCreated(final VirtualFileEvent event) {
            contentsChanged(event);
        }
        public void fileDeleted(final VirtualFileEvent event) {
            contentsChanged(event);
        }

        public void fileMoved(final VirtualFileMoveEvent event) {
            final String fileName = event.getFile().getName();
            fileChanged(fileName, event.getOldParent());
            fileChanged(fileName, event.getNewParent());
            performUpdate();
        }

        public void propertyChanged(final VirtualFilePropertyEvent event) {
            if (!VirtualFile.PROP_NAME.equals(event.getPropertyName())) {
                return;
            }
            final VirtualFile parent = event.getParent();
            final String oldName = (String)event.getOldValue();
            final String newName = (String)event.getNewValue();
            fileChanged(oldName, parent);
            fileChanged(newName, parent);
            performUpdate();
        }

        private void fileChanged(@NotNull final String fileName,
                                 final VirtualFile parentDir) {
            if (parentDir == null) {
                return;
            }

            //TODO if we delete some module content root? how to update tree?
            final Module module = RailsProjectTreeBuilder.this.myFileIndex.getModuleForFile(parentDir);
            if (module == null) {
                return;
            }

            // If file is RubyFileCache file, then tree will by updated by RubyFilesCache
            if (RubyVirtualFileScanner.isRubyFile(fileName)
                || !isValidView(fileName)) {
                return;
            }
            final String fileUrl = VirtualFileUtil.constructUrl(parentDir, fileName);
            final VirtualFile file = parentDir.findChild(fileName);
            addSubtreeToUpdate(file, fileUrl, module);
        }

        @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
        private boolean isValidView(final String fileName) {
            return TextUtil.isEmpty(VirtualFileUtil.getExtension(fileName))
                   || ViewsConventions.isValidViewFileName(fileName);
        }
    }
}