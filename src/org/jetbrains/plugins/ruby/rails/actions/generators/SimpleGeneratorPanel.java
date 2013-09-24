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

package org.jetbrains.plugins.ruby.rails.actions.generators;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 28.11.2006
 */
public class SimpleGeneratorPanel implements GeneratorPanel {

    private JTextField myGeneratorArgsLabelTextField;
    private JCheckBox myPretendCheckBox;
    private JCheckBox myForceCheckBox;
    private JCheckBox mySkipCheckBox;
    private JCheckBox myBacktraceCheckBox;
    private JPanel myContentPanel;
    private JCheckBox mySVNCheckBox;
    public GeneratorOptions myOptions;

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

    public String getMainArgument() {
        return myGeneratorArgsLabelTextField.getText().trim();
    }

    @NotNull
    public String getGeneratorArgs() {
        final StringBuilder buff = new StringBuilder();
        buff.append(GeneratorsUtil.calcGeneralOptionsString(myBacktraceCheckBox,
                                                             myForceCheckBox,
                                                             myPretendCheckBox,
                                                             mySkipCheckBox,
                                                             mySVNCheckBox));
        
        buff.append(myGeneratorArgsLabelTextField.getText().trim());
        return buff.toString();
    }

    public JComponent getPreferredFocusedComponent() {
        return myGeneratorArgsLabelTextField;
    }

    public void saveSettings(final Project project) {
        GeneratorsUtil.saveSettings(myPretendCheckBox, myForceCheckBox,
                                     mySkipCheckBox, myBacktraceCheckBox,
                                     mySVNCheckBox, myOptions, project);
    }
}
