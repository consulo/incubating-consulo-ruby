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

import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubySimpleLookupItem;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.RSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RStringLiteral;
import com.intellij.psi.PsiElement;
import consulo.awt.TargetAWT;

/**
 * @author yole
 */
public class ParamDefImplUtil
{
	public static RubySimpleLookupItem createSimpleLookupItem(final String value, final LookupItemType itemType, final RPsiElement valueElement)
	{
		String text = value;
		if(!(valueElement instanceof RStringLiteral) && !(valueElement instanceof RSymbol))
		{
			if(itemType == LookupItemType.String)
			{
				text = "'" + value + "'";
			}
			else if(itemType == LookupItemType.Symbol)
			{
				text = ":" + value;
			}
		}
		return new RubySimpleLookupItem(text, null, 0, true, TargetAWT.to(RubyIcons.RUBY_ICON));
	}

	public static String getElementText(final RPsiElement contextElement)
	{
		if(contextElement instanceof RStringLiteral)
		{
			return ((RStringLiteral) contextElement).getContent();
		}
		if(contextElement instanceof RSymbol)
		{
			PsiElement element = ((RSymbol) contextElement).getObject();
			return element.getText();
		}
		return contextElement.getText();
	}
}
