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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
public class PathUtil {
    @NonNls
    private static final String IDEA_RUNNER_TEST_ROOT = "classes/test/ruby";
    @NonNls
    private static final String IDEA_RUNNER_SOURCE_RUNNER = "classes/production/ruby";
    @NonNls
    private static final String ANT_RUNNER = "dist/testClasses";

    @Nullable
    public static String getDataPath(@NotNull Class s) {
        final String classDir = getClassDir(s);
        String moduleDir = getModuleDirPath(s);
        return classDir!=null && moduleDir!=null
                ? moduleDir + "/" + classDir+ "/data"
                : null;
    }

    @Nullable
    public static String getDataPath(@NotNull Class s, @NotNull final String relativePath) {
        return getDataPath(s) + "/" + relativePath;
    }

    @Nullable
    public static String getClassDir(@NotNull Class s) {
        String path = getClassFullPath(s);
// if tests are run, using ideaProjectRunner. testSrc - is test root
        String dataPath = getClassDirPath(path, IDEA_RUNNER_TEST_ROOT);
        if (dataPath!=null){
            return dataPath;
        }
// if tests are run, using ideaProjectRunner. testSrc - is source root
        dataPath = getClassDirPath(path, IDEA_RUNNER_SOURCE_RUNNER);
        if (dataPath!=null){
            return dataPath;
        }
// if tests are run using ant script
        dataPath = getClassDirPath(path, ANT_RUNNER);
        if (dataPath!=null){
            return dataPath;
        }
        return path;
    }

    public static String getClassFullPath(@NotNull final Class s) {
        String name = s.getSimpleName() + ".class";

        final URL url = s.getResource(name);
        return url.getPath();
    }

    @Nullable
    public static String getModuleDirPath(@NotNull Class s) {
        String path = getClassFullPath(s);
// if tests are run, using ideaProjectRunner. testSrc - is test root
        String moduleDir = getModuleDirPath(path, IDEA_RUNNER_TEST_ROOT);
        if (moduleDir!=null){
            return moduleDir;
        }
// if tests are run, using ideaProjectRunner. testSrc - is source root
        moduleDir = getModuleDirPath(path, IDEA_RUNNER_SOURCE_RUNNER);
        if (moduleDir!=null){
            return moduleDir;
        }
// if tests are run using ant script
        moduleDir = getModuleDirPath(path, ANT_RUNNER);
        if (moduleDir!=null){
            return moduleDir;
        }
        return path;
    }

    @Nullable
    private static String getModuleDirPath(@NotNull String s, @NotNull final String indicator){
        int n = s.indexOf(indicator);
        if (n==-1){
            return null;
        }
        return s.substring(0, n-1);
    }

    @Nullable
    private static String getClassDirPath(@NotNull String s, @NotNull final String indicator) {
        int n = s.indexOf(indicator);
        if (n==-1){
            return null;
        }
        s = "testSrc" + s.substring(n + indicator.length());
        s = s.substring(0, s.lastIndexOf('/'));
        return s;
    }

}
