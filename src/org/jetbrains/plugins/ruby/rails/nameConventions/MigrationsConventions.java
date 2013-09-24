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

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.rails.facet.configuration.StandardRailsPaths;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 08.05.2007
 */
public class MigrationsConventions {
    public static final String DB_SCHEMA_FILE = RailsConstants.DB_SCHEMA_FILE;

    public static boolean isMigrationFile(@NotNull final RVirtualFile rFile,
                                          @Nullable final Module module) {
        if (module == null){
            return false;
        }

        final String fileUrl = rFile.getContainingFileUrl();

        final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
        assert railsPaths != null; //Not null for modules with Rails Support

        final String viewsRoot =  railsPaths.getMigrationsRootURL();

        //TODO make more intelligent! check superclass!
        //noinspection RedundantIfStatement
        if (!fileUrl.startsWith(viewsRoot)) {
             return false;
        }
        return true;
    }

    /**
     * Searches schema.rb file.
     * @param migrDir Migrations root
     * @return file if found any, null otherwise
     */
    public static VirtualFile getSchema(@NotNull final VirtualFile migrDir) {
        final VirtualFile dbDir = migrDir.getParent();
        assert  dbDir != null;
        return dbDir.findChild(MigrationsConventions.DB_SCHEMA_FILE);
    }

    public static String getSchemaURL(final Module module) {
        final StandardRailsPaths railsPaths = RailsFacetUtil.getRailsAppPaths(module);
        assert railsPaths != null;

        final String migrUrl = railsPaths.getMigrationsRootURL();
        return VirtualFileUtil.getParentDir(migrUrl)
               + VirtualFileUtil.VFS_PATH_SEPARATOR
               + MigrationsConventions.DB_SCHEMA_FILE;
    }
}
