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

package org.jetbrains.plugins.ruby.support.ui.entriesEditor;


import com.intellij.ide.util.projectWizard.ToolbarPanel;
import com.intellij.ide.util.treeView.AbstractTreeBuilder;
import com.intellij.ide.util.treeView.AbstractTreeStructure;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.actions.NewFolderAction;
import com.intellij.openapi.fileChooser.ex.FileSystemTreeImpl;
import com.intellij.openapi.fileChooser.impl.FileTreeBuilder;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.SourceFolder;
import com.intellij.openapi.roots.ui.configuration.actions.IconWithTextAction;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.ui.TreeToolTipHandler;
import com.intellij.util.ui.Tree;
import com.intellij.util.ui.tree.TreeUtil;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.RubyContentEntryTreeCellRenderer;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Eugene Zhuravlev, Roman Chernyatchik
 * @date: Aug 20, 2007
 */
public abstract class RContentEntryTreeEditor {
  private final Project myProject;
  protected Tree myTree;
  private FileSystemTreeImpl myFileSystemTree;
  private JPanel myTreePanel;
  private final DefaultMutableTreeNode EMPTY_TREE_ROOT = new DefaultMutableTreeNode(RBundle.message("module.paths.empty.node"));
  protected DefaultActionGroup myEditingActionsGroup;
  private RContentEntryEditor myContentEntryEditor;
  private final MyContentEntryEditorListener myContentEntryEditorListener = new MyContentEntryEditorListener();
  private final FileChooserDescriptor myDescriptor;

  public RContentEntryTreeEditor(final Module module) {
    myProject = module.getProject();
    myTree = new Tree();
    myTree.setRootVisible(true);
    myTree.setShowsRootHandles(true);

    myEditingActionsGroup = new DefaultActionGroup();

    TreeToolTipHandler.install(myTree);
    TreeUtil.installActions(myTree);
    new TreeSpeedSearch(myTree);

    myTreePanel = new JPanel(new BorderLayout());
    final JScrollPane scrollPane = ScrollPaneFactory.createScrollPane(myTree);
    myTreePanel.add(new ToolbarPanel(scrollPane, myEditingActionsGroup), BorderLayout.CENTER);

    myTreePanel.setVisible(false);
    myDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
    myDescriptor.setShowFileSystemRoots(false);
  }

  protected abstract void createEditingActions();

  protected TreeCellRenderer getContentEntryCellRenderer() {
    return new RubyContentEntryTreeCellRenderer(this);
  }

  /**
   * @param contentEntryEditor : null means to clear the editor
   */
  public void setContentEntryEditor(final RContentEntryEditor contentEntryEditor) {
    if (myContentEntryEditor != null && myContentEntryEditor.equals(contentEntryEditor)) {
      return;
    }
    if (myFileSystemTree != null) {
      Disposer.dispose(myFileSystemTree);
      myFileSystemTree = null;
    }
    if (myContentEntryEditor != null) {
      myContentEntryEditor.removeContentEntryEditorListener(myContentEntryEditorListener);
      myContentEntryEditor = null;
    }
    if (contentEntryEditor == null) {
      ((DefaultTreeModel)myTree.getModel()).setRoot(EMPTY_TREE_ROOT);
      myTreePanel.setVisible(false);
      if (myFileSystemTree != null) {
        Disposer.dispose(myFileSystemTree);
      }
      return;
    }
    myTreePanel.setVisible(true);
    myContentEntryEditor = contentEntryEditor;
    myContentEntryEditor.addContentEntryEditorListener(myContentEntryEditorListener);
    final VirtualFile file = contentEntryEditor.getContentEntry().getFile();
    myDescriptor.setRoot(file);
    if (file != null) {
      myDescriptor.setTitle(file.getPresentableUrl());
    }
    else {
      final String url = contentEntryEditor.getContentEntry().getUrl();
      myDescriptor.setTitle(VirtualFileManager.extractPath(url).replace('/', File.separatorChar));
    }


    final Runnable init = new Runnable() {
      public void run() {
        myFileSystemTree.updateTree();
        if (file != null) {
          select(file);
        }
      }
    };


    myFileSystemTree = new FileSystemTreeImpl(myProject, myDescriptor, myTree, getContentEntryCellRenderer(), init) {
      protected AbstractTreeBuilder createTreeBuilder(JTree tree, DefaultTreeModel treeModel, AbstractTreeStructure treeStructure,
                                                      Comparator<NodeDescriptor> comparator, FileChooserDescriptor descriptor,
                                                      final Runnable onInitialized) {
        return new MyFileTreeBuilder(tree, treeModel, treeStructure, comparator, descriptor, onInitialized);
      }
    };


    Disposer.register(myProject, myFileSystemTree);


    final NewFolderAction newFolderAction = new MyNewFolderAction(myFileSystemTree);
    DefaultActionGroup mousePopupGroup = new DefaultActionGroup();
    mousePopupGroup.add(myEditingActionsGroup);
    mousePopupGroup.addSeparator();
    mousePopupGroup.add(newFolderAction);
    myFileSystemTree.registerMouseListener(mousePopupGroup);

  }

  public RContentEntryEditor getContentEntryEditor() {
    return myContentEntryEditor;
  }

  public JComponent createComponent() {
    createEditingActions();
    return myTreePanel;
  }

  public void select(VirtualFile file) {
    if (myFileSystemTree != null) {
      myFileSystemTree.select(file);
    }
  }

  public void requestFocus() {
    myTree.requestFocus();
  }

  public void update() {
    if (myFileSystemTree != null) {
      myFileSystemTree.updateTree();
      final DefaultTreeModel model = (DefaultTreeModel)myTree.getModel();
      final int visibleRowCount = myTree.getVisibleRowCount();
      for (int row = 0; row < visibleRowCount; row++) {
        final TreePath pathForRow = myTree.getPathForRow(row);
        if (pathForRow != null) {
          final TreeNode node = (TreeNode)pathForRow.getLastPathComponent();
          if (node != null) {
            model.nodeChanged(node);
          }
        }
      }
    }
  }

  private class MyContentEntryEditorListener extends RContentEntryEditorListenerAdapter {
    public void folderAdded(VirtualFile folder) {
      update();
    }

    public void folderRemoved(VirtualFile file) {
      update();
    }

    public void folderExcluded(VirtualFile file) {
      update();
    }

    public void folderIncluded(VirtualFile file) {
      update();
    }

    public void packagePrefixSet(SourceFolder folder) {
      update();
    }
  }

  private static class MyNewFolderAction extends NewFolderAction implements CustomComponentAction {
    public MyNewFolderAction(final FileSystemTreeImpl fileSystemTree) {
      super(fileSystemTree);
    }

    public JComponent createCustomComponent(final Presentation presentation) {
      return IconWithTextAction.createCustomComponentImpl(this, presentation);
    }
  }

  private static class MyFileTreeBuilder extends FileTreeBuilder {
    public MyFileTreeBuilder(final JTree tree, final DefaultTreeModel treeModel,
                             final AbstractTreeStructure treeStructure,
                             final Comparator<NodeDescriptor> comparator,
                             final FileChooserDescriptor descriptor,
                             final @Nullable Runnable onInitialized) {
      super(tree, treeModel, treeStructure, comparator, descriptor, onInitialized);
    }

    protected boolean isAlwaysShowPlus(NodeDescriptor nodeDescriptor) {
      return false; // need this in order to not show plus for empty directories
    }
  }
}
