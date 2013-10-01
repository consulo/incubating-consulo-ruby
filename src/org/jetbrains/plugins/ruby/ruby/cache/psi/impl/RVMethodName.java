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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Jul 6, 2007
 */
public class RVMethodName extends RVirtualNameImpl implements RVirtualName
{
	private static final String DOT = ".";

	public RVMethodName(@NotNull List<String> fullPath, boolean global)
	{
		super(fullPath, global);
	}

	@Override
	@NotNull
	public String getFullName()
	{
		if(myFullName == null)
		{
			final StringBuilder buffer = new StringBuilder();
			if(isGlobal)
			{
				buffer.append(COLON2);
			}
			boolean smthAdded = false;
			final int size = myFullPath.size();
			for(int i = 0; i < size; i++)
			{
				String s = myFullPath.get(i);
				if(smthAdded)
				{
					buffer.append(i == size - 1 ? DOT : COLON2);
				}
				buffer.append(s);
				smthAdded = true;
			}
			myFullName = buffer.toString();
		}
		return myFullName;
	}
}
