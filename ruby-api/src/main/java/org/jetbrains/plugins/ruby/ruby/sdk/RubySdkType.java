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

import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.SdkImpl;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import consulo.ui.image.Image;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.File;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.07.2006
 */

public class RubySdkType extends SdkType
{

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

	static
	{
		if(SystemInfo.isWindows)
		{
			//noinspection deprecation
			RUBY_EXE = RUBY_WIN_EXE;
		}
		else if(SystemInfo.isUnix)
		{
			//noinspection deprecation
			RUBY_EXE = RUBY_UNIX_EXE;
		}
		else
		{
			LOG.error(RBundle.message("os.not.supported"));
		}
	}

	protected RubySdkType()
	{
		super(RUBY_SDK_NAME);
	}

	protected RubySdkType(final String type)
	{
		super(type);
	}

	public static RubySdkType getInstance()
	{
		return EP_NAME.findExtension(RubySdkType.class);
	}

	public static void findAndSaveGemsRootsBy(final SdkModificator sdkModificator)
	{
		final List<VirtualFile> gemsRoots = findGemsRoots(sdkModificator);
		for(VirtualFile gemsRoot : gemsRoots)
		{
			sdkModificator.addRoot(gemsRoot, GemOrderRootType.getInstance());
		}
	}

	@Nonnull
	protected static List<VirtualFile> findGemsRoots(@Nonnull final SdkModificator sdkModificator)
	{
		final List<VirtualFile> gemsRoots = new ArrayList<VirtualFile>();
		final VirtualFile[] roots = sdkModificator.getRoots(OrderRootType.SOURCES);
		for(VirtualFile root : roots)
		{
			final String url = root.getUrl();
			if(RubySdkUtil.isGemsRootUrl(url))
			{
				gemsRoots.add(root);
			}
		}
		return gemsRoots;
	}

	public String getRubyExecutable()
	{
		//noinspection deprecation
		return RUBY_EXE;
	}

	@Nonnull
	@Override
	public Collection<String> suggestHomePaths()
	{
		String s = suggestHomePath();
		if(s != null)
		{
			return Collections.singletonList(s);
		}
		return super.suggestHomePaths();
	}

	@Override
	public boolean canCreatePredefinedSdks()
	{
		return true;
	}

	protected String suggestHomePath()
	{
		return RubySdkUtil.suggestRubyHomePath();
	}

	public String getExePath()
	{
		return BIN_DIR + VirtualFileUtil.VFS_PATH_SEPARATOR + getRubyExecutable();
	}

	@Nonnull
	public String getGemsBinDirectory(@Nonnull final Sdk sdk)
	{
		return getSdkAdditionalData(sdk).getGemsBinDirectory();
	}

	public void setGemsBinDirectory(@Nonnull final Sdk sdk, @Nonnull final String path)
	{
		getSdkAdditionalData(sdk).setGemsBinDirectory(path);
	}

	@Override
	public boolean isValidSdkHome(final String path)
	{
		return (new File(path + getExePath())).exists();
	}

	@Override
	@Nullable
	public String getVersionString(final String sdkHome)
	{
		return getFullVersion(sdkHome);
	}

	@Override
	public String suggestSdkName(final String currentSdkName, final String sdkHome)
	{
		final String version = getShortVersion(sdkHome);
		return getPresentableName() + (TextUtil.isEmpty(version) ? TextUtil.EMPTY_STRING : " " + version);
	}

	/**
	 * Adds pathes from $LOAD_PATH into classpath
	 *
	 * @param sdk current SDK
	 */
	@Override
	public void setupSdkPaths(final Sdk sdk)
	{
		final VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
		final String rubyInterpreterExecutable = getVMExecutablePath(sdk);

		final Set<String> urls = new LinkedHashSet<String>();
		final String scriptSource = GET_LOAD_PATH_SCRIPT;
		final Output result = RubyScriptRunner.runScriptFromSource(rubyInterpreterExecutable, new String[]{}, scriptSource, new String[]{});
		final String loadPaths[] = TextUtil.splitByLines(result.getStdout());
		for(String s : loadPaths)
		{
			if(!s.trim().equals("."))
			{
				urls.add(VirtualFileUtil.constructLocalUrl(s));
			}
		}

		// Adding GEM pathes to search for gems
		final Output gemsPathesResult = RubyScriptRunner.runScriptFromSource(rubyInterpreterExecutable, new String[]{}, GET_GEM_PATHES_SCRIPT, new String[]{});
		final String gemPaths[] = TextUtil.splitByLines(gemsPathesResult.getStdout());
		for(String s : gemPaths)
		{
			if(!s.trim().equals("."))
			{
				urls.add(VirtualFileUtil.constructLocalUrl(s + GEMS_SUBDIR));
			}
		}

		File pluginPath = PluginManager.getPluginPath(RubySdkType.class);

		// trying to add rubystubs from plugin jar file
		final VirtualFile rubyStubsDir = LocalFileSystem.getInstance().findFileByIoFile(new File(pluginPath, RUBYSTUBS_DIR));
		if(rubyStubsDir != null)
		{
			LOG.assertTrue(rubyStubsDir != null, "main.rb file cannot be null");
			urls.add(rubyStubsDir.getUrl());
		}

		// WARNING: not all ruby LOAD_PATH may exist!
		final SdkModificator sdkModificator = sdk.getSdkModificator();
		for(String url : urls)
		{
			final VirtualFile vFile = virtualFileManager.findFileByUrl(url);
			if(vFile != null)
			{
				RubySdkUtil.addToSourceAndClasses(sdkModificator, vFile);
			}
		}
		findAndSaveGemsRootsBy(sdkModificator);

		sdkModificator.commitChanges();
	}

	@Override
	public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator)
	{
		return new RubySdkConfigurable();
	}

	@Override
	public void saveAdditionalData(SdkAdditionalData additionalData, Element additional)
	{
		if(additionalData instanceof RubySdkAdditionalData)
		{
			((RubySdkAdditionalData) additionalData).save(additional);
		}
	}

	@Override
	@Nonnull
	public SdkAdditionalData loadAdditionalData(@Nonnull final Sdk sdk, @Nullable Element additional)
	{
		return RubySdkAdditionalData.load(sdk, additional);
	}

	@Nonnull
	public String getBinPath(final Sdk sdk)
	{
		return sdk.getHomePath() + BIN_DIR;
	}

	@Nullable
	public String getVMExecutablePath(final Sdk sdk)
	{

		return sdk.getHomePath() + getExePath();
	}

	@Override
	public String getPresentableName()
	{
		return RBundle.message("sdk.ruby.title");
	}

	@Override
	public Image getIcon()
	{
		return RubyIcons.RUBY_ICON;
	}

	private RubySdkAdditionalData getSdkAdditionalData(@Nonnull final Sdk sdk)
	{
		RubySdkAdditionalData rubySdkAdditionalData = (RubySdkAdditionalData) sdk.getSdkAdditionalData();
		if(rubySdkAdditionalData == null)
		{
			rubySdkAdditionalData = (RubySdkAdditionalData) loadAdditionalData(sdk, null);
			((SdkImpl) sdk).setSdkAdditionalData(rubySdkAdditionalData);
		}
		return rubySdkAdditionalData;
	}

	@Nullable
	private String getShortVersion(final String sdkHome)
	{
		final Output output = RubyScriptRunner.runScriptFromSource(sdkHome + getExePath(), new String[]{}, GET_VERSION_SCRIPT, new String[]{});
		return getSDKVersionByOutput(output, false);
	}

	@Nullable
	private String getFullVersion(final String sdkHome)
	{
		final Output output = Runner.run(sdkHome + getExePath(), VERSION_ARG);
		return getSDKVersionByOutput(output, true);
	}

	private String getSDKVersionByOutput(@Nonnull final Output output, final boolean showErrorMsg)
	{
		final String errorTitle = RBundle.message("sdk.error.cannot.create.sdk.title");
		if(output.getStdout().contains("JAVA_HOME"))
		{
			if(showErrorMsg)
			{
				Messages.showErrorDialog(output.getStdout(), errorTitle);
			}
			return null;
		}
		else if(!TextUtil.isEmpty(output.getStderr()))
		{
			if(showErrorMsg)
			{
				Messages.showErrorDialog(output.getStderr(), errorTitle);
			}
			return null;
		}
		return output.getStdout();
	}

	@Override
	public boolean isRootTypeApplicable(OrderRootType type)
	{
		return type == OrderRootType.SOURCES ||
				type == OrderRootType.CLASSES ||
				type == OrderRootType.DOCUMENTATION ||
				type == GemOrderRootType.getInstance();
	}
}
