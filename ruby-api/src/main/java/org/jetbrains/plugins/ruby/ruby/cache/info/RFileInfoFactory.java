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

package org.jetbrains.plugins.ruby.ruby.cache.info;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.impl.RFileInfoImpl;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualUtil;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.01.2007
 */
public class RFileInfoFactory
{

	/**
	 * Creates new RFileInfo for given file
	 *
	 * @param project Current project
	 * @param file    current file
	 * @return RFileInfo object containing information about file
	 *         or null if file cannot be found or isn`t ruby file
	 */
	@Nullable
	public static RFileInfo createRFileInfo(@Nonnull final Project project, @Nonnull final VirtualFile file)
	{
		final RFile rFile = getRFile(project, file);
		if(rFile == null)
		{
			return null;
		}

		// creating new RFileInfo
		return createRFileInfoByRFile(file, rFile);
	}

	/**
	 * Gets PsiFile by given virtualFile
	 *
	 * @param project Current project
	 * @param file    VirtualFile
	 * @return RFile object if found, null otherwise
	 */
	private static RFile getRFile(@Nonnull final Project project, @Nonnull final VirtualFile file)
	{
		if(!file.isValid())
		{
			return null;
		}
		final PsiManager myPsiManager = PsiManager.getInstance(project);
		final PsiFile psiFile = myPsiManager.findFile(file);
		if(psiFile == null || !(psiFile instanceof RFile))
		{
			return null;
		}
		return (RFile) psiFile;
	}

	/**
	 * Creates RFileInfo by RFile
	 *
	 * @param file  VirtualFile
	 * @param rFile RFile
	 * @return RFileInfo object, containing information about RFile inside
	 */
	private static RFileInfo createRFileInfoByRFile(@Nonnull final VirtualFile file, @Nonnull final RFile rFile)
	{
		final RFileInfo fileInfo = new RFileInfoImpl(file.getUrl(), file.getTimeStamp(), rFile.getProject());
		final RVirtualFile virtualFile = RVirtualUtil.createBy(rFile, fileInfo);
		((RFileInfoImpl) fileInfo).setRVirtualFile(virtualFile);
		return fileInfo;
	}

	/**
	 * Creates RFileInfo by pseudophysical RFile. (for tests only)
	 *
	 * @param rFile RFile
	 * @return RFileInfo object, containing information about RFile inside
	 */
	public static RFileInfo createRFileInfoByPseudPhysicalRFile(@Nonnull final RFile rFile)
	{
		final RFileInfo fileInfo = new RFileInfoImpl(VirtualFileUtil.constructLocalUrl(rFile.getName()), 0, rFile.getProject());
		final RVirtualFile virtualFile = RVirtualUtil.createBy(rFile, fileInfo);
		((RFileInfoImpl) fileInfo).setRVirtualFile(virtualFile);
		return fileInfo;
	}
}
