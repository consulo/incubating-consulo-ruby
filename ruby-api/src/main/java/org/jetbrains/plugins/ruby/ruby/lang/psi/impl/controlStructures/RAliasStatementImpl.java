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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;

import consulo.language.ast.ASTNode;
import consulo.language.psi.PsiElementVisitor;
import consulo.navigation.ItemPresentation;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RAliasStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RAliasPresentationUtil;
import consulo.language.psi.stub.StubElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.06.2006
 */
public class RAliasStatementImpl extends RPsiElementBase<StubElement> implements RAliasStatement
{
	public RAliasStatementImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRAliasStatement(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@Nullable
	public RPsiElement getPsiOldName()
	{
		return getChildByType(RPsiElement.class, 1);
	}

	@Override
	@Nullable
	public RPsiElement getPsiNewName()
	{
		return getChildByType(RPsiElement.class, 0);
	}

	@Override
	@Nonnull
	public String getOldName()
	{
		return getName(getPsiOldName());
	}

	@Override
	@Nonnull
	public String getNewName()
	{
		return getName(getPsiNewName());
	}

	@Override
	public StructureType getType()
	{
		return StructureType.ALIAS;
	}

	@Override
	@Nonnull
	public String getPresentableText()
	{
		return getText();
	}

	@Nonnull
	private static String getName(RPsiElement element)
	{
		if(element != null)
		{
			if(element instanceof RSymbol)
			{
				return ((RSymbol) element).getObject().getText();
			}
			return element.getText();
		}
		return "";
	}

	@Override
	public boolean equalsToVirtual(@Nonnull final RStructuralElement element)
	{
		if(!(element instanceof RAliasStatement))
		{
			return false;
		}
		final RAliasStatement alias = (RAliasStatement) element;
		return getNewName().equals(alias.getNewName()) && getOldName().equals(alias.getOldName());
	}

	@Override
	public ItemPresentation getPresentation()
	{
		return RAliasPresentationUtil.getPresentation(this);
	}
}
