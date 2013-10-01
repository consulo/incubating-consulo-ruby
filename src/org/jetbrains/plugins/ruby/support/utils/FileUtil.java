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

import java.io.File;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 1, 2007
 */
public class FileUtil
{
	public static boolean checkIfIsExistingFile(@NotNull final String path)
	{
		final File file = new File(path);
		try
		{
			return file.exists() && file.isFile();
		}
		catch(Exception e)
		{
			return false;
		}
	}

	public static boolean checkIfIsExistingDirectory(@NotNull final String path)
	{
		final File file = new File(path);
		try
		{
			return file.exists() && file.isDirectory();
		}
		catch(Exception e)
		{
			return false;
		}
	}
}
