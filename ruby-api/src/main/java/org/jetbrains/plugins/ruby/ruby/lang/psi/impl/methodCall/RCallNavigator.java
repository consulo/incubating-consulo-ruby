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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.methodCall;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.expressions.RListOfExpressions;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 04.05.2007
 */
public class RCallNavigator
{
	@Nullable
	public static RCall getByRListOfExpressions(@Nonnull final RListOfExpressions list)
	{
		final PsiElement parent = list.getParent();
		if(parent instanceof RCall && ((RCall) parent).getCallArguments() == list)
		{
			return (RCall) parent;
		}
		return null;
	}

	@Nullable
	public static RCall getByCommand(@Nonnull final RPsiElement command)
	{
		final PsiElement parent = command.getParent();
		if(parent instanceof RCall && ((RCall) parent).getPsiCommand() == command)
		{
			return (RCall) parent;
		}
		return null;
	}
}
