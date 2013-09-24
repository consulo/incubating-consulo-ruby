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

import com.intellij.facet.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationLowLevel;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 2, 2008
 */
public class BaseRailsFacetTestUtil {
    public static BaseRailsFacetType getRailsFacetType() {
        return RailsFacetType.INSTANCE;
    }

    public static BaseRailsFacetType getJRailsFacetType() {
        return JRailsFacetType.INSTANCE;
    }

    public static void addRailsToModule(final Module module, 
                                        final String rails_home_path) {

        final BaseRailsFacetType type;
        final Facet underlyingFacet;
        if (RubyUtil.isRubyModuleType(module)) {
            type = getRailsFacetType();
            underlyingFacet = null;
        } else {
            type = getJRailsFacetType();
            underlyingFacet = JRubyFacet.getInstance(module);
        }
        final FacetConfiguration configuration = type.createDefaultConfiguration();
        ((BaseRailsFacetConfigurationLowLevel)configuration).setRailsApplicationRootPath(rails_home_path);

        final String facetName = type.getDefaultFacetName();

        ApplicationManager.getApplication().runWriteAction(new Runnable() {
            public void run() {
                final Facet facet = FacetManagerImpl.createFacet(type, module, facetName, configuration, underlyingFacet);

                final ModifiableFacetModel model = FacetManager.getInstance(module).createModifiableModel();
                model.addFacet(facet);
                model.commit();
            }
        });
    }
}
