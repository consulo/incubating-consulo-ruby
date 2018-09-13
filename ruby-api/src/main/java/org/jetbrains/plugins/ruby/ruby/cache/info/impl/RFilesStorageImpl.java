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

package org.jetbrains.plugins.ruby.ruby.cache.info.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFilesStorage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.HashSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 21.10.2006
 */

/**
 * Info about files in some directory
 */
public class RFilesStorageImpl implements RFilesStorage
{

	// Info about all the files.
	private final Map<String, RFileInfo> myUrl2FileInfoMap = new HashMap<String, RFileInfo>();

	@Override
	public synchronized void addRInfo(@NotNull final RFileInfo rInfo)
	{
		myUrl2FileInfoMap.put(rInfo.getUrl(), rInfo);
	}

	@Override
	public synchronized void init(@NotNull final Project project)
	{
		final Collection<RFileInfo> infos = myUrl2FileInfoMap.values();
		for(RFileInfo info : infos)
		{
			if(info != null)
			{
				info.setProject(project);
			}
		}
	}

	@Override
	public synchronized RFileInfo getInfoByUrl(@NotNull final String url)
	{
		return myUrl2FileInfoMap.get(url);
	}

	@Override
	@NotNull
	public synchronized Set<String> getAllUrls()
	{
		return new HashSet<String>(myUrl2FileInfoMap.keySet());
	}

	@Override
	@Nullable
	public synchronized RFileInfo removeInfoByUrl(@NotNull final String url)
	{
		return myUrl2FileInfoMap.remove(url);
	}

	@Override
	public FileStatus getFileStatus(@NotNull final VirtualFile file)
	{
		final RFileInfo fileInfo = getInfoByUrl(file.getUrl());
		if(fileInfo == null)
		{
			return FileStatus.NOT_FOUND;
		}
		if(fileInfo.getTimestamp() < file.getTimeStamp())
		{
			return FileStatus.OBSOLETTE;
		}
		return FileStatus.UP_TO_DATE;
	}


	@Override
	public synchronized boolean containsUrl(@NotNull final String url)
	{
		return myUrl2FileInfoMap.containsKey(url);
	}

	@Override
	public synchronized void addUrl(@NotNull String url)
	{
		if(!containsUrl(url))
		{
			myUrl2FileInfoMap.put(url, null);
		}
	}
}
