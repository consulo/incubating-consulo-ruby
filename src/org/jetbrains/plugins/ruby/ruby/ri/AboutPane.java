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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.ui.LabeledComponent;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;

import javax.swing.*;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 8, 2006
 */
class AboutPane {
    private JPanel mainPanel;
    private LabeledComponent<JTextField> myJDKComp;
    private LabeledComponent<JTextField> myRIComp;
    private JLabel myLabel;
    private JTextField myJdkField;
    private JTextField myRiVersionField;
    private RDocPanel myDocPanel;
    private Project myProject;

    public AboutPane(final RDocPanel docPanel, final Project project){
        myDocPanel = docPanel;
        myProject = project;
        myLabel.setText(RBundle.message("ruby.ri.about.message"));
    }

    private void createUIComponents() {
        myJDKComp = new LabeledComponent<JTextField>();
        myJdkField = new JTextField();
        myJdkField.setEditable(false);
        myJDKComp.setComponent(myJdkField);
        myJDKComp.setText(RBundle.message("ruby.sdk.used"));

        myRIComp = new LabeledComponent<JTextField>();
        myRiVersionField = new JTextField();
        myRiVersionField.setEditable(false);
        myRIComp.setComponent(myRiVersionField);
        myRIComp.setText(RBundle.message("ruby.ri.ri.used"));

    }

    public void fireJDKChanged() {
        ProjectJdk jdk = myDocPanel.getProjectJdk();
        if (RubySdkUtil.isKindOfRubySDK(jdk)){
            myJdkField.setForeground(Color.BLACK);
            myJdkField.setText(jdk.getName());
            if (RIUtil.checkIfRiExists(jdk)){
                final String progressTitle = RBundle.message("ruby.ri.update.title");
                myRiVersionField.setText(RIUtil.getRiOutput(jdk, myProject, progressTitle, RIUtil.VERSION).getStdout());
            } else {
                myRiVersionField.setText(RBundle.message("ruby.ri.no.ri.found"));
            }
        } else {
            myJdkField.setForeground(Color.RED);
            myJdkField.setText(RBundle.message("ruby.ri.wrong.project.jdk"));
            myRiVersionField.setText(RBundle.message("ruby.ri.ri.unknown.version"));
        }
    }

    public JComponent getPanel() {
        return mainPanel;
    }
}
