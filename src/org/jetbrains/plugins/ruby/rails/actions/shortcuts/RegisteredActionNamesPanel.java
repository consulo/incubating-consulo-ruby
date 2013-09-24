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

package org.jetbrains.plugins.ruby.rails.actions.shortcuts;

import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.keymap.KeyMapBundle;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.KeymapManager;
import com.intellij.openapi.keymap.KeymapUtil;
import com.intellij.openapi.keymap.impl.ui.EditKeymapsDialog;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;
import org.jetbrains.plugins.ruby.rails.actions.generators.SerializableGenerator;
import org.jetbrains.plugins.ruby.rails.actions.rake.RakeUtil;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTask;
import org.jetbrains.plugins.ruby.rails.actions.rake.task.RakeTaskSerializableImpl;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 23.03.2007
 */
public class RegisteredActionNamesPanel {
    private JButton addNameButton;
    private JButton removeButton;
    private JButton openKeymapButton;
    private JPanel myContentPanel;
    private JTree myNamesTree;
    private JButton addGroupButton;
    private JList myShortcutsList;
    private JLabel myShortcutsLabel;
    private JScrollPane myShortcutsScrollPane;
    private MySortableTreeNode myGeneratorsNode;
    private MySortableTreeNode myRakeTasksNode;

    private ShortcutsTreeState myTreeState;
    private final Object LOCK = new Object();
    private final MySortableTreeNode myRootNode;

    public RegisteredActionNamesPanel() {
        myRootNode = new MySortableTreeNode("ROOT");
        myNamesTree.setModel(new DefaultTreeModel(myRootNode));
        myNamesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        myNamesTree.setRootVisible(false);
        myNamesTree.setCellRenderer(new NamesTreeRenderer());

        loadRegisteredData();

        myNamesTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(final TreeSelectionEvent e) {
                updateOnSelectionChanged();
            }
        });

        addNameButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNode(false);
            }
        });

        addGroupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addNode(true);
            }
        });

        removeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeSelectedNode();
            }
        });

        openKeymapButton.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                openKeyMap();
            }
        });

        //noinspection UnresolvedPropertyKey
        myShortcutsLabel.setText(KeyMapBundle.message("shortcuts.keymap.label"));
        myShortcutsList.setModel(new DefaultListModel());
        myShortcutsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myShortcutsList.setCellRenderer(new ShortcutListRenderer());
        myShortcutsScrollPane.setPreferredSize(new Dimension(160, 200));

        updateOnSelectionChanged();
        myNamesTree.updateUI();
    }

    public JPanel getContentPanel() {
        return myContentPanel;
    }

    private void loadRegisteredData() {
        final RubyShortcutsSettings settings = RubyShortcutsSettings.getInstance();

        //Init generators;
        final GeneratorNodeInfo genInfo = GeneratorNodeInfo.createRootNode();
        myGeneratorsNode = new MySortableTreeNode(genInfo);
        myRootNode.add(myGeneratorsNode);
        myNamesTree.scrollPathToVisible(new TreePath(myGeneratorsNode));
        if (settings.serializableGenerators != null) {
            final List<SerializableGenerator> children = settings.serializableGenerators.getChildren();
            for (SerializableGenerator child : children) {
                loadGenerator(child, myGeneratorsNode);
            }
            if (myGeneratorsNode.getChildCount() > 0) {
                final TreeNode[] path = ((MySortableTreeNode)myGeneratorsNode.getFirstChild()).getPath();
                myNamesTree.scrollPathToVisible(new TreePath(path));
            }
        }
        settings.serializableGenerators = genInfo.getData();

        //Init take task
        final RakeTaskNodeInfo rakeTaskInfo = RakeTaskNodeInfo.createRootNode();
        myRakeTasksNode = new MySortableTreeNode(rakeTaskInfo);
        myRootNode.add(myRakeTasksNode);
        if (settings.serializableRakeTask != null) {
            final List<? extends RakeTask> children = settings.serializableRakeTask.getSubTasks();
            for (RakeTask child : children) {
                loadRakeTask((RakeTaskSerializableImpl)child, myRakeTasksNode);
            }
            if (myRakeTasksNode.getChildCount() > 0) {
                final TreeNode[] path = ((MySortableTreeNode)myRakeTasksNode.getFirstChild()).getPath();
                myNamesTree.scrollPathToVisible(new TreePath(path));
            }
        }
        settings.serializableRakeTask = rakeTaskInfo.getData();
    }

    private void loadGenerator(final SerializableGenerator generator,
                               final MySortableTreeNode parent) {
        final MySortableTreeNode node =
                addNode(generator.getName(), generator.isGroup(), parent,
                        ShortcutsTreeState.GENERATORS_SUBTREE, true);
        if (!generator.isGroup() || node == null) {
            return;
        }
        final List<SerializableGenerator> children = generator.getChildren();
        for (SerializableGenerator child : children) {
            loadGenerator(child,  node);
        }
    }

    private void loadRakeTask(final RakeTaskSerializableImpl task,
                              final MySortableTreeNode parent) {
        final MySortableTreeNode node =
                addNode(task.getId(), task.isGroup(), parent,
                        ShortcutsTreeState.RAKE_SUBTREE, true);
        if (!task.isGroup() || node == null) {
            return;
        }
        final List<? extends RakeTask> children = task.getSubTasks();
        for (RakeTask child : children) {
            loadRakeTask((RakeTaskSerializableImpl)child,  node);
        }
    }

    private void addNode(final boolean isGroup) {
        final MySortableTreeNode selectedNode = getSelectedNode();
        if (selectedNode == null) {
            return;
        }
        final String msg = RBundle.message("dialog.register.shortcut.input.dialog.label");
        final String title = isGroup ? RBundle.message("dialog.register.shortcut.input.dialog.group.title")
                                       : RBundle.message("dialog.register.shortcut.input.dialog.title");
        final String cmdName = Messages.showInputDialog(myContentPanel, msg, title, null);
        if (TextUtil.isEmpty(cmdName)) {
            return;
        }
        final MySortableTreeNode childNode = addNode(cmdName, isGroup, selectedNode, getTreeState(), false);
        if (childNode != null) {
            myNamesTree.scrollPathToVisible(new TreePath(childNode.getPath()));
            myNamesTree.updateUI();
        }
    }

    @Nullable
    private MySortableTreeNode addNode(final String cmdName, final boolean isGroup,
                                       final MySortableTreeNode selectedNode,
                                       final ShortcutsTreeState treeState,
                                       final boolean isRegistered) {
        if (selectedNode == null) {
            return null;
        }

        //If group is selected, then add element to this group
        MySortableTreeNode containerNode = (MySortableTreeNode)selectedNode.getParent();
        if (((NodeInfo)selectedNode.getUserObject()).isGroup()) {
            containerNode = selectedNode;
        }

        String cmd = cmdName;
        NodeInfo info = null;
        switch (treeState) {
            case GENERATORS_SUBTREE:
                final SerializableGenerator parentGen = ((GeneratorNodeInfo)containerNode.getUserObject()).getData();

                //Exit, if such node exists and has same type(both are groups or not)
                if (GeneratorsUtil.findGenerator(cmdName, isGroup, parentGen) != null) {
                    return null;
                }
                info = GeneratorNodeInfo.createGeneratorNode(cmdName, isGroup, parentGen);
                break;
            case RAKE_SUBTREE:
                final RakeTaskSerializableImpl parentTask = ((RakeTaskNodeInfo)containerNode.getUserObject()).getData();

                //Exit, if such node exists and has same type(both are groups or not)
                if (RakeUtil.findSubTaskById(cmdName, isGroup, parentTask) != null) {
                    return null;
                }
                final RakeTaskNodeInfo rInfo = RakeTaskNodeInfo.createTaskNode(cmdName, parentTask, isGroup);
                cmd = rInfo.getData().getFullCommand();
                info = rInfo;
                break;
        }

        if (info != null) {
            //Register action
            if (!isGroup && !isRegistered) {
                new ShortcutAction(info.toString(), cmd,
                                   info.getOpenIcon(), treeState).registerInKeyMap(info.getActionId());
            }

            //Add to tree
            final MySortableTreeNode childNode = new MySortableTreeNode(info);
            containerNode.add(childNode);
            return childNode;
        }
        return null;
    }

    private ShortcutsTreeState getTreeState() {
        synchronized(LOCK) {
            return myTreeState;
        }
    }

    private MySortableTreeNode getSelectedNode() {
        return (MySortableTreeNode)myNamesTree.getLastSelectedPathComponent();
    }

    private String getSelectedActionId() {
        final MySortableTreeNode selectedNode = getSelectedNode();
        if (getTreeState() == ShortcutsTreeState.UNKNOWN || selectedNode == null) {
            return null;
        }
        return ((NodeInfo)selectedNode.getUserObject()).getActionId();
    }

    private void openKeyMap() {
        new EditKeymapsDialog(null, getSelectedActionId()).show();
        updateShortcutsList();
    }

    private void removeSelectedNode() {
        final MySortableTreeNode selectedNode = getSelectedNode();
        if (selectedNode == null) {
            return;
        }

        //Unregister action
        unregisterRecursively(selectedNode);

        final ShortcutsTreeState state = getTreeState();
        final Object data = ((NodeInfo)selectedNode.getUserObject()).getData();
        switch (state) {
            case GENERATORS_SUBTREE:
                ((SerializableGenerator)data).removeFromParent();
                break;
            case RAKE_SUBTREE:
                ((RakeTaskSerializableImpl)data).removeFromParent();
                break;
        }

        //Change selection
        final MySortableTreeNode parent = (MySortableTreeNode)selectedNode.getParent();
        final MySortableTreeNode prev = (MySortableTreeNode)parent.getChildBefore(selectedNode);
        if (prev != null) {
            myNamesTree.setSelectionPath(new TreePath(prev.getPath()));
        } else {
            final MySortableTreeNode next = (MySortableTreeNode)parent.getChildAfter(selectedNode);
            if (next != null) {
                myNamesTree.setSelectionPath(new TreePath(next.getPath()));
            } else {
                myNamesTree.clearSelection();
            }
        }

        //Remove from tree
        selectedNode.removeFromParent();

        myNamesTree.updateUI();
        updateOnSelectionChanged();
    }

    private void setTreeState(final ShortcutsTreeState myTreeState) {
        synchronized(LOCK) {
            this.myTreeState = myTreeState;
        }
    }

    private void unregisterRecursively(final MySortableTreeNode node) {
        final NodeInfo nodeInfo = (NodeInfo)node.getUserObject();
        if (!nodeInfo.isGroup()) {
            ShortcutAction.unregisterInKeyMap(nodeInfo.getActionId());
            return;
        }
        final int childrenCount = node.getChildCount();
        for (int i = 0; i < childrenCount; i++) {
            unregisterRecursively((MySortableTreeNode)node.getChildAt(i));
        }
    }

    private void updateShortcutsList() {
        final DefaultListModel shortcutsModel = (DefaultListModel)myShortcutsList.getModel();
        shortcutsModel.clear();
        final String actionId = getSelectedActionId();
        final Keymap keymap = KeymapManager.getInstance().getActiveKeymap();
        if (actionId != null && keymap != null) {
            final Shortcut[] shortcuts = keymap.getShortcuts(actionId);
            for (Shortcut shortcut : shortcuts) {
                shortcutsModel.addElement(shortcut);
            }
            if (shortcutsModel.size() > 0) {
                myShortcutsList.setSelectedIndex(0);
            }
        }
    }

    private void updateOnSelectionChanged() {
        final MySortableTreeNode selectedNode = getSelectedNode();
        ShortcutsTreeState treeState = ShortcutsTreeState.UNKNOWN;
        if (selectedNode != null) {
            TreeNode subtreeRoot = selectedNode;
            while (subtreeRoot.getParent() != myRootNode) {
                subtreeRoot = subtreeRoot.getParent();
            }
            if (subtreeRoot.equals(myGeneratorsNode)) {
                treeState = ShortcutsTreeState.GENERATORS_SUBTREE;
            } else if (subtreeRoot.equals(myRakeTasksNode)) {
                treeState = ShortcutsTreeState.RAKE_SUBTREE;
            }
        }
        final boolean isEnabled = treeState != ShortcutsTreeState.UNKNOWN;
        addNameButton.setEnabled(isEnabled);
        addGroupButton.setEnabled(isEnabled);
        removeButton.setEnabled(isEnabled && selectedNode.getParent() != myRootNode);
        setTreeState(treeState);
        updateShortcutsList();        
    }

    private class NamesTreeRenderer extends DefaultTreeCellRenderer {
        public Component getTreeCellRendererComponent(final JTree tree, final Object value,
                                                      boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
            final MySortableTreeNode node = (MySortableTreeNode)value;
            final Object obj = node.getUserObject();
            if (obj instanceof NodeInfo) {
                NodeInfo nodeInfo = (NodeInfo)obj;
                if (nodeInfo.isGroup() && !expanded) {
                    setIcon(nodeInfo.getClosedIcon());
                } else {
                    setIcon(nodeInfo.getOpenIcon());
                }
            }
            return this;
        }
    }

    private static final class ShortcutListRenderer extends DefaultListCellRenderer {
      public Component getListCellRendererComponent(final JList list, final Object value,
                                                    final int index, final boolean isSelected,
                                                    final boolean cellHasFocus) {
        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        Shortcut shortcut = (Shortcut)value;
        setText(KeymapUtil.getShortcutText(shortcut));
        setIcon(KeymapUtil.getShortcutIcon(shortcut));
        return this;
      }
    }

    private static class MySortableTreeNode extends DefaultMutableTreeNode {
        public MySortableTreeNode(Object userObject) {
            super(userObject);
        }

        /**
         * Supports alphabetical order of nodes and ignores dublicates.
         * For comparison uses <code>childNode.getUserObject().toString()</code>
         * @param newChild new child node
         */
        public void add(@NotNull final MutableTreeNode newChild) {
            final String newObjStr = ((MySortableTreeNode)newChild).getUserObject().toString();

            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                final MySortableTreeNode childNode = (MySortableTreeNode)getChildAt(i);
                if (newObjStr.compareTo(childNode.getUserObject().toString()) < 0) {
                    super.insert(newChild, i);
                    return;
                }
            }
            super.add(newChild);
        }
    }
}
