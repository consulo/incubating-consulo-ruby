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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions;

import java.util.LinkedList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.06.2006
 */
public class RListOfExpressionsImpl extends RPsiElementBase implements RListOfExpressions
{
	public RListOfExpressionsImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@NotNull
	public List<RPsiElement> getElements()
	{
		LinkedList<RPsiElement> elements = new LinkedList<RPsiElement>();
		for(PsiElement child : getChildren())
		{
			//noinspection ConstantConditions
			final IElementType type = child.getNode().getElementType();
			if(type != RubyTokenTypes.tCOMMA && child instanceof RPsiElement)
			{
				elements.add((RPsiElement) child);
			}
		}
		return elements;
	}

	@Override
	public RPsiElement getElement(final int number)
	{
		final List<RPsiElement> elementList = getElements();
		return elementList.size() > number ? elementList.get(number) : null;
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRListOfExpressions(this);
			return;
		}
		super.accept(visitor);
	}
}
