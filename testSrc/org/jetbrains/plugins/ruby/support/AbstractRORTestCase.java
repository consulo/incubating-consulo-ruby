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

package org.jetbrains.plugins.ruby.support;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.testFramework.IdeaTestCase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Oct 27, 2007
 */
public abstract class AbstractRORTestCase extends IdeaTestCase {
    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.loadRubySupport();
        TestUtil.loadRailsSupport();
    }

    protected String getDataUrl() {
        final String path =
                PathUtil.getModuleDirPath(getClass())
                        + VirtualFileUtil.VFS_PATH_SEPARATOR
                        + PathUtil.getClassDir(getClass())
                        + VirtualFileUtil.VFS_PATH_SEPARATOR + "data";
        return VirtualFileUtil.constructLocalUrl(path);
    }

    @Nullable
    protected VirtualFile getFileByRelativePath(@NotNull final String relativePath) {
        final String url = getDataUrl() + relativePath;
        return VirtualFileManager.getInstance().findFileByUrl(url);
    }    
}
