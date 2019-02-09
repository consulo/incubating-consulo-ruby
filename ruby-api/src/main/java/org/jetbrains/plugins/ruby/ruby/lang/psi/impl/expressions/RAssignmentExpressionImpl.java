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
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RAssignmentExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 08.06.2006
 */
public class RAssignmentExpressionImpl extends RPsiElementBase implements RAssignmentExpression
{
	public RAssignmentExpressionImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nonnull
	public RPsiElement getObject()
	{
		final RPsiElement object = getChildByType(RPsiElement.class, 0);
		assert object != null;
		return object;
	}

	@Override
	@Nullable
	public RPsiElement getValue()
	{
		PsiElement ASSGN = getASSGN();
		return ASSGN != null ? PsiTreeUtil.getNextSiblingOfType(ASSGN, RPsiElement.class) : null;
	}

	private PsiElement getASSGN()
	{
		return getChildByFilter(BNF.tASSGNS, 0);
	}

	@Override
	public void accept(@Nonnull final PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRAssignmentExpression(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@Nonnull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final RPsiElement value = getValue();
		return value instanceof RExpression ? ((RExpression) value).getType(fileSymbol) : RType.NOT_TYPED;
	}
}
