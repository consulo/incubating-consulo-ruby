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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types.impl;

import java.util.Collection;
import java.util.Collections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.jruby.codeInsight.types.JRubyDuckTypeUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.data.Children;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.DuckType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Message;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RJavaType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import com.intellij.psi.PsiType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 8, 2008
 */
public class RJavaTypeImpl extends RTypeBase implements RJavaType
{
	private PsiType myPsiType;
	private DuckType myJavaType;

	public RJavaTypeImpl(@NotNull final PsiType psiType, @Nullable final FileSymbol fileSymbol)
	{
		// TODO: OPTIMIZE! Don`t use ducktype
		myPsiType = psiType;
		final Children children = JRubyDuckTypeUtil.getChildrenByPsiType(fileSymbol, myPsiType);
		myJavaType = RTypeUtil.createDuckTypeByChildren(fileSymbol, children, Collections.emptySet(), RTypeUtil.createFilter(false));
	}

	@Override
	public boolean isTyped()
	{
		return true;
	}

	@Override
	@Nullable
	public String getName()
	{
		return myPsiType.getPresentableText();
	}

	@Override
	@NotNull
	public Collection<Message> getMessages()
	{
		return myJavaType.getMessages();
	}

	@Override
	public Collection<Message> getMessagesForName(@Nullable final String name)
	{
		return myJavaType.getMessagesForName(name);
	}

	public String toString()
	{
		return "Java type: " + myPsiType.getPresentableText();
	}
}
