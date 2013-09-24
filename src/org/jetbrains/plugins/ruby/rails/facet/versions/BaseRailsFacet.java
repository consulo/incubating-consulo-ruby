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

import com.intellij.ProjectTopics;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootEvent;
import com.intellij.openapi.roots.ModuleRootListener;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.rails.facet.BaseRailsFacetBuilder;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfigurationLowLevel;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.CacheScannerFilesProvider;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Mar 13, 2008
 */
public abstract class BaseRailsFacet extends Facet<BaseRailsFacetConfiguration> {
    private CacheScannerFilesProvider myRailsAdditionalScannerProvider;

    private MessageBusConnection myConnection;

    /**
     * Doesn't change given Root Model
     * @param rootModel Root Module
     * @return Simple suggests root path for rails application. It is the first content root.
     */
    @NotNull
    public abstract String getDefaultRailsApplicationHomePath(final ModifiableRootModel rootModel);


    public BaseRailsFacet(@NotNull final FacetType facetType,
                          @NotNull final Module module,
                          final String name,
                          @NotNull final BaseRailsFacetConfiguration configuration,
                          final Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);

        /**
         * only for loading of existing module. Doesn't work for new Ruby module.
         * (sdk is unknown)
         */
         final Sdk sdk;
         if (RubyUtil.isRubyModuleType(module)) {
             //ruby module
             sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
         } else {
             //jruby facet
             assert (underlyingFacet instanceof JRubyFacet);
             sdk = ((JRubyFacet)underlyingFacet).getConfiguration().getSdk();
         }
        final BaseRailsFacetConfigurationLowLevel facetConfiguration = (BaseRailsFacetConfigurationLowLevel)this.getConfiguration();
        facetConfiguration.setSdk(sdk);


///////////////// Setup Cache Manager /////////////////////////////////////////////////
        myRailsAdditionalScannerProvider = new CacheScannerFilesProvider() {
            public void scanAndAdd(final String[] rootUrls,
                                   final Collection<VirtualFile> files,
                                   final ModuleRootManager moduleRootManager) {
                RubyVirtualFileScanner.searchAdditionalRailsFileCacheFiles(moduleRootManager, files);
            }
        };

        //If we load serialized facet, FacetModulManger(thus module) doesn't know about all
        //facets yet. Thus we can't obtain JRuby face by module.
        final RubyModuleCachesManager cacheManager;
        if (underlyingFacet != null && underlyingFacet instanceof JRubyFacet) {
            //JRuby facet
            cacheManager = ((JRubyFacet)underlyingFacet).getRubyModuleCachesManager();
        } else {
            //Ruby module
            cacheManager = RubyModuleCachesManager.getInstance(module);
        }
        cacheManager.registerScanForFilesProvider(myRailsAdditionalScannerProvider);

///////////////// Init Facet /////////////////////////////////////////////////
         BaseRailsFacetBuilder.initFacetInstance(this);

//////////////// Roots Changed listener //////////////////////////////////////
        /**
         * Uses following contract:
         * The following actions leads to Roots Changed event
         * ### Changing of SDK in Ruby Module type
         * ### Changing of SDK in JRuby Facet Configuration
         *
         * If SDK was really changed this code will load Generators and RakeTasks for new SDK
         */
        final ModuleRootListener moduleRootListener = new ModuleRootListener() {
            public void beforeRootsChange(final ModuleRootEvent event) {
                // Do nothing
            }

            public void rootsChanged(final ModuleRootEvent event) {
                final Sdk newSDK = RModuleUtil.getModuleOrJRubyFacetSdk(module);
                final BaseRailsFacetConfigurationLowLevel conf = (BaseRailsFacetConfigurationLowLevel) getConfiguration();
                final Sdk oldSDK = conf.getSdk();
                if (newSDK != oldSDK) {
                    conf.setSdk(newSDK);

                    final Runnable runnable = new Runnable() {
                    public void run() {
                            BaseRailsFacetBuilder.regenerateRakeTasksAndGeneratorsSettings(BaseRailsFacet.this, newSDK);
                        }
                    };
                    final Project project = (Project) event.getSource();
                    if (project.isInitialized()) {
                        runnable.run();
                    } else {
                        StartupManager.getInstance(project).registerPostStartupActivity(runnable);
                    }
                }
            }
        };
        myConnection = module.getProject().getMessageBus().connect();
        myConnection.subscribe(ProjectTopics.PROJECT_ROOTS, moduleRootListener);
    }

    public void disposeFacet() {
        myConnection.disconnect();
        
        final Module module = getModule();
        if (!module.isDisposed()) {
            final RubyModuleCachesManager manager = RubyModuleCachesManager.getInstance(module);
            manager.unregisterScanForFilesProvider(myRailsAdditionalScannerProvider);
        }
        super.disposeFacet();
    }

    /**
     * Returns JRuby or Rails facet according given module
     * @param module Ruby or Java module
     * @return Base Rails facet
     */
    @Nullable
    public static BaseRailsFacet getInstance(@NotNull final Module module) {
        if (RubyUtil.isRubyModuleType(module)) {
            //in ruby module
            return RailsFacet.getInstance(module);
        }
         //in other modules (java)
        return JRailsFacet.getInstance(module);
    }

    public void projectClosed() {
        //TODO implement
    }

    public static FacetTypeId<? extends BaseRailsFacet> getRailsFacetID() {
        return RailsFacet.ID;
    }
    public static FacetTypeId<? extends BaseRailsFacet> getJRailsFacetID() {
        return JRailsFacet.ID;
    }
}
