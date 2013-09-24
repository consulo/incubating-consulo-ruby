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

package org.jetbrains.plugins.ruby.error;

import com.intellij.openapi.ui.Messages;
import org.jetbrains.plugins.ruby.support.OpenLinkInBrowserHyperlinkListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 27, 2007
 */

public class UrMsgDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JLabel myIconLabel;
    private JTextPane myTextPane;
    private boolean isOk;

    public UrMsgDialog(final String msg, final String title, final Icon icon) {
        super();        
        setTitle(title);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        myIconLabel.setIcon(icon);
        myIconLabel.setText(null);

        myTextPane.addHyperlinkListener(new OpenLinkInBrowserHyperlinkListener(this));
        myTextPane.setText(msg);
        myTextPane.setBackground(contentPane.getBackground());

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    @SuppressWarnings({"UnusedReturnValue"})
    public static boolean showInfoMessage(final String message,
                                          final String title,
                                          final Component parent) {
        final UrMsgDialog dialog =
                new UrMsgDialog(message, title, Messages.getInformationIcon());
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog.isOk;
    }

    private void onCancel() {
        isOk = false;
        dispose();
    }

    private void onOK() {
        isOk = true;
        dispose();
    }
}
