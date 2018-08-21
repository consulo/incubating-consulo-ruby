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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl.variables;

import java.io.Serializable;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualFieldHolder;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualElementBase;
import org.jetbrains.plugins.ruby.ruby.cache.psi.variables.RVirtualField;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 22, 2006
 */
public class RVirtualFieldImpl extends RVirtualElementBase implements RVirtualField, Serializable
{
	private String myName;
	private RVirtualFieldHolder myHolder;
	private FieldType myType;

	public RVirtualFieldImpl(@NotNull final String name, @NotNull final RVirtualFieldHolder holder, final FieldType type)
	{
		myName = name;
		myHolder = holder;
		myType = type;
	}

	@Override
	@NotNull
	public RVirtualFieldHolder getHolder()
	{
		return myHolder;
	}

	@Override
	@NotNull
	public String getName()
	{
		return myName;
	}

	@Override
	public FieldType getType()
	{
		return myType;
	}

	@Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor)
	{
		visitor.visitElement(this);
	}

	public String toString()
	{
		return myType + " " + myName;
	}

	@Override
	@NotNull
	public String getText()
	{
		return getPrefix() + myName;
	}

	private String getPrefix()
	{
		return myType == FieldType.INSTANCE_VARIABLE ? RInstanceVariable.PREFIX : RClassVariable.PREFIX;
	}
}
