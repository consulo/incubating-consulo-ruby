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

package org.jetbrains.plugins.ruby.ruby.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.RCacheUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.projectview.RClassNode;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScope;
import com.intellij.ide.projectView.impl.AbstractProjectTreeStructure;
import com.intellij.ide.projectView.impl.ProjectAbstractTreeStructureBase;
import com.intellij.ide.projectView.impl.ProjectTreeBuilder;
import com.intellij.ide.projectView.impl.nodes.PsiFileNode;
import com.intellij.ide.util.gotoByName.ChooseByNameModel;
import com.intellij.ide.util.gotoByName.ChooseByNamePanel;
import com.intellij.ide.util.gotoByName.ChooseByNamePopupComponent;
import com.intellij.ide.util.gotoByName.GotoClassModel2;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeRenderer;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.IdeFocusTraversalPolicy;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.util.PsiUtil;
import com.intellij.ui.TabbedPaneWrapper;
import com.intellij.ui.TreeSpeedSearch;
import com.intellij.util.ui.Tree;
import com.intellij.util.ui.UIUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class TreeRClassChooserDialog extends DialogWrapper {
    private final Project myProject;
    private final SearchScope mySearchScope;
    private Tree myTree;

    private RClass myInitialClass;
    private ProjectTreeBuilder myBuilder;
    private ChooseByNamePanel myGotoByNamePanel;
    private TabbedPaneWrapper myTabbedPane;

    private RClass mySelectedClass;
    private boolean myResult;
    private ClassFilter myClassFilter;

    //TODO Add opportunity setup baseClass, i.e parent for other classes, also implement ClassFilters
//    private RClass myBaseClass;

    public TreeRClassChooserDialog(@NotNull final Project project, @NotNull final String title,
                                   @NotNull final SearchScope scope,
                                   @Nullable final RClass initialClass,
                                   @Nullable final ClassFilter classFilter) {
        super(project, true);

        myProject = project;
        mySearchScope = scope;
        myInitialClass = initialClass;
        myClassFilter = classFilter;
        setTitle(title);

        init();

        if (myInitialClass != null) {
          selectClass(myInitialClass);
        }

        handleSelectionChanged();
    }

    public void selectClass(@NotNull final RClass rClass) {
        selectElementInTree(rClass);
    }

    public void selectFile(@Nullable final PsiFileSystemItem fileSystemItem) {
        if (fileSystemItem != null) {
            selectElementInTree(fileSystemItem);
        }
    }

    public JComponent getPreferredFocusedComponent() {
        return myGotoByNamePanel.getPreferredFocusedComponent();
    }

    public RVirtualClass getSelectedClass() {
        return mySelectedClass;
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public boolean showDialog() {
        myResult = false;
        show();
        return myResult;
    }

    public void dispose() {
        if (myBuilder != null) {
            Disposer.dispose(myBuilder);
            myBuilder = null;
        }
        super.dispose();
    }


    @Nullable
    private RClass calcSelectedClass() {
        if (myTabbedPane.getSelectedIndex() == 0) {
            //if goto by name is active
            return (RClass)myGotoByNamePanel.getChosenElement();
        } else {
            //if project view is enabled
            
            final TreePath path = myTree.getSelectionPath();
            if (path == null) {
                return null;
            }
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
            final Object userObject = node.getUserObject();
            if (userObject instanceof PsiFileNode) {
                final PsiFile file = ((PsiFileNode)userObject).getValue();
                if (file instanceof RFile) {
                    final List<RVirtualClass> classes = RContainerUtil.getTopLevelClasses((RFile)file);
                    for (RVirtualClass rClass : classes) {
                        if (rClass instanceof RClass
                            && (myClassFilter == null || myClassFilter.isAccepted(rClass))) {

                            return (RClass)rClass;
                        }
                    }
                }
            } else if (userObject instanceof RClassNode) {
                final RVirtualClass value = ((RClassNode) userObject).getValue();
                final RPsiElement psiElement = RVirtualPsiUtil.findPsiByVirtualElement(value, myProject);
                if (psiElement instanceof RClass) {
                    return (RClass) psiElement;
                }

            }
            return null;
        }
    }

    private RClass getContext() {
//      return myBaseClass != null
//              ? myBaseClass
//              : myInitialClass != null ? myInitialClass : null;
        return myInitialClass;
    }

    private ModalityState getModalityState() {
        return ModalityState.stateForComponent(getRootPane());
    }

    private void handleSelectionChanged() {
        RClass selection = calcSelectedClass();
        setOKActionEnabled(selection != null);
    }

    //select element in project view
    private void selectElementInTree(@NotNull final PsiElement element) {
      ApplicationManager.getApplication().invokeLater(new Runnable() {
        public void run() {
          if (myBuilder == null) {
              return;
          }
          final VirtualFile vFile = PsiUtil.getVirtualFile(element);
          myBuilder.select(element.getContainingFile(), vFile, false);
        }
      }, getModalityState());
    }

    @Nullable
    protected JComponent createCenterPanel() {
        final DefaultTreeModel model = new DefaultTreeModel(new DefaultMutableTreeNode());
        myTree = new Tree(model);

        //For project view tab
        ProjectAbstractTreeStructureBase treeStructure = new AbstractProjectTreeStructure(
                myProject) {

            public boolean isFlattenPackages() {
                return false;
            }

            public boolean isShowMembers() {
                return false;
            }

            public boolean isHideEmptyMiddlePackages() {
                return true;
            }


            public boolean isAbbreviatePackageNames() {
                return false;
            }

            public boolean isShowLibraryContents() {
                return true;
            }

            public boolean isShowModules() {
                return false;
            }
        };

        myBuilder = new ProjectTreeBuilder(myProject, myTree, model, AlphaComparator.INSTANCE, treeStructure);

        myTree.setRootVisible(false);
        myTree.setShowsRootHandles(true);
        myTree.expandRow(0);
        myTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        myTree.setCellRenderer(new NodeRenderer());
        UIUtil.setLineStyleAngled(myTree);

        final JScrollPane scrollPane = new JScrollPane(myTree);
        scrollPane.setPreferredSize(new Dimension(500, 300));

        myTree.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    doOKAction();
                }
            }
        });

        myTree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = myTree.getPathForLocation(e.getX(), e.getY());
                    if (path != null && myTree.isPathSelected(path)) {
                        doOKAction();
                    }
                }
            }
        });

        myTree.addTreeSelectionListener(
                new TreeSelectionListener() {
                    public void valueChanged(TreeSelectionEvent e) {
                        handleSelectionChanged();
                    }
                }
        );

        new TreeSpeedSearch(myTree);
        final JPanel dummyPanel = new JPanel(new BorderLayout());

        String name = null;
        myGotoByNamePanel = new MyChooseByNamePanel(name, dummyPanel);
//
//
        myTabbedPane = new TabbedPaneWrapper(Disposer.newDisposable());
        myTabbedPane.addTab(RBundle.message("tab.chooser.search.by.name"), dummyPanel);
        myTabbedPane.addTab(RBundle.message("tab.chooser.project"), scrollPane);

        myGotoByNamePanel.invoke(new MyCallback(), getModalityState(), false);

        myTabbedPane.addChangeListener(
                new ChangeListener() {
                    public void stateChanged(ChangeEvent e) {
                        handleSelectionChanged();
                    }
                }
        );

        return myTabbedPane.getComponent();
    }

    protected ChooseByNameModel createChooseByNameModel() {
        return new MyGotoClassModel(myProject);
    }

    protected void doOKAction() {
        myResult = true;
        mySelectedClass = calcSelectedClass();
        if (mySelectedClass == null) {
            return;
        }
        super.doOKAction();
    }

    protected String getDimensionServiceKey() {
        return "#com.intellij.ide.util.TreeClassChooserDialog";
    }

    public static interface ClassFilter {
        public boolean isAccepted(@NotNull final RVirtualClass rClass);
    }

//    private class SubclassGotoClassModel extends MyGotoClassModel {
//
//      public SubclassGotoClassModel(final Project project) {
//        super(project);
//      }
//
//      public String[] getNames(boolean checkBoxState) {
//        // RClass[] classes = RCacheUtil.findInheritors(myBaseClass, myScope, true);
//        ArrayList<String> names = new ArrayList<String>(classes.length + 1);
//        if ((myClassFilter == null || myClassFilter.isAccepted(myBaseClass)) && myBaseClass.getName() != null) {
//          names.add(myBaseClass.getName());
//        }
//        for (RClass aClass : classes) {
//          if ((myClassFilter == null || myClassFilter.isAccepted(aClass)) && aClass.getName() != null) {
//            names.add(aClass.getName());
//          }
//        }
//        return names.toArray(new String[names.size()]);
//      }
//
//      protected boolean isAccepted(RClass aClass) {
//        //return (aClass.isInheritor(myBaseClass, true) || aClass == myBaseClass)
//                 && (myClassFilter == null || myClassFilter.isAccepted(aClass));
//      }
//
//    }

    private class MyCallback extends ChooseByNamePopupComponent.Callback {
        public void elementChosen(Object element) {
            mySelectedClass = (RClass)element;
            close(OK_EXIT_CODE);
        }
    }

    private class MyChooseByNamePanel extends ChooseByNamePanel {
        private final JPanel dummyPanel;

        public MyChooseByNamePanel(final String name, final JPanel dummyPanel) {
            super(TreeRClassChooserDialog.this.myProject,
                  TreeRClassChooserDialog.this.createChooseByNameModel(),
                  name,
                  TreeRClassChooserDialog.this.mySearchScope.isSearchInSDKLibraries(),
                  TreeRClassChooserDialog.this.getContext());
            this.dummyPanel = dummyPanel;
        }

        protected void showTextFieldPanel() {
        }

        protected void close(boolean isOk) {
            super.close(isOk);

            if (isOk) {
                doOKAction();
            } else {
                doCancelAction();
            }
        }

        protected void initUI(ChooseByNamePopupComponent.Callback callback, ModalityState modalityState, boolean allowMultipleSelection) {
            super.initUI(callback, modalityState, allowMultipleSelection);
            dummyPanel.add(myGotoByNamePanel.getPanel(), BorderLayout.CENTER);
            IdeFocusTraversalPolicy.getPreferredFocusedComponent(myGotoByNamePanel.getPanel()).requestFocus();
        }

        protected void showList() {
            super.showList();
            if (myInitialClass != null && myList.getModel().getSize() > 0) {
                myList.setSelectedValue(myInitialClass, true);
                myInitialClass = null;
            }
        }

        protected void choosenElementMightChange() {
            handleSelectionChanged();
        }
    }

    private class MyGotoClassModel extends GotoClassModel2 {
        public MyGotoClassModel(Project project) {
            super(project);
        }

        public Object[] getElementsByName(final String name, final boolean checkBoxState) {
            RClass[] classes = RCacheUtil.getClassesByName(name, mySearchScope, myProject);

            final List<RClass> list = new ArrayList<RClass>();
            for (RClass rClass : classes) {
                if (isAccepted(rClass)) {
                    list.add(rClass);
                }
            }
            return list.toArray(new RClass[list.size()]);
        }

        @Nullable
        public String getPromptText() {
            return null;
        }


        protected boolean isAccepted(RClass rClass) {
            return myClassFilter == null || myClassFilter.isAccepted(rClass);
        }
    }
}
