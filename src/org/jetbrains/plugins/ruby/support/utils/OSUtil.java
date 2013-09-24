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

package org.jetbrains.plugins.ruby.support.utils;

import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.SystemInfo;
import static com.intellij.openapi.util.io.FileUtil.*;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.Runner;

import java.io.File;
import java.util.StringTokenizer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 12, 2007
 */
public class OSUtil {
    private static final  String JRUBY_CLASSPATH_NAME = "CLASSPATH";
    private static final  String UNIX_PATH_NAME = "PATH";
    private static final String WINDOWS_PATH_NAME = "Path";

    //svn
    private static final String SVN_DEFAULT_UNIX_PATH = "/usr/bin";
    private static final String SVN_DEFAULT_MAC_PATH = "/usr/local/bin";
    private static final String SVN_DEFAULT_WIN_PATH = "c:";
    private static final String[] SVN_CMD_ARGS = new String[]{"svn", "help"};

    private static final Logger LOG = Logger.getInstance(OSUtil.class.getName());

    /**
     * Checks if SVN is in IDEA load path
     * @return true if SVN is in load path
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public static boolean isSVNInIDEALoadPath() {
        final GeneralCommandLine cmdLine =
                Runner.createAndSetupCmdLine(null, null, null, null, true, SVN_CMD_ARGS[0], SVN_CMD_ARGS[1]);
        try {
            Process process = cmdLine.createProcess();
            process.destroy();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Nullable
    public static String getPATHenvVariableName() {
        if (SystemInfo.isWindows) {
            return WINDOWS_PATH_NAME;
        }
        if (SystemInfo.isUnix){
            return UNIX_PATH_NAME;
        }
        LOG.error(RBundle.message("os.not.supported"));
        return null;
    }

    @Nullable
    public static String getJRubyCLASSPATHenvVariableName() {
        if (SystemInfo.isWindows || SystemInfo.isUnix){
            return JRUBY_CLASSPATH_NAME;
        }
        LOG.error(RBundle.message("os.not.supported"));
        return null;
    }

    public static String appendToPATHenvVariable(@Nullable final String path,
                                                 @NotNull final String additionalPath) {
        final String pathValue;
        if (TextUtil.isEmpty(path)) {
            pathValue = additionalPath;
        } else {
            pathValue = path + File.pathSeparatorChar + additionalPath;
        }
        return toSystemDependentName(pathValue);
    }

    /**
     * Checks if SVN is in extended LOAD path
     * @param svnPath path to svn
     * @param sdk Ruby SDK (check uses ruby interpetator)
     * @return true if SVN is in load path
     */
    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public static boolean isSVNInExtendedLoadPath(final @Nullable String svnPath, @NotNull final Sdk sdk) {
        //TODO reimplement with parsing PATH environment variable
        final Output output = RubyScriptRunner.runSystemCommand(sdk, svnPath, null, SVN_CMD_ARGS[0], SVN_CMD_ARGS[1]);
        return TextUtil.isEmpty(output.getStderr());
    }


    @NotNull
    public static String getDefaultSVNPath() {
        if (SystemInfo.isWindows) {
            return SVN_DEFAULT_WIN_PATH;
        }
        if (SystemInfo.isMac) {
            return SVN_DEFAULT_MAC_PATH;
        }
        if (SystemInfo.isUnix){
            return SVN_DEFAULT_UNIX_PATH;
        }
        LOG.error(RBundle.message("os.not.supported"));
        return "";
    }

    public static String getIdeaSystemPath() {
        return System.getenv().get(OSUtil.getPATHenvVariableName());
    }

    /**
     * Finds executable by name in standart path environment
     * @param exeName executable name, gem for example
     * @return path if found
     */
    @Nullable
    public static String findExecutableByName(@NotNull final String exeName){
        final String path = getIdeaSystemPath();
        final VirtualFileManager manager = VirtualFileManager.getInstance();
        if (path!=null){
            final StringTokenizer st = new StringTokenizer(path, File.pathSeparator);

            //tokens - are pathes with system-dependent slashes
            while (st.hasMoreTokens()){
                final String s = VirtualFileUtil.convertToVFSPathAndNormalizeSlashes(st.nextToken());
                final String possible_path = s + VirtualFileUtil.VFS_PATH_SEPARATOR + exeName;
                if (manager.findFileByUrl(VirtualFileUtil.constructLocalUrl(possible_path))!=null){
                    return possible_path;
                }
            }
        }
        return null;
    }

}
