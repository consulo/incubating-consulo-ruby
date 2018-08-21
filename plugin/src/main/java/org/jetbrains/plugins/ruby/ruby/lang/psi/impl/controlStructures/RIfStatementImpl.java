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

import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RIfStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RElseBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RElsifBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 10.06.2006
 */
public class RIfStatementImpl extends RConditionalStatementImpl implements RIfStatement
{
	public RIfStatementImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRIfStatement(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@NotNull
	public RCompoundStatement getThenBlock()
	{
		//noinspection ConstantConditions
		return RubyPsiUtil.getChildByType(this, RCompoundStatement.class, 0);
	}

	@Override
	@NotNull
	public List<RElsifBlock> getElsifBlocks()
	{
		return RubyPsiUtil.getChildrenByType(this, RElsifBlock.class);
	}

	@Override
	@Nullable
	public RElseBlock getElseBlock()
	{
		return RubyPsiUtil.getChildByType(this, RElseBlock.class, 0);
	}

	@Override
	@NotNull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final RCompoundStatement thenBlock = getThenBlock();
		final List<RElsifBlock> elsifBlocks = getElsifBlocks();
		final RElseBlock elseBlock = getElseBlock();
		RType result = thenBlock.getType(fileSymbol);
		for(RElsifBlock elsifBlock : elsifBlocks)
		{
			result = RTypeUtil.joinOr(result, elsifBlock.getBody().getType(fileSymbol));
		}
		if(elseBlock != null)
		{
			result = RTypeUtil.joinOr(result, elseBlock.getBody().getType(fileSymbol));
		}
		return result;
	}
}
