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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RMathBinExpressionImpl;
import com.intellij.lang.ASTNode;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 25, 2006
 */
public class RBaseStringImpl extends RStringLiteralBase implements RBaseString
{
	public RBaseStringImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void replaceByRSymbol(@Nonnull final RSymbol rSymbol)
	{
		super.replace(rSymbol);
	}

	@Override
	public void replaceByRMathBinExpression(@Nonnull final RMathBinExpressionImpl mathExpr)
	{
		super.replace(mathExpr);
	}

}
