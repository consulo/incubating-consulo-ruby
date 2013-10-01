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

package org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.impl;

import java.util.Collection;
import java.util.HashMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.PseudoScopeHolder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.RootScope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.Scope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeHolder;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Jul 12, 2007
 */
public class RootScopeImpl extends ScopeImpl implements RootScope
{
	private HashMap<PseudoScopeHolder, Scope> mySubScopes = new HashMap<PseudoScopeHolder, Scope>();

	public RootScopeImpl(@NotNull final ScopeHolder holder)
	{
		super(holder);
	}

	@Override
	@Nullable
	public Scope getChildScope(@NotNull final PseudoScopeHolder holder)
	{
		return mySubScopes.get(holder);
	}

	@Override
	public void registerSubScope(@NotNull final PseudoScopeHolder scopeHolder, @NotNull final Scope childScope)
	{
		mySubScopes.put(scopeHolder, childScope);
	}

	@Override
	public Collection<Scope> getAllChildScopes()
	{
		return mySubScopes.values();
	}

}
