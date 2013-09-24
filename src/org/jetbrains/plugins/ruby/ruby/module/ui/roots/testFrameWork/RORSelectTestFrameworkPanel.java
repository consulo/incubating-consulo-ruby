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

package org.jetbrains.plugins.ruby.ruby.module.ui.roots.testFrameWork;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.jruby.JRubyModuleContentRootManager;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.roots.RModuleContentRootManager;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.OpenLinkInBrowserHyperlinkListener;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyUIUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 14, 2007
 */
public class RORSelectTestFrameworkPanel implements TestFrameworkOptions{
    private JTextPane myTPRSpecHomePageLink;
    private JPanel myContentPane;
    private JRadioButton myRBUseRSpecPlugin;
    private JRadioButton myRBUseRspecGem;
    private JButton myBGetRSpecGemVersion;

    private JCheckBox myCBUseRSpec;
    private JCheckBox myCBUseTestUnit;

    private LabeledComponent testUnitRootDirComponent;
    private TextFieldWithBrowseButton testUnitRootDirTextField;


    public RORSelectTestFrameworkPanel(final boolean useRSpec,
                                       final boolean preferRSpecSplugin,
                                       final boolean useTestUnit,
                                       @NotNull final Module module,
                                       final boolean forRailsModule) {

        myTPRSpecHomePageLink.addHyperlinkListener(new OpenLinkInBrowserHyperlinkListener(myContentPane));
        myTPRSpecHomePageLink.setText(RubyUIUtil.wrapToHtmlWithLabelFont(RBundle.message("module.settings.dialog.select.test.framework.choose.rspec.html.link", RSpecUtil.RSPEC_HOME_PAGE_URL)));
        myTPRSpecHomePageLink.setBackground(myContentPane.getBackground());

        myCBUseRSpec.setSelected(useRSpec);

        myCBUseTestUnit.setSelected(useTestUnit);
        myCBUseTestUnit.addActionListener(new ActionListener() {
            public void actionPerformed(final ActionEvent e) {
                testUnitRootDirComponent.setEnabled(myCBUseTestUnit.isSelected());
            }
        });

        final boolean jRubySupport = JRubyUtil.hasJRubySupport(module);
        testUnitRootDirComponent.setVisible(jRubySupport);
        if (jRubySupport) {
            //For JRuby
            final RModuleContentRootManager manager = RModuleUtil.getModuleContentManager(module);
            final String testUnitFolderUrl = ((JRubyModuleContentRootManager) manager).getUnitTestsRootUrl();
            if (testUnitFolderUrl != null) {
                final String path = FileUtil.toSystemDependentName(VfsUtil.urlToPath(testUnitFolderUrl));
                testUnitRootDirTextField.setText(path);
            } else {
                testUnitRootDirTextField.setText("");
            }
            
            // adding browse action to Test::Unit surces chooser
            final String title = RBundle.message("module.settings.dialog.test.framework.test.unit.root.path.chooser.caption");
            final FileChooserDescriptor desc =
                    RubyRunConfigurationUIUtil.addFolderChooser(title, testUnitRootDirTextField, module.getProject());
            desc.setContextModule(module);
        }



        if (preferRSpecSplugin && forRailsModule) {
            myRBUseRSpecPlugin.setSelected(true);
        } else {
            myRBUseRspecGem.setSelected(true);
        }

        myRBUseRSpecPlugin.setVisible(forRailsModule);

        final boolean useRSpecFramework = shouldUseRSpecFramework();
        myRBUseRSpecPlugin.setEnabled(useRSpecFramework);
        myRBUseRspecGem.setEnabled(useRSpecFramework);

        myCBUseRSpec.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                final boolean useRSpecFramework = shouldUseRSpecFramework();
                myRBUseRSpecPlugin.setEnabled(useRSpecFramework);
                myRBUseRspecGem.setEnabled(useRSpecFramework);
            }
        });


        myBGetRSpecGemVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final ProjectJdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
                if (sdk == null || !RubySdkUtil.isSDKValid(sdk)) {
                    final String msg = RBundle.message("module.settings.dialog.test.framework.rspec.use.no.sdk.messages");
                    final String title = RBundle.message("module.settings.dialog.test.framework.rspec.use.no.sdk.title");
                    myRBUseRspecGem.setText(RBundle.message("module.settings.dialog.test.framework.rspec.use.gem"));
                    Messages.showErrorDialog(msg, title);
                    return;
                }
                if (RSpecUtil.checkIfRSpecGemExists(sdk)) {
                    String vers = RSpecUtil.getRSpecGemVersion(sdk, true, null);
                    if (TextUtil.isEmpty(vers)) {
                        vers = RBundle.message("gem.unknown.version");
                    }
                    final String text = RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem", vers);
                    myRBUseRspecGem.setText(text);
                    return;
                }
                final String notInstalled = RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem.version.not.installed");
                final String text = RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem", notInstalled);
                myRBUseRspecGem.setText(text);
            }
        });
    }

    public JPanel getContentPane() {
         return myContentPane;
     }

     public boolean shouldUseRSpecFramework() {
         return myCBUseRSpec.isSelected();
     }

    public boolean shouldUseTestUnitFramework() {
        return myCBUseTestUnit.isSelected();
    }

    public boolean shouldPreferRSpecPlugin() {
        return myRBUseRSpecPlugin.isSelected();
    }

    @Nullable
    public String getTestUnitRootUrl(){
        final String path = FileUtil.toSystemIndependentName(testUnitRootDirTextField.getText().trim());
        return TextUtil.isEmpty(path)
                ? null
                : VirtualFileUtil.constructLocalUrl(path);
    }

    private void createUIComponents() {
        final Ref<TextFieldWithBrowseButton> wordDirComponentWrapper = new Ref<TextFieldWithBrowseButton>();
        testUnitRootDirComponent = RubyRunConfigurationUIUtil.createDirChooserComponent(wordDirComponentWrapper, RBundle.message("module.settings.dialog.test.framework.test.unit.root.path.title"));
        testUnitRootDirTextField = wordDirComponentWrapper.get();
    }
}