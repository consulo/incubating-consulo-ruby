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

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import org.jetbrains.plugins.ruby.ruby.scope.SearchScopeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
@SuppressWarnings({"ConstantConditions"})
public class RCacheUtilTest extends AbstractRubyModuleCacheTest {

    protected String getDataDirPath() {
        final Class<? extends RCacheUtilTest> aClass = this.getClass();

        return PathUtil.getModuleDirPath(aClass) + "/"
                + PathUtil.getClassDir(aClass)
                + "/commonData/"
                + aClass.getSimpleName().toLowerCase();
    }

    public void testGetModuleByFile() {
        final VirtualFile file = getFile("f1.rb", myModule);
        assertNotNull(file);
        assertNotNull(RCacheUtil.getModuleByFile(file, myProject));
    }
        
    public void testGetCacheByFile() {
        final VirtualFile file = getFile("f1.rb", myModule);
        assertNotNull(file);

        final RubyFilesCache[] caches =
                RCacheUtil.getCachesByFile(file, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), myProject);
        assertNotNull(caches);
        assertEquals(2, caches.length);
        assertNotNull(caches[0]);
        assertNull(caches[1]);
        assertTrue(caches[0].containsUrl(file.getUrl()));
    }

    public void testGetWordIndexByFile() {
        final VirtualFile file = getFile("f1.rb", myModule);
        final DeclarationsIndex[] indexes =
                RCacheUtil.getDeclarationsIndexByFile(file, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), myProject);
        assertEquals(indexes.length, 2);
        assertNotNull(indexes[0]);
        assertNull(indexes[1]);
        assertNotNull(indexes[0].getClassesByName("Foo"));
    }

//    public void testGetCachesManager() throws IOException {
//        final Module module1 =
//                createTempModuleAndRegisterCache("module11", RubyModuleType.RUBY);
//        assertNotNull(module1);
//        final RubyModuleCachesManager manager1 = RCacheUtil.getCachesManager(module1);
//        assertNotNull(manager1);
//        assertEquals(manager1.getClass(), RubyModuleCachesManager.class);
//
//        final Module module2 =
//                createTempModuleAndRegisterCache("module21", RailsModuleType.RAILS_GEM_EXECUTABLE);
//        assertNotNull(module2);
//        final RubyModuleCachesManager manager2 = RCacheUtil.getCachesManager(module2);
//        assertNotNull(manager2);
//        assertEquals(manager2.getClass(), RailsModuleCachesManager.class);
//    }

    public void testGetClassessByName() {
        RClass[] byName =
                RCacheUtil.getClassesByName("DoesntExist", SearchScopeUtil.getTestUnitClassSearchScope(getProject()), myProject);
        assertNotNull(byName);
        assertEmpty(byName);

        byName = RCacheUtil.getClassesByName("Foo", SearchScopeUtil.getTestUnitClassSearchScope(getProject()), myProject);
        assertEquals(6, byName.length);
    }

    public void testGetFirstClassByNameInScript() {
        final VirtualFile file = getFile("f1.rb", myModule);
        final RFile rClassRVFile = (RFile)PsiManager.getInstance(myProject).findFile(file);
        assert rClassRVFile != null;
        final FileSymbol fileSymbol = FileSymbolUtil.getFileSymbol(rClassRVFile);

        RVirtualClass rClass = RCacheUtil.getFirstClassByNameInScript("Foo", myProject, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), file);
        assert rClass != null;
        assertEquals("Foo", RClassPresentationUtil.getRuntimeQualifiedName(fileSymbol, rClass));

        rClass = RCacheUtil.getFirstClassByNameInScript("Boo::Foo", myProject, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), file);
        assertNull(rClass);
    }

    public void testGetFirstClassByNameInScript_TestMode() {
        final VirtualFile file = getFile("f1.rb", myModule);

        RVirtualClass rClass =
                RCacheUtil.getClassByNameInScriptInRubyTestMode("DoesntExist", myProject, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), file, null);
        assertNull(rClass);

        rClass = RCacheUtil.getClassByNameInScriptInRubyTestMode("Foo", myProject, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), file, null);
        assert rClass != null;
        assertEquals("Foo", RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(rClass, null));


        rClass = RCacheUtil.getClassByNameInScriptInRubyTestMode("Boo::Foo", myProject, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), file, null);
        assertEquals("Boo::Foo", RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(rClass, null));

        rClass = RCacheUtil.getClassByNameInScriptInRubyTestMode("MyModule::Foo", myProject, SearchScopeUtil.getTestUnitClassSearchScope(getProject()), file, null);
        assertEquals("MyModule::Foo", RClassPresentationUtil.getRuntimeQualifiedNameInRubyTestMode(rClass, null));
    }
}
