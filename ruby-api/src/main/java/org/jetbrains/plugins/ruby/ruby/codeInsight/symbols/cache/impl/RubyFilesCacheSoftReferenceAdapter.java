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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.impl;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.cache.fileCache.RubyFilesCacheListener;
import com.intellij.reference.SoftReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: 19.10.2007
 */
public class RubyFilesCacheSoftReferenceAdapter implements RubyFilesCacheListener
{
	private SoftReference<RubyFilesCacheListener> myReference;

	public RubyFilesCacheSoftReferenceAdapter(@Nonnull final RubyFilesCacheListener listener)
	{
		myReference = new SoftReference<RubyFilesCacheListener>(listener);
	}

	@Override
	public void fileAdded(@Nonnull String url)
	{
		final RubyFilesCacheListener listener = myReference.get();
		if(listener != null)
		{
			listener.fileAdded(url);
		}
	}

	@Override
	public void fileRemoved(@Nonnull String url)
	{
		final RubyFilesCacheListener listener = myReference.get();
		if(listener != null)
		{
			listener.fileRemoved(url);
		}
	}

	@Override
	public void fileUpdated(@Nonnull String url)
	{
		final RubyFilesCacheListener listener = myReference.get();
		if(listener != null)
		{
			listener.fileUpdated(url);
		}
	}
}
