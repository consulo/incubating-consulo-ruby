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

package org.jetbrains.plugins.ruby.jruby;

import com.intellij.facet.FacetManager;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.facet.impl.FacetUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacet;
import org.jetbrains.plugins.ruby.jruby.facet.JRubyFacetType;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkUtil;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 24, 2008
 */
public abstract class AbstractJRubyModuleTest extends AbstractRubyModuleCacheTest{
    protected Module createModule(final File moduleFile) {
        return createModule(moduleFile, RubyModuleType.getInstance());
    }

    protected Module createModule(final File moduleFile, final ModuleType moduleType) {
        final String path = moduleFile.getAbsolutePath();
        return createModule(path, moduleType);
    }

    protected Module createModule(final String path) {
        return createModule(path, JavaModuleType.JAVA);
    }

    protected Module createMainModule() throws IOException {
        return createModuleFromTestData(getDataDirPath(), "jruby_module", JavaModuleType.JAVA);
    }

    protected void loadComponents() throws IOException {
        TestUtil.loadRubySupport();
        TestUtil.loadRailsSupport();
        TestUtil.loadJRubySupport();

// Adding JRuby facet
        final ModifiableFacetModel facetModel = FacetManager.getInstance(myModule).createModifiableModel();
        JRubyFacet facet = FacetUtil.createFacet(JRubyFacetType.INSTANCE, myModule, null);
        facet.getConfiguration().setSdk(getTestProjectJdk());
        facetModel.addFacet(facet);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          public void run() {
            facetModel.commit();
          }
        });

// Adding src roots
        final ModifiableRootModel rootModel = ModuleRootManager.getInstance(myModule).getModifiableModel();
        final String[] urls = rootModel.getContentRootUrls();
        final ContentEntry[] entries = rootModel.getContentEntries();
        entries[0].addSourceFolder(urls[0], false);
        ApplicationManager.getApplication().runWriteAction(new Runnable() {
          public void run() {
            rootModel.commit();
          }
        });

        myModuleCacheManager = registerCacheForRoRModule(myModule);
        registerRContentRootManagers(myModule);
    }

    protected ProjectJdk getTestProjectJdk() {
        return JRubySdkUtil.getMockSdk("empty-mock-sdk");
    }
}
