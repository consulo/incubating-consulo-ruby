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
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.RMethodImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyMethodStub;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.impl.RubyMethodStubImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index.RubyMethodNameIndex;

import java.io.IOException;

public class RubyMethodElementType extends RubyStubElementType<RubyMethodStub, RMethod>
{
	public RubyMethodElementType()
	{
		super("METHOD");
	}

	public RubyMethodElementType(String debugName)
	{
		super(debugName);
	}

	@Override
	public PsiElement createElement(ASTNode node)
	{
		return new RMethodImpl(node);
	}

	@Override
	public RMethod createPsi(RubyMethodStub stub)
	{
		return new RMethodImpl(stub, this);
	}

	@Override
	public RubyMethodStub createStub(RMethod psi, StubElement parentStub)
	{
		return new RubyMethodStubImpl(parentStub, this, psi.getName());
	}

	@Override
	public void serialize(RubyMethodStub stub, StubOutputStream dataStream) throws IOException
	{
		dataStream.writeName(stub.getName());
	}

	@Override
	public RubyMethodStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		final String name = dataStream.readNameString();
		return new RubyMethodStubImpl(parentStub, this, name);
	}

	@Override
	public void indexStub(RubyMethodStub stub, IndexSink sink)
	{
		final String name = stub.getName();
		if(name != null)
		{
			sink.occurrence(RubyMethodNameIndex.KEY, name);
		}
	}
}
