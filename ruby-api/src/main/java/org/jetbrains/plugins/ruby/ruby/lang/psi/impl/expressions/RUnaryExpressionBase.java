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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RQualifiedReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceHelper;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.TokenBNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RUnaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 30, 2007
 */
public class RUnaryExpressionBase extends RPsiElementBase implements RUnaryExpression
{
	public RUnaryExpressionBase(@Nonnull ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRUnaryExpression(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@Nullable
	public RPsiElement getElement()
	{
		return getChildByType(RPsiElement.class, 0);
	}

	@Override
	@Nonnull
	public PsiElement getOperation()
	{
		final PsiElement operation = getChildByFilter(TokenBNF.tUNARY_OPS, 0);
		assert operation != null;
		return operation;
	}

	@Override
	@Nullable
	public PsiReference getReference()
	{
		final String opName = getOperationName();
		if(!opName.equals("!"))
		{
			return new RQualifiedReference(getProject(), this, getElement(), getOperation(), RReference.Type.COLON_REF, opName);
		}
		return null;
	}

	@Override
	@Nonnull
	public String getOperationName()
	{
		//noinspection ConstantConditions
		final IElementType type = getOperation().getNode().getElementType();
		if(type == RubyTokenTypes.kNOT || type == RubyTokenTypes.tEXCLAMATION)
		{
			return "!";
		}
		if(type == RubyTokenTypes.tUPLUS)
		{
			return "+@";
		}
		if(type == RubyTokenTypes.tUMINUS)
		{
			return "-@";
		}
		return getOperation().getText();
	}

	@Override
	@Nonnull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final TypeInferenceHelper helper = TypeInferenceHelper.getInstance(getProject());
		helper.testAndSet(fileSymbol);
		return helper.inferUnaryExpressionType(this);
	}
}
