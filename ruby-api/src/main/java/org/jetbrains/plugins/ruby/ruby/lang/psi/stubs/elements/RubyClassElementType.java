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
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RSuperClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RNameUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes.RClassImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyClassStub;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.impl.RubyClassStubImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index.RubyClassNameIndex;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.index.RubySuperClassIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RubyClassElementType extends RubyStubElementType<RubyClassStub, RClass>
{
	public RubyClassElementType()
	{
		super("CLASS");
	}

	public RubyClassElementType(String debugName)
	{
		super(debugName);
	}

	@Override
	public PsiElement createElement(ASTNode node)
	{
		return new RClassImpl(node);
	}

	@Override
	public RClass createPsi(RubyClassStub stub)
	{
		return new RClassImpl(stub, this);
	}

	@Override
	public RubyClassStub createStub(RClass psi, StubElement parentStub)
	{
		final RSuperClass superClass = psi.getPsiSuperClass();
		String superClassName = null;
		List<String> superClassPath = null;
		boolean superClassGlobal = false;
		if(superClass != null)
		{
			superClassPath = RNameUtil.getPath(superClass);
			superClassGlobal = RNameUtil.isGlobal(superClass);
			if(!superClassPath.isEmpty())
			{
				superClassName = superClassPath.get(superClassPath.size() - 1);
			}
		}
		return new RubyClassStubImpl(parentStub, this, psi.getName(), superClassName, superClassPath, superClassGlobal);
	}

	@Override
	public void serialize(RubyClassStub stub, StubOutputStream dataStream) throws IOException
	{
		dataStream.writeName(stub.getName());
		dataStream.writeName(stub.getSuperClassName());
		dataStream.writeBoolean(stub.isSuperClassGlobal());
		final List<String> path = stub.getSuperClassPath();
		final int size = path == null ? -1 : path.size();
		dataStream.writeVarInt(size);
		if(path != null)
		{
			for(String p : path)
			{
				dataStream.writeName(p);
			}
		}
	}

	@Override
	public RubyClassStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		final String name = dataStream.readNameString();
		final String superClassName = dataStream.readNameString();
		final boolean superClassGlobal = dataStream.readBoolean();
		final int size = dataStream.readVarInt();
		List<String> path = null;
		if(size >= 0)
		{
			path = new ArrayList<>(size);
			for(int i = 0; i < size; i++)
			{
				path.add(dataStream.readNameString());
			}
		}
		return new RubyClassStubImpl(parentStub, this, name, superClassName, path, superClassGlobal);
	}

	@Override
	public void indexStub(RubyClassStub stub, IndexSink sink)
	{
		final String name = stub.getName();
		if(name != null)
		{
			sink.occurrence(RubyClassNameIndex.KEY, name);
		}
		final String superClassName = stub.getSuperClassName();
		if(superClassName != null)
		{
			sink.occurrence(RubySuperClassIndex.KEY, superClassName);
		}
	}
}
