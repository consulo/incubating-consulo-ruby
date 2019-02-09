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
import javax.annotation.Nullable;

import com.intellij.codeInsight.lookup.DeferredUserLookupValue;
import com.intellij.codeInsight.lookup.LookupItem;
import com.intellij.codeInsight.lookup.LookupValueWithPriority;
import com.intellij.codeInsight.lookup.LookupValueWithUIHint;
import com.intellij.codeInsight.lookup.PresentableLookupValue;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import consulo.ui.image.Image;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.04.2007
 */
public class RubySimpleLookupItem implements RubyLookupItem, PresentableLookupValue, DeferredUserLookupValue, LookupValueWithUIHint, LookupValueWithPriority, Iconable
{

	private String myLookupString;
	private String myTypeText;
	private boolean isBold;
	private Image myIcon;
	private int myPriority;

	public RubySimpleLookupItem(@Nonnull final String lookupString, @Nullable final String typeText, final int priority, final boolean bold, final Image icon)
	{
		myLookupString = lookupString;
		myTypeText = typeText;
		isBold = bold;
		myPriority = priority;
		myIcon = icon;
	}

	@Override
	@Nonnull
	public String getName()
	{
		return myLookupString;
	}

	@Override
	public String getPresentation()
	{
		return myLookupString;
	}

	@Override
	public boolean handleUserSelection(LookupItem item, Project project)
	{
		return true;
	}

	@Override
	public String getTypeHint()
	{
		return myTypeText != null ? myTypeText : "";
	}

	@Override
	public Color getColorHint()
	{
		return null;
	}

	@Override
	public boolean isBold()
	{
		return isBold;
	}

	@Override
	public Image getIcon(int flags)
	{
		return myIcon;
	}

	@Override
	public int getPriority()
	{
		return myPriority;
	}
}
