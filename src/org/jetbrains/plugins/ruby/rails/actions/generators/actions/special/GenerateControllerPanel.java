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

package org.jetbrains.plugins.ruby.rails.actions.generators.actions.special;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.InputValidator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.DocumentAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorOptions;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorPanel;
import org.jetbrains.plugins.ruby.rails.actions.generators.GeneratorsUtil;
import org.jetbrains.plugins.ruby.rails.nameConventions.ControllersConventions;
import org.jetbrains.plugins.ruby.rails.nameConventions.NamingConventions;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 28.11.2006
 */
public class GenerateControllerPanel implements GeneratorPanel {
    protected static final String DOTS = "...";

    private JButton myAddButton;
    private JButton myRemoveButton;
    private JTextField myControllerName;
    private JList myActionsList;
    private JLabel myLocationLabel;
    private JPanel myContentPanel;
    private JCheckBox myPretendCheckBox;
    private JCheckBox myForceCheckBox;
    private JCheckBox mySkipCheckBox;
    private JCheckBox myBacktraceCheckBox;
    private JLabel myControllerLocationValueLabel;
    private JCheckBox mySVNCheckBox;
    private JPanel myLocationPanel;
    private JTextField myControllerDir;
    private final DefaultListModel myListModel;

    private final StringBuffer myBuff = new StringBuffer();
    private final String myRootPath;
    private GeneratorOptions myOptions;

    public GenerateControllerPanel(@NotNull final String controllersRootPath,
                                   @Nullable final String path) {
        myControllerDir.setText(path == null ? "" : path);
        myRootPath = controllersRootPath;

        myListModel = new DefaultListModel();
        myActionsList.setModel(myListModel);
        myActionsList.setCellRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(final JList list,
                                                          final Object value,
                                                          final int index,
                                                          final boolean isSelected,
                                                          final boolean cellHasFocus) {
                final Component comp =
                        super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setIcon(RubyIcons.RUBY_METHOD_NODE);
                return comp;
            }
        });

        final MyActionInputValidator validator = new MyActionInputValidator();
        myAddButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final String text = Messages.showInputDialog(
                        RBundle.message("dialog.generate.controller.actions.promt"),
                        RBundle.message("dialog.generate.controller.actions.promt.title"),
                        Messages.getQuestionIcon(),
                        TextUtil.EMPTY_STRING,
                        validator);
                if (!TextUtil.isEmpty(text)) {
                    myListModel.addElement(NamingConventions.toUnderscoreCase(text));
                    if (myListModel.size() == 1) {
                        myActionsList.setSelectedIndex(0);
                    }
                }
                myActionsList.requestFocus();
            }
        });

        myRemoveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final Object[] selection = myActionsList.getSelectedValues();
                int selectedIndex = -2;
                for (Object obj : selection) {
                    if (selectedIndex  == -2) {
                        selectedIndex = myListModel.indexOf(obj);
                    }
                    myListModel.removeElement(obj);
                }
                if (myListModel.size() > 0) {
                    if (selectedIndex < myListModel.size()) {
                        myActionsList.setSelectedIndex(selectedIndex);
                    } else {
                        myActionsList.setSelectedIndex(myListModel.size() - 1);
                    }
                }
                myActionsList.requestFocus();
            }
        });

        myControllerName.getDocument().addDocumentListener(new DocumentAdapter() {
            public void textChanged(DocumentEvent event) {
                updateLocation();
            }

        });
        myControllerDir.getDocument().addDocumentListener(new DocumentAdapter() {
            public void textChanged(DocumentEvent event) {
                updateLocation();
            }

        });

        myControllerName.setText(DOTS);
        myControllerName.setText(TextUtil.EMPTY_STRING);

        myContentPanel.doLayout();
    }

    public void initPanel(final GeneratorOptions options) {
        myOptions = options;
        GeneratorsUtil.initOptionsCheckBoxes(myPretendCheckBox, myForceCheckBox,
                                              mySkipCheckBox, myBacktraceCheckBox,
                                              mySVNCheckBox, myOptions);
    }

    @NotNull
    public JPanel getContent() {
        return myContentPanel;
    }

    @NotNull
    public String getGeneratorArgs() {
        final StringBuffer buff = new StringBuffer();
        buff.append(GeneratorsUtil.calcGeneralOptionsString(myBacktraceCheckBox,
                                                             myForceCheckBox,
                                                             myPretendCheckBox,
                                                             mySkipCheckBox,
                                                             mySVNCheckBox));

        final String path = FileUtil.toSystemIndependentName(myControllerDir.getText()).trim();
        if (!TextUtil.isEmpty(path)) {
            buff.append(path);
            buff.append("/");
        }
        buff.append(myControllerName.getText().trim());

        final int count = myListModel.getSize();
        for (int i = 0; i < count; i ++) {
            buff.append(" ");
            buff.append(myListModel.getElementAt(i).toString());
        }
        return buff.toString();
    }

    public String getMainArgument() {
        return myControllerName.getText().trim();
    }

    @NotNull
    public JComponent getPreferredFocusedComponent() {
        return myControllerName;
    }

    public void saveSettings(final Project project) {
        GeneratorsUtil.saveSettings(myPretendCheckBox, myForceCheckBox,
                                     mySkipCheckBox, myBacktraceCheckBox,
                                     mySVNCheckBox, myOptions, project);
    }

    private void updateLocation() {
        myBuff.delete(0, myBuff.length());
        myBuff.append(myRootPath);
        myBuff.append("/");
        String path = FileUtil.toSystemIndependentName(myControllerDir.getText().trim());
        if (!TextUtil.isEmpty(path)) {
            myBuff.append(path);
            myBuff.append("/");
        }
        myBuff.append(NamingConventions.toUnderscoreCase(myControllerName.getText().trim()));
        myBuff.append(RailsConstants.CONTROLLERS_FILE_NAME_SUFFIX);
        myBuff.append(".");
        myBuff.append(RubyFileType.RUBY.getDefaultExtension());
        final int width = myControllerName.getWidth() - myLocationLabel.getSize().width;
        final FontMetrics fontMetrics =
                myLocationPanel.getFontMetrics(myLocationPanel.getFont());
        TextUtil.truncWithDots(myBuff, width, fontMetrics);
        myControllerLocationValueLabel.setText(FileUtil.toSystemDependentName(myBuff.toString()));
    }

    private class MyActionInputValidator implements InputValidator {

        public boolean canClose(final String inputString) {
            if (TextUtil.isEmpty(inputString)
                || !ControllersConventions.isValidActionName(inputString)) {
                showIncorrectNameError(inputString);
                return false;
            }
            return true;
        }

        public boolean checkInput(final String inputString) {
            return true;
        }

        private String getErrorTitle() {
            return RBundle.message("dialog.generate.controller.actions.error.title");
        }

        private void showIncorrectNameError(final String actionName) {
            final String msg = RBundle.message("popup.generate.action.error.script.argument.is.not.valid",
                                               actionName,
                                               ControllersConventions.toValidActionName(actionName));
            Messages.showErrorDialog(msg,getErrorTitle());
        }
    }
}
