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

package org.jetbrains.plugins.ruby.ruby.sdk;

import consulo.annotation.component.ExtensionImpl;
import consulo.application.Application;
import consulo.application.util.SystemInfo;
import consulo.container.plugin.PluginManager;
import consulo.content.OrderRootType;
import consulo.content.base.BinariesOrderRootType;
import consulo.content.base.DocumentationOrderRootType;
import consulo.content.base.SourcesOrderRootType;
import consulo.content.bundle.*;
import consulo.localize.LocalizeValue;
import consulo.logging.Logger;
import consulo.ui.ex.awt.Messages;
import consulo.ui.image.Image;
import consulo.virtualFileSystem.LocalFileSystem;
import consulo.virtualFileSystem.VirtualFile;
import consulo.virtualFileSystem.VirtualFileManager;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jdom.Element;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.run.Output;
import org.jetbrains.plugins.ruby.ruby.run.RubyScriptRunner;
import org.jetbrains.plugins.ruby.ruby.run.Runner;
import org.jetbrains.plugins.ruby.ruby.sdk.gemRootType.GemOrderRootType;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.07.2006
 */

@ExtensionImpl
public class RubySdkType extends SdkType {

    /**
     * "/bin"
     */
    @NonNls
    public static final String BIN_DIR = VirtualFileUtil.VFS_PATH_SEPARATOR + "bin";

    //pathes use VFS path separator
    /**
     * "/gems"
     */
    @NonNls
    public static final String GEMS_SUBDIR = VirtualFileUtil.VFS_PATH_SEPARATOR + "gems";
    @NonNls
    public static final String RUBYSTUBS_DIR = "rubystubs";
    @NonNls
    public static final String MAC_OS_BUNDLED_RUBY_PATH_PREFIX = "/System/Library/Frameworks/Ruby.framework/Versions";
    @NonNls
    public static final String MAC_OS_BUNDLED_RUBY_GEM_BIN_PATH = "/usr/bin";
    protected static final Logger LOG = Logger.getInstance(RubySdkType.class.getName());
    @NonNls
    private static final String GET_LOAD_PATH_SCRIPT = "puts $LOAD_PATH";
    @NonNls
    private static final String GET_GEM_PATHES_SCRIPT = "require 'rubygems'; puts Gem.path";
    @NonNls
    private static final String GET_VERSION_SCRIPT = "print VERSION";
    @NonNls
    private static final String VERSION_ARG = "--version";
    @NonNls
    private static final String JAR = ".jar";
    @NonNls
    private static final String RUBY_SDK_NAME = "RUBY_SDK";
    @NonNls
    private static final String RUBY_WIN_EXE = "ruby.exe";
    @NonNls
    private static final String RUBY_UNIX_EXE = "ruby";
    /**
     * @deprecated Don't use direct this constant(you can affect JRubySdkType), use getRubyExecutable()
     */
    @NonNls
    private static String RUBY_EXE;

    static {
        if (SystemInfo.isWindows) {
            //noinspection deprecation
            RUBY_EXE = RUBY_WIN_EXE;
        }
        else if (SystemInfo.isUnix) {
            //noinspection deprecation
            RUBY_EXE = RUBY_UNIX_EXE;
        }
        else {
            LOG.error(RBundle.message("os.not.supported"));
        }
    }

    public RubySdkType() {
        super(RUBY_SDK_NAME, LocalizeValue.localizeTODO(RBundle.message("sdk.ruby.title")), RubyIcons.RUBY_ICON);
    }

    protected RubySdkType(final String type, LocalizeValue displayName, Image icon) {
        super(type, displayName, icon);
    }

    public static RubySdkType getInstance() {
        return Application.get().getExtensionPoint(SdkType.class).findExtensionOrFail(RubySdkType.class);
    }

    public static void findAndSaveGemsRootsBy(final SdkModificator sdkModificator) {
        final List<VirtualFile> gemsRoots = findGemsRoots(sdkModificator);
        for (VirtualFile gemsRoot : gemsRoots) {
            sdkModificator.addRoot(gemsRoot, GemOrderRootType.ID);
        }
    }

    @Nonnull
    protected static List<VirtualFile> findGemsRoots(@Nonnull final SdkModificator sdkModificator) {
        final List<VirtualFile> gemsRoots = new ArrayList<VirtualFile>();
        final VirtualFile[] roots = sdkModificator.getRoots(SourcesOrderRootType.ID);
        for (VirtualFile root : roots) {
            final String url = root.getUrl();
            if (RubySdkUtil.isGemsRootUrl(url)) {
                gemsRoots.add(root);
            }
        }
        return gemsRoots;
    }

    public String getRubyExecutable() {
        //noinspection deprecation
        return RUBY_EXE;
    }

    @Nonnull
    @Override
    public Collection<String> suggestHomePaths() {
        String s = suggestHomePath();
        if (s != null) {
            return Collections.singletonList(s);
        }
        return super.suggestHomePaths();
    }

    @Override
    public boolean canCreatePredefinedSdks() {
        return true;
    }

    protected String suggestHomePath() {
        return RubySdkUtil.suggestRubyHomePath();
    }

    public String getExePath() {
        return BIN_DIR + VirtualFileUtil.VFS_PATH_SEPARATOR + getRubyExecutable();
    }

    @Nonnull
    public String getGemsBinDirectory(@Nonnull final Sdk sdk) {
        return getSdkAdditionalData(sdk).getGemsBinDirectory();
    }

    public void setGemsBinDirectory(@Nonnull final Sdk sdk, @Nonnull final String path) {
        getSdkAdditionalData(sdk).setGemsBinDirectory(path);
    }

    @Override
    public boolean isValidSdkHome(final String path) {
        return (new File(path + getExePath())).exists();
    }

    @Override
    @Nullable
    public String getVersionString(final String sdkHome) {
        return getFullVersion(sdkHome);
    }

    @Override
    public String suggestSdkName(final String currentSdkName, final String sdkHome) {
        final String version = getShortVersion(sdkHome);
        return getDisplayName().get() + (TextUtil.isEmpty(version) ? TextUtil.EMPTY_STRING : " " + version);
    }

    /**
     * Adds pathes from $LOAD_PATH into classpath
     *
     * @param sdk current SDK
     */
    @Override
    public void setupSdkPaths(final Sdk sdk) {
        final VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
        final String rubyInterpreterExecutable = getVMExecutablePath(sdk);

        final Set<String> urls = new LinkedHashSet<String>();
        final String scriptSource = GET_LOAD_PATH_SCRIPT;
        final Output result = RubyScriptRunner.runScriptFromSource(rubyInterpreterExecutable, new String[]{}, scriptSource, new String[]{});
        final String loadPaths[] = TextUtil.splitByLines(result.getStdout());
        for (String s : loadPaths) {
            if (!s.trim().equals(".")) {
                urls.add(VirtualFileUtil.constructLocalUrl(s));
            }
        }

        // Adding GEM pathes to search for gems
        final Output gemsPathesResult = RubyScriptRunner.runScriptFromSource(rubyInterpreterExecutable, new String[]{}, GET_GEM_PATHES_SCRIPT, new String[]{});
        final String gemPaths[] = TextUtil.splitByLines(gemsPathesResult.getStdout());
        for (String s : gemPaths) {
            if (!s.trim().equals(".")) {
                urls.add(VirtualFileUtil.constructLocalUrl(s + GEMS_SUBDIR));
            }
        }

        File pluginPath = PluginManager.getPluginPath(RubySdkType.class);

        // trying to add rubystubs from plugin jar file
        final VirtualFile rubyStubsDir = LocalFileSystem.getInstance().findFileByIoFile(new File(pluginPath, RUBYSTUBS_DIR));
        if (rubyStubsDir != null) {
            LOG.assertTrue(rubyStubsDir != null, "main.rb file cannot be null");
            urls.add(rubyStubsDir.getUrl());
        }

        // WARNING: not all ruby LOAD_PATH may exist!
        final SdkModificator sdkModificator = sdk.getSdkModificator();
        for (String url : urls) {
            final VirtualFile vFile = virtualFileManager.findFileByUrl(url);
            if (vFile != null) {
                RubySdkUtil.addToSourceAndClasses(sdkModificator, vFile);
            }
        }
        findAndSaveGemsRootsBy(sdkModificator);

        sdkModificator.commitChanges();
    }

    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return new RubySdkConfigurable();
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof RubySdkAdditionalData) {
            ((RubySdkAdditionalData) additionalData).save(additional);
        }
    }

    @Override
    @Nonnull
    public SdkAdditionalData loadAdditionalData(@Nonnull final Sdk sdk, @Nullable Element additional) {
        return RubySdkAdditionalData.load(sdk, additional);
    }

    @Nonnull
    public String getBinPath(final Sdk sdk) {
        return sdk.getHomePath() + BIN_DIR;
    }

    @Nullable
    public String getVMExecutablePath(final Sdk sdk) {

        return sdk.getHomePath() + getExePath();
    }


    private RubySdkAdditionalData getSdkAdditionalData(@Nonnull final Sdk sdk) {
        RubySdkAdditionalData rubySdkAdditionalData = (RubySdkAdditionalData) sdk.getSdkAdditionalData();
        if (rubySdkAdditionalData == null) {
            rubySdkAdditionalData = new RubySdkAdditionalData();
        }
        return rubySdkAdditionalData;
    }

    @Nullable
    private String getShortVersion(final String sdkHome) {
        final Output output = RubyScriptRunner.runScriptFromSource(sdkHome + getExePath(), new String[]{}, GET_VERSION_SCRIPT, new String[]{});
        return getSDKVersionByOutput(output, false);
    }

    @Nullable
    private String getFullVersion(final String sdkHome) {
        final Output output = Runner.run(sdkHome + getExePath(), VERSION_ARG);
        return getSDKVersionByOutput(output, true);
    }

    private String getSDKVersionByOutput(@Nonnull final Output output, final boolean showErrorMsg) {
        final String errorTitle = RBundle.message("sdk.error.cannot.create.sdk.title");
        if (output.getStdout().contains("JAVA_HOME")) {
            if (showErrorMsg) {
                Messages.showErrorDialog(output.getStdout(), errorTitle);
            }
            return null;
        }
        else if (!TextUtil.isEmpty(output.getStderr())) {
            if (showErrorMsg) {
                Messages.showErrorDialog(output.getStderr(), errorTitle);
            }
            return null;
        }
        return output.getStdout();
    }

    @Override
    public boolean isRootTypeApplicable(String type) {
        return SourcesOrderRootType.ID.equals(type) ||
            BinariesOrderRootType.ID.equals(type) ||
            DocumentationOrderRootType.ID.equals(type) ||
            GemOrderRootType.ID.equals(type);
    }
}
