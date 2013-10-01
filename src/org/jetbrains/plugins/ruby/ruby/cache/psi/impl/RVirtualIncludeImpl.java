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
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualInclude;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Apr 3, 2007
 */
public class RVirtualIncludeImpl extends RVirtualStructuralElementBase implements RVirtualInclude, Serializable
{
	protected List<RVirtualName> myNames;

	public RVirtualIncludeImpl(final RVirtualContainer container, @NotNull List<RVirtualName> includes)
	{
		super(container);
		myNames = includes;
	}

	@Override
	public StructureType getType()
	{
		return StructureType.CALL_INCLUDE;
	}

	public String toString()
	{
		return RCall.INCLUDE_COMMAND;
	}

	@Override
	public void dump(@NotNull StringBuilder buffer, int indent)
	{
		super.dump(buffer, indent);
		for(RVirtualName myInclude : myNames)
		{
			buffer.append("\n");
			((RVirtualNameImpl) myInclude).dump(buffer, indent + 1);
		}
	}

	@Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualInclude(this);
	}

	@Override
	@NotNull
	public List<RVirtualName> getNames()
	{
		return myNames;
	}
}
