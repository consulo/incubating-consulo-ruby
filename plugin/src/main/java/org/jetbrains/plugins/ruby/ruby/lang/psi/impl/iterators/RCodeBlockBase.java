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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.iterators;

import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceContext;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceHelper;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyElementTypes;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.Instruction;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.impl.RControlFlowBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RBlockVariables;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RCodeBlock;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 11.06.2006
 */
public abstract class RCodeBlockBase extends RPsiElementBase implements RCodeBlock
{
	private Instruction[] myControlFlow;

	public RCodeBlockBase(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public RBlockVariables getBlockVariables()
	{
		final PsiElement result = getChildByFilter(RubyElementTypes.BLOCK_VARIABLES, 0);
		return result != null ? (RBlockVariables) result : null;
	}

	@Override
	public void subtreeChanged()
	{
		super.subtreeChanged();

		// Clear control flow and inference info
		final TypeInferenceContext context = TypeInferenceHelper.getInstance(getProject()).getContext();
		if(context != null)
		{
			context.localVariablesTypesCache.remove(this);
		}
		myControlFlow = null;
	}

	@Override
	public Instruction[] getControlFlow()
	{
		if(myControlFlow == null)
		{
			myControlFlow = new RControlFlowBuilder().buildControlFlow(null, this, null, null);
		}
		return myControlFlow;
	}
}
