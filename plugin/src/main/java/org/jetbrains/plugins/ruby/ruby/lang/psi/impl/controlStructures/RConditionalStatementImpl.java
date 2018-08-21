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

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RCondition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RConditionalStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 07.08.2006
 */
public abstract class RConditionalStatementImpl extends RPsiElementBase implements RConditionalStatement
{
	public RConditionalStatementImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nullable
	public RCondition getCondition()
	{
		PsiElement cond = RubyPsiUtil.getChildByFilter(this, RubyElementTypes.CONDITION, 0);
		return cond != null ? (RCondition) cond : null;
	}
}
