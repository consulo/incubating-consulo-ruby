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

package org.jetbrains.plugins.ruby.ruby.codeInsight.completion;

import java.awt.Color;

import javax.annotation.Nonnull;

import com.intellij.codeInsight.lookup.LookupValueWithPriority;
import com.intellij.codeInsight.lookup.LookupValueWithPsiElement;
import com.intellij.codeInsight.lookup.LookupValueWithUIHint;
import com.intellij.codeInsight.lookup.PresentableLookupValue;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import consulo.ide.IconDescriptorUpdaters;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 13, 2007
 */
public class JavaLookupItem implements RubyLookupItem, PresentableLookupValue, LookupValueWithPsiElement, LookupValueWithUIHint, LookupValueWithPriority, Iconable
{

	private String myLookupString;
	private PsiElement myElement;

	@Override
	@Nonnull
	public String getName()
	{
		return myLookupString;
	}

	public JavaLookupItem(@Nonnull final String name, @Nonnull final PsiElement element)
	{
		myLookupString = name;
		myElement = element;
	}

	public JavaLookupItem(@Nonnull final PsiElement element)
	{
		if(element instanceof PsiNamedElement)
		{
			myLookupString = ((PsiNamedElement) element).getName();
			if(myLookupString == null)
			{
				myLookupString = "";
			}
		}
		myElement = element;
	}

	@Override
	@Nonnull
	public String getPresentation()
	{
		return myLookupString;
	}

	@Override
	public PsiElement getElement()
	{
		return myElement;
	}

	@Override
	public Image getIcon(int flags)
	{
		return IconDescriptorUpdaters.getIcon(myElement, 0);
	}

	@Override
	public String getTypeHint()
	{
		return "";
	}

	@Override
	public Color getColorHint()
	{
		return null;
	}

	@Override
	public boolean isBold()
	{
		return true;
	}

	@Override
	public int getPriority()
	{
		return HIGH;
	}
}
