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

import com.intellij.facet.ui.FacetEditorValidator;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.ui.NiiChAVOUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */

public class RailsFacetSupportProvider extends BaseRailsFacetSupportProvider<RailsFacet> {
    public RailsFacetSupportProvider() {
        super(RailsFacetType.INSTANCE);
    }

    @Nullable
    protected ProjectJdk getSDKFromMagic(final JComponent component) {
        return NiiChAVOUtil.getJRubyFacetSdkMagic(component);
    }

    protected void registorErrorMsgUpdater(@NotNull final JButton editSettingsButton,
                                           @NotNull final FacetValidatorsManager validatorsManager,
                                           @NotNull final FacetEditorValidator sdkValidator) {
        NiiChAVOUtil.addOnComponentFirstTimeEnabledHandler(editSettingsButton, new Runnable() {
            public void run() {
                validatorsManager.registerValidator(sdkValidator);

                validatorsManager.validate();
            }
        });

        //Revalidate on enable/disable and put properties to perform hacks.
        NiiChAVOUtil.addValidateOnEnabledOrDisabledHandler(editSettingsButton, validatorsManager,  new Function<Boolean, Object>() {
            public Object fun(final Boolean isEnabled) {
                NiiChAVOUtil.putRailsFacetEnabledMagic(editSettingsButton, isEnabled);
                NiiChAVOUtil.putRailsFacetValidatorsManagerMagic(editSettingsButton, validatorsManager);

                return null;
            }
        });
    }
}
