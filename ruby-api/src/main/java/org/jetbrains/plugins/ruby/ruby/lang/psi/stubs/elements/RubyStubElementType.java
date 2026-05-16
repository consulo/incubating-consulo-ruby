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
import consulo.language.psi.stub.IStubElementType;
import consulo.language.psi.stub.IndexSink;
import consulo.language.psi.stub.StubElement;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;

public abstract class RubyStubElementType<StubT extends StubElement, PsiT extends RPsiElement> extends IStubElementType<StubT, PsiT>
{
	public RubyStubElementType(String debugName)
	{
		super(debugName, RubyLanguage.INSTANCE);
	}

	@Override
	public String toString()
	{
		return "Ruby:" + super.toString();
	}

	public abstract PsiElement createElement(ASTNode node);

	@Override
	public void indexStub(StubT stub, IndexSink sink)
	{
	}

	@Override
	public String getExternalId()
	{
		return "ruby." + super.toString();
	}
}
