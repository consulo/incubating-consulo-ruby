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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods.arguments;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.ArgumentInfo;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RArgument;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 26.06.2006
 */
public class RArgumentImpl extends RPsiElementBase implements RArgument
{
	public RArgumentImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@NotNull
	public String getName()
	{
		final RIdentifier identifier = getIdentifier();
		//noinspection ConstantConditions
		return identifier != null ? identifier.getName() : "";
	}

	@Override
	public RIdentifier getIdentifier()
	{
		return RubyPsiUtil.getChildByType(this, RIdentifier.class, 0);
	}

	@Override
	public void accept(@NotNull final PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRParameter(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	public ArgumentInfo.Type getType()
	{
		return ArgumentInfo.Type.SIMPLE;
	}

}
