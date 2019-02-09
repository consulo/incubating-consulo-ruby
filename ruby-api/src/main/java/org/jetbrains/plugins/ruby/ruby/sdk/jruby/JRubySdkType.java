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

package org.jetbrains.plugins.ruby.ruby.sdk.jruby;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.JRubyIcons;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkType;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.util.SystemInfo;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Aug 22, 2007
 */
public class JRubySdkType extends RubySdkType
{
	@NonNls
	private static final String JRUBY_WIN_EXE = "jruby.bat";
	@NonNls
	private static final String JRUBY_UNIX_EXE = "jruby";
	/**
	 * @deprecated Don't use direct this constant(you can affect JRubySdkType), use getRubyExecutable()
	 */
	@NonNls
	private static String JRUBY_EXE;

	static
	{
		if(SystemInfo.isWindows)
		{
			//noinspection deprecation
			JRUBY_EXE = JRUBY_WIN_EXE;
		}
		else if(SystemInfo.isUnix)
		{
			//noinspection deprecation
			JRUBY_EXE = JRUBY_UNIX_EXE;
		}
		else
		{
			LOG.error(RBundle.message("os.not.supported"));
		}
	}

	public JRubySdkType()
	{
		super("JRUBY_SDK");
	}

	public static JRubySdkType getInstance()
	{
		return EP_NAME.findExtension(JRubySdkType.class);
	}

	public static boolean isJRubySDK(@Nullable final Sdk sdk)
	{
		return sdk != null && sdk.getSdkType() instanceof JRubySdkType;
	}

	@Override
	public String getRubyExecutable()
	{
		//noinspection deprecation
		return JRUBY_EXE;
	}

	@Override
	public String getPresentableName()
	{
		return RBundle.message("sdk.jruby.title");
	}

	@Override
	protected String suggestHomePath()
	{
		return RubySdkUtil.suggestJRubyHomePath();
	}

	@Override
	public Image getIcon()
	{
		return JRubyIcons.JRUBY_SDK_ICON_CLOSED;
	}
}
