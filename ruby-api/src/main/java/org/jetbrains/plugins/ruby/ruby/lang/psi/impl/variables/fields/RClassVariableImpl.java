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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.fields;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.FieldType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RClassVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public class RClassVariableImpl extends RFieldBase implements RClassVariable
{
	public RClassVariableImpl(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public void accept(@Nonnull PsiElementVisitor visitor)
	{
		if(visitor instanceof RubyElementVisitor)
		{
			((RubyElementVisitor) visitor).visitRClassVariable(this);
			return;
		}
		super.accept(visitor);
	}

	@Override
	public FieldType getType()
	{
		return FieldType.CLASS_VARIABLE;
	}


	@Override
	public String getPrefix()
	{
		return PREFIX;
	}
}
