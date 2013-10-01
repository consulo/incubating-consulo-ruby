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
import org.jetbrains.plugins.ruby.support.utils.IdeaInternalUtil;
import com.intellij.execution.filters.FileHyperlinkInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ActionRunner;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 16, 2008
 */
public class OpenIOFileHyperlinkInfo implements FileHyperlinkInfo
{
	private final Project myProject;
	private final File myFile;
	private final int myLine;
	private final int myColumn;

	private OpenFileDescriptor myDescriptor;
	private boolean isLazyInitialized;

	public OpenIOFileHyperlinkInfo(final Project project, @NotNull final File file, final int line, final int column)
	{
		myProject = project;
		myFile = file;
		myLine = line;
		myColumn = column;
	}

	public OpenIOFileHyperlinkInfo(final Project project, @NotNull final File file, final int line)
	{
		this(project, file, line, 0);
	}

	@Override
	@Nullable
	public OpenFileDescriptor getDescriptor()
	{
		if(!isLazyInitialized)
		{
			final Ref<VirtualFile> virtualFileRef = new Ref<VirtualFile>(null);
			IdeaInternalUtil.runInsideWriteAction(new ActionRunner.InterruptibleRunnable()
			{
				@Override
				public void run() throws Exception
				{
					virtualFileRef.set(LocalFileSystem.getInstance().refreshAndFindFileByIoFile(myFile));
				}
			});
			final VirtualFile virtualFile = virtualFileRef.get();
			myDescriptor = virtualFile == null ? null : new OpenFileDescriptor(myProject, virtualFile, myLine, myColumn);

			isLazyInitialized = true;
		}
		return myDescriptor;
	}

	public File getFile()
	{
		return myFile;
	}

	public int getLine()
	{
		return myLine;
	}

	public int getColumn()
	{
		return myColumn;
	}

	@Override
	public void navigate(final Project project)
	{
		ApplicationManager.getApplication().runReadAction(new Runnable()
		{
			@Override
			public void run()
			{
				final OpenFileDescriptor fileDesc = getDescriptor();
				if(fileDesc == null)
				{
					return;
				}
				if(fileDesc.getFile().isValid())
				{
					FileEditorManager.getInstance(project).openTextEditor(fileDesc, true);
				}
			}
		});
	}
}

