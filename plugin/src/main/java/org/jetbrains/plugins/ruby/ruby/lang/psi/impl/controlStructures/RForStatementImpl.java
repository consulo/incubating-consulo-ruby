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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RForStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public class RForStatementImpl extends RPsiElementBase implements RForStatement
{
	public RForStatementImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@NotNull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRForStatement(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	public RPsiElement getVariable()
	{
		return RubyPsiUtil.getChildByType(this, RPsiElement.class, 0);
	}

	@Override
	public RPsiElement getExpression()
	{
		final RPsiElement expr = RubyPsiUtil.getChildByType(this, RPsiElement.class, 1);
		return !(expr instanceof RCompoundStatement) ? expr : null;
	}

	@Override
	public RCompoundStatement getBody()
	{
		return RubyPsiUtil.getChildByType(this, RCompoundStatement.class, 0);
	}
}
