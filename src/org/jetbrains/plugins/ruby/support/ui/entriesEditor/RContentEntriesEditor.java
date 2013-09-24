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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModuleElementsEditor;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.VirtualFileManagerAdapter;
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx;
import com.intellij.ui.ScrollPaneFactory;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Eugene Zhuravlev, Roman Chernyatchik
 * @date: Aug 18, 2007
 */
public abstract class RContentEntriesEditor extends ModuleElementsEditor {
    public static final String NAME = ProjectBundle.message("module.paths.title");
    public static final Icon ICON = IconLoader.getIcon("/modules/sources.png");

    private RContentEntryTreeEditor myRootTreeEditor;

    private ContentEntry myEntry;
    private RContentEntryEditor myEditor;

    private JRadioButton myRbRelativePaths;
    private Module myModule;

    public abstract RContentEntryEditor createEditor(final ContentEntry entry, final Module module);
    public abstract RContentEntryTreeEditor createEntryTreeEditor(final Module module);

    public RContentEntriesEditor(final Project project,
                                 final ModifiableRootModel model) {
        super(project, model);
        myModule = myModel.getModule();

        final VirtualFileManagerEx fileManager = ((VirtualFileManagerEx)VirtualFileManager.getInstance());
        final VirtualFileManagerAdapter fileManagerListener = new VirtualFileManagerAdapter() {
            public void afterRefreshFinish(boolean asynchonous) {
                final Module module = getModule();
                if (module == null || module.isDisposed() || module.getProject().isDisposed()) {
                    return;
                }

                if (myEditor != null) {
                    myEditor.update();
                }

            }
        };
        fileManager.addVirtualFileManagerListener(fileManagerListener);

        registerDisposable(new Disposable() {
            public void dispose() {
                fileManager.removeVirtualFileManagerListener(fileManagerListener);
            }
        });
    }

    public String getHelpTopic() {
        return "project.paths.paths";
    }

    public String getDisplayName() {
        return NAME;
    }

    public Icon getIcon() {
        return ICON;
    }

    public void disposeUIResources() {
        if (myRootTreeEditor != null) {
            myRootTreeEditor.setContentEntryEditor(null);
        }
        super.disposeUIResources();
    }

    public boolean isModified() {
        if (super.isModified()) {
            return true;
        }
        final Module selfModule = getModule();
        return  (selfModule != null
                && myRbRelativePaths != null
                && selfModule.isSavePathsRelative() != myRbRelativePaths.isSelected())
                || myEditor.isModified();
    }

    public JPanel createComponentImpl() {
        final Module module = getModule();

        //Editor and Contnet entry
        myEntry = myModel.getContentEntries()[0];

        //main panel
        final JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        //splitter  : folder | file tree view
        final Splitter splitter = new Splitter(false);
        splitter.setHonorComponentsMinimumSize(true);
        mainPanel.add(splitter, BorderLayout.CENTER);

        //splitter.first :  selected folders
        myEditor = createEditor(myEntry, myModule);
        Border border = BorderFactory.createEmptyBorder(2, 2, 0, 2);
        final JComponent component = myEditor.getComponent();
        final Border componentBorder = component.getBorder();
        if (componentBorder != null) {
            border = BorderFactory.createCompoundBorder(border, componentBorder);
        }
        component.setBorder(border);
        splitter.setFirstComponent(ScrollPaneFactory.createScrollPane(component));

        final RContentEntryEditorListener myContentEntryEditorListener =
                new MyContentEntryEditorListener();
        myEditor.addContentEntryEditorListener(myContentEntryEditorListener);
        registerDisposable(new Disposable() {
            public void dispose() {
                myEditor.removeContentEntryEditorListener(myContentEntryEditorListener);
            }
        });

        //splitter.second : files tree
        myRootTreeEditor = createEntryTreeEditor(myModule);
        myRootTreeEditor.setContentEntryEditor(myEditor);
        splitter.setSecondComponent(myRootTreeEditor.createComponent());

        //other controls
        final JPanel innerPanel = new JPanel(new GridBagLayout());
        innerPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 6));
        myRbRelativePaths = new JRadioButton(ProjectBundle.message("module.paths.outside.module.dir.relative.radio"));
        final JRadioButton rbAbsolutePaths = new JRadioButton(ProjectBundle.message("module.paths.outside.module.dir.absolute.radio"));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(myRbRelativePaths);
        buttonGroup.add(rbAbsolutePaths);
        innerPanel.add(new JLabel(ProjectBundle.message("module.paths.outside.module.dir.label")),
                new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
                        0, 0));
        innerPanel.add(rbAbsolutePaths,
                new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
                        0, 0));
        innerPanel.add(myRbRelativePaths,
                new GridBagConstraints(2, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0),
                        0, 0));
        if (module.isSavePathsRelative()) {
            myRbRelativePaths.setSelected(true);
        } else {
            rbAbsolutePaths.setSelected(true);
        }

        mainPanel.add(innerPanel, BorderLayout.SOUTH);

        selectContentEntry();

        return mainPanel;
    }

    protected Module getModule() {
//        return myModulesProvider.getModule(myModuleName);
        return myModule;
    }

    private void selectContentEntry() {
        myEditor.setSelected(true);
        final JComponent component = myEditor.getComponent();
        final JComponent scroller = (JComponent)component.getParent();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                scroller.scrollRectToVisible(component.getBounds());
            }
        });
        myRootTreeEditor.requestFocus();
    }

    public void moduleStateChanged() {
        if (myRootTreeEditor != null) { //in order to update exclude output root if it is under content root
            myRootTreeEditor.update();
        }
    }

    public void saveData() {
    }

    public void apply() throws ConfigurationException {
        final Module module = getModule();
        module.setSavePathsRelative(myRbRelativePaths.isSelected());

        myEditor.apply();
    }

    private final class MyContentEntryEditorListener extends RContentEntryEditorListenerAdapter {
        public void editingStarted() {
            selectContentEntry();
        }

        public void navigationRequested(RContentEntryEditor editor, VirtualFile file) {
            if (myEntry.equals(editor.getContentEntry())) {
                myRootTreeEditor.requestFocus();
                myRootTreeEditor.select(file);
            }
        }
    }  
}

