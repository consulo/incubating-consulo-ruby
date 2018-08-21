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
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualImportJavaClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 9, 2008
 */
public class RVirtualImportJavaClassImpl extends RVirtualStructuralElementBase implements RVirtualImportJavaClass
{
	private final List<RVirtualName> myNames;

	public RVirtualImportJavaClassImpl(final RVirtualContainer container, @NotNull final List<RVirtualName> names)
	{
		super(container);
		myNames = names;
	}

	@Override
	@NotNull
	public List<RVirtualName> getNames()
	{
		return myNames;
	}

	@Override
	public StructureType getType()
	{
		return StructureType.CALL_IMPORT;
	}

	public String toString()
	{
		return RCall.IMPORT_COMMAND;
	}

	@Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualImportJavaClass(this);
	}
}
