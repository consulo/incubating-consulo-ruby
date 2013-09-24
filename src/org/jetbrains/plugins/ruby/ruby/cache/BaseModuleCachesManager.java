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

package org.jetbrains.plugins.ruby.ruby.cache;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 29.04.2007
 */
public abstract class BaseModuleCachesManager implements ModuleComponent {

    @NonNls private static final String RUBY_CACHE_DIR = "ruby_caches";
    @NonNls private static final String RUBY_CACHE_FILE = "module";

    protected Module myModule;
    protected RubyModuleFilesCache myModuleFilesCache;
    protected ModuleRootManager myModuleRootManager;

    //PsiManager should build caches thus register prestarup activity thus create it's instance
    //  before ruby cache manager
    @SuppressWarnings({"UnusedDeclaration", "UnusedParameters"})
    public BaseModuleCachesManager(@NotNull final Module module,
                                   @NotNull final ModuleRootManager manager,
                                   @NotNull final PsiManager psiManger) {
        myModule = module;
        myModuleRootManager = manager;
    }

    /**
     * @return Path to file where cached information is saved.
     */
    @NotNull
    protected String generateCacheFilePath() {
        return PathManager.getSystemPath()+"/" + RUBY_CACHE_DIR + "/" + RUBY_CACHE_FILE + "/" + myModule.getProject().getName()+ "/" +myModule.getName() + "_" + myModule.getModuleFilePath().hashCode();
    }

    public void projectOpened() {
    }

    public void projectClosed() {
        myModuleFilesCache.saveCacheToDisk();
    }

    public void initComponent() {
    }

    public void disposeComponent() {
    }

    @NotNull
    public RubyModuleFilesCache getFilesCache() {
        return myModuleFilesCache;
    }

    @NotNull
    public DeclarationsIndex getDeclarationsIndex() {
        return myModuleFilesCache.getDeclarationsIndex();
    }

    public void moduleAdded() {
        //do nothing
    }
}
