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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.global;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.cache.psi.holders.RVirtualGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi.RGlobalVariableReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RGlobalVarHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RAssignmentExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.RNamedElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.global.RGlobalVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.presentation.RGlobalVariablePresentationUtil;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public class RGlobalVariableImpl extends RNamedElementBase implements RGlobalVariable
{

	private RGlobalVarHolder myHolder;

	@NonNls
	private static final String DOLLAR = "$";

	public RGlobalVariableImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRGlobalVariable(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	protected PsiReference createReference()
	{
		return new RGlobalVariableReference(this);
	}

	@Nullable
	public Image getIcon(int flags)
	{
		return RGlobalVariablePresentationUtil.getIcon();
	}

	@Override
	public ItemPresentation getPresentation()
	{
		return RGlobalVariablePresentationUtil.getPresentation(this);
	}

	@Override
	public boolean isInDefinition()
	{
		return RAssignmentExpressionNavigator.getAssignmentByLeftPart(this) != null;
	}

	@Override
	@Nonnull
	public RVirtualGlobalVarHolder getHolder()
	{
		if(myHolder == null)
		{
			myHolder = PsiTreeUtil.getParentOfType(this, RGlobalVarHolder.class);
		}
		assert myHolder != null;
		return myHolder;
	}

	@Override
	@Nullable
	protected String getPrefix()
	{
		return DOLLAR;
	}

	@Override
	@Nonnull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		return RTypeUtil.createTypeBySymbol(fileSymbol, ResolveUtil.resolveToSymbol(fileSymbol, getReference()), Context.INSTANCE, true);
	}

	@Override
	protected void checkName(@NonNls @Nonnull String newName) throws IncorrectOperationException
	{
		if(!TextUtil.isCID(newName))
		{
			throw new IncorrectOperationException(RBundle.message("rename.incorrect.name"));
		}
	}
}
