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

import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.psi.stub.StubBuilder;
import consulo.language.psi.stub.StubElement;
import consulo.language.psi.stub.StubInputStream;
import consulo.language.psi.stub.StubOutputStream;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyFileStub;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyFileStubBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.impl.RubyFileStubImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RubyFileElementType extends IStubFileElementType<RubyFileStub>
{
	public static final RubyFileElementType INSTANCE = new RubyFileElementType();

	private RubyFileElementType()
	{
		super("RUBY_FILE", RubyLanguage.INSTANCE);
	}

	@Override
	public StubBuilder getBuilder()
	{
		return new RubyFileStubBuilder();
	}

	@Override
	public int getStubVersion()
	{
		return 2;
	}

	@Override
	public String getExternalId()
	{
		return "ruby.FILE";
	}

	@Override
	public void serialize(RubyFileStub stub, StubOutputStream dataStream) throws IOException
	{
		final List<String> urls = stub.getRequiredUrls();
		dataStream.writeVarInt(urls.size());
		for(String url : urls)
		{
			dataStream.writeName(url);
		}
	}

	@Override
	public RubyFileStub deserialize(StubInputStream dataStream, StubElement parentStub) throws IOException
	{
		final int size = dataStream.readVarInt();
		final List<String> urls = new ArrayList<String>(size);
		for(int i = 0; i < size; i++)
		{
			urls.add(dataStream.readNameString());
		}
		return new RubyFileStubImpl(null, urls);
	}
}
