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

import java.util.StringTokenizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 3, 2007
 */
public class GemInfo
{
	private final String myName;
	private final String myVersion;

	public GemInfo(@Nonnull final String name, @Nonnull final String version)
	{
		myName = name;
		myVersion = version;
	}

	@Nonnull
	public String getName()
	{
		return myName;
	}

	@Nonnull
	public String getVersion()
	{
		return myVersion;
	}

	public static GemInfo create(@Nonnull final VirtualFile gemFile)
	{
		return create(gemFile.getName());
	}

	private static GemInfo create(@Nonnull final String name)
	{
		final StringTokenizer st = new StringTokenizer(name, "-");
		final String gemName = st.nextToken();
		final String gemVersion = st.nextToken();
		return new GemInfo(gemName, gemVersion);
	}

	@Nullable
	public static String getGemNameByUrl(@Nonnull final String gemsRootUrl, @Nonnull final String fileUrl)
	{
		final String s = fileUrl.substring(gemsRootUrl.length() + 1);
		final int slashIndex = s.indexOf('/');
		if(slashIndex != -1)
		{
			return s.substring(0, slashIndex);
		}
		return null;
	}
}
