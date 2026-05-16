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

import consulo.annotation.component.ExtensionImpl;
import consulo.application.util.function.Processor;
import consulo.content.scope.SearchScope;
import consulo.ide.navigation.GotoClassOrTypeContributor;
import consulo.language.psi.scope.GlobalSearchScope;
import consulo.language.psi.search.FindSymbolParameters;
import consulo.language.psi.stub.IdFilter;
import consulo.language.psi.stub.StubIndex;
import consulo.navigation.NavigationItem;
import consulo.project.Project;
import consulo.project.content.scope.ProjectAwareSearchScope;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index.RubyClassNameIndex;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index.RubyModuleNameIndex;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 29.10.2006
 */
@ExtensionImpl
public class RubyClassAndModuleContributor extends RubyBaseContributor implements GotoClassOrTypeContributor {

    @Override
    public void processNames(Processor<String> processor, SearchScope searchScope, IdFilter idFilter) {
        StubIndex.getInstance().processAllKeys(RubyClassNameIndex.KEY, processor, (ProjectAwareSearchScope) searchScope, idFilter);
        StubIndex.getInstance().processAllKeys(RubyModuleNameIndex.KEY, processor, (ProjectAwareSearchScope) searchScope, idFilter);
    }

    @Override
    public void processElementsWithName(String s, Processor<NavigationItem> processor, FindSymbolParameters findSymbolParameters) {
        NavigationItem[] itemsByName = getItemsByName(s, findSymbolParameters.getCompletePattern(), findSymbolParameters.getProject(), findSymbolParameters.isSearchInLibraries());

        for (NavigationItem navigationItem : itemsByName) {
            if (!processor.process(navigationItem)) {
                break;
            }
        }
    }

    @Override
    public String[] getNames(final Project project, boolean includeNonProjectItems) {
        final Set<String> names = new LinkedHashSet<String>();
        names.addAll(RubyClassNameIndex.allKeys(project));
        names.addAll(RubyModuleNameIndex.allKeys(project));
        return names.toArray(new String[names.size()]);
    }

    @Override
    public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems) {
        final GlobalSearchScope scope = includeNonProjectItems ? GlobalSearchScope.allScope(project) : GlobalSearchScope.projectScope(project);
        final ArrayList<NavigationItem> items = new ArrayList<NavigationItem>();
        final List<RPsiElement> elements = new ArrayList<RPsiElement>();
        elements.addAll(RubyClassNameIndex.find(name, project, scope));
        elements.addAll(RubyModuleNameIndex.find(name, project, scope));
        addItems(elements, project, items);
        return items.toArray(new NavigationItem[items.size()]);
    }

    private void addItems(@Nonnull final List<RPsiElement> elements, @Nonnull final Project project, @Nonnull final ArrayList<NavigationItem> items) {
        items.addAll(getItems(elements, project));
    }
}
