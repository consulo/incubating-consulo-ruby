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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.testFramework.ModuleTestCase;
import com.intellij.testFramework.PsiTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacetTestUtil;
import org.jetbrains.plugins.ruby.jruby.JRubyUtil;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyModuleFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.index.impl.DeclarationsIndexImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;
import org.jetbrains.plugins.ruby.ruby.module.RubyModuleType;
import org.jetbrains.plugins.ruby.ruby.roots.RProjectContentRootManager;
import org.jetbrains.plugins.ruby.ruby.roots.RProjectContentRootManagerTestUtil;
import org.jetbrains.plugins.ruby.ruby.roots.RubyModuleContentRootManagerImpl;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScope;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.TestUtil;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public abstract class AbstractRubyModuleCacheTest extends ModuleTestCase {
    protected RubyModuleCachesManager myModuleCacheManager;

    @NonNls
    public static final String DEFAULT_RAILS_APP_HOME = "rails_app_home";

    protected String getDataDirPath() {
        return PathUtil.getDataPath(this.getClass());
    }

    protected Module createModule(final String path, final ModuleType moduleType) {
        final Module module = super.createModule(path, RubyModuleType.getInstance());

        //rails for Ruby module
        if (getRailsAppHome() != null) {
            final Ref<VirtualFile> virtualFileRef = new Ref<VirtualFile>();

            ApplicationManager.getApplication().runWriteAction(new Runnable() {
                public void run() {
                    try {
                        virtualFileRef.set(prepareTestProjectStructure(getRailsAppHome(), module));
                    } catch (Exception e) {
                        e.printStackTrace();

                        fail(e.getMessage());
                    }
                }
            });


            BaseRailsFacetTestUtil.addRailsToModule(module, virtualFileRef.get().getPath());
        }
        return module;
    }

    /**
     * Override to add Rails facet with this application home path
     * @return application home path
     */
    @Nullable
    protected String getRailsAppHome() {
        return null;
    }

    private VirtualFile prepareTestProjectStructure(final String testPath, final Module module) throws Exception {
        final String path = PathUtil.getDataPath(getClass(), testPath);
        return PsiTestUtil.createTestProjectStructure(getProject(), module, path, myFilesToDelete);
    }

    protected void initApplication() throws Exception {
        super.initApplication();
        TestUtil.loadRORAppComponents();
    }

    @SuppressWarnings({"ConstantConditions"})
    protected void setUp() throws Exception {
        super.setUp();
        loadComponents();
    }

    protected void loadComponents() throws IOException {
        TestUtil.loadRubySupport();
        TestUtil.loadRailsSupport();
        myModuleCacheManager = registerCacheForRoRModule(myModule);
        registerRContentRootManagers(myModule);
    }

    protected void tearDown() throws Exception {
        if (myModuleCacheManager != null) {
            myModuleCacheManager.getFilesCache().removeCacheFile();
        }
        super.tearDown();
    }

    protected Module createModule(final File moduleFile) {
        return createModule(moduleFile, RubyModuleType.getInstance());
    }

    protected Module createModule(final File moduleFile, final ModuleType moduleType) {
        final String path = moduleFile.getAbsolutePath();
        return createModule(path, moduleType);
    }

    protected Module createModule(final String path) {
        return createModule(path, RubyModuleType.getInstance());
    }

    protected Module createMainModule() throws IOException {
        return createModuleFromTestData(getDataDirPath(), "ruby_module", RubyModuleType.getInstance());
    }

    protected ProjectJdk getTestProjectJdk() {
      return RubySdkUtil.getMockSdk("empty-mock-sdk");
    }

    protected VirtualFile getFile(final String relative_path, final Module module) {
        final String path = getFullPath(relative_path, module);
        return LocalFileSystem.getInstance().findFileByPath(path);
    }

    protected String getFullPath(String relative_path, Module module) {
        final VirtualFile[] sourceRootUrls = ModuleRootManager.getInstance(module).getContentRoots();
        return sourceRootUrls[0].getPath() + "/" + relative_path;
    }

    protected RubyModuleCachesManager registerCacheForRoRModule(final Module module)
            throws IOException {

        RubyModuleCachesManager cachesManager = null;
//TODO
//        if (RailsUtil.isRailsModule(module)) {
//            TestUtil.loadModuleComponentIfIsntLoaded(module, RailsModuleCachesManager.class);
//            cachesManager = RailsModuleCachesManager.getInstance(module);
//        } else
        if (RubyUtil.isRubyModuleType(module)) {
            TestUtil.loadModuleComponentIfIsntLoaded(module, RubyModuleCachesManager.class);
            cachesManager = RubyModuleCachesManager.getInstance(module);
        } else if (JRubyUtil.hasJRubySupport(module)){
            TestUtil.loadModuleComponentIfIsntLoaded(module, RubyModuleCachesManager.class);
            cachesManager = RubyModuleCachesManager.getInstance(module);
        }
        if (cachesManager != null) {
            cachesManager.moduleAdded();
            final RubyModuleFilesCache cache = cachesManager.getFilesCache();
            cache.setCacheFilePath(createTempDir("cachedir").getPath()+"/testCache");
            cache.initFileCacheAndRegisterListeners();
            final String[] urls = ModuleRootManager.getInstance(module).getContentRootUrls();
            cache.setCacheRootURLs(urls);
            cache.registerDeaclarationsIndex(new DeclarationsIndexImpl(myProject));
            cache.setupFileCache(false);
            cache.forceUpdate();
        }
        return cachesManager;
    }

    protected void registerRContentRootManagers(final Module module) {
        RubyModuleContentRootManagerImpl manager = null;
        if (RubyUtil.isRubyModuleType(module)) {
            TestUtil.loadModuleComponentIfIsntLoaded(module, RubyModuleContentRootManagerImpl.class);
            manager = RubyModuleContentRootManagerImpl.getInstance(module);
        }
//        else if (RailsUtil.isRailsModule(module)) {
//            //TODO
//            //TestUtil.loadModuleComponentIfIsntLoaded(module, RailsModuleContentRootManager.class);
//            //manager = RailsModuleContentRootManager.getInstance(module);
//        }

        if (manager != null) {
            final VirtualFile moduleRoot = RModuleUtil.getRubyModuleTypeRoot(module);
            if (moduleRoot == null) {
                manager.setTestUnitFolderUrls(new ArrayList<String>());
            } else {
                manager.setTestUnitFolderUrls(Arrays.asList(moduleRoot.getUrl()));
            }
            manager.moduleAdded();
        }

        final Project project = module.getProject();
        // loading LastSymbolStorage
        TestUtil.loadProjectComponentIfIsntLoaded(project, LastSymbolStorage.class);
        // load RProjectContentRootManager
        TestUtil.loadProjectComponentIfIsntLoaded(project, RProjectContentRootManager.class);
        final RProjectContentRootManager pRootManager = RProjectContentRootManager.getInstance(project);
        RProjectContentRootManagerTestUtil.processNewModule(pRootManager, module);
        pRootManager.projectOpened();
    }

    protected RVirtualClass getClassByQualifiedName(final String qualifiedName, final VirtualFile file) {
        return RCacheUtil.getClassByNameInScriptInRubyTestMode(qualifiedName, myProject,
                new SearchScope() {
                    public boolean isSearchInModuleContent(@NotNull final Module aModule) {
                        return true;
                    }

                    public boolean isSearchInSDKLibraries() {
                        return false;
                    }

                    public boolean isFileValid(@NotNull String url) {
                        return true;
                    }
                }, file, null);
    }

    @Nullable
    protected RMethod getDirectMethodOfClass(final String classQualifiedName,
                                             final String methodName,
                                             final VirtualFile file) {
        final RVirtualClass rVClass = getClassByQualifiedName(classQualifiedName, file);
        final RClass rClass =
                (RClass) RVirtualPsiUtil.findPsiByVirtualElement(rVClass, myProject);
        return rClass != null
             ? RContainerUtil.getMethodByName(rClass, methodName)
             : null;
    }
}
