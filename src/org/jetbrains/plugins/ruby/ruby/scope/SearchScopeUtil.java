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

package org.jetbrains.plugins.ruby.ruby.scope;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.roots.RProjectContentRootManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 06.08.2007
 */
public class SearchScopeUtil {
    private static final AllClassSearchScope ALL_CLASS_SEARCH_SCOPE = new AllClassSearchScope();

    /**
     * In modules, sdk, qualified names
     * @param project Project
     * @return SearchScope
     */
    public static SearchScope getTestUnitClassSearchScope(Project project) {
        return new TestUnitClassSearchScope(project);
    }

    /**
     * In modules, sdk, not qualified names
     * @return SearchScope
     */
    public static SearchScope getAllSearchScope() {
        return ALL_CLASS_SEARCH_SCOPE;
    }

    /**
     * In modules, sdk, qualified names
     */
    private static class TestUnitClassSearchScope implements SearchScope {
        private RProjectContentRootManager myManager;

        public TestUnitClassSearchScope(@NotNull final Project project) {
            myManager = RProjectContentRootManager.getInstance(project);
        }

        public boolean isSearchInModuleContent(@NotNull final Module aModule) {
            return true;
        }

        public boolean isSearchInSDKLibraries() {
            return false;
        }

        public boolean isFileValid(@NotNull final String url) {
            return myManager.isUnderTestUnitRoot(url);
        }
    }

    /**
     * In modules, sdk, not qualified names
     */
    private static class AllClassSearchScope implements SearchScope {
        public boolean isSearchInModuleContent(@NotNull final Module aModule) {
            return true;
        }

        public boolean isSearchInSDKLibraries() {
            return true;
        }

        public boolean isFileValid(@NotNull String url) {
            return true;
        }
    }
}
