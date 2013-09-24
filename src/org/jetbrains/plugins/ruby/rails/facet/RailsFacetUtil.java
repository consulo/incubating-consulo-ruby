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

package org.jetbrains.plugins.ruby.rails.facet;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ActionRunner;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;
import org.jetbrains.plugins.ruby.rails.facet.configuration.BaseRailsFacetConfiguration;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Apr 14, 2008
 */
public class RailsFacetUtil {
    /**
     * @param module Ruby or Java module.
     * @return If module contain Rails/JRails facet returns true, otherwise false
     */
    public static boolean hasRailsSupport(@Nullable final Module module) {
        return module != null && BaseRailsFacet.getInstance(module) != null;
    }

    /**
     * If module contain Rails/JRails facet returns Rails Application
     * Home directory path, otherwise null;
     * @param module Ruby or Java module.
     * @return Rails Application Home directory path for given module
     */
    @Nullable
    public static String getRailsAppHomeDirPath(@NotNull final Module module) {
        final BaseRailsFacetConfiguration conf = getRailsFacetConfiguration(module);
        return conf != null ? conf.getRailsApplicationRootPath() : null;
    }

    /**
     * @param module Module
     * @return If module hasn't rails support - null, otherwize RailsPathesSettings
     */
    @Nullable
    public static StandardRailsPaths getRailsAppPaths(@NotNull final Module module) {
        final BaseRailsFacet baseRailsFacet = BaseRailsFacet.getInstance(module);
        if (baseRailsFacet == null) {
            return null;
        }
        return baseRailsFacet.getConfiguration().getPaths();
    }

    /**
     * If module contain Rails/JRails facet returns Rails Application
     * Home directory path orl, otherwise null;
     * @param module Ruby or Java module.
     * @return Rails Application Home directory path url for given module
     */
    @Nullable
    public static String getRailsAppHomeDirPathUrl(@NotNull final Module module) {
        final BaseRailsFacetConfiguration conf = getRailsFacetConfiguration(module);
        return conf != null ? conf.getRailsApplicationRootPathUrl() : null;
    }

    /**
     * If module contain Rails/JRails facet returns Rails Application
     * Home directory file, otherwise null;
     * @param module Ruby or Java module.
     * @return Rails Application Home directory file for given module
     */
    @Nullable
    public static VirtualFile getRailsAppHomeDir(@NotNull final Module module) {
        final String path = getRailsAppHomeDirPath(module);
        if (path == null) {
            return null;
        }

        return VirtualFileUtil.findFileByLocalPath(path);
    }

    /**
     * If module contain Rails/JRails facet returns Rails Facet configuration, otherwise null;
     * @param module Ruby or Java module.
     * @return Rails Facet Configuration for given module
     */
    @Nullable
    public static BaseRailsFacetConfiguration getRailsFacetConfiguration(@NotNull final Module module) {
        final BaseRailsFacet railsFacet = BaseRailsFacet.getInstance(module);
        if (railsFacet != null) {
            return railsFacet.getConfiguration();
        }
        return null;
    }
    /**
     * Refreshes in VFS all content under Rails Application Home Directory for given module. If module doens't
     * contain rails support method does nothing
     * @param module Some module
     */
    public static void refreshRailsAppHomeContent(final Module module) {
        final String homeDirPath = getRailsAppHomeDirPath(module);

        if (homeDirPath != null) {
            IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable() {
                public void run() throws Exception {
                    final VirtualFile moduleRoot = VirtualFileUtil.refreshAndFindFileByLocalPath(homeDirPath);
                    if (moduleRoot != null) {
                        moduleRoot.refresh(false, true);
                    }
                }
            });
        }
    }
}
