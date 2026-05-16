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

package org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.impl;

import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.StubBase;
import consulo.language.psi.stub.StubElement;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyClassStub;

import java.util.List;

public class RubyClassStubImpl extends StubBase<RClass> implements RubyClassStub
{
	private final String myName;
	private final String mySuperClassName;
	private final List<String> mySuperClassPath;
	private final boolean mySuperClassGlobal;

	public RubyClassStubImpl(StubElement parent,
							 IStubElementType stubElementType,
							 @Nullable String name,
							 @Nullable String superClassName,
							 @Nullable List<String> superClassPath,
							 boolean superClassGlobal)
	{
		super(parent, stubElementType);
		myName = name;
		mySuperClassName = superClassName;
		mySuperClassPath = superClassPath;
		mySuperClassGlobal = superClassGlobal;
	}

	@Nullable
	@Override
	public String getName()
	{
		return myName;
	}

	@Nullable
	@Override
	public String getSuperClassName()
	{
		return mySuperClassName;
	}

	@Nullable
	@Override
	public List<String> getSuperClassPath()
	{
		return mySuperClassPath;
	}

	@Override
	public boolean isSuperClassGlobal()
	{
		return mySuperClassGlobal;
	}

	@Override
	public String toString()
	{
		return "RubyClassStub(" + myName + ")";
	}
}
