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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.support.utils.RubyVirtualFileScanner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.05.2007
 */
public class GeneralConventions {
    public static final String ACTIVE_SUPPORT = RailsConstants.SDK_ACTIVE_SUPPORT;

    /**
     *
     * @param activeSupportFileUrl active_support
     * @return list of files
     */
    public static List<VirtualFile> getBuiltCoreExtention(@NotNull final String activeSupportFileUrl) {
        final VirtualFileManager manager = VirtualFileManager.getInstance();

        final String url =
                VirtualFileUtil.removeExtension(activeSupportFileUrl)
                        + VirtualFileUtil.VFS_PATH_SEPARATOR
                        + RailsConstants.SDK_ACTIVE_SUPPORT_CORE_EXT;
        final VirtualFile dir = manager.findFileByUrl(url );
        if (dir == null) {
            return Collections.emptyList();
        }
        final Set<VirtualFile> files = new HashSet<VirtualFile>();
        RubyVirtualFileScanner.addRubyFiles(dir, files);
        final ArrayList<VirtualFile> filesList = new ArrayList<VirtualFile>();
        for (VirtualFile virtualFile : files) {
            filesList.add(virtualFile);
        }
        return filesList;
    }
}
