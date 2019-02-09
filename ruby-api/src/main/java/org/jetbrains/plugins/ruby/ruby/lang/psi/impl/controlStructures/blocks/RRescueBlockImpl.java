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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.blocks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RRescueBlock;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RBlockVariables;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 16.06.2006
 */
public class RRescueBlockImpl extends RPsiElementBase implements RRescueBlock
{
	public RRescueBlockImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nullable
	public RBlockVariables getBlockVariables()
	{
		final PsiElement result = getChildByFilter(RubyElementTypes.BLOCK_VARIABLES, 0);
		return result != null ? (RBlockVariables) result : null;
	}

	@Override
	@Nullable
	public RPsiElement getException()
	{
		final RBlockVariables vars = getBlockVariables();
		return vars != null ? RubyPsiUtil.getElementToLeftWithSameParent(vars, RPsiElement.class) : null;
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRRescueBlock(this);
			return;
		}
		super.accept(visitor);
	}
}
