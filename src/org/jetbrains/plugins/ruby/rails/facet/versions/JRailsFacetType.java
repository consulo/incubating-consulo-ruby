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

import com.intellij.facet.Facet;
import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.impl.autodetecting.FacetDetectorRegistryEx;
import com.intellij.facet.impl.autodetecting.UnderlyingFacetSelector;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.patterns.impl.MatchingContext;
import com.intellij.patterns.impl.Pattern;
import com.intellij.patterns.impl.TraverseContext;
import com.intellij.patterns.impl.VirtualFilePattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetConfiguration;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetType;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationImpl;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */

/**
 * Package private. You should use general class - BaseRailsFacetType
 */
class JRailsFacetType extends BaseRailsFacetType<JRailsFacet> {
    public static final JRailsFacetType INSTANCE = new JRailsFacetType();

    public static void load() {
        final FacetTypeRegistry registry = FacetTypeRegistry.getInstance();
        if (registry.findFacetType(JRailsFacet.ID.toString()) == null) {
            registry.registerFacetType(INSTANCE);
        }
    }

    public JRailsFacetType() {
        super(JRailsFacet.ID, JRailsFacet.ID.toString(), JRubyFacetType.INSTANCE.getId());
    }

    public BaseRailsFacetConfiguration createDefaultConfiguration() {
        return new BaseRailsFacetConfigurationImpl();
    }

    public JRailsFacet createFacet(@NotNull final Module module, final String name,
                                  @NotNull final BaseRailsFacetConfiguration configuration,
                                  @Nullable final Facet underlyingFacet) {
        return new JRailsFacet(this, module, name, configuration, underlyingFacet);
    }

    public boolean isSuitableModuleType(@NotNull final ModuleType moduleType) {
        return BaseRailsFacetType.isJRailsFacetSuitableModuleType(moduleType);
    }

    protected void registerDetectorForWizard(final FacetDetectorRegistryEx<BaseRailsFacetConfiguration> detectorRegistry, 
                                           final VirtualFileFilter railsFacetFilter,
                                           final FacetDetector<VirtualFile, BaseRailsFacetConfiguration> facetDetector) {
        detectorRegistry.registerSubFacetDetectorForWizard(
                RubyFileType.RUBY,
                new VirtualFilePattern().with(new Pattern.PatternCondition<VirtualFile>() {
                    protected boolean accepts(@NotNull final VirtualFile virtualFile,
                                              final MatchingContext matchingContext,
                                              @NotNull final TraverseContext traverseContext) {
                        
                        return railsFacetFilter.accept(virtualFile);
                    }
                }),
                facetDetector,
                new UnderlyingFacetSelector<VirtualFile, FacetConfiguration>() {
                    public FacetConfiguration selectUnderlyingFacet(final VirtualFile source, final Collection<FacetConfiguration> underlyingFacetsConf) {
                        for (FacetConfiguration configuration : underlyingFacetsConf) {
                            if (configuration instanceof JRubyFacetConfiguration) {
                                return configuration;
                            }
                        }
                        return null;
                    }
                });
    }
}