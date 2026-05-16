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

package org.jetbrains.plugins.ruby.ruby.cache.psi;

import jakarta.annotation.Nonnull;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 4, 2006
 */
public interface RVirtualName
{
	/**
	 * @return only name without path
	 */
	@Nonnull
	public String getName();

	/**
	 * @return List of paths
	 */
	@Nonnull
	public List<String> getPath();

	/**
	 * @return full name,i.e. name with path
	 */
	@Nonnull
	public String getFullName();

	public boolean isGlobal();

	/**
	 * Build a simple RVirtualName from a path and global flag.
	 */
	@Nonnull
	static RVirtualName of(@Nonnull final List<String> path, final boolean global)
	{
		return new RVirtualName()
		{
			@Nonnull
			@Override
			public String getName()
			{
				return path.isEmpty() ? "" : path.get(path.size() - 1);
			}

			@Nonnull
			@Override
			public List<String> getPath()
			{
				return path;
			}

			@Nonnull
			@Override
			public String getFullName()
			{
				return String.join("::", path);
			}

			@Override
			public boolean isGlobal()
			{
				return global;
			}
		};
	}
}
