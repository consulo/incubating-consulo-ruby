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

package org.jetbrains.plugins.ruby.jruby.facet.ui;

import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.util.Function;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 4, 2008
 */

/**
 * Some UI hacks for JRuby Facet
 */
public class NiiChAVOUtil {
    private static final Logger LOG = Logger.getInstance(NiiChAVOUtil.class.getName());

    //For JRuby facet
    @NonNls
    private static final String JRUBY_FACET_SDK_SETTING = "JRUBY_FACET_SDK_SETTING";
    @NonNls
    private static final String JRUBY_FACET_SDK_CHOOSER_SETTING = "JRUBY_FACET_SDK_CHOOSER_SETTING";

    //Only for Rails Facet(not JRails)
    @NonNls
    private static final String RAILS_FACET_SELECTED = "RAILS_FACET_SELECTED";
    @NonNls
    private static final String RAILS_FACET_VALIDATORS_MANAGER = "RAILS_FACET_VALIDATORS_MANAGER";

    @NonNls
    private static final String SWING_ENABLED_PROPERTY_NAME = "enabled";

    public static void addOnComponentFirstTimeEnabledHandler(@NotNull final JComponent component,
                                                             @NotNull final Runnable closure) {
        component.addPropertyChangeListener(new PropertyChangeListener() {
            private boolean firstInvocationFlag = true;

            public void propertyChange(final PropertyChangeEvent evt) {
                if (firstInvocationFlag
                        && NiiChAVOUtil.SWING_ENABLED_PROPERTY_NAME.equals(evt.getPropertyName())
                        && (Boolean)evt.getNewValue()) {

                    // Closure
                    closure.run();

                    firstInvocationFlag = false;
                }
            }
        });
    }

    /**
     * Triggers on component enabled / disabled and validates facet validator.
     * @param component Component to find UI hack
     * @param manager Validators manager
     * @param facetPropertiesSetterFun Function thas sets required properties for Rails or JRails Facet to perform UI hacks.
     */
    public static void addValidateOnEnabledOrDisabledHandler(@NotNull final JComponent component,
                                                             @NotNull final FacetValidatorsManager manager,
                                                             @Nullable final Function<Boolean, Object> facetPropertiesSetterFun) {
        component.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(final PropertyChangeEvent evt) {
                if (SWING_ENABLED_PROPERTY_NAME.equals(evt.getPropertyName())) {
                    manager.validate();

                    //Puts magic when it is possible. In fact we can't put magic only
                    //in the beginning of component live, we can ignore it!
                    if (facetPropertiesSetterFun != null && findJRubyFacetUIMagicStorageContainer(component) != null) {
                        facetPropertiesSetterFun.fun((Boolean)evt.getNewValue());
                    }
                }
            }
        });
    }

    @Nullable
    public static JComboBox getJRubyFacetSdkChooserMagic(@NotNull final JComponent target) {
        return getPropertyMagic(target, JRUBY_FACET_SDK_CHOOSER_SETTING);
    }

    @Nullable
    public static ProjectJdk getJRubyFacetSdkMagic(@NotNull final JComponent target) {
        return getPropertyMagic(target, JRUBY_FACET_SDK_SETTING);
    }

    public static void putJRubyFacetSdkMagic(@NotNull final JComponent target,
                                             @Nullable final ProjectJdk sdk) {
        findJRubyFacetUIMagicStorage(target).putClientProperty(JRUBY_FACET_SDK_SETTING, sdk);
    }
    public static void putJRubyFacetUIMagic(@NotNull final JComboBox sdkChooser) {
        findJRubyFacetUIMagicStorage(sdkChooser).putClientProperty(JRUBY_FACET_SDK_CHOOSER_SETTING, sdkChooser);
    }


    /**
     * For Rails Facet only(not for JRails one)
     * @param target Component
     * @return Validator
     */
    @Nullable
    public static FacetValidatorsManager getRailsFacetValidatorsManagerMagic(@NotNull final JComponent target) {
        return getPropertyMagic(target, RAILS_FACET_VALIDATORS_MANAGER);
    }

    /**
     * For Rails Facet only(not for JRails one)
     * @param target Component
     * @return true, if is facet enabled
     */
    @Nullable
    public static Boolean isRailsFacetEnabledMagic(@NotNull final JComponent target) {
        final Boolean value =  getPropertyMagic(target, RAILS_FACET_SELECTED);
        return value == null ? false : value;
    }

    /**
     * For Rails Facet only(not for JRails one)
     * @param component Component
     */
    public static void revalidateRailsFacetSettings(@NotNull final JComponent component) {
        final FacetValidatorsManager validatorsManger = getRailsFacetValidatorsManagerMagic(component);
        if (validatorsManger != null) {
            validatorsManger.validate();
        }
    }

    /**
     * Saves FacetValidators to revalidate component after changinh SDK for [Ruby] module wizard.
     * Only for Rails Facet, not for JRails!
     * @param target Component
     * @param validatorsManager Facets validator
     */
    public static void putRailsFacetValidatorsManagerMagic(@NotNull final JComponent target,
                                                    FacetValidatorsManager validatorsManager) {
        findJRubyFacetUIMagicStorage(target).putClientProperty(RAILS_FACET_VALIDATORS_MANAGER, validatorsManager);
    }

    /**
     * Saves Rails(not JRails) facet isEnabled state.
     * Is Used for [Ruby] module wizard to disable other pure Ruby steps.
     * @param target Component
     * @param enabled is facet enabled
     */
    public static void putRailsFacetEnabledMagic(@NotNull final JComponent target,
                                                  boolean enabled) {
        findJRubyFacetUIMagicStorage(target).putClientProperty(RAILS_FACET_SELECTED, enabled);
    }

    private static JComponent findJRubyFacetUIMagicStorage(@NotNull final Container target) {
        JDialog dialog = (JDialog)findJRubyFacetUIMagicStorageContainer(target);

        LOG.assertTrue(dialog != null, "Unable to find Dialog Root!");
        return dialog.getRootPane();
    }

    private static Container findJRubyFacetUIMagicStorageContainer(@NotNull final Container target) {
        Container current = target;
        while (current != null && !(current instanceof JDialog)) {
            current = current.getParent();
        }
        return current;
    }

    @Nullable
    private static <T> T getPropertyMagic(@NotNull final JComponent target, final String propertyName) {
        final Object value = findJRubyFacetUIMagicStorage(target).getClientProperty(propertyName);
        if (value != null) {
            try {
                //noinspection unchecked
                return (T)value;
            } catch (ClassCastException e) {
                // shouldn't happen!
                LOG.error(e);
            }
        }
        return null;
    }
}
