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

import com.intellij.facet.FacetManagerImpl;
import com.intellij.facet.FacetTypeId;
import com.intellij.ide.impl.convert.JDomConvertingUtil;
import com.intellij.openapi.util.Condition;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;
import org.jetbrains.plugins.ruby.ruby.roots.RModuleContentRootManager;
import org.jetbrains.plugins.ruby.RComponents;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 9, 2008
 */
public class RailsModule_BackwardConversionUtil {
    public static void convertRootElement(Element root, String moduleName) {
        final Element facetManager = JDomConvertingUtil.findComponent(root, FacetManagerImpl.COMPONENT_NAME);
        if (facetManager == null) return;

        //noinspection unchecked
        final List<Element> facetsList = facetManager.getChildren(FacetManagerImpl.FACET_ELEMENT);
        for (Element facetElement : new ArrayList<Element>(facetsList)) {
            final String facetType = facetElement.getAttributeValue(FacetManagerImpl.TYPE_ATTRIBUTE);
            final String moduleType = getRailsModuleTypeByFacetType(facetType);
            if (moduleType != null) {
                final FacetTypeId<? extends BaseRailsFacet> facetTypeId = RailsModule_ConvertingUtil.getFacetTypeByModuleType(moduleType);
                convertFacetElement(facetManager, facetElement, moduleType, facetTypeId, root, moduleName);

                return;
            }
        }
    }

    private static void convertFacetElement(final Element facetManagerElement,
                                            final Element facetElement,
                                            final String moduleType,
                                            final @NotNull FacetTypeId<? extends BaseRailsFacet> facetTypeId,
                                            final Element root,
                                            final String moduleName) {

        root.setAttribute(RailsModule_JDomConstants.MODULE_TYPE_ATTRIBUTE, moduleType);

        // RModuleSettingsStorage - ok
        // NewModuleRootManager - ok

        // RailsModuleContentRootManager
        final Element oldRootManager = new Element(RailsModule_JDomConstants.COMPONENT_ELEMENT);
        oldRootManager.setAttribute(RailsModule_JDomConstants.NAME_ATTRUBUTE, RailsModule_JDomConstants.RAILS_MODULE_CONTENT_ROOT_MANAGER);

        final Element rubyRootManager = JDomConvertingUtil.findComponent(root, RModuleContentRootManager.class.getSimpleName());
        JDomConvertingUtil.copyChildren(rubyRootManager, oldRootManager, new Condition<Element>() {
            public boolean value(final Element element) {
                return element.getName().equals(RailsModule_JDomConstants.RMCRM_TAG_TEST_URLS);
            }
        });
        final Element railsViewFolderManager = JDomConvertingUtil.findComponent(root, RComponents.RAILS_VIEW_FOLDERS_MANAGER);
        JDomConvertingUtil.copyChildren(railsViewFolderManager, oldRootManager, new Condition<Element>() {
            public boolean value(final Element element) {
                return element.getName().equals(RailsModule_JDomConstants.RMCRM_TAG_USER_URLS);
            }
        });
        
        root.removeContent(rubyRootManager);
        root.removeContent(railsViewFolderManager);
        root.addContent(oldRootManager);

        //remove facetManager - Rails module doesn't work with facets
        root.removeContent(facetManagerElement);
    }


    @Nullable
    private static String getRailsModuleTypeByFacetType(String facetType) {
        if (RailsModule_ConvertingContext.getStringId(BaseRailsFacet.getRailsFacetID()).equals(facetType)) {
            return RailsModule_JDomConstants.RAILS_MODULE_TYPE;
        }
        return null;
    }
}
