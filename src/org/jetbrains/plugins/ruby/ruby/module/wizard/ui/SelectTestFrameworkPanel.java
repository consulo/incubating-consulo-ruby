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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.ruby.module.ui.roots.testFrameWork.TestFrameworkOptions;
import org.jetbrains.plugins.ruby.support.OpenLinkInBrowserHyperlinkListener;
import org.jetbrains.plugins.ruby.support.utils.RubyUIUtil;

import javax.swing.*;
import java.awt.event.ItemListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 14, 2007
 */

public class SelectTestFrameworkPanel implements TestFrameworkOptions {
    private JTextPane myTPRSpecHomePageLink;
    private JPanel myContentPane;
    private JCheckBox myCBUseStdTestUnit;
    private JCheckBox myCBUseRSpec;

    public SelectTestFrameworkPanel(@Nullable final ItemListener changedListener,
                                    final boolean shouldUseTestUnit,
                                    final boolean shouldUseRSpecFramework) {
        myTPRSpecHomePageLink.addHyperlinkListener(new OpenLinkInBrowserHyperlinkListener(myContentPane));
        myTPRSpecHomePageLink.setText(RubyUIUtil.wrapToHtmlWithLabelFont(RBundle.message("module.settings.dialog.select.test.framework.choose.rspec.html.link", RSpecUtil.RSPEC_HOME_PAGE_URL)));
        myTPRSpecHomePageLink.setBackground(myContentPane.getBackground());

        myCBUseRSpec.setSelected(shouldUseRSpecFramework);
        myCBUseStdTestUnit.setSelected(shouldUseTestUnit);

        if (changedListener != null) {
            myCBUseStdTestUnit.addItemListener(changedListener);
            myCBUseRSpec.addItemListener(changedListener);
        }
    }

    public JPanel getContentPane() {
         return myContentPane;
     }

     @Override
	 public boolean shouldUseRSpecFramework() {
         return myCBUseRSpec.isSelected();
     }

    @Override
	public boolean shouldUseTestUnitFramework() {
        return myCBUseStdTestUnit.isSelected();
    }

    @Override
	public boolean shouldPreferRSpecPlugin() {
        //N/A
        return false;
    }

    @Override
	@Nullable
    public String getTestUnitRootUrl() {
        return null;
    }
}
