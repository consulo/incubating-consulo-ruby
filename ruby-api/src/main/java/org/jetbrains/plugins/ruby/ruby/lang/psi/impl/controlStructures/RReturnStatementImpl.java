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

import java.util.Collections;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.CoreTypes;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RReturnStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RReturnStatementImpl extends RPsiElementBase implements RReturnStatement
{
	public RReturnStatementImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRReturnStatement(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@NotNull
	public List<RPsiElement> getReturnValues()
	{
		final RListOfExpressions list = getReturnList();
		return list != null ? list.getElements() : Collections.<RPsiElement>emptyList();
	}

	@Override
	public RListOfExpressions getReturnList()
	{
		return RubyPsiUtil.getChildByType(this, RListOfExpressions.class, 0);
	}

	@Override
	@NotNull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final List<RPsiElement> values = getReturnValues();
		if(values.isEmpty())
		{
			return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.NilClass), Context.INSTANCE, true);
		}
		if(values.size() == 1)
		{
			final RPsiElement v = values.get(0);
			return v instanceof RExpression ? ((RExpression) v).getType(fileSymbol) : RType.NOT_TYPED;
		}
		return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.Array), Context.INSTANCE, true);
	}
}
