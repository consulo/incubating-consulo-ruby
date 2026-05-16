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

package org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.elements;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElement;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RObjectClassImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyObjectClassStub;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.impl.RubyObjectClassStubImpl;

import java.io.IOException;

public class RubyObjectClassElementType extends RubyStubElementType<RubyObjectClassStub, RObjectClass>
{
	public RubyObjectClassElementType()
	{
		super("OBJECT_CLASS");
	}

	@Override
	public PsiElement createElement(ASTNode node)
	{
		return new RObjectClassImpl(node);
	}

	@Override
	public RObjectClass createPsi(RubyObjectClassStub stub)
	{
		return new RObjectClassImpl(stub, this);
	}

	@Override
	public RubyObjectClassStub createStub(RObjectClass psi, StubElement parentStub)
	{
		return new RubyObjectClassStubImpl(parentStub, this);
	}

	@Override
	public void serialize(RubyObjectClassStub stub, StubOutputStream dataStream) throws IOException
	{
	}

	@Override
	public RubyObjectClassStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		return new RubyObjectClassStubImpl(parentStub, this);
	}
}
