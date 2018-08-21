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
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualRequire;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 20, 2007
 */
public class RVirtualRequireImpl extends RVirtualStructuralElementBase implements RVirtualRequire, Serializable
{
	protected List<String> myRequires;

	public RVirtualRequireImpl(final RVirtualContainer container, @NotNull final List<String> requires)
	{
		super(container);
		myRequires = requires;
	}

	@Override
	@NotNull
	public List<String> getNames()
	{
		return myRequires;
	}

	@Override
	public StructureType getType()
	{
		return StructureType.CALL_REQUIRE;
	}

	@Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualRequire(this);
	}

	public String toString()
	{
		return RCall.REQUIRE_COMMAND + " " + myRequires;
	}
}
