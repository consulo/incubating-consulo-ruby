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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCache;
import org.jetbrains.plugins.ruby.ruby.cache.fileCache.impl.RubyFilesCacheImpl;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfoFactory;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFilesStorage;
import org.jetbrains.plugins.ruby.ruby.cache.info.impl.RFilesStorageImpl;

import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
@SuppressWarnings({"ConstantConditions"})
public class MockFilesCache extends RubyFilesCacheImpl implements RubyFilesCache {
    private RFilesStorageImpl myRFilesStorage;

    public MockFilesCache(final Project project) {
        super(project, "Mashen'ka cache");
        myRFilesStorage = new RFilesStorageImpl();
    }


    public void setupFileCache(final boolean runProcessWithProgressSynchronously) {
    }

    public void dispose() {
    }

    protected void onClose() {
    }

    public void setCacheFilePath(@NotNull final String path) {
    }

    public void setCacheRootURLs(@NotNull final String[] urls) {
    }

    @NotNull
    public RFileInfo getUp2DateFileInfo(@NotNull final VirtualFile file) {
        return RFileInfoFactory.createRFileInfo(myProject, file);
    }

    @NotNull
    public Set<String> getAllUrls() {
        return myRFilesStorage.getAllUrls();
    }


    public RFilesStorage getRFilesStorage() {
        return myRFilesStorage;
    }

    public String toString() {
        return super.toString() + " It is Mock storage.";
    }    
}
