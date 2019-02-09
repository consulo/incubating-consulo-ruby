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

package org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods;

import java.io.Serializable;

import javax.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 13, 2007
 */
public class ArgumentInfo implements Serializable
{
	private final String myName;
	private final Type myType;
	private int myHash;

	public ArgumentInfo(@Nonnull final String name, final Type type)
	{
		myName = name;
		myType = type;
	}

	public String getName()
	{
		return myName;
	}

	public Type getType()
	{
		return myType;
	}


	public String getPresentableName()
	{
		if(myType == Type.ARRAY)
		{
			return "*" + myName;
		}
		if(myType == Type.BLOCK)
		{
			return "&" + myName;
		}
		if(myType == Type.PREDEFINED)
		{
			return myName + "=...";
		}
		return myName;
	}

	public enum Type
	{
		SIMPLE,
		PREDEFINED,
		BLOCK,
		ARRAY
	}

	public boolean equals(Object obj)
	{
		if(this == obj)
		{
			return true;
		}
		if(obj instanceof ArgumentInfo)
		{
			final ArgumentInfo argumentInfo = (ArgumentInfo) obj;
			return getType() == argumentInfo.getType() && getName().equals(argumentInfo.getName());
		}
		return false;
	}

	public int hashCode()
	{
		if(myHash == 0)
		{
			myHash = (myName != null ? myName.hashCode() : 0);
			myHash = 31 * myHash + (myType != null ? myType.hashCode() : 0);
		}
		return myHash;
	}
}
