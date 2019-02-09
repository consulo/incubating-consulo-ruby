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

package org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Oct 16, 2007
 */
public class Prototypes
{
	public static Prototypes EMPTY_PROTOTYPES = new Prototypes(null);

	private final List<RVirtualElement> myList = new ArrayList<RVirtualElement>();
	private final Object LOCK = new Object();

	private final Prototypes myBasePrototypes;

	private RVirtualElement myLastPrototype;

	public Prototypes(@Nullable final Prototypes prototypes)
	{
		myBasePrototypes = prototypes;
	}

	@Nonnull
	public List<RVirtualElement> getAll()
	{
		final ArrayList<RVirtualElement> all = new ArrayList<RVirtualElement>();
		addAll(all);
		return all;
	}

	protected void addAll(@Nonnull final List<RVirtualElement> list)
	{
		if(myBasePrototypes != null)
		{
			myBasePrototypes.addAll(list);
		}
		synchronized(LOCK)
		{
			list.addAll(myList);
		}
	}

	public void add(@Nonnull final RVirtualElement prototype)
	{
		myLastPrototype = prototype;
		synchronized(LOCK)
		{
			myList.add(prototype);
		}
	}

	public RVirtualElement getLast()
	{
		return myLastPrototype != null ? myLastPrototype : myBasePrototypes != null ? myBasePrototypes.getLast() : null;
	}

	public boolean hasElements()
	{
		return getLast() != null;
	}
}
