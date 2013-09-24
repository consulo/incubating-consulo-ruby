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

import java.util.Collection;

import javax.swing.Icon;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.RailsIcons;
import org.jetbrains.plugins.ruby.rails.RailsUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationLowLevel;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.facet.autodetecting.FacetDetector;
import com.intellij.facet.autodetecting.FacetDetectorRegistry;
import com.intellij.facet.impl.autodetecting.FacetDetectorRegistryEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileFilter;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */
public abstract class BaseRailsFacetType<F extends BaseRailsFacet> extends FacetType<F, BaseRailsFacetConfiguration> {
    public static final String PRESENTABLE_NAME = RBundle.message("rails.facet");

    public static void load() {
        RailsFacetType.load();
        JRailsFacetType.load();
    }

    protected void registerDetectorForWizard(final FacetDetectorRegistryEx<BaseRailsFacetConfiguration> detectorRegistry,
                                           final VirtualFileFilter railsFacetFilter,
                                           final FacetDetector<VirtualFile, BaseRailsFacetConfiguration> facetDetector)
	{};


    protected BaseRailsFacetType(@NotNull final FacetTypeId<F> baseRailsFacetFacetTypeId,
                                 @NotNull final String stringId,
                                 @Nullable final FacetTypeId underlyingFacetType) {
        super(baseRailsFacetFacetTypeId, stringId, PRESENTABLE_NAME, underlyingFacetType);
    }



    public Icon getIcon() {
        return RailsIcons.RAILS_SMALL;
    }

    public void registerDetectors(@NotNull final FacetDetectorRegistry<BaseRailsFacetConfiguration> registry) {
        final FacetDetectorRegistryEx<BaseRailsFacetConfiguration> detectorRegistry = (FacetDetectorRegistryEx<BaseRailsFacetConfiguration>) registry;

        final VirtualFileFilter railsFacetFilter = new VirtualFileFilter(){
            public boolean accept(final VirtualFile virtualFile) {

                return isRailsAppBootFile(virtualFile);
            }
        };

        //checks if file is boot.rb of Rails application and module has JRuby support
        final Condition<PsiFile> condition = new Condition<PsiFile>() {
            public boolean value(PsiFile psiFile) {
                final VirtualFile bootFileCandidate = psiFile.getVirtualFile();
                if (bootFileCandidate == null) {
                    return false;
                }
                final Module module = ModuleUtil.findModuleForFile(bootFileCandidate, psiFile.getProject());
                return  module != null
                        && JRubyUtil.hasJRubySupport(module)
                        //&& isSuitableModuleType(module.getModuleType())
                        && isRailsAppBootFile(bootFileCandidate);
            }
        };


        //checks if file is boot.rb of Rails application
        detectorRegistry.registerOnTheFlyDetector(RubyFileType.RUBY, railsFacetFilter, condition, new FacetDetector<PsiFile, BaseRailsFacetConfiguration>() {
            public BaseRailsFacetConfiguration detectFacet(final PsiFile psiFile,
                                                           final Collection<BaseRailsFacetConfiguration> existentFacetConfigurations) {
                final VirtualFile bootFileCandidate = psiFile.getVirtualFile();
                if (bootFileCandidate == null || !isRailsAppBootFile(bootFileCandidate)) {
                    return null;
                }
                return BaseRailsFacetType.this.detectFacet(bootFileCandidate, existentFacetConfigurations);
            }
        });

        final FacetDetector<VirtualFile, BaseRailsFacetConfiguration> facetDetector = new FacetDetector<VirtualFile, BaseRailsFacetConfiguration>() {
            public BaseRailsFacetConfiguration detectFacet(final VirtualFile environmentFile,
                                                           final Collection<BaseRailsFacetConfiguration> existentFacetConfigurations) {
                return BaseRailsFacetType.this.detectFacet(environmentFile, existentFacetConfigurations);
            }
        };
        registerDetectorForWizard(detectorRegistry, railsFacetFilter, facetDetector);
    }

    private boolean isRailsAppBootFile(@NotNull final VirtualFile virtualFile) {
        if (RailsConstants.RAILS_APP_CORE_FILES_BOOT_FILE.equals(virtualFile.getName())) {
            final VirtualFile configDir = virtualFile.getParent();
            if (configDir == null) {
                return false;
            }

            final VirtualFile railsHome = configDir.getParent();
            return railsHome != null && RailsUtil.containsRailsApp(railsHome.getPath());
        }
        return false;
    }

    private BaseRailsFacetConfiguration detectFacet(@NotNull final VirtualFile bootFile,
                                                    final Collection<BaseRailsFacetConfiguration> existentFacetConfigurations) {
        final VirtualFile configDir = bootFile.getParent();
        assert configDir != null;
        final VirtualFile railsHomeDir = configDir.getParent();
        assert railsHomeDir != null;
        
        //We allow only one JRails Facet per module!
        //We should check that directory belongs to Existing Facet
        //and return existing configuration, otherwise file doens't belong
        //to any facet, we can't create new facet so we should ignore it.
        if (!existentFacetConfigurations.isEmpty()) {
            final BaseRailsFacetConfiguration configuration = existentFacetConfigurations.iterator().next();
            //if we have detected the root of existing facet
            if (configuration.getRailsApplicationRootPath().equals(railsHomeDir.getPath())) {
                return configuration;
            } else {
                return null;
            }
        }
        final BaseRailsFacetConfigurationLowLevel conf = (BaseRailsFacetConfigurationLowLevel)createDefaultConfiguration();
        conf.setRailsApplicationRootPath(railsHomeDir.getPath());

        return conf;
    }
}