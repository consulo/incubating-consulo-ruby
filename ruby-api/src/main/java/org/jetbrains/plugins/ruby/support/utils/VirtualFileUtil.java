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

import static com.intellij.openapi.util.io.FileUtil.toSystemIndependentName;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 05.03.2007
 */
public class VirtualFileUtil
{
	@NonNls
	public static final char VFS_PATH_SEPARATOR = '/';

	/**
	 * @param dir  directory
	 * @param name file name
	 * @return url for file with name in directory dir
	 */
	public static String constructUrl(@Nonnull final VirtualFile dir, final String name)
	{
		return dir.getUrl() + VFS_PATH_SEPARATOR + name;
	}

	/**
	 * Searches files for list of urls
	 *
	 * @param urls Urls
	 * @return found files or empty array
	 */
	@Nonnull
	public static VirtualFile[] getFiles(@Nonnull final List<String> urls)
	{
		final VirtualFileManager virtualFileManager = VirtualFileManager.getInstance();
		final List<VirtualFile> files = new ArrayList<VirtualFile>();
		for(String url : urls)
		{
			if(TextUtil.isEmpty(url))
			{
				continue;
			}
			final VirtualFile file = virtualFileManager.findFileByUrl(url);
			if(file != null)
			{
				files.add(file);
			}
		}
		return files.toArray(new VirtualFile[files.size()]);
	}

	/**
	 * @param url Url for virtual file
	 * @return file name with extension
	 */
	@Nonnull
	public static String getFileName(@Nonnull final String url)
	{
		final int index = url.lastIndexOf(VFS_PATH_SEPARATOR);
		return index < 0 ? url : url.substring(index + 1);
	}

	/**
	 * @param url Url for virtual file
	 * @return url for parent directory of virtual file
	 */
	@Nullable
	public static String getParentDir(@Nullable final String url)
	{
		if(url == null)
		{
			return null;
		}
		final int index = url.lastIndexOf(VFS_PATH_SEPARATOR);
		return index < 0 ? null : url.substring(0, index);
	}

	/**
	 * @param fileName Virtual file name
	 * @return file extension
	 */
	@Nullable
	public static String getExtension(@Nonnull final String fileName)
	{
		int index = fileName.lastIndexOf('.');
		return index < 0 ? null : fileName.substring(index + 1);
	}

	/**
	 * @param fileName virtual file name
	 * @return name without extension
	 */
	@Nonnull
	public static String removeExtension(@Nonnull final String fileName)
	{
		int i = fileName.length() - 1;
		for(; i >= 0; i--)
		{
			if(fileName.charAt(i) == '.')
			{
				return fileName.substring(0, i);
			}
		}
		return fileName;
	}

	/**
	 * Converts OS depended path to VirtualFile url
	 *
	 * @param path Path
	 * @return url
	 */
	@Nonnull
	public static String constructLocalUrl(@Nonnull final String path)
	{
		return VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, toSystemIndependentName(path));
	}

	@Nonnull
	public static String getNameWithoutExtension(@Nonnull final String fileName)
	{
		int index = fileName.lastIndexOf('.');
		return index < 0 ? fileName : fileName.substring(0, index);
	}

	/**
	 * For comparators. Compares files pathes.
	 *
	 * @param file1 file1
	 * @param file2 file2
	 * @return compare result
	 */
	public static int compareVirtualFiles(@Nonnull final VirtualFile file1, @Nonnull final VirtualFile file2)
	{
		String path1 = file1.getPath();
		String path2 = file2.getPath();
		return path1.compareToIgnoreCase(path2);
	}

	public static boolean isValid(@Nullable final VirtualFile file)
	{
		return file != null && file.isValid();
	}

	public static String buildUrl(@Nonnull final String rootUrl, @Nonnull final String relativePath)
	{
		return rootUrl +
				//#TODO normalize
				(rootUrl.endsWith(String.valueOf(VFS_PATH_SEPARATOR)) ? "" : VFS_PATH_SEPARATOR) + toSystemIndependentName(relativePath);
	}

	public static String buildSystemIndependentPath(@Nonnull final String rootPath, @Nonnull final String relativePath)
	{
		final String rootPathIN = toSystemIndependentName(rootPath);
		return rootPathIN + (rootPathIN.endsWith(String.valueOf(VFS_PATH_SEPARATOR)) ? "" : VFS_PATH_SEPARATOR) + toSystemIndependentName(relativePath);
	}

	/**
	 * Converst OS depended path to VirtualFile url.
	 *
	 * @param path Path
	 * @return If path is null or empty string returns null, otherwise  url
	 */
	@Nullable
	public static String pathToURL(@Nullable final String path)
	{
		if(TextUtil.isEmpty(path))
		{
			return null;
		}
		assert path != null;
		return constructLocalUrl(path);
	}

	public static VirtualFile findFileByLocalPath(@Nonnull final String generateScriptPath)
	{
		return VirtualFileManager.getInstance().findFileByUrl(constructLocalUrl(generateScriptPath));
	}

	public static VirtualFile refreshAndFindFileByLocalPath(@Nonnull final String generateScriptPath)
	{
		return VirtualFileManager.getInstance().refreshAndFindFileByUrl(constructLocalUrl(generateScriptPath));
	}

	public static boolean fileExists(@Nullable final VirtualFile file)
	{
		return file != null && file.exists();
	}

	@Nullable
	public static String getRelativePath(@Nonnull final String filePathOrUrl, @Nonnull final String rootPathOrUrl)
	{
		if(filePathOrUrl.length() < rootPathOrUrl.length())
		{
			return null;
		}
		String path = filePathOrUrl.substring(rootPathOrUrl.length());
		if(path.length() > 0 && path.charAt(0) == VFS_PATH_SEPARATOR)
		{
			path = path.substring(1);
		}
		return path;
	}

	public static class VirtualFilesComparator implements Comparator<VirtualFile>
	{
		@Override
		public int compare(VirtualFile file1, VirtualFile file2)
		{
			return compareVirtualFiles(file1, file2);
		}
	}

	@Nonnull
	public static String convertToVFSPathAndNormalizeSlashes(@Nonnull final String path)
	{
		final String newPath = toSystemIndependentName(path);
		if(newPath.length() != 0 && newPath.charAt(newPath.length() - 1) == VFS_PATH_SEPARATOR)
		{
			return newPath.substring(0, newPath.length() - 1);
		}
		return newPath;
	}
}
