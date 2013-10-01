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

package org.jetbrains.plugins.ruby.ruby.cache.fileCache;

import org.jetbrains.annotations.NotNull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 20.02.2007
 */

/**
 * Tracks <code>org.jetbrains.plugins.ruby.ruby.cache.fileCache.FilesCache</code>
 * state changes.
 */
public interface RubyFilesCacheListener
{
	/**
	 * File was added to cache
	 *
	 * @param url file url
	 */
	public void fileAdded(@NotNull final String url);

	/**
	 * File was removed from cache
	 *
	 * @param url file url
	 */
	public void fileRemoved(@NotNull final String url);

	/**
	 * File was updated in cache
	 *
	 * @param url file url
	 */
	public void fileUpdated(@NotNull final String url);
}
