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

import consulo.language.parser.ParserDefinition;
import consulo.language.psi.stub.IStubFileElementType;
import consulo.language.psi.stub.PsiFileStubImpl;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.stubs.RubyFileStub;

import java.util.Collections;
import java.util.List;

public class RubyFileStubImpl extends PsiFileStubImpl<RFile> implements RubyFileStub
{
	private final List<String> myRequiredUrls;

	public RubyFileStubImpl(RFile file, @Nonnull List<String> requiredUrls)
	{
		super(file);
		myRequiredUrls = requiredUrls;
	}

	public RubyFileStubImpl(RFile file)
	{
		this(file, Collections.emptyList());
	}

	@Nonnull
	@Override
	public List<String> getRequiredUrls()
	{
		return myRequiredUrls;
	}

	@Override
	public IStubFileElementType getType()
	{
		return (IStubFileElementType) ParserDefinition.forLanguage(RubyLanguage.INSTANCE).getFileNodeType();
	}
}
