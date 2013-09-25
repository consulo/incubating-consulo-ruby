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

package org.jetbrains.plugins.ruby.ruby.gotoByName;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.RCacheUtil;
import org.jetbrains.plugins.ruby.ruby.cache.RubyModuleCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.RubySdkCachesManager;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.navigation.ChooseByNameContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 29.10.2006
 */

public class RubyClassAndModuleContributor extends RubyBaseContributor implements ChooseByNameContributor{

    @Override
	public String[] getNames(final Project project, boolean includeNonProjectItems) {
        final ArrayList<String> names = new ArrayList<String>();
        final Module[] modules = ModuleManager.getInstance(project).getModules();
        final RubySdkCachesManager sdkCachesManager = RubySdkCachesManager.getInstance(project);

        for (Module module : modules) {
            final RubyModuleCachesManager cachesManager = RCacheUtil.getCachesManager(module);
// CachesManager is null for not ruby modules
            if (cachesManager!=null){
                final DeclarationsIndex declarationsIndex = cachesManager.getDeclarationsIndex();
                names.addAll(declarationsIndex.getAllClassesNames());
                names.addAll(declarationsIndex.getAllModulesNames());
            }

// Adding sdk`s info if needed
            if (includeNonProjectItems) {
                final Sdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
                final DeclarationsIndex declarationsIndex = sdkCachesManager.getSdkDeclarationsIndex(sdk);
                if (declarationsIndex!=null){
                    names.addAll(declarationsIndex.getAllClassesNames());
                    names.addAll(declarationsIndex.getAllModulesNames());
                }
            }
        }
        return names.toArray(new String[names.size()]);
    }

	@Override
	public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems) {
        //TODO Refactor with RCacheUtils
        final Module[] modules = RModuleUtil.getAllModulesWithRubySupport(project);
        final ArrayList<NavigationItem> items = new ArrayList<NavigationItem>();
        final RubySdkCachesManager sdkCachesManager = RubySdkCachesManager.getInstance(project);

        for (Module module : modules) {
            final RubyModuleCachesManager cachesManager = RCacheUtil.getCachesManager(module);
// CachesManager is null for not ruby modules
            if (cachesManager!=null){
                final DeclarationsIndex declarationsIndex = cachesManager.getDeclarationsIndex();
                addItems(declarationsIndex.getClassesByName(name), project, items);
                addItems(declarationsIndex.getModulesByName(name), project, items);
            }

// Adding sdk`s info if needed
            if (includeNonProjectItems) {
                final Sdk sdk = RModuleUtil.getModuleOrJRubyFacetSdk(module);
                final DeclarationsIndex declarationsIndex = sdkCachesManager.getSdkDeclarationsIndex(sdk);
                if (declarationsIndex!=null){
                    addItems(declarationsIndex.getClassesByName(name), project, items);
                    addItems(declarationsIndex.getModulesByName(name), project, items);
                }
            }
        }
        return items.toArray(new NavigationItem[items.size()]);
      }

    private void addItems(@NotNull final List elements, @NotNull final Project project, @NotNull final ArrayList<NavigationItem> items) {
        //noinspection unchecked
        items.addAll(getItems(elements,  project));
    }
}