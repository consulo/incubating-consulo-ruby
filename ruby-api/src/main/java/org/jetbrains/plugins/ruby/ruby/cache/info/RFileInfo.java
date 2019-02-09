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

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 21.10.2006
 */

/**
 * Stores cached RVirtualFileInfo
 */
public interface RFileInfo extends Serializable
{

	public long getTimestamp();

	@Nonnull
	public String getUrl();

	@Nullable
	public String getFileDirectoryUrl();

	@Nonnull
	public RVirtualFile getRVirtualFile();

	@Nullable
	public VirtualFile getVirtualFile();

	@Nonnull
	public Project getProject();

	public void setProject(@Nonnull Project project);
}