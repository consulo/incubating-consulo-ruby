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

package org.jetbrains.plugins.ruby.ruby.lang.findUsages;

import consulo.annotation.component.ExtensionImpl;
import consulo.language.psi.PsiElement;
import consulo.usage.UsageType;
import consulo.usage.UsageTypeProvider;
import consulo.language.psi.PsiReference;
import consulo.util.dataholder.Key;
import org.jetbrains.plugins.ruby.RBundle;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 9, 2007
 */
@ExtensionImpl
public class RubyUsageTypeProvider implements UsageTypeProvider
{
	public static final Key<RubyUsageType> RUBY_USAGE_TYPE_KEY = Key.create("RubyUsageType");

	private static final UsageType TEXT_TYPED = new UsageType(RBundle.message("find.usages.text.matched.group"));
	private static final UsageType EXPLICITLY_TYPED = new UsageType(RBundle.message("find.usages.explicitly.typed.group"));
	private static final UsageType DECLARATION = new UsageType(RBundle.message("find.usages.ruby.declarations.group"));

	@Override
	@Nullable
	public UsageType getUsageType(@Nonnull final PsiElement element)
	{
		final RubyUsageType type = getType(element);
		if(type == RubyUsageType.DECLARATION)
		{
			return DECLARATION;
		}
		if(type == RubyUsageType.EXPLICITLY_TYPED)
		{
			return EXPLICITLY_TYPED;
		}
		if(type == RubyUsageType.UNCLASSIFIED)
		{
			return UsageType.UNCLASSIFIED;
		}
		if(type == RubyUsageType.TEXT_MATCHED)
		{
			return TEXT_TYPED;
		}
		return null;
	}

	public static void setType(@Nonnull final PsiReference reference, final RubyUsageType type)
	{
		reference.getElement().putUserData(RUBY_USAGE_TYPE_KEY, type);

	}

	@Nullable
	public static RubyUsageType getType(@Nonnull final PsiElement element)
	{
		return element.getUserData(RUBY_USAGE_TYPE_KEY);
	}
}
