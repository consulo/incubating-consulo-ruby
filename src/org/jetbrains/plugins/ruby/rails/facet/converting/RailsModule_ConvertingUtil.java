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

package org.jetbrains.plugins.ruby.rails.facet.converting;

import com.intellij.application.options.PathMacrosImpl;
import com.intellij.facet.FacetManagerImpl;
import com.intellij.facet.FacetTypeId;
import com.intellij.ide.impl.convert.JDomConvertingUtil;
import com.intellij.openapi.util.Condition;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RComponents;
import static org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationExternalizer.*;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacetType;
import org.jetbrains.plugins.ruby.ruby.roots.RModuleContentRootManager;
import org.jetbrains.plugins.ruby.settings.SettingsExternalizer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_ConvertingUtil {
    @Nullable
    public static FacetTypeId<? extends BaseRailsFacet> getFacetType(final Element root) {
        final String type = root.getAttributeValue(RailsModule_JDomConstants.MODULE_TYPE_ATTRIBUTE);
        return getFacetTypeByModuleType(type);
    }

    @Nullable
    public static FacetTypeId<? extends BaseRailsFacet> getFacetTypeByModuleType(final String type) {
        if (RailsModule_JDomConstants.RAILS_MODULE_TYPE.equals(type)) {
            return BaseRailsFacet.getRailsFacetID();
        }
        return null;
    }


    public static void convertRootElement(final Element root,
                                          final String moduleName,
                                          final RailsModule_ConvertingContext context) {
        FacetTypeId<? extends BaseRailsFacet> type = context.getFacetType(moduleName);
        if (type == null) {
            return;
        }

        root.setAttribute(RailsModule_JDomConstants.MODULE_TYPE_ATTRIBUTE, RailsModule_JDomConstants.RUBY_MODULE_TYPE);

        // RModuleSettingsStorage - ok
        // NewModuleRootManager - ok

        final Element oldRootManager = JDomConvertingUtil.findComponent(root, RailsModule_JDomConstants.RAILS_MODULE_CONTENT_ROOT_MANAGER);
        if (oldRootManager != null) {
            // RailsViewFoldersManager -> add USER_URLS from RModuleContentRootManager
            final Element railsViewFolderManager = new Element(RailsModule_JDomConstants.COMPONENT_ELEMENT);
            railsViewFolderManager.setAttribute(RailsModule_JDomConstants.NAME_ATTRUBUTE, RComponents.RAILS_VIEW_FOLDERS_MANAGER);
            JDomConvertingUtil.copyChildren(oldRootManager, railsViewFolderManager, new Condition<Element>() {
                public boolean value(final Element element) {
                    return element.getName().equals(RailsModule_JDomConstants.RMCRM_TAG_USER_URLS);
                }
            });

            // RModuleContentRootManager -> remove USER_URLS
            final Element rubyRootManager = new Element(RailsModule_JDomConstants.COMPONENT_ELEMENT);
            rubyRootManager.setAttribute(RailsModule_JDomConstants.NAME_ATTRUBUTE, RModuleContentRootManager.class.getSimpleName());
            JDomConvertingUtil.copyChildren(oldRootManager, rubyRootManager, new Condition<Element>() {
                public boolean value(final Element element) {
                    return element.getName().equals(RailsModule_JDomConstants.RMCRM_TAG_TEST_URLS);
                }
            });

            root.removeContent(oldRootManager);
            root.addContent(rubyRootManager);
            root.addContent(railsViewFolderManager);
        }

        // rails facet configuration
        final Element facetConfiguration = new Element(FacetManagerImpl.CONFIGURATION_ELEMENT);
        final Element facetAppHome = new Element(RAILS_FACET_CONFIG_ID);
        facetAppHome.setAttribute(SettingsExternalizer.NAME, RAILS_FACET_APPLIATION_ROOT);
        facetAppHome.setAttribute(SettingsExternalizer.VALUE, "$" + PathMacrosImpl.MODULE_DIR_MACRO_NAME + "$");

        final Element shouldUseRspecPlugin = new Element(RAILS_FACET_CONFIG_ID);
        shouldUseRspecPlugin.setAttribute(SettingsExternalizer.NAME, SHOULD_USE_RSPEC_PLUGIN);
        shouldUseRspecPlugin.setAttribute(SettingsExternalizer.VALUE, Boolean.FALSE.toString());

        facetConfiguration.addContent(facetAppHome);
        facetConfiguration.addContent(shouldUseRspecPlugin);


        // rails facet element
        final Element facetElement = new Element(FacetManagerImpl.FACET_ELEMENT);
        facetElement.setAttribute(FacetManagerImpl.TYPE_ATTRIBUTE, RailsModule_ConvertingContext.getStringId(type));
        facetElement.setAttribute(FacetManagerImpl.NAME_ATTRIBUTE, BaseRailsFacetType.PRESENTABLE_NAME);
        facetElement.addContent(facetConfiguration);

        // facet manager
        Element facetManager = JDomConvertingUtil.findComponent(root, FacetManagerImpl.COMPONENT_NAME);
        if (facetManager == null) {
            facetManager = new Element(RailsModule_JDomConstants.COMPONENT_ELEMENT);
            facetManager.setAttribute(RailsModule_JDomConstants.NAME_ATTRUBUTE, FacetManagerImpl.COMPONENT_NAME);
            root.addContent(facetManager);
        }
        facetManager.addContent(facetElement);
    }
}
