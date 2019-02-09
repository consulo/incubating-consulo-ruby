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

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeVariable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.PsiElementSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.TypeInferenceHelper;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowOwner;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 4, 2007
 */
public class ScopeVariableImpl implements ScopeVariable
{
	private String myName;
	private boolean isParameter;
	private RIdentifier myPrototype;
	private Symbol mySymbol;

	public ScopeVariableImpl(@Nonnull final String name, @Nonnull final RIdentifier element, final boolean isParameter)
	{
		myName = name;
		myPrototype = element;
		this.isParameter = isParameter;
	}

	@Override
	@Nonnull
	public String getName()
	{
		return myName;
	}

	@Override
	@Nonnull
	public RIdentifier getPrototype()
	{
		return myPrototype;
	}


	@Override
	public boolean isParameter()
	{
		return isParameter;
	}


	@Override
	@Nonnull
	public Symbol createSymbol()
	{
		if(mySymbol == null)
		{
			mySymbol = new PsiElementSymbol(myPrototype, getName(), Type.LOCAL_VARIABLE);
		}
		return mySymbol;
	}

	@Override
	public RType getType(@Nullable final FileSymbol fileSymbol, @Nonnull final RIdentifier usage)
	{
		final RControlFlowOwner controlFlowOwner = PsiTreeUtil.getParentOfType(myPrototype, RControlFlowOwner.class);
		assert controlFlowOwner != null;
		final TypeInferenceHelper helper = TypeInferenceHelper.getInstance(usage.getProject());
		helper.testAndSet(fileSymbol);
		return helper.inferLocalVariableType(controlFlowOwner, usage);
	}
}
