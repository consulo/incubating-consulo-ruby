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

package org.jetbrains.plugins.ruby.jruby.facet;

import java.util.Arrays;
import java.util.Collection;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import com.intellij.facet.Facet;
import com.intellij.facet.FacetRootsProvider;
import com.intellij.facet.FacetType;
import com.intellij.facet.FacetTypeId;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LibraryOrderEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderEntry;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ActionRunner;

public class JRubyFacet extends Facet<RSupportPerModuleSettingsImpl> implements FacetRootsProvider
{
    public static final FacetTypeId<JRubyFacet> ID = new FacetTypeId<JRubyFacet>("JRubyFacetType");

    @NonNls
    private static final String JRUBY_FACET_SDK_OLD_LIB_NAME = "JRuby facet sdk";
    @NonNls
    public static final String JRUBY_FACET_LIBRARY_NAME_SUFFIX = " facet library";


    private RubyModuleCachesManager myRubyModuleCachesManager;

    public JRubyFacet(@NotNull final FacetType facetType,
                      @NotNull final Module module,
                      final String name,
                      @NotNull RSupportPerModuleSettingsImpl configuration,
                      final Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);


        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        final PsiManager psiManager = PsiManager.getInstance(module.getProject());
        myRubyModuleCachesManager = new RubyModuleCachesManager(module, rootManager, psiManager);
        myRubyModuleCachesManager.initComponent();
    }

    /**
     * Here we try to add JRuby SDK as library invisible to user to module
     */
    public void updateSdkLibrary() {
       /* IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable() {
            @Override
			public void run() throws Exception {
                final Module module = getModule();
                final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                final ModifiableRootModel model = rootManager.getModifiableModel();
                boolean modelUsed = false;
                // Just remove all old facet libraries except one, that is neccessary
                final Sdk sdk = getSdk();
                final String name = (sdk != null) ? getFacetLibraryName(sdk.getName()) : null;
                boolean librarySeen = false;
                for (OrderEntry entry : model.getOrderEntries()) {
                    if (entry instanceof LibraryOrderEntry){
                        final String libraryName = ((LibraryOrderEntry) entry).getLibraryName();
                        // We check existing libraries name and remove old libraries
                        // If it isn't current facet SDK library and it is
                        // Module library with old style name  or
                        // Global library with name in new format with special suffix  - remove

                        if (name!=null && name.equals(libraryName)){
                            librarySeen = true;
                            continue;
                        }
                        if (libraryName != null
                                && (libraryName.endsWith(JRUBY_FACET_LIBRARY_NAME_SUFFIX)
                                || JRUBY_FACET_SDK_OLD_LIB_NAME.equals(libraryName))){
                            
                            model.removeOrderEntry(entry);
                            modelUsed = true;
                        }
                    }
                }
                if (!librarySeen && name != null) {
                    Library library = LibraryTablesRegistrar.getInstance().getLibraryTable().getLibraryByName(name);
                    if (library == null) {
                        // we just create new project library
                        library = JRubySdkTableListener.addLibrary(sdk);
                    }
                    model.addLibraryEntry(library);
                    modelUsed = true;
                }
                if (modelUsed){
                    // !!!!!!!!!! WARNING !!!!!!!!!
                    // This generates Roots Changed Event and BaseRailsFacet uses such behaviour!
                    // Don't remove it without updating BaseRailsFacet behaviour!
                    model.commit();
                } else {
                    model.dispose();
                }
            }
        });  */
    }

    public void removeSdkLibrary() {
        IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable() {
            @Override
			public void run() throws Exception {
                final Module module = getModule();
                final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                final ModifiableRootModel model = rootManager.getModifiableModel();
                // Just remove all old facet libraries
                for (OrderEntry entry : model.getOrderEntries()) {
                    if (entry instanceof LibraryOrderEntry){
                        final Library library = ((LibraryOrderEntry) entry).getLibrary();
                        if (library!=null){
                            final String libraryName = library.getName();
                            if (libraryName!=null && libraryName.endsWith(JRUBY_FACET_LIBRARY_NAME_SUFFIX)){
                                model.removeOrderEntry(entry);
                            }
                        }
                    }
                }
                model.commit();
            }
        });
    }

    @NotNull
    public static String getFacetLibraryName(final String sdkName) {
        return sdkName + JRUBY_FACET_LIBRARY_NAME_SUFFIX;
    }

    @Override
	public void initFacet() {
        super.initFacet();
        updateSdkLibrary();
    }

    @Nullable
    public static JRubyFacet getInstance(@NotNull final Module module) {
        return null;
    }


    public RubyModuleCachesManager getRubyModuleCachesManager() {
        return myRubyModuleCachesManager;
    }

    public void projectClosed() {
        myRubyModuleCachesManager.projectClosed();


        myRubyModuleCachesManager.disposeComponent();
    }

    @NotNull
    public Collection<VirtualFile> getFacetRoots() {
        return Arrays.asList(ModuleRootManager.getInstance(getModule()).getContentRoots());        
    }
}
