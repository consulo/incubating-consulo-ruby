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

package org.jetbrains.plugins.ruby.addins.gems;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.support.utils.FileUtil;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.projectRoots.Sdk;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 1, 2007
 */
public class GemUtil
{

	/**
	 * Determinates existing path to gem executable script:
	 * {gems_bin}/{rubyScriptName}
	 * {gems_bin}/{rubyScriptName}.rb
	 *
	 * @param sdk            Ruby SDK. If null method will also return null
	 * @param rubyScriptName Executable ruby script name(e.g. "rails", "spec", "rake", etc.)
	 * @return path if can be found in gems bin folder, otherwise null
	 */
	@Nullable
	public static String getGemExecutableRubyScriptPath(@Nullable final Sdk sdk, @Nonnull final String rubyScriptName)
	{
		if(sdk == null)
		{
			return null;
		}
		String path = RubySdkUtil.getGemsBinFolderPath(sdk) + VirtualFileUtil.VFS_PATH_SEPARATOR + rubyScriptName;
		if(FileUtil.checkIfIsExistingFile(path))
		{
			return path;
		}
		path += "." + RubyFileType.INSTANCE.getDefaultExtension();

		if(FileUtil.checkIfIsExistingFile(path))
		{
			return path;
		}
		return null;
	}

	public static boolean isGemExecutableRubyScriptExists(@Nullable final Sdk sdk, @Nonnull final String rubyScriptName)
	{
		return getGemExecutableRubyScriptPath(sdk, rubyScriptName) != null;
	}
}
