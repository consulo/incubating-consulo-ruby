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

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualIncludeJavaClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 12, 2007
 */
public class RVirtualIncludeJavaClassImpl extends RVirtualStructuralElementBase implements RVirtualIncludeJavaClass, Serializable
{
	private String myQualifiedName;

	public RVirtualIncludeJavaClassImpl(final RVirtualContainer container, final String qualifiedName)
	{
		super(container);
		myQualifiedName = qualifiedName;
	}

	@Override
	public void accept(@Nonnull final RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualIncludeJavaClass(this);
	}

	public String toString()
	{
		return RCall.INCLUDE_CLASS_COMMAND + " " + myQualifiedName;
	}

	@Override
	public String getQualifiedName()
	{
		return myQualifiedName;
	}

	@Override
	public StructureType getType()
	{
		return StructureType.CALL_INCLUDE_CLASS;
	}
}
