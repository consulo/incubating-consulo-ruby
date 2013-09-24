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
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.IdeaTestCase;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubySupportLoader;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
public class RubyVirtualFileScannerTest extends IdeaTestCase {

    public void test1(){
        RubySupportLoader.loadRuby();
        String dataUrl = getDataUrl();
        final VirtualFile dataDir = VirtualFileManager.getInstance().findFileByUrl(dataUrl);
        Set<VirtualFile> files = scan(dataDir);
        assertEquals(files.size(), 9);
    }

    public void test2(){
        RubySupportLoader.loadRuby();
        String dataUrl = getDataUrl()+"/root";
        final VirtualFile dataDir = VirtualFileManager.getInstance().findFileByUrl(dataUrl);
        Set<VirtualFile> files = scan(dataDir);
        assertEquals(files.size(), 5);
    }

    public void test3(){
        RubySupportLoader.loadRuby();
        String dataUrl = getDataUrl()+"/root2";
        final VirtualFile dataDir = VirtualFileManager.getInstance().findFileByUrl(dataUrl);
        Set<VirtualFile> files = scan(dataDir);
        assertEquals(files.size(), 2);
    }

    public void test4(){
        RubySupportLoader.loadRuby();
        String dataUrl = getDataUrl()+"/root3";
        final VirtualFile dataDir = VirtualFileManager.getInstance().findFileByUrl(dataUrl);
        Set<VirtualFile> files = scan(dataDir);
        assertEquals(files.size(), 2);
    }

    public void test5(){
        RubySupportLoader.loadRuby();
        String dataUrl = getDataUrl()+"/fooo";
        final VirtualFile dataDir = VirtualFileManager.getInstance().findFileByUrl(dataUrl);
        Set<VirtualFile> files = scan(dataDir);
        assertEquals(files.size(), 0);
    }

    private static Set<VirtualFile> scan(final VirtualFile virtualFile) {
        final Set<VirtualFile> myFiles = new HashSet<VirtualFile>();
        RubyVirtualFileScanner.addRubyFiles(virtualFile, myFiles);
        return myFiles;
    }

    private String getDataUrl() {
        final String path =
                PathUtil.getModuleDirPath(RubyVirtualFileScannerTest.class)
                        + VirtualFileUtil.VFS_PATH_SEPARATOR
                        + PathUtil.getClassDir(RubyVirtualFileScannerTest.class)
                        + VirtualFileUtil.VFS_PATH_SEPARATOR + "data";
        return VirtualFileUtil.constructLocalUrl(path);
    }
}
