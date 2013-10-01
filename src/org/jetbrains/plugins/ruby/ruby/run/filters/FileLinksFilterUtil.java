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

package org.jetbrains.plugins.ruby.ruby.run.filters;

import java.io.File;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.RubyFileType;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 16, 2008
 */
public class FileLinksFilterUtil
{
	private static final int LENGTH_THRESHOLD = 255;

	/**
	 * Searches File object by given file path. If file doesn't exist of is a directory
	 * method returns null. If file with path filePath doesn't exists method tries to find
	 * file at path: filePaht+".rb"
	 *
	 * @param filePath File path
	 * @return File
	 */
	@Nullable
	public static File getFileByRubyLink(@NotNull final String filePath)
	{
		File srcFile = new File(filePath);

		try
		{
			// sometimes ruby files are mentioned without extension, e.g. "examle.rb" -> "example"
			if(!srcFile.exists() || srcFile.isDirectory())
			{
				final File srcRBFile = new File(filePath + "." + RubyFileType.RUBY.getDefaultExtension());
				if(!srcFile.exists() || (srcFile.isDirectory() && srcRBFile.exists()))
				{
					srcFile = srcRBFile;
				}
			}
			if(srcFile.exists() && !srcFile.isDirectory())
			{
				return srcFile;
			}
		}
		catch(SecurityException e)
		{
			// Do nothing
		}
		return null;
	}

	public static boolean hasExeExtention(@NotNull final File srcFile)
	{
		// excludes .exe
		final String ext = VirtualFileUtil.getExtension(srcFile.getName());
		if(ext != null)
		{
			if("exe".equals(ext.toLowerCase()))
			{
				return true;
			}
		}
		return false;
	}

	public static String cutLineIfLong(final String line)
	{
		if(line.length() <= LENGTH_THRESHOLD)
		{
			return line;
		}
		return line.substring(0, LENGTH_THRESHOLD + 1);
	}
}
