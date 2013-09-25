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

package org.jetbrains.plugins.ruby.ruby.module.wizard.ui;

import com.intellij.ide.util.BrowseFilesListener;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.util.Computable;
import static com.intellij.openapi.util.io.FileUtil.toSystemDependentName;
import static com.intellij.openapi.util.io.FileUtil.toSystemIndependentName;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.FieldPanel;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RubyModuleBuilder;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 21, 2007
 */
public class TestSourceRootPanel { //TODO rename to TestUnitSourceRootPanel
    private static final Logger LOG = Logger.getInstance(TestSourceRootPanel.class.getName());
    private final static String TEST_FOLDER = "test";

    private JRadioButton myRBSearchInModule;
    private JPanel myContentPane;
    private JTextField myTFAbsoluteTestsPath;
    private JTextField myTFRelativeTestsPath;

    private final FieldPanel myFieldPanel;

    private RubyModuleBuilder mySettingsHolder;

    public TestSourceRootPanel(final RubyModuleBuilder settingsHolder) {
        mySettingsHolder = settingsHolder;

        myContentPane = new JPanel(new GridBagLayout());
        myContentPane.setBorder(new EmptyBorder(10, 7, 7, 7));

        final String text = RBundle.message("module.settings.dialog.source.roots.choose.test.text");
        final JLabel descr = new JLabel(text);
        descr.setUI(new MultiLineLabelUI());
        myContentPane.add(descr, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(8, 10, 0, 10), 0, 0));

        final JRadioButton myRbCreateTestDir = new JRadioButton(RBundle.message("module.settings.dialog.source.roots.choose.test.create"), true);
        myContentPane.add(myRbCreateTestDir, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(8, 10, 0, 10), 0, 0));


        myTFRelativeTestsPath = new JTextField();
        final JLabel testPathLabel = new JLabel(RBundle.message("module.settings.dialog.source.roots.choose.test.comment"));
        myContentPane.add(testPathLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(8, 30, 0, 0), 0, 0));
        final FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        chooserDescriptor.setIsTreeRootVisible(true);
        myFieldPanel =
                ModuleWizardStep.createFieldPanel(myTFRelativeTestsPath, null,
                                                  new BrowsePathListener(myTFRelativeTestsPath, chooserDescriptor));
        myContentPane.add(myFieldPanel,
                          new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(8, 30, 0, 10), 0, 0));

        myRBSearchInModule = new JRadioButton(RBundle.message("module.settings.dialog.source.roots.choose.test.do.not.create"), false);
        myContentPane.add(myRBSearchInModule, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(8, 10, 0, 10), 0, 0));

        final JLabel fullPathLabel = new JLabel(RBundle.message("module.settings.dialog.source.roots.choose.test.full.path"));
        myContentPane.add(fullPathLabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.NONE, new Insets(8, 10, 0, 10), 0, 0));

        myTFAbsoluteTestsPath = new JTextField();
        myTFAbsoluteTestsPath.setEditable(false);
        final Insets borderInsets = myTFAbsoluteTestsPath.getBorder().getBorderInsets(myTFAbsoluteTestsPath);
        myTFAbsoluteTestsPath.setBorder(BorderFactory.createEmptyBorder(borderInsets.top, borderInsets.left, borderInsets.bottom, borderInsets.right));
        myTFAbsoluteTestsPath.setBackground(myContentPane.getBackground());
        myContentPane.add(myTFAbsoluteTestsPath, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 0.0, GridBagConstraints.SOUTHWEST, GridBagConstraints.HORIZONTAL, new Insets(8, 10, 8, 10), 0, 0));

        //button group
        final ButtonGroup chooseTestsRootGroup = new ButtonGroup();
        chooseTestsRootGroup.add(myRBSearchInModule);
        chooseTestsRootGroup.add(myRbCreateTestDir);

        myTFRelativeTestsPath.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
			public void textChanged(DocumentEvent event) {
                updateFullPathField();
            }
        });


        myRbCreateTestDir.addItemListener(new ItemListener() {
            @Override
			public void itemStateChanged(ItemEvent e) {
                final boolean enabled = e.getStateChange() == ItemEvent.SELECTED;

                testPathLabel.setEnabled(enabled);
                myFieldPanel.setEnabled(enabled);

                fullPathLabel.setVisible(enabled);
                myTFAbsoluteTestsPath.setVisible(enabled);
                if (enabled) {
                    myTFRelativeTestsPath.requestFocus();
                }
            }
        });

        myTFRelativeTestsPath.setText(TEST_FOLDER);
    }

    public JPanel getContentPane() {
        return myContentPane;
    }

    public JComponent getPreferredFocusedComponent() {
        return myTFRelativeTestsPath;
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public boolean shouldSearchInWholeModule() {
        return myRBSearchInModule.isSelected();
    }

    public String getAbsoluteTestsPath() {
        return toSystemIndependentName(myTFAbsoluteTestsPath.getText().trim());
    }

    @Nullable
    private String getTestsDirectoryPath() {
        final String contentEntryPath = getContentRootPath();
        final String dirName = toSystemIndependentName(myTFRelativeTestsPath.getText().trim());
        if (contentEntryPath != null) {
            return dirName.length() > 0 ? contentEntryPath + "/" + dirName : contentEntryPath;
        }
        return null;
    }

    private void updateFullPathField() {
      final String sourceDirectoryPath = getTestsDirectoryPath();
      if (sourceDirectoryPath != null) {
          myTFAbsoluteTestsPath.setText(toSystemDependentName(sourceDirectoryPath));
      }
      else {
        myTFAbsoluteTestsPath.setText("");
      }
    }


    private String getContentRootPath() {
        return mySettingsHolder.getContentEntryPath();
    }

    public void update() {
        final String srcFolder = myTFRelativeTestsPath.getText().trim();
        if (mySettingsHolder.isRSpecSupportEnabled() && TEST_FOLDER.equals(srcFolder)) {
            myTFRelativeTestsPath.setText(RSpecUtil.SPECS_FOLDER);
        } else if (!mySettingsHolder.isRSpecSupportEnabled() && RSpecUtil.SPECS_FOLDER.equals(srcFolder)) {
            myTFRelativeTestsPath.setText(TEST_FOLDER);        
        }
        updateFullPathField();
    }

    private class BrowsePathListener extends BrowseFilesListener {
        private final FileChooserDescriptor myChooserDescriptor;
        private final JTextField myField;

        public BrowsePathListener(final JTextField textField,
                                  final FileChooserDescriptor chooserDescriptor) {
            super(textField, "prompt.select.test.source.directory", "", chooserDescriptor);
            myChooserDescriptor = chooserDescriptor;
            myField = textField;
        }

        @Nullable
        private VirtualFile getContentEntryDir() {
            final String contentEntryPath = getContentRootPath();
            if (contentEntryPath != null) {
                return ApplicationManager.getApplication().runWriteAction(new Computable<VirtualFile>() {
                    @Override
					public VirtualFile compute() {
                        return LocalFileSystem.getInstance().refreshAndFindFileByPath(contentEntryPath);
                    }
                });
            }
            return null;
        }

        @Override
		public void actionPerformed(ActionEvent e) {
            final VirtualFile contentEntryDir = getContentEntryDir();
            if (contentEntryDir != null) {
                myChooserDescriptor.setRoot(contentEntryDir);
                final String textBefore = myField.getText().trim();
                super.actionPerformed(e);
                if (!textBefore.equals(myField.getText().trim())) {
                    final String fullPath = toSystemIndependentName(myField.getText().trim());
                    final VirtualFile fileByPath = LocalFileSystem.getInstance().findFileByPath(fullPath);
                    LOG.assertTrue(fileByPath != null);
                    myField.setText(VfsUtil.getRelativePath(fileByPath, contentEntryDir, File.separatorChar));
                }
            }
        }
    }
}