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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RExpression;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RSelfAssignmentExpression;
import com.intellij.lang.ASTNode;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 17.01.2007
 */
public class RSelfAssignmentExpressionImpl extends RAssignmentExpressionImpl implements RSelfAssignmentExpression
{
	public RSelfAssignmentExpressionImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@NotNull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		final RPsiElement object = getObject();
		return object instanceof RExpression ? ((RExpression) object).getType(fileSymbol) : RType.NOT_TYPED;
	}
}
