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

package org.jetbrains.plugins.ruby.ruby.module.wizard.ui.rspec;

import com.intellij.openapi.projectRoots.Sdk;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.module.wizard.RRModuleBuilder;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyUIUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 14, 2007
 */
public class RubyRSpecInstallComponentsForm {
    private JPanel myContentPane;
    private JTextPane myTPInfo;
    private final RRModuleBuilder myBuilder;

    public RubyRSpecInstallComponentsForm(final RRModuleBuilder builder) {
        myBuilder = builder;
    }

    protected void initBeforeShow() {
        final Sdk sdk = myBuilder.getSdk();

        //check is gem installed.
        final boolean gemExists = RubySdkUtil.isKindOfRubySDK(sdk) ? RSpecUtil.checkIfRSpecGemExists(sdk) : null;

        final String text;
        if (gemExists) {
            final String rSpecGemVersion = RSpecUtil.getRSpecGemVersion(sdk, true, null);
            final String versionStr = TextUtil.isEmpty(rSpecGemVersion)
                    ? RBundle.message("gem.unknown.version")
                    : rSpecGemVersion;

            text = RubyUIUtil.wrapToHtmlWithLabelFont(RBundle.message("module.settings.dialog.select.test.spec.ruby.installed.rspec.gem", versionStr));
        } else {
            text = RubyUIUtil.wrapToHtmlWithLabelFont(RBundle.message("module.settings.dialog.select.test.spec.ruby.please.install.rspec.gem.html"));
        }
        myTPInfo.setText(text);
    }

    public JPanel getContentPane() {
        return myContentPane;
    }
}
