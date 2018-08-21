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

import java.io.Serializable;

import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 21, 2007
 */
public abstract class RVirtualStructuralElementBase extends RVirtualElementBase implements RVirtualStructuralElement, Serializable
{

	private RVirtualContainer myContainer;

	protected RVirtualStructuralElementBase(RVirtualContainer container)
	{
		myContainer = container;
	}

	@Override
	public final RVirtualContainer getVirtualParentContainer()
	{
		return myContainer;
	}
}
