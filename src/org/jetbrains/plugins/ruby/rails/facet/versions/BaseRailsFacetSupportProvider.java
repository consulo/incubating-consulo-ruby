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

package org.jetbrains.plugins.ruby.rails.facet.versions;

import com.intellij.facet.FacetType;
import com.intellij.facet.impl.ui.FacetErrorPanel;
import com.intellij.facet.impl.ui.FacetTypeFrameworkSupportProvider;
import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.facet.ui.ValidationResult;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.ui.VerticalFlowLayout;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.rspec.rails.facet.ui.wizard.tabs.RSpecComponentsInstallerTab;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.BaseRailsFacetBuilder;
import org.jetbrains.plugins.ruby.rails.facet.RailsWizardSettingsHolderImpl;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationLowLevel;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.RailsWizardSettingsHolder;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.TabbedSettingsContext;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.TabbedSettingsContextImpl;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.TabbedSettingsDialog;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs.RailsProjectGeneratorTab;
import org.jetbrains.plugins.ruby.rails.facet.ui.wizard.ui.tabs.TabbedSdkDependSettingsEditorTab;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 15, 2008
 */
public abstract class BaseRailsFacetSupportProvider<T extends BaseRailsFacet> extends FacetTypeFrameworkSupportProvider<T> {
    @Nullable protected RailsWizardSettingsHolder mySettingsHolder;

    protected BaseRailsFacetSupportProvider(@NotNull final FacetType<T, ?> baseRailsFacetFacetType) {
        super(baseRailsFacetFacetType);
    }

    @NotNull
    public VersionConfigurable createConfigurable() {
        return new BaseRailsConfigurable(getVersions(), getDefaultVersion());
    }

    @Nullable
    protected abstract ProjectJdk getSDKFromMagic(final JComponent component);
    protected abstract void registorErrorMsgUpdater(@NotNull final JButton editSettingsButton,
                                                    @NotNull final FacetValidatorsManager validatorsManager,
                                                    @NotNull final FacetEditorValidator sdkValidator);

    protected void addSupport(final Module module, final ModifiableRootModel rootModel, final String version, @Nullable final Library library) {
        super.addSupport(module, rootModel, version, library);    //To change body of overridden methods use File | Settings | File Templates.
    }

    protected void setupConfiguration(final T facet, final ModifiableRootModel rootModel,
                                      final String version) {
        final String rootPath = facet.getDefaultRailsApplicationHomePath(rootModel);
        assert mySettingsHolder != null;

        final String rootRelativePath = mySettingsHolder.getRailsApplicationHomeDirRelativePath();
        final String appHomePath;
        if (!TextUtil.isEmpty(rootRelativePath)) {
            //noinspection ConstantConditions
            appHomePath = VirtualFileUtil.buildSystemIndependentPath(rootPath, rootRelativePath);
        } else {
            appHomePath = rootPath;
        }

        assert mySettingsHolder != null;
        //noinspection ConstantConditions
        ((BaseRailsFacetConfigurationLowLevel)facet.getConfiguration()).setSdk(mySettingsHolder.getSdk());

        //Setup SDK, etc.
        //noinspection ConstantConditions
        BaseRailsFacetBuilder.initGreenhornFacet(facet, rootModel, appHomePath, mySettingsHolder.getSdk());
     }

    /**
     * Is invoked after facet had been added(and all events were processed) and commited
     * @param facet Facet
     * @param rootModel  Modules model
     * @param version Facet version
     */
    protected void onFacetCreated(final T facet,
                                  final ModifiableRootModel rootModel,
                                  final String version) {
        super.onFacetCreated(facet, rootModel, version);

        assert mySettingsHolder != null;
        BaseRailsFacetBuilder.setupGreenhornFacet(rootModel, facet, mySettingsHolder);
    }

    /////////////////////////////////////////////////////////////////////////////
    public class BaseRailsConfigurable extends VersionConfigurable {

        private JPanel myPanel;

        public BaseRailsConfigurable(String[] versions, String defaultVersion) {
            super(versions, defaultVersion);
        }

        public JComponent getComponent() {
            if (myPanel == null) {
                // Old data
                myPanel = new JPanel(new VerticalFlowLayout(VerticalFlowLayout.TOP, 0, 0, true, true));
                final JComponent superC = super.getComponent();
                if (superC != null) {
                    myPanel.add(superC);
                }

                mySettingsHolder = RailsWizardSettingsHolderImpl.createDefaultConf();
                //noinspection ConstantConditions
                final TabbedSdkDependSettingsEditorTab[] tabs = new TabbedSdkDependSettingsEditorTab[]{
                        new RailsProjectGeneratorTab(mySettingsHolder),
                        new RSpecComponentsInstallerTab(mySettingsHolder, null)
                };

                //More settings button
                final JButton editSettingsButton = new JButton(RBundle.message("rails.facet.wizard.more.button"));
                editSettingsButton.addActionListener(new ActionListener() {
                    public void actionPerformed(final ActionEvent e) {
                        final ProjectJdk sdk = getSDKFromMagic(editSettingsButton);

                        assert mySettingsHolder != null;
                        if (mySettingsHolder.getSdk() != sdk) {
                            for (TabbedSdkDependSettingsEditorTab tab : tabs) {
                                tab.resetSdkSettings();
                            }
                        }
                        //noinspection ConstantConditions
                        mySettingsHolder.setSdk(sdk);

                        final TabbedSettingsContext context = new TabbedSettingsContextImpl(sdk);
                        TabbedSettingsDialog.showDialog(RBundle.message("rails.facet.wizard.more.dialog.title"), tabs, context);
                    }
                });

                //Error panel
                final FacetErrorPanel errorPanel = new FacetErrorPanel();
                final JComponent errorPanelComponent = errorPanel.getComponent();

                //Add more+error element
                final GridBagLayout layout = new GridBagLayout();
                final GridBagConstraints c = new GridBagConstraints();
                final JPanel panel = new JPanel();
                panel.setLayout(layout);

                c.fill = GridBagConstraints.NONE;
                c.anchor = GridBagConstraints.FIRST_LINE_START;
                c.gridy = 0;
                panel.add(editSettingsButton, c);

                c.fill = GridBagConstraints.HORIZONTAL;
                c.anchor = GridBagConstraints.FIRST_LINE_START;
                c.gridy = 1;
                panel.add(errorPanelComponent, c);

                myPanel.add(panel);


                final FacetEditorValidator sdkValidator = createSdkValidator(editSettingsButton);
                final FacetValidatorsManager validatorsManager = errorPanel.getValidatorsManager();
                registorErrorMsgUpdater(editSettingsButton, validatorsManager, sdkValidator);
            }
            return myPanel;
        }

        private FacetEditorValidator createSdkValidator(final JComponent component) {
            return new FacetEditorValidator() {
                public ValidationResult check() {
                    if (component.isEnabled()) {
                        final ProjectJdk sdk = getSDKFromMagic(component);

                        //We should save selected SDK if user doestn't press "more button"
                        assert mySettingsHolder != null;
                        mySettingsHolder.setSdk(sdk);

                        if (sdk == null) {
                            return new ValidationResult(RBundle.message("rails.facet.wizard.error.no.skd"));
                        } else if (!RailsUtil.hasRailsSupportInSDK(sdk)) {
                            return new ValidationResult(RBundle.message("rails.facet.wizard.error.skd.without.rails"));
                        }
                    }
                    return ValidationResult.OK;
                }
            };
        }
   }
}
