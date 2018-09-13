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

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.Context;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RType;
import org.jetbrains.plugins.ruby.ruby.codeInsight.types.RTypeUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.references.RReferenceNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RNamedElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 24, 2007
 */
public abstract class RNamedElementBase extends RPsiElementBase implements RNamedElement
{
	protected RNamedElementBase(ASTNode astNode)
	{
		super(astNode);
	}

	@Override
	public PsiReference getReference()
	{
		final RReference ref = RReferenceNavigator.getReferenceByRightPart(this);
		return ref != null ? null : createReference();
	}

	protected abstract PsiReference createReference();

	@Override
	@NotNull
	public String getName()
	{
		final String text = getText();
		final String prefix = getPrefix();
		return prefix != null ? text.replace(prefix, "") : text;
	}

	@Nullable
	abstract protected String getPrefix();

	protected abstract void checkName(@NonNls @NotNull final String newName) throws IncorrectOperationException;

	@Override
	public PsiElement setName(@NonNls @NotNull final String newName) throws IncorrectOperationException
	{
		// We shouldn`t do anything if name is the same
		if(newName.equals(getName()))
		{
			return null;
		}
		checkName(newName);

		final String prefix = getPrefix();
		final String fieldText = prefix != null ? prefix + newName : newName;
		final PsiElement element = RubyPsiUtil.getTopLevelElements(getProject(), fieldText).get(0);
		RubyPsiUtil.replaceInParent(this, element);
		return element;
	}

	@Override
	@NotNull
	public RType getType(@Nullable final FileSymbol fileSymbol)
	{
		return RTypeUtil.createTypeBySymbol(fileSymbol, ResolveUtil.resolveToSymbol(fileSymbol, getReference()), Context.INSTANCE, true);
	}
}
