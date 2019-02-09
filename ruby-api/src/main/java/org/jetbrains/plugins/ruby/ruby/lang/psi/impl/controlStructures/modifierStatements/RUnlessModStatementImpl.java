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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.modifierStatements;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modifierStatements.RUnlessModStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.RConditionalStatementImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 13.06.2006
 */
public class RUnlessModStatementImpl extends RConditionalStatementImpl implements RUnlessModStatement
{
	public RUnlessModStatementImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRUnlessModStatement(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	public RPsiElement getCommand()
	{
		return RubyPsiUtil.getChildByType(this, RPsiElement.class, 0);
	}

	@Override
	@Nonnull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final RPsiElement command = getCommand();
		return command instanceof RExpression ? ((RExpression) command).getType(fileSymbol) : RType.NOT_TYPED;
	}
}
