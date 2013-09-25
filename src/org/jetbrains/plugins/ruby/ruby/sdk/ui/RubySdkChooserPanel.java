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

package org.jetbrains.plugins.ruby.ruby.sdk.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkType;
import com.intellij.ide.util.projectWizard.JdkChooserPanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkType;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.util.ui.UIUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman.Chernyatchik
 * @date: Oct 26, 2006
 */
public class RubySdkChooserPanel extends JComponent {
    private JdkChooserPanel myJdkChooser;

    /**
     * RubySdkChooserPanel - the panel to choose Ruby SDK
     *
     * @param project Current project
     */
    public RubySdkChooserPanel(final Project project) {
        myJdkChooser = new JdkChooserPanel(project);

        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEtchedBorder());

        final JLabel label = new JLabel(RBundle.message("module.rails.select.jdk"));
        label.setUI(new MultiLineLabelUI());
        add(label, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(8, 10, 8, 10), 0, 0));

        final JLabel jdklabel = new JLabel(RBundle.message("module.rails.prompt.label.project.jdk"));
        jdklabel.setFont(UIUtil.getLabelFont().deriveFont(Font.BOLD));
        add(jdklabel, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(8, 10, 0, 10), 0, 0));

        add(myJdkChooser, new GridBagConstraints(0, GridBagConstraints.RELATIVE, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 10, 10, 5), 0, 0));
        JButton configureButton = new JButton(RBundle.message("button.configure"));
        add(configureButton, new GridBagConstraints(1, GridBagConstraints.RELATIVE, 1, 1, 0.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 0, 10, 5), 0, 0));


        configureButton.addActionListener(new ActionListener() {
            @Override
			public void actionPerformed(ActionEvent e) {
                myJdkChooser.editJdkTable();
            }
        });

        myJdkChooser.setAllowedJdkTypes(new SdkType[]{RubySdkType.getInstance(),
                                                      JRubySdkType.getInstance()});

        final Sdk selectedJdk = null;
        myJdkChooser.updateList(selectedJdk, null);
    }

    @Nullable
    public Sdk getChosenJdk() {
        return myJdkChooser.getChosenJdk();
    }

    public JComponent getPreferredFocusedComponent() {
        return myJdkChooser;
    }

    public void selectSdk(@Nullable final Sdk sdk) {
        myJdkChooser.selectJdk(sdk);
    }
}
