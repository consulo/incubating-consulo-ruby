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

package org.jetbrains.plugins.ruby.addins.rspec.rails.facet.ui.wizard.tabs;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.DocumentAdapter;
import com.intellij.util.Function;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecApplicationSettings;
import org.jetbrains.plugins.ruby.addins.rspec.RSpecUtil;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.RailsWizardSettingsHolder;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.TabbedSettingsContext;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs.EvaluatingComponent;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs.TabbedSdkDependSettingsEditorTab;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.confuguration.RubyRunConfigurationUIUtil;
import org.jetbrains.plugins.ruby.support.utils.OSUtil;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 5, 2008
 */
public class RSpecComponentsInstallerTab extends TabbedSdkDependSettingsEditorTab {
    private static final String TITLE = RBundle.message("rspec.rails.facet.wizard.tab.rails.project.generator.title");

     private JPanel myContentPane;

    private JCheckBox myCBInstallOrUseRSpec;
    private JCheckBox myCBInstallRSpecRails;

    private JRadioButton myRBUseRSpecGem;
    private JRadioButton myRBInstallRSpecPlugin;
    private JRadioButton myRBRSpecPluginLastest;
    private JRadioButton myRBRSpecPluginAny;
    private JRadioButton myRBRSpecPluginTrunk;

    private JTextField myTFRSpecArgs;
    private JRadioButton myRBRSpecRailsLastest;
    private JRadioButton myRBRSpecRailsPluginAny;
    private JRadioButton myRBRSpecRailsPluginTrunk;
    private JTextField myTFRSpecRailsArgs;
    private JLabel myLabelRspecArgs;
    private JPanel myPanelRSpecRails;
    private JLabel myLabeleRSpecRailsArgs;
    private JPanel myPanelRSpec;
    private JLabel myLSvnMustBeInPath;

    private LabeledComponent mySvnPathComponent;
    private EvaluatingComponent<String> myECInstallRSpecGem;
    private TextFieldWithBrowseButton mySvnPathTextField;

    private RailsWizardSettingsHolder mySettingsHolder;
    private boolean myUseGemAllowed = true;
    private String myRSpecGemVersion;

    /**
     * Form is closed state
     */
    private volatile boolean myIsClosed;

    public RSpecComponentsInstallerTab(@NotNull final RailsWizardSettingsHolder settingsHolder,
                                       @Nullable final Project project) {
        mySettingsHolder = settingsHolder;

        //Button group with custom created component
        final ButtonGroup gbrSpec = new ButtonGroup();
        gbrSpec.add(myRBUseRSpecGem);
        gbrSpec.add(myRBInstallRSpecPlugin);
        
        // SVN Note icon
        myLSvnMustBeInPath.setUI(new MultiLineLabelUI());
        myLSvnMustBeInPath.setIcon(RailsIcons.WARINIG_ICON);
        // adding browse action to script chooser
        final String title = RBundle.message("module.wizard.test.framework.rspec.svn.path.label.select.dialog.title");
        RubyRunConfigurationUIUtil.addFileChooser(title, mySvnPathTextField, project);

        //disable component by previous user experience
        final RSpecApplicationSettings settings = RSpecApplicationSettings.getInstance();
        myCBInstallOrUseRSpec.setSelected(settings.wizardRailsFacetIsRSpecEnabled);
        myCBInstallRSpecRails.setSelected(settings.wizardRailsFacetIsRSpecRailsEnabled);

        //if rpsec gem then we should install specific rspec_rails release
        myRBUseRSpecGem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                final boolean use_gem = e.getStateChange() == ItemEvent.SELECTED;

                rspecSetupOnChanged(use_gem);
            }
        });

        addRSpecPluginSrcTypeListeners();
        addRSpecRailsPluginSrcTypeListeners();

        myTFRSpecArgs.getDocument().addDocumentListener(new DocumentAdapter() {
            protected void textChanged(final DocumentEvent e) {
                setRSpecRailsPluginTextByRSpecPluginText();
            }
        });

        myCBInstallOrUseRSpec.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {

                updateUI();
            }
        });

        myCBInstallRSpecRails.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {

                updateUI();
            }
        });
    }


    @Nls
    public String getDisplayName() {
        return TITLE;
    }

    public JComponent createComponent() {
        return myContentPane;
    }

    /**
     * N/A
     * @return true
     */
    public boolean isModified() {
        return true;
    }

    public void apply() throws ConfigurationException {
        final RailsWizardSettingsHolder.RSpecConfiguration config = getConf();

        mySettingsHolder.setRSpecConf(config);

        final RSpecApplicationSettings settings = RSpecApplicationSettings.getInstance();
        //rspec
        settings.wizardRailsFacetIsRSpecEnabled = config.enableRSpecSupport();
        settings.wizardRailsFacetRSpecPluginSrcType = getRSpecPluginSrcType();
        settings.wizardRailsFacetRSpecArgs = config.getRSpecArgs();
        settings.wizardRailsFacetRSpecRailsArgs = config.getRSpecRailsArgs();
        //rspec rails
        settings.wizardRailsFacetIsRSpecRailsEnabled = config.enableRSpecRailsSupport();

        myIsClosed = true;
    }

    public void reset() {
        final RailsWizardSettingsHolder.RSpecConfiguration conf = mySettingsHolder.getRSpecConf();
        myCBInstallOrUseRSpec.setSelected(conf.enableRSpecSupport());
        myCBInstallRSpecRails.setSelected(conf.enableRSpecRailsSupport());
        myRBInstallRSpecPlugin.setSelected(conf.shouldInstallRSpecPlugin());
        myTFRSpecArgs.setText(conf.getRSpecArgs());
        myTFRSpecRailsArgs.setText(conf.getRSpecRailsArgs());

        final String fileName = conf.getSvnPath();
        mySvnPathTextField.setText(FileUtil.toSystemDependentName(fileName == null ? "" : fileName));
    }

    public void disposeUIResources() {
        // Do nothing
    }

     private void createUIComponents() {
         final Ref<TextFieldWithBrowseButton> testScriptTextFieldWrapper = new Ref<TextFieldWithBrowseButton>();
         final String specFileTitle = RBundle.message("module.wizard.test.framework.rspec.svn.path.label.text");
         mySvnPathComponent = RubyRunConfigurationUIUtil.createScriptPathComponent(testScriptTextFieldWrapper, specFileTitle);
         mySvnPathTextField = testScriptTextFieldWrapper.get();

         myRBUseRSpecGem = new JRadioButton("");
         myECInstallRSpecGem = new EvaluatingComponent<String>(myRBUseRSpecGem);
    }


    private void addRSpecPluginSrcTypeListeners() {
        myRBRSpecPluginLastest.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rspecPluginSetupOnChanged(RSpecApplicationSettings.SrcType.LATEST, true);
                }
            }
        });
        myRBRSpecPluginTrunk.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rspecPluginSetupOnChanged(RSpecApplicationSettings.SrcType.TRUNK, true);
                }
            }
        });
        myRBRSpecPluginAny.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    rspecPluginSetupOnChanged(RSpecApplicationSettings.SrcType.SPECIFIC, true);
                }
            }
        });

    }

    private void addRSpecRailsPluginSrcTypeListeners() {
        myRBRSpecRailsLastest.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    myTFRSpecRailsArgs.setText(addForceParameter(RSpecUtil.getRSpecRailsPluginCurrentUrl()));
                }
            }
        });
        myRBRSpecRailsPluginTrunk.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    myTFRSpecRailsArgs.setText(addForceParameter(RSpecUtil.getRSpecRailsPluginTrunkUrl()));
                }
            }
        });
        myRBRSpecRailsPluginAny.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    myTFRSpecRailsArgs.setText(addForceParameter(RSpecUtil.getRSpecRailsPluginXYZUrl()));
                }
            }
        });

    }

    private void updateUI() {

        final boolean rRSpecSelected = myCBInstallOrUseRSpec.isSelected();
        final boolean rRSpecRailsSelected = myCBInstallRSpecRails.isSelected();

        //rspec rails
        myLabeleRSpecRailsArgs.setEnabled(rRSpecRailsSelected);
        myPanelRSpecRails.setEnabled(rRSpecRailsSelected);

        myRBRSpecRailsPluginTrunk.setEnabled(rRSpecRailsSelected);
        myRBRSpecRailsLastest.setEnabled(rRSpecRailsSelected);
        myRBRSpecRailsPluginAny.setEnabled(rRSpecRailsSelected);
        myTFRSpecRailsArgs.setEnabled(rRSpecRailsSelected);

        //rpec
        myRBRSpecPluginTrunk.setEnabled(rRSpecSelected);
        myRBRSpecPluginLastest.setEnabled(rRSpecSelected);
        myRBRSpecPluginAny.setEnabled(rRSpecSelected);
        myTFRSpecArgs.setEnabled(rRSpecSelected);
        myLabelRspecArgs.setEnabled(rRSpecSelected);
        myPanelRSpec.setEnabled(rRSpecSelected);
        myRBUseRSpecGem.setEnabled(rRSpecSelected && myUseGemAllowed);
        myRBInstallRSpecPlugin.setEnabled(rRSpecSelected);

        if (rRSpecRailsSelected && rRSpecSelected) {
            myRBRSpecRailsPluginTrunk.setEnabled(false);
            myRBRSpecRailsLastest.setEnabled(false);
            myRBRSpecRailsPluginAny.setEnabled(false);
            myTFRSpecRailsArgs.setEnabled(false);
        }

    }

    public void resetSdkSettings() {
        myRSpecGemVersion = null;
    }

    public void beforeShow() {
        myIsClosed = false;

        if (myRSpecGemVersion == null) {
            final RSpecApplicationSettings settings = RSpecApplicationSettings.getInstance();

            // Sdk was changed
            final Sdk sdk = getSdk();

            setupSVNPathLabel();

            //At first we gem isn't installed or we don't know its verison. If
            //gem is installed we will dtermine it's version and enable support in dialog.
            myUseGemAllowed = false;
            myRBUseRSpecGem.setEnabled(false);
            myRBUseRSpecGem.setSelected(false);
            myRBInstallRSpecPlugin.setSelected(true); // "install rspec plugin" , we prefer to install plugin at  this moment

            //check gem is installed.
            final boolean gemExists = RSpecUtil.checkIfRSpecGemExists(sdk);
            if (!gemExists) {
                myRBUseRSpecGem.setText(RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem",
                                        RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem.version.not.installed")));
            } else {
                myECInstallRSpecGem.setHanlders(
                        // Sets loading message instead of label
                        new Runnable() {
                            public void run() {
                                myRBUseRSpecGem.setText(RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem", ""));
                            }
                        },
                        // Evaluates RSpec Gem version
                        new Function<Object, String>() {
                            public String fun(final Object o) {
                                return RSpecUtil.getRSpecGemVersion(sdk, false, new Function<Object, Boolean>() {
                                    public Boolean fun(final Object o) {
                                        // Cancel process if form was closed
                                        return myIsClosed;
                                    }
                                });
                            }
                        },
                        // Sets found RSpec gem version, enables controls
                        new Function<String, Object>() {
                            public Object fun(final String vers) {
                                myRSpecGemVersion = vers;
                                myUseGemAllowed = !TextUtil.isEmpty(vers);

                                myRBUseRSpecGem.setText(RBundle.message("module.settings.dialog.select.test.spec.rb.use.rspec.gem", vers));
                                if (myUseGemAllowed) {
                                    myRBUseRSpecGem.setEnabled(myCBInstallOrUseRSpec.isSelected());
                                }
                                
                                return null;
                            }
                        },
                        RBundle.message("common.msgs.fetching.version"));
            }

            //if install rspec is endabled dialog should
            //automatically set the same settings for RSpec and RSpecRails
            final boolean installOrUseRSpec = myCBInstallOrUseRSpec.isSelected();
            myRBRSpecRailsLastest.setEnabled(!installOrUseRSpec);
            myRBRSpecRailsPluginAny.setEnabled(!installOrUseRSpec);
            myRBRSpecRailsPluginTrunk.setEnabled(!installOrUseRSpec);
            myTFRSpecRailsArgs.setEnabled(!installOrUseRSpec);

            final boolean useRSpecGem = myRBUseRSpecGem.isSelected();
            if (!useRSpecGem) {
                switch (settings.wizardRailsFacetRSpecPluginSrcType) {
                    case LATEST:
                        myRBRSpecPluginLastest.setSelected(true);
                        break;
                    case SPECIFIC:
                        myRBRSpecPluginAny.setSelected(true);
                        break;
                    case TRUNK:
                        myRBRSpecPluginTrunk.setSelected(true);
                        break;
                }
            }
            rspecSetupOnChanged(useRSpecGem);

            updateUI();

            //Starts RSpecGem evaluator
            if (gemExists) {
                myECInstallRSpecGem.run();
            }
        }
    }

    @Nullable
    private Sdk getSdk() {
        final TabbedSettingsContext tabbedSettingsContext = getContext();
        return tabbedSettingsContext == null ? null : tabbedSettingsContext.getSdk();
    }

    private void setupSVNPathLabel() {
        final Sdk  sdk = getSdk();
        if (sdk != null && !OSUtil.isSVNInExtendedLoadPath(null, sdk)) {
            myLSvnMustBeInPath.setVisible(true);
            final String text = RBundle.message("module.wizard.test.framework.rspec.svn.note");
            myLSvnMustBeInPath.setText(text);
            if (TextUtil.isEmpty(mySvnPathTextField.getText().trim())) {
                mySvnPathTextField.setText(FileUtil.toSystemDependentName(OSUtil.getDefaultSVNPath()));
            }
            mySvnPathComponent.setVisible(true);
        } else {
            myLSvnMustBeInPath.setVisible(false);
            mySvnPathComponent.setVisible(false);
        }
    }

    private void rspecSetupOnChanged(final boolean use_gem) {
        myRBRSpecPluginTrunk.setEnabled(!use_gem);
        myRBRSpecPluginLastest.setEnabled(!use_gem);
        myRBRSpecPluginAny.setEnabled(!use_gem);
        myLabelRspecArgs.setEnabled(!use_gem);
        myTFRSpecArgs.setEnabled(!use_gem);


        if (use_gem) {
            myRBRSpecRailsPluginAny.setSelected(true);
            final String url = RSpecUtil.getRSpecRailsPluginUrl(RSpecUtil.getRSpecGemTag(myRSpecGemVersion));
            myTFRSpecRailsArgs.setText(addForceParameter(url));
        } else {
            rspecPluginSetupOnChanged(getRSpecPluginSrcType(), false);
        }
    }

    private String addForceParameter(String url) {
        return url;
        //TODO
        // It is quick fix. "-force" flag doesn't supported by old rails
        // versions, we should check it before...
        // return RailsConstants.PARAM_FORCE_OVERWRITE + " " + url;
    }

    protected RSpecApplicationSettings.SrcType getRSpecPluginSrcType() {
        if (myRBRSpecPluginTrunk.isSelected()) {
            return RSpecApplicationSettings.SrcType.TRUNK;
        }
        if (myRBRSpecPluginLastest.isSelected()) {
            return RSpecApplicationSettings.SrcType.LATEST;
        }
        return RSpecApplicationSettings.SrcType.SPECIFIC;
    }

    private void rspecPluginSetupOnChanged(final RSpecApplicationSettings.SrcType srcType, boolean clearText) {
        myRBRSpecRailsLastest.setSelected(srcType == RSpecApplicationSettings.SrcType.LATEST);
        myRBRSpecRailsPluginAny.setSelected(srcType == RSpecApplicationSettings.SrcType.SPECIFIC);
        myRBRSpecRailsPluginTrunk.setSelected(srcType == RSpecApplicationSettings.SrcType.TRUNK);

        switch (srcType) {
            case LATEST:
                myTFRSpecArgs.setText(addForceParameter(RSpecUtil.getRSpecPluginCurrentUrl()));
                myTFRSpecRailsArgs.setText(addForceParameter(RSpecUtil.getRSpecRailsPluginCurrentUrl()));
                break;
            case TRUNK:
                myTFRSpecArgs.setText(addForceParameter(RSpecUtil.getRSpecPluginTrunkUrl()));
                myTFRSpecRailsArgs.setText(addForceParameter(RSpecUtil.getRSpecRailsPluginTrunkUrl()));
                break;
            case SPECIFIC:
                if (clearText) {
                    myTFRSpecArgs.setText(addForceParameter(RSpecUtil.getRSpecPluginXYZUrl()));
                    myTFRSpecRailsArgs.setText(addForceParameter(RSpecUtil.getRSpecRailsPluginXYZUrl()));
//TODO improve
//                    final String installationArgs = RApplicationSettings.getInstance().railsRSpecPluginInstallationArgs;
//                    if (!TextUtil.isEmpty(installationArgs)) {
//                        myTFRSpecArgs.setText(installationArgs);
//                        setRSpecRailsPluginTextByRSpecPluginText();
//                    }
                } else {
                    setRSpecRailsPluginTextByRSpecPluginText();
                }
                break;
        }
    }

    private void setRSpecRailsPluginTextByRSpecPluginText() {
        final String args = myTFRSpecArgs.getText().trim();
        final String pluginNamePart = RSpecUtil.SVN_PATH_SEPARATOR + RSpecUtil.RSPEC_PLUGIN_NAME;
        final String pattern = pluginNamePart + "( *|$)";
        if (args.endsWith(pluginNamePart) || args.contains(pluginNamePart)) {
            myTFRSpecRailsArgs.setText(args.replaceAll(pattern, RSpecUtil.SVN_PATH_SEPARATOR + RSpecUtil.RSPEC_RAILS_PLUGIN_NAME + " ").trim());
        }

//        if (myRBRSpecPluginLastest.isSelected() && !TextUtil.isEmpty(args)) {
//            RApplicationSettings.getInstance().railsRSpecPluginInstallationArgs = args;
//        }
    }


    @NotNull
    public RailsWizardSettingsHolder.RSpecConfiguration getConf() {
        return new RailsWizardSettingsHolder.RSpecConfiguration() {
            public boolean enableRSpecSupport() {
                return myCBInstallOrUseRSpec.isSelected();
            }

            public boolean enableRSpecRailsSupport() {
                return myCBInstallRSpecRails.isSelected();
            }

            public boolean shouldInstallRSpecPlugin() {
                return myRBInstallRSpecPlugin.isSelected();
            }

            public boolean shouldInstallRSpecRailsPlugin() {
                return enableRSpecRailsSupport();
            }

            @NotNull
            public String getRSpecArgs() {
                return myTFRSpecArgs.getText().trim();
            }

            @NotNull
            public String getRSpecRailsArgs() {
                return myTFRSpecRailsArgs.getText().trim();
            }

            public String getSvnPath() {
                return FileUtil.toSystemIndependentName(mySvnPathTextField.getText().trim());
            }
        };
    }

    /**
     * Create RSpec configuration using settings from previously choosen settings
     * @return Configuration
     */
    public static RailsWizardSettingsHolder.RSpecConfiguration getStoredDefaultConf() {
        final RSpecApplicationSettings settings = RSpecApplicationSettings.getInstance();

        final boolean enableRSpecSupport =
                settings.wizardRailsFacetIsRSpecEnabled
                && !TextUtil.isEmpty(settings.wizardRailsFacetRSpecArgs);

        final boolean enableRSpecRailsSupport = settings.wizardRailsFacetIsRSpecRailsEnabled
                && !TextUtil.isEmpty(settings.wizardRailsFacetRSpecRailsArgs);

        final String rSpecArgs = RSpecApplicationSettings.getInstance().wizardRailsFacetRSpecArgs;
        final String rSpecRailsArgs = RSpecApplicationSettings.getInstance().wizardRailsFacetRSpecRailsArgs;

        return new RailsWizardSettingsHolder.RSpecConfiguration() {
            public boolean enableRSpecSupport() {
                return enableRSpecSupport;
            }

            public boolean enableRSpecRailsSupport() {
                return enableRSpecRailsSupport;
            }

            public boolean shouldInstallRSpecPlugin() {
                return true;
            }

            public boolean shouldInstallRSpecRailsPlugin() {
                return enableRSpecRailsSupport();
            }

            @NotNull
            public String getRSpecArgs() {
                return rSpecArgs;
            }

            @NotNull
            public String getRSpecRailsArgs() {
                return rSpecRailsArgs;
            }

            @Nullable
            public String getSvnPath() {
                return null;
            }
        };
    }
}
