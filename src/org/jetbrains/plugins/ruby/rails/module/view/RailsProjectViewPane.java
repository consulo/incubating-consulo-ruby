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
import com.intellij.history.LocalHistory;
import com.intellij.history.LocalHistoryAction;
import com.intellij.ide.*;
import com.intellij.ide.projectView.HelpID;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.ide.projectView.impl.AbstractProjectViewPane;
import com.intellij.ide.projectView.impl.ModuleGroup;
import com.intellij.ide.projectView.impl.ProjectViewTree;
import com.intellij.ide.util.DeleteHandler;
import com.intellij.ide.util.EditorHelper;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.actionSystem.IdeActions;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.*;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.util.EditSourceOnDoubleClickHandler;
import com.intellij.util.OpenSourceUtil;
import com.intellij.util.ui.UIUtil;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.RComponents;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeId;
import org.jetbrains.plugins.ruby.rails.module.view.id.NodeIdUtil;
import org.jetbrains.plugins.ruby.rails.module.view.nodes.RailsNode;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleListenerAdapter;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import javax.swing.*;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author: Roman Chernyatchik
 * @date: 14.09.2006
 */
public class RailsProjectViewPane extends AbstractProjectViewPane implements Disposable {
    private JScrollPane myComponent;
    @NonNls
    public static final String ID = "RailsProjectView";
    @NonNls
    public static final String VIEW = RComponents.RAILS_PROJECT_VIEW_PANE;

    private boolean myInitialized;
    private boolean myIsShown;
    private CopyPasteManagerEx.CopyPasteDelegator myCopyPasteDelegator;
    private IdeView myIdeView;
    private RailsProjectViewPane.RailsDeletePSIElementProvider myDeletePSIElementProvider;
    private final VirtualFileManager myVFManager;

    public RailsProjectViewPane(final Project project) {
        super(project);
        project.getMessageBus().connect(this).subscribe(ProjectTopics.MODULES, new RubyModuleListenerAdapter() {
            public void moduleRemoved(Project project, Module module) {
                modulesChanged();
                updateFromRoot(true);
            }
            public void moduleAdded(Project project, Module module) {
                modulesChanged();
                updateFromRoot(true);
            }
        });
        myVFManager = VirtualFileManager.getInstance();
    }

    public SelectInTarget createSelectInTarget() {
        return new RailsProjectSelectInTarget(myProject);
    }

    private void modulesChanged() {
        if (!isInitialized()) {
            return;
        }

        boolean shouldShow = RailsUtil.getAllModulesWithRailsSupport(myProject).length > 0;
        if (shouldShow && !myIsShown) {
            addMe();
        }
        if (!shouldShow && myIsShown) {
            removeMe();
        }
    }

     public String getTitle() {
        return RBundle.message("rails.project.module.view.presentable");
    }

    public Icon getIcon() {
        return RailsIcons.RAILS_PROJECT_VIEW;
    }

    @NotNull
    public String getId() {
        return ID;
    }

    public JComponent createComponent() {
        initTree();
        return myComponent;
    }

    private void initTree() {
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(null);
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        myTree = new ProjectViewTree(treeModel) {
            public String toString() {
                return getTitle() + " " + super.toString();
            }

            public DefaultMutableTreeNode getSelectedNode() {
                return RailsProjectViewPane.this.getSelectedNode();
            }
        };

        myTreeBuilder = new RailsProjectTreeBuilder(myTree, myProject);

        myTree.setRootVisible(false);
        myTree.setShowsRootHandles(true);
        UIUtil.setLineStyleAngled(myTree);
        myTree.expandPath(new TreePath(myTree.getModel().getRoot()));
        TreeUtil.expandRootChildIfOnlyOne(myTree);

        myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
        EditSourceOnDoubleClickHandler.install(myTree);
        TreeUtil.installActions(myTree);

        addTreeListeners();

        new TreeSpeedSearch(myTree);

        myTreeStructure = myTreeBuilder.getTreeStructure();

        myComponent = new JScrollPane(myTree);
        myComponent.setBorder(BorderFactory.createEmptyBorder());
        myCopyPasteDelegator = new RailsCopyPastDelegator(myProject, myComponent);
        myDeletePSIElementProvider = new RailsDeletePSIElementProvider();
        myIdeView = new RailsIdeView();
//        installTreePopupHandler(RAILS_PROJECT_VIEW_POPUP,
//                                GROUP_RAILS_PROJECT_VIEW_POPUP);
        installTreePopupHandler(ActionPlaces.PROJECT_VIEW_POPUP,
                                IdeActions.GROUP_PROJECT_VIEW_POPUP);
    }

    public RailsProjectTreeBuilder getTreeBuilder() {
        return ((RailsProjectTreeBuilder)myTreeBuilder);
    }


    private void addTreeListeners() {
        myTree.getSelectionModel().addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                fireTreeChangeListener();
            }
        });
        myTree.getModel().addTreeModelListener(new TreeModelListener() {
            public void treeNodesChanged(TreeModelEvent e) {
                fireTreeChangeListener();
            }

            public void treeNodesInserted(TreeModelEvent e) {
                fireTreeChangeListener();
            }

            public void treeNodesRemoved(TreeModelEvent e) {
                fireTreeChangeListener();
            }

            public void treeStructureChanged(TreeModelEvent e) {
                fireTreeChangeListener();
            }
        });
        myTree.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    DataContext dataContext = DataManager.getInstance().getDataContext(myTree);
                    OpenSourceUtil.openSourcesFrom(dataContext, false);
                } else if (KeyEvent.VK_ESCAPE == e.getKeyCode()) {
                    if (e.isConsumed()) return;
                    CopyPasteManagerEx copyPasteManager = (CopyPasteManagerEx)CopyPasteManager.getInstance();
                    boolean[] isCopied = new boolean[1];
                    if (copyPasteManager.getElements(isCopied) != null && !isCopied[0]) {
                        copyPasteManager.clear();
                        e.consume();
                    }
                }
            }
        });
    }

    public void updateFromRoot(boolean restoreExpandedPaths) {
        if (myTree == null) {
            return;
        }
        ((RailsProjectTreeBuilder)myTreeBuilder).queueUpdate();
    }

    public void selectModule(Module module, boolean requestFocus) {
        selectAfterRebuild(module, null);
    }

    public void selectModuleGroup(ModuleGroup moduleGroup, boolean requestFocus) {
        selectAfterRebuild(moduleGroup, null);
    }

    public void select(Object element, VirtualFile file, boolean requestFocus) {
        // selects objects by auto sroll from source
        selectAfterRebuild(element, null);
    }

    public boolean canSelect(PsiFile file) {
        return !getPathToSelect(file).isEmpty();
    }

    private boolean buildNodeIdsPath(@NotNull final VirtualFile file,
                                     @NotNull final Module module,
                                     @NotNull final VirtualFile moduleRoot,
                                     @NotNull final LinkedList<Object> path,
                                     @Nullable final Object element) {
        if (moduleRoot.equals(file)) {
            return true;
        }
        final NodeId[] parentId =
                NodeIdUtil.getRailsTreeNodeParentID(file, file.getUrl(), module, element);

        if (parentId == null) {
            return false;
        }
        for (NodeId nodeId : parentId) {
            final String parentUrl = nodeId.getFileUrl();
            final VirtualFile parentFile = myVFManager.findFileByUrl(parentUrl);
            if (!VirtualFileUtil.isValid(parentFile)) {
                continue;
            }
            path.addFirst(nodeId);

            final RVirtualContainer container = nodeId.getRContainer();

            //noinspection ConstantConditions
            if (buildNodeIdsPath(parentFile, module,  moduleRoot, path, container)) {
                return true;
            }
            // this node id didn't lead to successful result
            assert path.getFirst() == nodeId;
            path.removeFirst();
        }
        return false;
    }

    @NotNull
    private List<Object> getPathToSelect(@NotNull final PsiElement element) {
        final VirtualFile file;
        if (element instanceof PsiFileSystemItem) {
            file = ((PsiFileSystemItem)element).getVirtualFile();
        } else {
            file = element.getContainingFile().getVirtualFile();
        }
        final Module module = ModuleUtil.findModuleForPsiElement(element);

        if (module == null || !VirtualFileUtil.isValid(file)
            || !RailsFacetUtil.hasRailsSupport(module)) {
            return Collections.emptyList();
        }
        final VirtualFile railsApplicationHome = RailsFacetUtil.getRailsAppHomeDir(module);
        if (railsApplicationHome == null) {
            return Collections.emptyList();
        }

        final LinkedList<Object> path = new LinkedList<Object>();
        if (file != null) {
            final NodeId[] info = NodeIdUtil.getRailsTreeNodeID(file, file.getUrl(), module, element);
            if (info != null && info.length > 0) {
                path.addFirst(info[0]);
            }
        }
        //noinspection ConstantConditions
        buildNodeIdsPath(file, module,  railsApplicationHome, path, element);
        return path;
    }



    private void selectAfterRebuild(final Object element, Object parent) {
        final RailsProjectTreeBuilder treeBuilder = (RailsProjectTreeBuilder)myTreeBuilder;

        if (parent != null) {
            // refresh child list
            final DefaultMutableTreeNode node = treeBuilder.getNodeForElement(parent);
            if (node == null) {
                return;
            }
            treeBuilder.updateNode(node);
        } else if (element instanceof PsiElement) {
            List<Object> path = getPathToSelect((PsiElement)element);
            if (!path.isEmpty()) {
                expand(path.toArray(new Object[path.size()]), true);
                return;
            }
        }
        treeBuilder.runAfterUpdate(new Runnable() {
            public void run() {
                DefaultMutableTreeNode node = myTreeBuilder.getNodeForElement(element);
                if (node == null) {
                    myTreeBuilder.buildNodeForElement(element);
                    node = myTreeBuilder.getNodeForElement(element);
                }
                if (node != null) {
                    final TreePath path = new TreePath(node.getPath());
                    TreeUtil.selectPath(myTree, path);
                }
            }
        });
        myTreeBuilder.buildNodeForElement(element);
    }

    public void projectOpened() {
        StartupManager.getInstance(myProject).registerPostStartupActivity(new Runnable() {
            public void run() {
                synchronized (RailsProjectViewPane.this) {
                    myInitialized = true;
                    modulesChanged();
                }
            }
        });
    }

    private void addMe() {
        final ProjectView projectView = ProjectView.getInstance(myProject);
        projectView.addProjectPane(this);
        myIsShown = true;
    }

    private void removeMe() {
        final ProjectView projectView = ProjectView.getInstance(myProject);
        projectView.removeProjectPane(this);
        myIsShown = false;
    }

    public void projectClosed() {
        //Do nothing
    }

    /**
     * Rails View Implementation.
     * @return Array of PsiElements
     */
    @NotNull
    public final PsiElement[] getSelectedPsiElements() {
      final List<PsiElement> psiElements = new ArrayList<PsiElement>();
      for (Object element : getSelectedElements()) {
          if (!(element instanceof NodeId)) {
              continue;
          }
          final VirtualFile file = getFileByElement(element);
          final PsiElement psiElement =
                  org.jetbrains.plugins.ruby.rails.RailsUtil.getPsiElementByNodeId((NodeId)element, file, myProject);
          if (psiElement != null) {
              psiElements.add(psiElement);
          }
      }
      return psiElements.toArray(new PsiElement[psiElements.size()]);
    }

    /**
     * Rails View Implementation.
     * @return Array of PsiFiles
     */
    @NotNull
    public final PsiFile[] getSelectedPsiFiles() {
        final List<PsiFile> psiElements = new ArrayList<PsiFile>();
        for (Object element : getSelectedElements()) {
            if (!(element instanceof NodeId)) {
                continue;
            }

            final VirtualFile file = getFileByElement(element);
            if (VirtualFileUtil.isValid(file)) {
                final PsiFile psiFile =
                        PsiManager.getInstance(myProject).findFile(file);
                if (psiFile != null) {
                    psiElements.add(psiFile);
                }
            }
        }
        return psiElements.toArray(new PsiFile[psiElements.size()]);
    }

    private VirtualFile getFileByElement(@NotNull final Object element) {
        if (!(element instanceof NodeId)) {
            return null;
        }
        final String fileUrl = ((NodeId)element).getFileUrl();
        return myVFManager.findFileByUrl(fileUrl);
    }

    /**
     * Rails View Implementation.
     * @return Array of VirtualFiles
     */
    @NotNull
    public final VirtualFile[] getSelectedVirtualFiles() {
      final List<VirtualFile> files = new ArrayList<VirtualFile>();
      for (Object element : getSelectedElements()) {
          if (!(element instanceof NodeId)) {
              continue;
          }
          final VirtualFile file = getFileByElement(element);
          if (VirtualFileUtil.isValid(file)) {
              files.add(file);
          }
      }
      return files.toArray(new VirtualFile[files.size()]);
    }

    @NotNull
    public String getComponentName() {
        return RComponents.RAILS_PROJECT_VIEW_PANE;
    }

    public void initComponent() {
    }

    public void disposeComponent() {
        Disposer.dispose(this);
    }

    public int getWeight() {
        return 5;
    }

    @Nullable
    public final RailsNode getSelectedRailsNode() {
        final NodeDescriptor nodeDescriptor = getSelectedDescriptor();
        return nodeDescriptor instanceof RailsNode
                ? (RailsNode)nodeDescriptor
                : null;
    }


    public Object getData(String dataId) {
        if (DataKeys.PROJECT.getName().equals(dataId)) {
            return myProject;
        }
        if (DataKeys.MODULE.getName().equals(dataId)) {
            final RailsNode nodeDescriptor = getSelectedRailsNode();
            if (nodeDescriptor != null) {
                return (nodeDescriptor).getModule();
            }
            return null;
        }


        if (DataKeys.PSI_ELEMENT.getName().equals(dataId)) {
            final RailsNode nodeDescriptor = getSelectedRailsNode();
            if (nodeDescriptor != null) {
                return org.jetbrains.plugins.ruby.rails.RailsUtil.getPsiElementByRailsNode(nodeDescriptor,
                                                            myProject);
            }
            return null;
        }

        if (DataKeys.PSI_FILE.getName().equals(dataId)) {
            final RailsNode nodeDescriptor = getSelectedRailsNode();
            if (nodeDescriptor != null) {
                return org.jetbrains.plugins.ruby.rails.RailsUtil.getPsiFileByRailsNode(nodeDescriptor,
                                                         myProject);
            }
            return null;
        }

        if (DataKeys.VIRTUAL_FILE.getName().equals(dataId)) {
            final RailsNode nodeDescriptor = getSelectedRailsNode();
            if (nodeDescriptor != null) {
                final VirtualFile file = nodeDescriptor.getVirtualFile();
                if (VirtualFileUtil.isValid(file)) {
                    return file;                    
                }
            }
            return null;
        }

        if (DataKeys.VIRTUAL_FILE_ARRAY.getName().equals(dataId)) {
            return getSelectedVirtualFiles();
        }

        if (DataKeys.MODULE_CONTEXT_ARRAY.getName().equals(dataId) ||
                   DataKeys.MODULE_CONTEXT.getName().equals(dataId) ||
                   DataKeys.PROJECT_CONTEXT.getName().equals(dataId) ||
                   DataKeys.EDITOR.getName().equals(dataId) ||
                   DataKeys.FILE_EDITOR.getName().equals(dataId)
//                   DataKeys.USAGES.getName().equals(dataId)
//                   DataKeys.USAGES_TARGET.getName().equals(dataId)
                   ) {
            return null;
        }

        if (DataKeys.IDE_VIEW.getName().equals(dataId)) {
            return myIdeView;
        }

        if (DataKeys.CUT_PROVIDER.getName().equals(dataId)) {
            return myCopyPasteDelegator.getCutProvider();
        }

        if (DataKeys.COPY_PROVIDER.getName().equals(dataId)) {
            return myCopyPasteDelegator.getCopyProvider();
        }

        if (DataKeys.PASTE_PROVIDER.getName().equals(dataId)) {
            return myCopyPasteDelegator.getPasteProvider();
        }

        if (VIEW.equals(dataId)) {
            return this;
        }

        if (DataKeys.NAVIGATABLE.getName().equals(dataId)) {
            final RailsNode nodeDescriptor = getSelectedRailsNode();
            if (nodeDescriptor != null) {
                return RailsUtil.getPsiElementByRailsNode(nodeDescriptor,
                                                            myProject);
            }
            return null;
        }

        if (DataKeys.DELETE_ELEMENT_PROVIDER.getName().equals(dataId)) {
            return myDeletePSIElementProvider;
        }

        if (DataKeys.PSI_ELEMENT_ARRAY.getName().equals(dataId)) {
            return getSelectedPsiElements();
        }

        if (DataKeys.NAVIGATABLE_ARRAY.getName().equals(dataId)) {
            final PsiElement[] elements = getSelectedPsiElements();
            final List<NavigationItem> list = new LinkedList<NavigationItem>();
            for (PsiElement element : elements) {
                if (element instanceof NavigationItem) {
                    list.add((NavigationItem)element);
                }
            }            
            return list.toArray(new NavigationItem[list.size()]);
        }

        if (DataKeys.HELP_ID.getName().equals(dataId)) {
            return HelpID.PROJECT_VIEWS;
        }

        return null;
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    private synchronized boolean isInitialized() {
        return myInitialized;
    }

    public void select(PsiElement element) {
        selectAfterRebuild(element, null);
    }

    public void facetAdded() {
        modulesChanged();
    }

    public void facetRemoved() {
        modulesChanged();
    }

    private final class RailsIdeView implements IdeView {
        private final PsiDirectory[] EMPTY = new PsiDirectory[0];
        private NodeDescriptor lastDescriptor;
        private PsiDirectory lastPsiDir;

        public void selectElement(final PsiElement element) {
            if (element != null) {
                final boolean isDirectory = element instanceof PsiDirectory;
                if (!isDirectory) {
                    Editor editor = EditorHelper.openInEditor(element);
                    if (editor != null) {
                        ToolWindowManager.getInstance(myProject)
                                .activateEditorComponent();
                    }
                }
            }
        }
        @Nullable
        private PsiDirectory getDirectory() {
            final NodeDescriptor nodeDescriptor = getSelectedDescriptor();
            if (lastDescriptor != nodeDescriptor) {
                lastDescriptor = nodeDescriptor;
                if (nodeDescriptor instanceof RailsNode) {
                    lastPsiDir = org.jetbrains.plugins.ruby.rails.RailsUtil.getPsiDirByRailsNode((RailsNode)nodeDescriptor, myProject);
                } else {
                    lastPsiDir = null;
                }
            }
            return lastPsiDir;
        }

        public PsiDirectory[] getDirectories() {
            final PsiDirectory dir = getDirectory();
            if (dir == null) {
                return EMPTY;
            }
            return new PsiDirectory[]{dir};
        }

        public PsiDirectory getOrChooseDirectory() {
            return getDirectory();
        }
    }

    private final class RailsDeletePSIElementProvider implements DeleteProvider {
        public boolean canDeleteElement(DataContext dataContext) {
            final Object elem =
                    dataContext.getData(DataKeys.PSI_ELEMENT.getName());
            return elem != null
                   && DeleteHandler.shouldEnableDeleteAction(new PsiElement[]{(PsiElement)elem});

        }

        public void deleteElement(DataContext dataContext) {
            final Object elem =
                    dataContext.getData(DataKeys.PSI_ELEMENT.getName());
            final PsiElement psiElem = (PsiElement)elem;
            if (psiElem == null || !psiElem.isValid()) {
                return;
            }

            final LocalHistoryAction a = LocalHistory.startAction(myProject, RBundle.message("progress.deleting"));
            try {
                DeleteHandler.deletePsiElement(new PsiElement[]{psiElem}, myProject);
            }
            finally {
                a.finish();
            }
        }
    }

    private final class RailsCopyPastDelegator extends CopyPasteManagerEx.CopyPasteDelegator {
        private PsiElement[] EMPTY = new PsiElement[0];

        public RailsCopyPastDelegator(Project project, JComponent keyReceiver) {
            super(project, keyReceiver);
        }

        @NotNull
        protected PsiElement[] getSelectedElements() {
            final NodeDescriptor nodeDescriptor = getSelectedDescriptor();
            if (nodeDescriptor == null || !(nodeDescriptor instanceof RailsNode)) {
                return EMPTY;
            }
            final PsiElement el =
                    org.jetbrains.plugins.ruby.rails.RailsUtil.getPsiElementByRailsNode((RailsNode)nodeDescriptor,
                                                        myProject);
            if (el != null) {
                return new PsiElement[]{el};
            }
            return EMPTY;
        }
    }

    public static RailsProjectViewPane getInstance(@NotNull final Project project) {
        return project.getComponent(RailsProjectViewPane.class);
    }
}
