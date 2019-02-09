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

import java.util.List;

import javax.annotation.Nullable;

import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman.Chernyatchik, oleg
 * @date: Jan 25, 2007
 * <p/>
 * Cache for module sources.
 * Supports on the fly changes handling.
 * Extends <code>RubyFilesCache</code> functionality
 */
public interface RubyModuleFilesCache extends RubyFilesCache
{

	public List<String> getAllRelativeUrlsForDirectory(@Nullable final VirtualFile directory, final boolean onlyDirectoryFiles);

	public void registerScanForFilesProvider(final CacheScannerFilesProvider provider);

	public void unregisterScanForFilesProvider(final CacheScannerFilesProvider provider);
}
