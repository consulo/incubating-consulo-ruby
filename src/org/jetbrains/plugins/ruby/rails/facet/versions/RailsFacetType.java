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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationImpl;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.impl.autodetecting.FacetDetectorRegistryEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */

/**
 * Package private. You should use general class - BaseRailsFacetType
 */
class RailsFacetType extends BaseRailsFacetType<RailsFacet> {
    public static final RailsFacetType INSTANCE = new RailsFacetType();

    public static void load() {
        final FacetTypeRegistry registry = FacetTypeRegistry.getInstance();
        if (registry.findFacetType(RailsFacet.ID.toString()) == null) {
            registry.registerFacetType(INSTANCE);
        }
    }

    public RailsFacetType() {
        super(RailsFacet.ID, RailsFacet.ID.toString(), null);
    }

    public BaseRailsFacetConfiguration createDefaultConfiguration() {
        return new BaseRailsFacetConfigurationImpl();
    }

    public RailsFacet createFacet(@NotNull final Module module, final String name,
                                  @NotNull final BaseRailsFacetConfiguration configuration,
                                  @Nullable final Facet underlyingFacet) {
        return new RailsFacet(this, module, name, configuration, underlyingFacet);
    }


    protected void registerDetectorForWizard(final FacetDetectorRegistryEx<BaseRailsFacetConfiguration> detectorRegistry,
                                             final VirtualFileFilter railsFacetFilter,
                                             final FacetDetector<VirtualFile, BaseRailsFacetConfiguration> facetDetector) {
        detectorRegistry.registerDetectorForWizard(RubyFileType.RUBY, railsFacetFilter, facetDetector);
    }
}
