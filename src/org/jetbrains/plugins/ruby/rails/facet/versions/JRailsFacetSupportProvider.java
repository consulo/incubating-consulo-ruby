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

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */

public class JRailsFacetSupportProvider /*extends BaseRailsFacetSupportProvider<JRailsFacet> */
{
 /*   public JRailsFacetSupportProvider() {
		super(JRailsFacetType.INSTANCE);
    }

    @Nullable
    protected Sdk getSDKFromMagic(final JComponent component) {
        final JComboBox comboBox = NiiChAVOUtil.getJRubyFacetSdkChooserMagic(component);
        if (comboBox != null) {
            // JRails facet
            return (Sdk) comboBox.getSelectedItem();
        }
        return null;
    }

    protected void registorErrorMsgUpdater(@NotNull final JButton editSettingsButton,
                                           @NotNull final FacetValidatorsManager validatorsManager,
                                           @NotNull final FacetEditorValidator sdkValidator) {
        NiiChAVOUtil.addOnComponentFirstTimeEnabledHandler(editSettingsButton, new Runnable() {
            public void run() {
                final JComponent watchedComponent = NiiChAVOUtil.getJRubyFacetSdkChooserMagic(editSettingsButton);

                assert watchedComponent != null; //"Can't be null for Rails Facet on JRuby"

                validatorsManager.registerValidator(sdkValidator, watchedComponent);
                validatorsManager.validate();
            }
        });

        //Revalidate on enable/disable
        NiiChAVOUtil.addValidateOnEnabledOrDisabledHandler(editSettingsButton, validatorsManager, null);
    }

    //TODO
//    public static void runOnModuleIsInitialized(final Module module, final Runnable runnable) {
//        if (!module.getProject().isInitialized()) {
//            StartupManager.getInstance(module.getProject()).runWhenProjectIsInitialized(runnable);
//        } else if (module.isLoaded()) {
//            runnable.run();
//        } else {
//            final MessageBusConnection connection = module.getProject().getMessageBus().connect();
//            connection.subscribe(ProjectTopics.PROJECT_ROOTS, new ModuleRootListener() {
//                public void beforeRootsChange(final ModuleRootEvent event) {
//                }
//
//                public void rootsChanged(final ModuleRootEvent event) {
//                    if (module.isLoaded()) {
//                        connection.disconnect();
//                        runnable.run();
//                    } else if (module.isDisposed()) {
//                        connection.disconnect();
//                    }
//                }
//            });
//        }
//    }
       */
}