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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.NonNls;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi.RNamedReference;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RFid;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.06.2006
 */
public class RFidImpl extends RNamedElementBase implements RFid
{
	public RFidImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	protected PsiReference createReference()
	{
		return new RNamedReference(this);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRFid(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	@Nullable
	protected String getPrefix()
	{
		return null;
	}

	@Override
	protected void checkName(@NonNls @Nonnull String newName) throws IncorrectOperationException
	{
		if(!TextUtil.isCID(newName) && !TextUtil.isFID(newName) && !TextUtil.isAID(newName))
		{
			throw new IncorrectOperationException(RBundle.message("rename.incorrect.name"));
		}
	}
}
