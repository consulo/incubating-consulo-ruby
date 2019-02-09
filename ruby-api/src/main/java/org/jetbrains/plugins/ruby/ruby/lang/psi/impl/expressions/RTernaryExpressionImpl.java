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
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RCondition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RTernaryExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import com.intellij.lang.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 06.07.2006
 */
public class RTernaryExpressionImpl extends RPsiElementBase implements RTernaryExpression
{
	public RTernaryExpressionImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public RPsiElement getTrueCommand()
	{
		return RubyPsiUtil.getChildByType(this, RPsiElement.class, 1);
	}

	@Override
	public RPsiElement getFalseCommand()
	{
		return RubyPsiUtil.getChildByType(this, RPsiElement.class, 2);
	}

	@Override
	public RCondition getCondition()
	{
		return RubyPsiUtil.getChildByType(this, RCondition.class, 0);
	}

	@Override
	@Nonnull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final RPsiElement trueCommand = getTrueCommand();
		final RPsiElement falseCommand = getFalseCommand();
		final RType result = trueCommand instanceof RExpression ? ((RExpression) trueCommand).getType(fileSymbol) : RType.NOT_TYPED;
		if(falseCommand instanceof RExpression)
		{
			return RTypeUtil.joinOr(result, ((RExpression) falseCommand).getType(fileSymbol));
		}
		return result;
	}
}
