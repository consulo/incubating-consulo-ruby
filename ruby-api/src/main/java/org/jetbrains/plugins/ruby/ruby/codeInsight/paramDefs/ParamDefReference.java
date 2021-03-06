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

package org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs;

import java.util.Collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.util.IncorrectOperationException;

/**
 * @author yole
 */
public class ParamDefReference implements PsiReference
{
	private RPsiElement myElement;
	private ParamDef myParamDef;
	private ParamContext myContext;

	public ParamDefReference(final RPsiElement element, final ParamDef paramDef, final ParamContext context)
	{
		myElement = element;
		myParamDef = paramDef;
		myContext = context;
	}

	@Override
	public PsiElement getElement()
	{
		return myElement;
	}

	@Override
	public TextRange getRangeInElement()
	{
		if(myElement instanceof RStringLiteral)
		{
			return new TextRange(1, myElement.getTextLength() - 1);
		}
		if(myElement instanceof RSymbol)
		{
			return new TextRange(1, myElement.getTextLength());
		}
		return new TextRange(0, myElement.getTextLength());
	}

	@Override
	@Nullable
	public PsiElement resolve()
	{
		return myParamDef.resolveReference(myContext);
	}

	@Override
	public String getCanonicalText()
	{
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public PsiElement handleElementRename(final String s) throws IncorrectOperationException
	{
		if(myElement instanceof RStringLiteral)
		{
			String text = "'" + s + "'";
			final RPsiElement newElement = RubyPsiUtil.getTopLevelElements(myElement.getProject(), text).get(0);
			assert newElement instanceof RStringLiteral;
			return myElement.replace(newElement);
		}
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public PsiElement bindToElement(@Nonnull final PsiElement psiElement) throws IncorrectOperationException
	{
		throw new IncorrectOperationException();
	}

	@Override
	public boolean isReferenceTo(final PsiElement psiElement)
	{
		PsiElement resolveResult = resolve();
		return psiElement.getManager().areElementsEquivalent(resolveResult, psiElement);
	}

	@Override
	public Object[] getVariants()
	{
		Collection<RubyLookupItem> rubyLookupItemList = myParamDef.getVariants(myContext);
		if(rubyLookupItemList != null)
		{
			return rubyLookupItemList.toArray(new Object[rubyLookupItemList.size()]);
		}
		return new Object[0];
	}

	@Override
	public boolean isSoft()
	{
		return false;
	}
}
