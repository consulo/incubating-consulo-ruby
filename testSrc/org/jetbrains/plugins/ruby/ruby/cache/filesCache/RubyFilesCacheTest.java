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

package org.jetbrains.plugins.ruby.ruby.cache.filesCache;

import com.intellij.openapi.util.Disposer;
import com.intellij.testFramework.IdeaTestCase;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.impl.RubyFilesCacheImpl;
import org.jetbrains.plugins.ruby.ruby.cache.index.DeclarationsIndex;
import org.jetbrains.plugins.ruby.ruby.cache.index.impl.DeclarationsIndexImpl;
import org.jetbrains.plugins.ruby.ruby.lang.RubySupportLoader;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;


/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
public class RubyFilesCacheTest extends IdeaTestCase {
    private RubyFilesCache myFilesCache;
    private DeclarationsIndex myIndex;

    public void testChangeUrls(){
        init();
        final String dataPath = getDataPath();
        final String dataUrl = VirtualFileUtil.constructLocalUrl(dataPath);
        final String rootUrl = dataUrl+"/root";
        final String rootUrl2 = dataUrl+"/root2";
        final String rootUrl3 = dataUrl+"/root3";

        myFilesCache.setCacheFilePath(dataPath+"/testCache");        
        myFilesCache.setCacheRootURLs(new String[]{rootUrl, rootUrl2, rootUrl3});
        myFilesCache.initFileCacheAndRegisterListeners();
        myFilesCache.setupFileCache(false);
        myFilesCache.forceUpdate();
        myFilesCache.removeCacheFile();

        /* Debug print
           ((WordsIndexImpl) myIndex).dump(System.out);
        */

        assertEquals(myIndex.getAllClassesNames().size(), 10);
        assertEquals(myIndex.getAllModulesNames().size(), 5);
        assertEquals(myIndex.getAllMethodsNames().size(), 13);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 4);

// changing urls
        myFilesCache.setCacheRootURLs(new String[]{rootUrl, rootUrl2});
        myFilesCache.forceUpdate();

        /* Debug print
           ((WordsIndexImpl) myIndex).dump(System.out);
        */

        assertEquals(myIndex.getAllClassesNames().size(), 8);
        assertEquals(myIndex.getAllModulesNames().size(), 4);
        assertEquals(myIndex.getAllMethodsNames().size(), 10);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 4);

// changing urls
        myFilesCache.setCacheRootURLs(new String[]{rootUrl});
        myFilesCache.forceUpdate();

        /* Debug print
           ((WordsIndexImpl) myIndex).dump(System.out);
        */

        assertEquals(myIndex.getAllClassesNames().size(), 6);
        assertEquals(myIndex.getAllModulesNames().size(), 4);
        assertEquals(myIndex.getAllMethodsNames().size(), 10);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 3);

// changing urls
        myFilesCache.setCacheRootURLs(new String[]{});
        myFilesCache.forceUpdate();

        /* Debug print
           ((WordsIndexImpl) myIndex).dump(System.out);
        */

        assertEquals(myIndex.getAllClassesNames().size(), 0);
        assertEquals(myIndex.getAllModulesNames().size(), 0);
        assertEquals(myIndex.getAllMethodsNames().size(), 0);
        assertEquals(myIndex.getAllFieldsNames().size(), 0);
        assertEquals(myIndex.getAllConstantsNames().size(), 0);
    }

    public void testSaveAndLoad(){
        init();
        final String dataPath = getDataPath();
        final String dataUrl = VirtualFileUtil.constructLocalUrl(dataPath);
        final String rootUrl = dataUrl+"/root";
        final String rootUrl2 = dataUrl+"/root2";
        final String rootUrl3 = dataUrl+"/root3";
        final String[] myUrls = new String[]{rootUrl, rootUrl2, rootUrl3};

        myFilesCache.setCacheFilePath(dataPath+"/testCache");
        myFilesCache.setCacheRootURLs(myUrls);
        myFilesCache.initFileCacheAndRegisterListeners();
        myFilesCache.setupFileCache(false);
        myFilesCache.forceUpdate();
        myIndex.build(false);

        /* Debug print
           ((WordsIndexImpl) myIndex).dump(System.out);
        */

        final int classNamesNumber = myIndex.getAllClassesNames().size();
        final int moduleNamesNumber = myIndex.getAllModulesNames().size();
        final int methodNamesNumber = myIndex.getAllMethodsNames().size();
        final int fieldNamesNumber = myIndex.getAllFieldsNames().size();
        final int constantNamesNumber = myIndex.getAllConstantsNames().size();
        final int globalVarNamesNumber = myIndex.getAllGlobalVarsNames().size();

        Disposer.dispose(myFilesCache);

// loading cache from disk
        myFilesCache.setupFileCache(false);
        myFilesCache.forceUpdate();
        myFilesCache.removeCacheFile();

        assertEquals(myIndex.getAllClassesNames().size(), classNamesNumber);
        assertEquals(myIndex.getAllModulesNames().size(), moduleNamesNumber);
        assertEquals(myIndex.getAllMethodsNames().size(), methodNamesNumber);
        assertEquals(myIndex.getAllFieldsNames().size(), fieldNamesNumber);
        assertEquals(myIndex.getAllConstantsNames().size(), constantNamesNumber);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), globalVarNamesNumber);
    }

    private void init() {
        RubySupportLoader.loadRuby();
        myFilesCache = new RubyFilesCacheImpl(myProject, "mock_cache");

        myIndex = new DeclarationsIndexImpl(myProject);
        myFilesCache.registerDeaclarationsIndex(myIndex);
    }

    private String getDataPath() {
        return PathUtil.getModuleDirPath(RubyFilesCacheTest.class)+"/"+ PathUtil.getClassDir(RubyFilesCacheTest.class) + "/../data";
    }
}
