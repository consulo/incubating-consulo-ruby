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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.reference.SoftReference;

/**
 * Used for information for files and directories etc
 */
public class RFileInfoImpl implements RFileInfo
{
	private final String myUrl;
	private final long myTimestamp;
	private transient Project myProject;
	private RVirtualFile myVirtualFile;

	private transient SoftReference<VirtualFile> myFileRef;

	public RFileInfoImpl(final String url, final long timestamp, @Nullable final Project project)
	{
		myUrl = url;
		myTimestamp = timestamp;
		myProject = project;
	}

	@Override
	@NotNull
	public Project getProject()
	{
		return myProject;
	}

	@Override
	public void setProject(@NotNull Project project)
	{
		myProject = project;
	}

	@Override
	public long getTimestamp()
	{
		return myTimestamp;
	}

	@Override
	@NotNull
	public String getUrl()
	{
		return myUrl;
	}

	@Override
	@Nullable
	public String getFileDirectoryUrl()
	{
		return VirtualFileUtil.getParentDir(myUrl);
	}

	@Override
	@NotNull
	public RVirtualFile getRVirtualFile()
	{
		return myVirtualFile;
	}

	public void setRVirtualFile(@NotNull final RVirtualFile virtualFile)
	{
		myVirtualFile = virtualFile;
	}

	public String toString()
	{
		return getUrl() + " [" + myTimestamp + "]";
	}

	@Override
	@Nullable
	public VirtualFile getVirtualFile()
	{
		VirtualFile file;
		if(myFileRef == null || (file = myFileRef.get()) == null)
		{
			file = VirtualFileManager.getInstance().findFileByUrl(myUrl);
			myFileRef = new SoftReference<VirtualFile>(file);
		}
		return file;
	}
}
