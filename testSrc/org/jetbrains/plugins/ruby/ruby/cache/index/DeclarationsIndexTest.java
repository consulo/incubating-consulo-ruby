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

package org.jetbrains.plugins.ruby.ruby.cache.index;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.IdeaTestCase;
import com.intellij.util.containers.HashSet;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.index.impl.DeclarationsIndexImpl;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfoFactory;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFilesStorage;
import org.jetbrains.plugins.ruby.ruby.lang.RubySupportLoader;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
public class DeclarationsIndexTest extends IdeaTestCase {
    private MockFilesCache myMockFilesCache;
    private DeclarationsIndex myIndex;

    public void testIndexBuild(){
        init();
        final RFilesStorage filesStorage = myMockFilesCache.getRFilesStorage();
        String dataUrl = getDataUrl();
        final VirtualFile dataDir = VirtualFileManager.getInstance().findFileByUrl(dataUrl);
        final Set<VirtualFile> files = new HashSet<VirtualFile>();
        RubyVirtualFileScanner.addRubyFiles(dataDir, files);
        for (VirtualFile file : files) {
            //noinspection ConstantConditions
            filesStorage.addRInfo(RFileInfoFactory.createRFileInfo(myProject, file));
        }
        myIndex.build(false);
        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 10);
        assertEquals(myIndex.getAllModulesNames().size(), 5);
        assertEquals(myIndex.getAllMethodsNames().size(), 13);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 4);
        assertEquals(myIndex.getAllAliasesNames().size(), 3);
        assertEquals(myIndex.getAllFieldAttrsNames().size(), 2);

// classes
        assertEquals(myIndex.getClassesByName("C1").size(), 4);
        assertEquals(myIndex.getClassesByName("TEST1").size(), 1);
        assertEquals(myIndex.getClassesByName("Doo").size(), 1);
        assertEquals(myIndex.getClassesByName("SomeClass").size(), 1);
        assertEquals(myIndex.getClassesByName("Xxx").size(), 0);

// modules
        assertEquals(myIndex.getModulesByName("A").size(), 1);
        assertEquals(myIndex.getModulesByName("M").size(), 3);
        assertEquals(myIndex.getModulesByName("M1").size(), 3);
        assertEquals(myIndex.getModulesByName("xxx").size(), 0);

// methods
        assertEquals(myIndex.getMethodsByName("test").size(), 6);
        assertEquals(myIndex.getMethodsByName("f1").size(), 9);
        assertEquals(myIndex.getMethodsByName("f").size(), 3);
        assertEquals(myIndex.getMethodsByName("xxx").size(), 0);

// Fields
        assertEquals(myIndex.getFieldsByName("a").size(), 6);
        assertEquals(myIndex.getFieldsByName("b").size(), 5);
        assertEquals(myIndex.getFieldsByName("c").size(), 4);
        assertEquals(myIndex.getFieldsByName("d").size(), 0);
// constants
        assertEquals(myIndex.getConstantsByName("A").size(), 2);
        assertEquals(myIndex.getConstantsByName("B").size(), 1);
        assertEquals(myIndex.getConstantsByName("C").size(), 2);
        assertEquals(myIndex.getConstantsByName("D").size(), 0);
// global vars
        assertEquals(myIndex.getGlobalVarsByName("$A").size(), 4);
        assertEquals(myIndex.getGlobalVarsByName("$B").size(), 4);
        assertEquals(myIndex.getGlobalVarsByName("$C").size(), 3);
        assertEquals(myIndex.getGlobalVarsByName("$D").size(), 0);
// aliases
        assertEquals(myIndex.getAliasesByName("f1").size(), 0);
        assertEquals(myIndex.getAliasesByName("f2").size(), 3);
        assertEquals(myIndex.getAliasesByName("f3").size(), 3);
        assertEquals(myIndex.getAliasesByName("f4").size(), 3);
// attr fields
        assertEquals(myIndex.getFieldAttrsByName("a").size(), 2);
        assertEquals(myIndex.getFieldAttrsByName("b").size(), 1);
    }

    private void init() {
        RubySupportLoader.loadRuby();
        myMockFilesCache = new MockFilesCache(myProject);
        myIndex = new DeclarationsIndexImpl(myProject);
        myMockFilesCache.registerDeaclarationsIndex(myIndex);
    }

    @SuppressWarnings({"ConstantConditions"})
    public void testAddInfo(){
        init();
        String dataUrl = getDataUrl();

        myIndex.build(false);
        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 0);
        assertEquals(myIndex.getAllModulesNames().size(), 0);
        assertEquals(myIndex.getAllMethodsNames().size(), 0);
        assertEquals(myIndex.getAllFieldsNames().size(), 0);
        assertEquals(myIndex.getAllConstantsNames().size(), 0);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 0);
        assertEquals(myIndex.getAllAliasesNames().size(), 0);
        assertEquals(myIndex.getAllFieldAttrsNames().size(), 0);

        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(dataUrl+"/root2/new.rb");
        myIndex.addFileInfoToIndex(RFileInfoFactory.createRFileInfo(myProject, file));

        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 2);
        assertEquals(myIndex.getAllModulesNames().size(), 1);
        assertEquals(myIndex.getAllMethodsNames().size(), 1);

        final VirtualFile file2 = VirtualFileManager.getInstance().findFileByUrl(dataUrl+"/root/lib/lib1.rb");
        //noinspection ConstantConditions
        myIndex.addFileInfoToIndex(RFileInfoFactory.createRFileInfo(myProject, file2));

        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 5);
        assertEquals(myIndex.getAllModulesNames().size(), 3);
        assertEquals(myIndex.getAllMethodsNames().size(), 8);

// classes
        assertEquals(myIndex.getClassesByName("C1").size(), 1);
        assertEquals(myIndex.getClassesByName("M1").size(), 1);

// modules
        assertEquals(myIndex.getModulesByName("M").size(), 2);
        assertEquals(myIndex.getModulesByName("M1").size(), 1);

// methods
        assertEquals(myIndex.getMethodsByName("f").size(), 3);
        assertEquals(myIndex.getMethodsByName("f1").size(), 2);
    }

    @SuppressWarnings({"ConstantConditions"})
    public void testRemoveInfo(){
        init();
        String dataUrl = getDataUrl();

        myIndex.build(false);

        final VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(dataUrl+"/root2/new.rb");
        RFileInfo fileInfo1 = RFileInfoFactory.createRFileInfo(myProject, file);
        myIndex.addFileInfoToIndex(fileInfo1);

        final VirtualFile file2 = VirtualFileManager.getInstance().findFileByUrl(dataUrl+"/root/lib/lib1.rb");
        RFileInfo fileInfo2 = RFileInfoFactory.createRFileInfo(myProject, file2);
        myIndex.addFileInfoToIndex(fileInfo2);

        final VirtualFile file3 = VirtualFileManager.getInstance().findFileByUrl(dataUrl+"/root/lib/lib2.rb");
        RFileInfo fileInfo3 = RFileInfoFactory.createRFileInfo(myProject, file3);
        myIndex.addFileInfoToIndex(fileInfo3);

        final VirtualFile file4 = VirtualFileManager.getInstance().findFileByUrl(dataUrl+"/root/lib/lib3.rb");
        RFileInfo fileInfo4 = RFileInfoFactory.createRFileInfo(myProject, file4);
        myIndex.addFileInfoToIndex(fileInfo4);

        ((DeclarationsIndexImpl) myIndex).printIndex();
        assertEquals(myIndex.getAllClassesNames().size(), 7);
        assertEquals(myIndex.getAllModulesNames().size(), 4);
        assertEquals(myIndex.getAllMethodsNames().size(), 10);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 4);
        assertEquals(myIndex.getAllAliasesNames().size(), 3);
        assertEquals(myIndex.getAllFieldAttrsNames().size(), 2);

        myIndex.removeFileInfoFromIndex(fileInfo1);
        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 6);
        assertEquals(myIndex.getAllModulesNames().size(), 4);
        assertEquals(myIndex.getAllMethodsNames().size(), 10);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 3);
        assertEquals(myIndex.getAllAliasesNames().size(), 3);
        assertEquals(myIndex.getAllFieldAttrsNames().size(), 0);

// classes
        assertEquals(myIndex.getClassesByName("C1").size(), 4);

// modules
        assertEquals(myIndex.getModulesByName("M1").size(), 3);
        assertEquals(myIndex.getModulesByName("TEST").size(), 1);

// methods
        assertEquals(myIndex.getMethodsByName("f").size(), 1);
        assertEquals(myIndex.getMethodsByName("test").size(), 6);
        assertEquals(myIndex.getMethodsByName("f1").size(), 9);

// Fields
        assertEquals(myIndex.getFieldsByName("a").size(), 4);
        assertEquals(myIndex.getFieldsByName("b").size(), 4);
        assertEquals(myIndex.getFieldsByName("c").size(), 4);
        assertEquals(myIndex.getFieldsByName("d").size(), 0);
// constants
        assertEquals(myIndex.getConstantsByName("A").size(), 2);
        assertEquals(myIndex.getConstantsByName("B").size(), 1);
// global vars
        assertEquals(myIndex.getGlobalVarsByName("$A").size(), 3);
        assertEquals(myIndex.getGlobalVarsByName("$B").size(), 3);
        assertEquals(myIndex.getGlobalVarsByName("$C").size(), 2);
        assertEquals(myIndex.getGlobalVarsByName("$D").size(), 0);
// aliases
        assertEquals(myIndex.getAliasesByName("f1").size(), 0);
        assertEquals(myIndex.getAliasesByName("f2").size(), 3);
        assertEquals(myIndex.getAliasesByName("f3").size(), 3);
        assertEquals(myIndex.getAliasesByName("f4").size(), 3);
// attr fields
        assertEquals(myIndex.getFieldAttrsByName("a").size(), 0);
        assertEquals(myIndex.getFieldAttrsByName("b").size(), 0);


        myIndex.removeFileInfoFromIndex(fileInfo2);
        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 4);
        assertEquals(myIndex.getAllModulesNames().size(), 3);
        assertEquals(myIndex.getAllMethodsNames().size(), 3);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 3);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 3);

// Fields
        assertEquals(myIndex.getFieldsByName("a").size(), 4);
        assertEquals(myIndex.getFieldsByName("b").size(), 4);
        assertEquals(myIndex.getFieldsByName("c").size(), 4);
        assertEquals(myIndex.getFieldsByName("d").size(), 0);
// constants
        assertEquals(myIndex.getConstantsByName("A").size(), 2);
        assertEquals(myIndex.getConstantsByName("B").size(), 1);
// global vars
        assertEquals(myIndex.getGlobalVarsByName("$A").size(), 2);
        assertEquals(myIndex.getGlobalVarsByName("$B").size(), 2);
        assertEquals(myIndex.getGlobalVarsByName("$C").size(), 1);
        assertEquals(myIndex.getGlobalVarsByName("$D").size(), 0);
// aliases
        assertEquals(myIndex.getAliasesByName("f1").size(), 0);
        assertEquals(myIndex.getAliasesByName("f2").size(), 2);
        assertEquals(myIndex.getAliasesByName("f3").size(), 2);
        assertEquals(myIndex.getAliasesByName("f4").size(), 2);
// attr fields
        assertEquals(myIndex.getFieldAttrsByName("a").size(), 0);
        assertEquals(myIndex.getFieldAttrsByName("b").size(), 0);


        myIndex.removeFileInfoFromIndex(fileInfo3);
        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 3);
        assertEquals(myIndex.getAllModulesNames().size(), 2);
        assertEquals(myIndex.getAllMethodsNames().size(), 1);
        assertEquals(myIndex.getAllFieldsNames().size(), 3);
        assertEquals(myIndex.getAllConstantsNames().size(), 2);
        assertEquals(myIndex.getAllAliasesNames().size(), 3);
        assertEquals(myIndex.getAllFieldAttrsNames().size(), 0);

// Fields
        assertEquals(myIndex.getFieldsByName("a").size(), 2);
        assertEquals(myIndex.getFieldsByName("b").size(), 2);
// constants
        assertEquals(myIndex.getConstantsByName("A").size(), 1);
        assertEquals(myIndex.getConstantsByName("B").size(), 0);
        assertEquals(myIndex.getConstantsByName("C").size(), 1);
// global vars
        assertEquals(myIndex.getGlobalVarsByName("$A").size(), 1);
        assertEquals(myIndex.getGlobalVarsByName("$B").size(), 1);
        assertEquals(myIndex.getGlobalVarsByName("$C").size(), 1);
        assertEquals(myIndex.getGlobalVarsByName("$D").size(), 0);
// aliases
        assertEquals(myIndex.getAliasesByName("f1").size(), 0);
        assertEquals(myIndex.getAliasesByName("f2").size(), 1);
        assertEquals(myIndex.getAliasesByName("f3").size(), 1);
        assertEquals(myIndex.getAliasesByName("f4").size(), 1);
// attr fields
        assertEquals(myIndex.getFieldAttrsByName("a").size(), 0);
        assertEquals(myIndex.getFieldAttrsByName("b").size(), 0);

        myIndex.removeFileInfoFromIndex(fileInfo2);
        ((DeclarationsIndexImpl) myIndex).printIndex();
        myIndex.removeFileInfoFromIndex(fileInfo4);
        ((DeclarationsIndexImpl) myIndex).printIndex();

        assertEquals(myIndex.getAllClassesNames().size(), 0);
        assertEquals(myIndex.getAllModulesNames().size(), 0);
        assertEquals(myIndex.getAllMethodsNames().size(), 0);
        assertEquals(myIndex.getAllFieldsNames().size(), 0);
        assertEquals(myIndex.getAllConstantsNames().size(), 0);
        assertEquals(myIndex.getAllGlobalVarsNames().size(), 0);
        assertEquals(myIndex.getAllAliasesNames().size(), 0);
        assertEquals(myIndex.getAllFieldAttrsNames().size(), 0);
    }

    private String getDataUrl() {
        final String path = PathUtil.getModuleDirPath(DeclarationsIndexTest.class) + "/" + PathUtil.getClassDir(DeclarationsIndexTest.class) + "/../data";

        return VirtualFileUtil.constructLocalUrl(path);
    }
}
