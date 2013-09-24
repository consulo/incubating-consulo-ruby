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

package org.jetbrains.plugins.ruby.ruby.ri;

import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.ui.LabeledComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 2, 2006
 */
class InfoPane {

    private static final String CONTENT_TYPE = "text/html";

    private JPanel mainPanel;
    private LabeledComponent<JTextField> myNameComp;
    private JEditorPane myOutputPane;
    private JButton backButton;
    private JButton forwardButton;
    private JButton clearButton;
    private RDocPanel myDocPanel;
    private JTextField myTextField;

    private final Map<String,String> riCache = new HashMap<String,String>();
    private final Stack<NavigationCommand> undoStack = new Stack<NavigationCommand>();
    private final Stack<NavigationCommand> redoStack = new Stack<NavigationCommand>();

    public InfoPane(final RDocPanel docPanel) {
        myDocPanel = docPanel;
        final Color bgColor = EditorColorsManager.getInstance().getGlobalScheme().getColor(EditorColors.CARET_ROW_COLOR);
        myOutputPane.setBackground(bgColor != null ? bgColor : Color.WHITE);
        myOutputPane.setEditable(false);
        myOutputPane.setContentType(CONTENT_TYPE);

        myOutputPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    createAndExecuteCmd(e.getDescription().trim());
                }
            }
        });

        backButton.setEnabled(false);
        backButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                undo();
            }
        });

        forwardButton.setEnabled(false);
        forwardButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                redo();
            }
        });

        clearButton.setEnabled(true);
        clearButton.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                clear();
            }
        });

        backButton.setBorder(null);
        backButton.setBackground(mainPanel.getBackground());
        forwardButton.setBorder(null);
        forwardButton.setBackground(mainPanel.getBackground());
    }

    public JComponent getPanel() {
        return mainPanel;
    }

    public void showHelp(final String name) {
        createAndExecuteCmd(name);
    }

    protected void fireJDKChanged() {
        myNameComp.enableInputMethods(false);
        myNameComp.setEnabled(false);
        myTextField.setEnabled(false);
        myOutputPane.setText(RIUtil.WRONG_JDK_OR_RI_MESSAGE);

        ProjectJdk jdk = myDocPanel.getProjectJdk();
        if (RubySdkUtil.isKindOfRubySDK(jdk)) {
            myOutputPane.setText("");
            if (RIUtil.checkIfRiExists(jdk)) {
                myNameComp.setEnabled(true);
                myTextField.setEnabled(true);
            } else {
                myOutputPane.setText(RBundle.message("ruby.ri.no.ri.found"));
            }
        }
    }

    private void clear() {
        riCache.clear();
        undoStack.clear();
        redoStack.clear();
        updateNavigationButtons();

        myTextField.setText("");
        fireJDKChanged();
    }

    private void createUIComponents() {
        myNameComp = new LabeledComponent<JTextField>();
        myTextField = new JTextField();
        myNameComp.setComponent(myTextField);
        myNameComp.setText(RBundle.message("ruby.ri.name"));

        myTextField.addKeyListener(new KeyAdapter() {
            public void keyTyped(KeyEvent e) {
                char keyChar = e.getKeyChar();
                if (TextUtil.isEol(keyChar)) {
                    final String text = myTextField.getText().trim();
                    if (!TextUtil.isEmpty(text)){
                        final String key = text.trim();
                        if(key.length() > 0){
                            showHelp(key);
                        }
                    }
                }
            }
        });
    }

    private void createAndExecuteCmd(@NotNull final String data) {
        if (!undoStack.empty() && data.equals(undoStack.peek().getData())) {
            return;
        }
        final NavigationCommand cmd = new NavigationCommand(data);
        cmd.doCmd();
        redoStack.clear();
        undoStack.add(cmd);
        updateNavigationButtons();
    }

    private void disableControls() {
        backButton.setEnabled(false);
        forwardButton.setEnabled(false);
        clearButton.setEnabled(false);
    }

    private void redo() {
        if (!redoStack.isEmpty()) {
            final InfoPane.NavigationCommand cmd = redoStack.pop();
            cmd.redoCmd();
            undoStack.push(cmd);
        }
        updateNavigationButtons();
    }

    private void undo() {
        if (!undoStack.isEmpty()) {
            final NavigationCommand cmd = undoStack.pop();
            cmd.undoCmd();
            redoStack.push(cmd);
        }
        updateNavigationButtons();
    }

    private void updateNavigationButtons() {
        backButton.setEnabled(undoStack.size() > 1);
        forwardButton.setEnabled(!redoStack.empty());
        clearButton.setEnabled(true);
    }

    private class NavigationCommand {
        private String myData;

        public NavigationCommand(final String data) {
            myData = data;
        }

        public String getData() {
            return myData;
        }

        public void doCmd() {
            showRDoc(myData);
        }

        public void redoCmd() {
            doCmd();
        }

        public void undoCmd() {
            if (undoStack.isEmpty()) {
                return;
            }
            showRDoc(undoStack.peek().getData());
        }

        public String toString() {
            return getData();
        }

        private void showRDoc(final String data) {
            String rDoc = riCache.get(data);
            if (rDoc == null) {
                disableControls();
                rDoc = myDocPanel.lookup(data);
                riCache.put(data, rDoc);
            }
            myTextField.setText(data);
            myOutputPane.setText(rDoc);
            myOutputPane.setCaretPosition(0);
        }
    }
}
