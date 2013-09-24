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

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.ui.Messages;
import org.apache.xmlrpc.XmlRpcClient;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.support.OpenLinkInBrowserHyperlinkListener;
import org.jetbrains.plugins.ruby.support.utils.RubyUIUtil;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Jan 11, 2007
 */

public class ErrorReportDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonSend;
    private JButton buttonCancel;
    private JTextArea descriptionTextArea;
    private JTextPane myDescriptionPane;
    private JRadioButton myJIRAUserRB;
    private JRadioButton myNotJIRAUserRB;
    private JPasswordField myPasswordTF;
    private JTextField myLoginTF;
    private JLabel myLoginLabel;
    private JLabel myPasswordLabel;
    private JTextPane mySignupForAnAccountTextPane;
    private JCheckBox mySavePasswordCBCheckBox;
    private Status myStatus = null;

    private boolean isJIRAUser() {
        return myJIRAUserRB.isSelected();
    }

    public enum Status {
        SENT,
        CANCELED
    }

    public ErrorReportDialog(final Component parentComponent) {
        super();
        setContentPane(contentPane);
        setModal(true);

        myNotJIRAUserRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableFields(false);
            }
        });
        myJIRAUserRB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                enableFields(true);
            }
        });

        myJIRAUserRB.doClick();

        getRootPane().setDefaultButton(buttonSend);
        setLocationRelativeTo(parentComponent);
        buttonSend.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onSend();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

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

        descriptionTextArea.setBorder(LineBorder.createGrayLineBorder());
        myDescriptionPane.addHyperlinkListener(new OpenLinkInBrowserHyperlinkListener(this));
        myDescriptionPane.setBackground(contentPane.getBackground());

        mySignupForAnAccountTextPane.addHyperlinkListener(new HyperlinkListener() {
            public void hyperlinkUpdate(HyperlinkEvent e) {
                final HyperlinkEvent.EventType eventType = e.getEventType();
                if (eventType.equals(HyperlinkEvent.EventType.ACTIVATED)) {
                    BrowserUtil.launchBrowser(e.getURL().toExternalForm());
                } else if (eventType.equals(HyperlinkEvent.EventType.ENTERED)){
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                } else if (eventType.equals(HyperlinkEvent.EventType.EXITED)){
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        });
        final String signupAccountText =
                RubyUIUtil.wrapToHtmlWithLabelFont(RBundle.message("error.report.submit.register.in.jira",
                                                   RubyErrorReportSubmitter.JIRA_REGISTER_URL));
        mySignupForAnAccountTextPane.setText(signupAccountText);
        mySignupForAnAccountTextPane.setBackground(contentPane.getBackground());
        mySavePasswordCBCheckBox.setText(RBundle.message("error.report.form.rb.jira.password.save"));
        loadInfo();
    }

    private void storeInfo () {
        final RubyErrorReporter reporter = RubyErrorReporter.getInstance();
        reporter.JIRA_LOGIN = myLoginTF.getText().trim();
        reporter.setPlainJiraPassword(new String(myPasswordTF.getPassword()));
        reporter.KEEP_JIRA_PASSWORD = mySavePasswordCBCheckBox.getModel().isSelected();
    }

    private void loadInfo () {
        final RubyErrorReporter reporter = RubyErrorReporter.getInstance();
        myLoginTF.setText(reporter.JIRA_LOGIN);
        myPasswordTF.setText(reporter.getPlainJiraPassword());
        mySavePasswordCBCheckBox.getModel().setSelected(reporter.KEEP_JIRA_PASSWORD);
    }

    public void setVisible(boolean visible) {
        if (visible) {
            descriptionTextArea.requestFocusInWindow();
        }
        super.setVisible(visible);
    }

    public String getDescription(){
        return descriptionTextArea.getText().trim();
    }

    @Nullable
    public Vector<String> getLoginParams() {
        if (!isJIRAUser()) {
            return null;
        }
        final Vector<String> params = new Vector<String>();
        params.add(myLoginTF.getText().trim());
        params.add(String.valueOf(myPasswordTF.getPassword()));
        return params;
    }

    public void setLabel(final String label){
        myDescriptionPane.setText(label);
    }

    public Status getStatus(){
        assert myStatus!=null;
        return myStatus;
    }

    private void enableFields(final boolean enabled) {
        myLoginTF.setEnabled(enabled);
        myLoginLabel.setEnabled(enabled);
        myPasswordTF.setEnabled(enabled);
        myPasswordLabel.setEnabled(enabled);
        mySavePasswordCBCheckBox.setEnabled(enabled);
    }

    private void onCancel() {
        myStatus = Status.CANCELED;
        dispose();
    }

    private void onSend() {
        if (isJIRAUser()) {
            boolean isValid;
            final Vector<String> loginParams = getLoginParams();
            try {
                isValid = new XmlRpcClient(RubyErrorReportSubmitter.JIRA_RPC).execute(
                        RubyErrorReportSubmitter.JIRA_LOGIN_COMMAND,
                        loginParams) != null;
            } catch (Exception e) {
                isValid = false;
            }
            if (!isValid) {
                Messages.showErrorDialog(RBundle.message("error.report.wrong.login"),
                                         RBundle.message("error.report.dialog.wrong.password.title"));
                return;
            }
        }
        storeInfo();
        myStatus = Status.SENT;
        dispose();
    }
}
