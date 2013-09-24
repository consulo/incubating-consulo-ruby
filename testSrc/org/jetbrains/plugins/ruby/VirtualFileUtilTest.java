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

package org.jetbrains.plugins.ruby;

import com.intellij.testFramework.IdeaTestCase;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 23, 2007
 */
public class VirtualFileUtilTest extends IdeaTestCase {
    public void testRemoveExtension() {
        assertEquals("file", VirtualFileUtil.removeExtension("file.rb"));
        assertEquals("file", VirtualFileUtil.removeExtension("file"));
        assertEquals("file.rb", VirtualFileUtil.removeExtension("file.rb.rb"));
    }

    public void testGetRelativePath() {
        assertEquals("controllers/a.rb",
                VirtualFileUtil.getRelativePath("file://depot/app/controllers/a.rb",
                        "file://depot/app"));
        assertEquals("a.rb",
                VirtualFileUtil.getRelativePath("file://depot/app/controllers/a.rb",
                        "file://depot/app/controllers"));
        assertEquals(TextUtil.EMPTY_STRING,
                VirtualFileUtil.getRelativePath("file://depot/app/controllers",
                        "file://depot/app/controllers"));
        assertNull(VirtualFileUtil.getRelativePath("file://depot/app/controllers",
                "file://depot/app/controllers/a.rb"));
    }

    public void testPathToURL() {
        assertNull(VirtualFileUtil.pathToURL(null));
        assertEquals("file://C:/my/my.exe",
                VirtualFileUtil.pathToURL("C:\\my\\my.exe"));
        assertEquals("file://C:",
                VirtualFileUtil.pathToURL("C:"));
    }

    public void testGetParentDir() {
        assertNull(VirtualFileUtil.getParentDir(null));
        assertNull(VirtualFileUtil.getParentDir(""));
        assertEquals("", VirtualFileUtil.getParentDir("/"));
        assertEquals("file:/",
                VirtualFileUtil.getParentDir("file://my.exe"));
        assertEquals("file://c:",
                VirtualFileUtil.getParentDir("file://c:/my.exe"));
        assertEquals("file://c:/folder",
                VirtualFileUtil.getParentDir("file://c:/folder/my.exe"));
    }
}
