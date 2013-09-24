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

package org.jetbrains.plugins.ruby.rails.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetManagerAdapter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.RailsComponents;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 17, 2008
 */
public class BaseRailsFacetListener extends FacetManagerAdapter implements ModuleComponent {
  private MessageBusConnection myConnection;

  private Module myModule;

  public BaseRailsFacetListener(@NotNull final Module module) {
    myModule = module;
  }

  public void initComponent() {
    myConnection = myModule.getMessageBus().connect();
    myConnection.subscribe(FacetManager.FACETS_TOPIC, new FacetManagerAdapter() {
      public void beforeFacetAdded(@NotNull Facet facet) {
          //Is invoked after ROOTS changed.
      }

      public void facetAdded(@NotNull final Facet facet) {
          if (! (facet instanceof BaseRailsFacet)) {
              // ignores not Rails Facets
              return;
          }

          //noinspection ConstantConditions
          assert ((BaseRailsFacet)facet).getConfiguration().getRailsApplicationRootPath() != null;

          // Only if module is loaded and committed. Otherwise RubySdkCachesManager will recreate SymbolCaches  on module added event!
          final Module module = facet.getModule();
          if (module.isLoaded()) {
            performSymbolCacheUpdateCausedByFacet(facet);
              RailsProjectViewPane.getInstance(module.getProject()).facetAdded();
          }
      }

//      public void beforeFacetRemoved(@NotNull final Facet facet) {
//          //Is invoked after ROOTS changed.(but roots change event doesn't know that facet was removed)
//          //Module contains JRubyFacet
//          if (! (facet instanceof BaseRailsFacet)) {
//              // ignores not Rails Facets
//              return;
//          }
//      }

        public void facetRemoved(@NotNull final Facet facet) {
            //Is invoked after ROOTS changed.(but roots change event doesn't know that facet was removed)
            //Module doesn't contain JRubyFacet
            if (! (facet instanceof BaseRailsFacet)) {
                // ignores not Rails Facets
                return;
            }

            performSymbolCacheUpdateCausedByFacet(facet);
            RailsProjectViewPane.getInstance(facet.getModule().getProject()).facetAdded();
      }

//        public void facetConfigurationChanged(@NotNull final Facet facet) {
//            //Is invoked after ROOTS changed.
//            if (! (facet instanceof BaseRailsFacet)) {
//                // ignores not Rails Facets
//                return;
//            }
//        }
    });
  }

    /**
     * Adding / removing of Rails/JRails Facet causes caches rebuilding!
     * Because caches for Ruby singnificantly differs from caches for Rails!
     * @param facet JRuby/Rails facet
     */
    private void performSymbolCacheUpdateCausedByFacet(final Facet facet) {
        //Perform SymbolCache update caused by Rails/JRails facet
        final Module facetModule = facet.getModule();
        SymbolsCache.getInstance(facetModule.getProject()).recreateBuiltInCaches(new Module[]{facetModule});

        //Refresh markers and higlighting
        DaemonCodeAnalyzer.getInstance(facetModule.getProject()).restart();
    }

    public void disposeComponent() {
    myConnection.disconnect();
  }

  @NotNull
  public String getComponentName() {
    return RailsComponents.BASE_RAILS_FACET_LISTENER;
  }

  public void projectOpened() {
    // called when project is opened
  }

  public void projectClosed() {
      // called when project is being closed
      if (RailsFacetUtil.hasRailsSupport(myModule)) {
          final BaseRailsFacet baseRailsFacet = BaseRailsFacet.getInstance(myModule);
          assert baseRailsFacet != null;
          baseRailsFacet.projectClosed();
      }
  }

  public void moduleAdded() {
    // Invoked when the module corresponding to this component instance has been completely
    // loaded and added to the project.
  }
}