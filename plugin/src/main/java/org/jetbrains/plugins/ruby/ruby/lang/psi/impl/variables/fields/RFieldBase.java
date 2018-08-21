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

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiReference;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import consulo.awt.TargetAWT;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.psi.RFieldReference;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.FieldDefinition;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RFieldHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.variables.RNamedElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RField;
import org.jetbrains.plugins.ruby.ruby.presentation.RFieldPresentationUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public abstract class RFieldBase extends RNamedElementBase implements RField
{
	private RFieldHolder myHolder;


	public RFieldBase(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	@Nullable
	public FieldDefinition getDescription()
	{
		return getHolder().getDefinition(this);
	}

	@Override
	protected PsiReference createReference()
	{
		return new RFieldReference(this);
	}

	@Override
	@NotNull
	public RFieldHolder getHolder()
	{
		if(myHolder == null)
		{
			myHolder = PsiTreeUtil.getParentOfType(this, RFieldHolder.class);
			assert myHolder != null;
		}
		return myHolder;
	}

	@Nullable
	public Icon getIcon(final int flags)
	{
		return TargetAWT.to(RFieldPresentationUtil.getIcon(this, flags));
	}

	@Override
	@Nullable
	public ItemPresentation getPresentation()
	{
		return RFieldPresentationUtil.getPresentation(this);
	}

	@Override
	protected void checkName(@NonNls @NotNull String newName) throws IncorrectOperationException
	{
		if(!TextUtil.isCID(newName))
		{
			throw new IncorrectOperationException(RBundle.message("rename.incorrect.name"));
		}
	}
}

