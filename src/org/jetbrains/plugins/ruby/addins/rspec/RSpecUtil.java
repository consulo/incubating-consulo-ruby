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

package org.jetbrains.plugins.ruby.addins.rspec;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.Function;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.addins.gems.GemUtil;
import org.jetbrains.plugins.ruby.addins.gems.GemsRunner;
import org.jetbrains.plugins.ruby.rails.RailsConstants;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Sep 5, 2007
 */
public class RSpecUtil {
    private static final Logger LOG = Logger.getInstance(RSpecUtil.class.getName());

    public static final String RSPEC_PROJECT_SVN_URL = "svn://rubyforge.org/var/svn/rspec/";
    public static final String RSPEC_PROJECT_SVN_TAGS_URL = RSPEC_PROJECT_SVN_URL+ "tags/";
    public static final String RSPEC_PLUGIN_NAME = "rspec ";
    public static final String RSPEC_RAILS_PLUGIN_NAME = "rspec_on_rails";
    public static final String RSPEC_TAG_CURRENT = "CURRENT";
    public static final String RSPEC_TAG_TRUNK = "trunk";
    public static final String RSPEC_TAG_REL_PREFIX = "REL_";
    public static final String RSPEC_TAG_XYZ = "X_Y_X";
    public static final String RSPEC_VERSION_PREFIX = "RSpec-";
    public static final String RSPEC_VERSION_ARG = RailsConstants.PARAM_VERSION;
    public static final String SPEC_GENERATOR_NAME = "rspec";

    public static final String SVN_PATH_SEPARATOR = "/";
    @NonNls
    public static final String RSPEC_HOME_PAGE_URL = "http://rspec.rubyforge.org";
    public static final String SPECS_FOLDER = "spec";
    public static final String COLOURED_COMMAND_LINE_ARG = "-c";
    public static final String WIN_32_CONSOLE_GEM = "win32console";

    private static final String SPEC_TEST_FILE_SUFFIX = "_spec.rb";

    public static boolean checkIfRSpecGemExists(@Nullable final ProjectJdk sdk) {
        return GemUtil.isGemExecutableRubyScriptExists(sdk, RailsConstants.RSPEC_GEM_EXECUTABLE);
    }

    public static String getRSpecPluginCurrentUrl() {
        return getRSpecPluginUrl(RSPEC_TAG_CURRENT);
    }

    public static String getRSpecPluginTrunkUrl() {
        return RSPEC_PROJECT_SVN_URL + RSPEC_TAG_TRUNK +  SVN_PATH_SEPARATOR + RSPEC_PLUGIN_NAME;
    }

    public static String getRSpecPluginXYZUrl() {
        return getRSpecPluginUrl(RSPEC_TAG_XYZ);
    }

    public static String getRSpecRailsPluginCurrentUrl() {
        return getRSpecRailsPluginUrl(RSPEC_TAG_CURRENT);
    }

    public static String getRSpecRailsPluginTrunkUrl() {
        return RSPEC_PROJECT_SVN_URL + RSPEC_TAG_TRUNK + SVN_PATH_SEPARATOR + RSPEC_RAILS_PLUGIN_NAME;
    }

    public static String getRSpecRailsPluginXYZUrl() {
        return getRSpecRailsPluginUrl(RSPEC_TAG_XYZ);
    }

    @Nullable
    public static String getRSpecGemVersion(@NotNull final ProjectJdk sdk,
                                            final boolean runWithModalProgress,
                                            @Nullable final Function<Object, Boolean> shouldCancelFun) {
        final Output output;
        try {
            final String progressTitle = RBundle.message("execution.get.rspec.gem.version");
            final Runner.ExecutionMode mode;
            if (runWithModalProgress) {
                mode = new Runner.ModalProgressMode(progressTitle);
            } else {
                mode = new Runner.SameThreadMode(progressTitle);
            }
            if (shouldCancelFun != null) {
                mode.setShouldCancelFun(shouldCancelFun);
            }
            output = GemsRunner.runGemsExecutableScript(sdk, null, RailsConstants.RSPEC_GEM_EXECUTABLE, null, mode,
                                                        false, null, RSPEC_VERSION_ARG);
        } catch (Exception e) {
            LOG.error(e);
            return null;
        }
        if (output == null) {
            return null;
        }
        final String stdout = output.getStdout();
        if (TextUtil.isEmpty(stdout)) {
            return null;
        }
        if (!stdout.contains(RSPEC_VERSION_PREFIX)) {
            return null;
        }
        final int start = stdout.indexOf(RSPEC_VERSION_PREFIX);
        int end = start + RSPEC_VERSION_PREFIX.length();
        for (; end < stdout.length(); end++) {
            if (Character.isWhitespace(stdout.charAt(end))) {
                break;
            }
        }
        if (end != start) {
            return stdout.substring(start, end);
        }
        return null;
    }

    @Nullable
    public static String getRSpecGemExecutablePath(@NotNull final ProjectJdk sdk) {
        return GemUtil.getGemExecutableRubyScriptPath(sdk, RailsConstants.RSPEC_GEM_EXECUTABLE);
    }

    public static String getRSpecGemTag(@Nullable final String rspecVersion) {
        final String rSpecVersionTag = rspecVersion == null || !rspecVersion.startsWith(RSPEC_VERSION_PREFIX)
                ? RSPEC_TAG_XYZ
                : rspecVersion.substring(RSPEC_VERSION_PREFIX.length()).replaceAll("\\.", "_");
        return RSPEC_TAG_REL_PREFIX + rSpecVersionTag;
    }

    public static String getRSpecPluginUrl(final String tag) {
        return RSPEC_PROJECT_SVN_TAGS_URL + tag + SVN_PATH_SEPARATOR + RSPEC_PLUGIN_NAME;
    }

    public static String getRSpecRailsPluginUrl(final String tag) {
        return RSPEC_PROJECT_SVN_TAGS_URL + tag + SVN_PATH_SEPARATOR+ RSPEC_RAILS_PLUGIN_NAME;
    }

 
   /* Requires root privilegies

    public static void installRSpecGem(final Module module, final ProjectJdk sdk) {
       final Project project = module.getProject();

        try {
            final ProcessListener listener = new ProcessAdapter() {
                public void processTerminated(final ProcessEvent event) {
                    RModuleUtil.synchronizeFSChanged();
                }
            };

            final String[] params =
                    new String[]{RailsUtil.getGemExecutablePath(sdk),
                                 RailsConstants.INSTALL_PARAMETER, RailsConstants.RSPEC_GEM_NAME};

            final RubyScriptRunnerArgumentsProvider provider =
                    new RubyScriptRunnerArgumentsProvider(params, null, null);

            final String title = RBundle.message("rails.plugins.install.process.title", module.getName());
            ConsoleRunner.run(project, listener,
                              new Filter[]{new RFileLinksFilter(module)},
                              null, true, title, null, provider);
        } catch (Exception exp) {
            String errorMessage = GeneratorsUtil.filterIoExceptionMessage(exp.getMessage());
            if (errorMessage == null || errorMessage.length() == 0) {
                errorMessage = exp.toString();
            }

            Messages.showErrorDialog(module.getProject(), errorMessage, RBundle.message("rails.plugins.install.error.title"));
        }
    }*/

    @NotNull
    public static String getRailsSpecFolderPathOrUrl(@NotNull final String railsAppRootPathOrUlr) {
        return railsAppRootPathOrUlr + VirtualFileUtil.VFS_PATH_SEPARATOR + SPECS_FOLDER;
    }

    @NotNull
    public static String getRailsSpecScriptPathOrUrl(@NotNull final String railsAppRootPathOrUlr) {
        return railsAppRootPathOrUlr + VirtualFileUtil.VFS_PATH_SEPARATOR + RailsConstants.SPECS_SCRIPT_PATH;
    }

    @SuppressWarnings({"BooleanMethodIsAlwaysInverted"})
    public static boolean isSpecScriptSupportInstalledInRailsProject(@NotNull final String rorAppliactionContentRootUrl) {
        final VirtualFileManager manager = VirtualFileManager.getInstance();
        final VirtualFile specFolder = manager.findFileByUrl(getRailsSpecFolderPathOrUrl(rorAppliactionContentRootUrl));
        if (VirtualFileUtil.fileExists(specFolder)) {
            final VirtualFile specScript = manager.findFileByUrl(getRailsSpecScriptPathOrUrl(rorAppliactionContentRootUrl));
            return VirtualFileUtil.fileExists(specScript);
        }
        return false;
    }

    public static boolean isRSpecTestFile(@Nullable final VirtualFile file) {
        //noinspection SimplifiableIfStatement
        return  file != null
                && !file.isDirectory()
                && isFileWithRSpecTestFileName(file);
    }

    public static boolean isFileWithRSpecTestFileName(VirtualFile file) {
        return file.getName().endsWith(SPEC_TEST_FILE_SUFFIX);
    }
}
