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

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Jul 12, 2007
 */
public class ScopeUtil
{

	/**
	 * Finds the scope, where current identifier can be found
	 *
	 * @param identifier Indentifier to find scope
	 * @return Scope
	 */
	@Nullable
	public static Scope findScopeByIdentifier(@NotNull final RIdentifier identifier)
	{
		final String name = identifier.getName();
		PseudoScopeHolder holder = PseudoScopeHolderNavigator.getScopeHolder(identifier);
		assert holder != null;
		final RootScope rootScope = getRootScope(holder);
		while(true)
		{
			final Scope childScope = rootScope.getChildScope(holder);
			if(childScope != null && childScope.getScopeNames().contains(name))
			{
				return childScope;
			}
			if(holder instanceof ScopeHolder)
			{
				return null;
			}
			holder = PsiTreeUtil.getParentOfType(holder, PseudoScopeHolder.class);
			if(holder == null)
			{
				return null;
			}
		}
	}

	/**
	 * @param element context
	 * @return Returns the root scope for the given context
	 */
	@NotNull
	public static RootScope getRootScope(@NotNull final PsiElement element)
	{
		final ScopeHolder holder = element instanceof ScopeHolder ? (ScopeHolder) element : PsiTreeUtil.getParentOfType(element, ScopeHolder.class);
		assert holder != null;
		return holder.getScope();
	}

	/**
	 * Gathers all the scopeVariables of given context
	 *
	 * @param context Context context
	 * @return List of ScopeVariables
	 */
	public static List<ScopeVariable> gatherScopeVariables(@NotNull PsiElement context)
	{
		final RootScope rootScope = ScopeUtil.getRootScope(context);
		final ScopeHolder holder = PsiTreeUtil.getParentOfType(context, ScopeHolder.class);
		final ArrayList<ScopeVariable> list = new ArrayList<ScopeVariable>();
		while(true)
		{
			if(context instanceof PseudoScopeHolder)
			{
				final Scope childScope = rootScope.getChildScope((PseudoScopeHolder) context);
				if(childScope != null)
				{
					list.addAll(childScope.getVariables());
				}
			}
			if(context == holder)
			{
				return list;
			}
			context = context.getParent();
		}
	}
}
