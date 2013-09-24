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

package org.jetbrains.plugins.ruby.rails.facet.ui.settings.tabs.general;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.ui.RailsUIUtil;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs.EvaluatingComponent;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 25, 2008
 */
public class GeneralSettingsForm implements RailsUIUtil.RailsVersionComponent{
    private JLabel myRailsVersionLabel;
    private JPanel myContentPane;
    private EvaluatingComponent<String> myECRailsVersionLabel;
    private JLabel myRailsAppHomeDirPathValue;

    private String myRailsVersion;
    /**
     * Form is closed state
     */
    private volatile boolean myIsClosed;

    public GeneralSettingsForm(final String railsApplicationRootPath) {
        myRailsAppHomeDirPathValue.setText(railsApplicationRootPath);
    }


    public void beforeShow(@Nullable final Sdk sdk) {
        myIsClosed = false;

        if (myRailsVersion == null) {
            RailsUIUtil.setupRailsVersionEvaluator(sdk, myRailsVersionLabel, myECRailsVersionLabel, this);
        }
    }

    public void setClose() {
        myIsClosed = true;
    }

    private void createUIComponents() {
        myRailsVersionLabel = new JLabel("");
        myECRailsVersionLabel = new EvaluatingComponent<String>(myRailsVersionLabel);
    }

    public boolean isCloosed() {
        return myIsClosed;
    }

    public void setRailsVersion(@Nullable final String railsVersion) {
        myRailsVersion = railsVersion;
    }

    public JPanel getContentPane() {
        return myContentPane;
    }
}
