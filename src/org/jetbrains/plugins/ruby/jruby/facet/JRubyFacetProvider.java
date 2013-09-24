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

package org.jetbrains.plugins.ruby.jruby.facet;

import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetType;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.facet.impl.FacetUtil;
import com.intellij.ide.util.newProjectWizard.FrameworkSupportConfigurable;
import com.intellij.ide.util.newProjectWizard.FrameworkSupportProvider;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.libraries.Library;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.ui.JRubySDKsComboboxWithBrowseButton;
import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman.Chernyatchik
 * @date: Jan 21, 2008
 */
public class JRubyFacetProvider extends FrameworkSupportProvider {

    private static final String FACET_SUPPORT_PREFIX = "facet:";

    public JRubyFacetProvider() {
        super(getProviderId(JRubyFacetType.INSTANCE), JRubyFacetType.INSTANCE.getPresentableName());
    }

    @NotNull
    public FrameworkSupportConfigurable createConfigurable() {
        return new JRubyFrameworkConfigurable();
    }

    protected static String getProviderId(final FacetType facetType) {
        return FACET_SUPPORT_PREFIX + facetType.getStringId();
    }

    @Nullable
    public String getUnderlyingFrameworkId() {
        return null;
    }

    public boolean isEnabledForModuleType(@NotNull final ModuleType moduleType) {
        return JRubyFacetType.INSTANCE.isSuitableModuleType(moduleType);
    }

    public boolean isSupportAlreadyAdded(@NotNull final Module module) {
        return !FacetManager.getInstance(module).getFacetsByType(JRubyFacetType.INSTANCE.getId()).isEmpty();
    }

    private static class JRubyFrameworkConfigurable extends FrameworkSupportConfigurable {
        private final JRubySDKsComboboxWithBrowseButton mySdksComboBox;
        protected Module myModule;
        protected ProjectJdk mySelectedSdk;

        public JRubyFrameworkConfigurable() {
//setting skds
            mySdksComboBox = new JRubySDKsComboboxWithBrowseButton();
            mySdksComboBox.addComboboxActionListener(new ActionListener() {
                public void actionPerformed(final ActionEvent e) {
                    mySelectedSdk = getSelectedSdk();
                }
            });

            NiiChAVOUtil.addOnComponentFirstTimeEnabledHandler(mySdksComboBox, new Runnable() {
                public void run() {
                    NiiChAVOUtil.putJRubyFacetUIMagic(mySdksComboBox.getComboBox());
                }
            });

            mySdksComboBox.rebuildSdksListAndSelectSdk(null);
        }

        @Nullable
        public JComponent getComponent() {
            return mySdksComboBox;
        }

        public ProjectJdk getSelectedSdk() {
            final Object selectedObject = mySdksComboBox.getComboBox().getSelectedItem();
            return selectedObject instanceof ProjectJdk ? (ProjectJdk) selectedObject : null;
        }

        public void addSupport(final Module module, final ModifiableRootModel model, final @Nullable Library library) {
            ModifiableFacetModel facetModel = FacetManager.getInstance(module).createModifiableModel();
            JRubyFacet facet = FacetUtil.createFacet(JRubyFacetType.INSTANCE, module, null);
            facet.getConfiguration().setSdk(mySelectedSdk);
            facetModel.addFacet(facet);
            facetModel.commit();
        }
    }
}
