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

package org.jetbrains.plugins.ruby.rails.module.view.id;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 01.03.2007
 */
public class NodeId
{

	private final String myFileUrl;
	private final Object myParams;
	private RVirtualContainer myRContainer;

	private int hash; // Default cached hash code equals to 0
	private String myString;

	NodeId(@Nonnull final String fileUrl)
	{
		this(fileUrl, null, null);
	}

	public NodeId(@Nonnull final String fileUrl, @Nullable final RVirtualContainer container, @Nullable Object params)
	{
		myFileUrl = fileUrl;
		myRContainer = container;
		myParams = params;
	}

	@Nonnull
	public String getFileUrl()
	{
		return myFileUrl;
	}

	@Nullable
	public Object getParams()
	{
		return myParams;
	}

	@Nullable
	public RVirtualContainer getRContainer()
	{
		return myRContainer;
	}

	public String toString()
	{
		if(myString == null)
		{
			final StringBuilder buff = new StringBuilder();
			buff.append("[url = ");
			buff.append(getFileUrl());
			buff.append(", rContainer= ");
			buff.append(getRContainer());
			buff.append(", params = (");
			buff.append(getParams());
			buff.append(")]");
			myString = buff.toString();
		}
		return myString;
	}


	public boolean equals(Object obj)
	{
		if(obj == this)
		{
			return true;
		}
		//noinspection SimplifiableIfStatement
		if(obj == null || !(obj instanceof NodeId))
		{
			return false;
		}
		return toString().equals(obj.toString());
	}


	public int hashCode()
	{
		if(hash == 0)
		{
			hash = toString().hashCode();
		}
		return hash;
	}
}