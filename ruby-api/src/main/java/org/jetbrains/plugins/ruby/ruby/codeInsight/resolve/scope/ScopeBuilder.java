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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.impl.RootScopeImpl;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.impl.ScopeImpl;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RAssignmentExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.expressions.RSelfAssingmentExpressionNavigator;
import org.jetbrains.plugins.ruby.ruby.lang.psi.iterators.RBlockVariables;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import com.intellij.psi.PsiElement;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 4, 2007
 */
public class ScopeBuilder
{

	public static RootScope buildScope(@Nonnull final ScopeHolder holder)
	{
		final RootScope rootScope = new RootScopeImpl(holder);
		//noinspection unchecked
		buildScope(rootScope, Collections.EMPTY_SET, holder, true);
		rootScope.registerSubScope(holder, rootScope);
		return rootScope;
	}

	public static Scope buildScope(@Nonnull final RootScope root, @Nonnull final Set<String> outerNames, @Nonnull final PseudoScopeHolder holder, final boolean isRoot)
	{
		final Scope scope = isRoot ? root : new ScopeImpl(holder);
		final ScopeVisitor scopeVisitor = new ScopeVisitor();
		holder.acceptChildren(scopeVisitor);

		for(RIdentifier identifier : scopeVisitor.getCandidates())
		{
			if(!outerNames.contains(identifier.getName()))
			{
				scope.processIdentifier(identifier);
			}
		}
		final HashSet<String> names = new HashSet<String>(outerNames);

		names.addAll(scope.getScopeNames());
		for(PseudoScopeHolder scopeHolder : scopeVisitor.getSubHolders())
		{
			final Scope childScope = buildScope(root, names, scopeHolder, false);
			((ScopeImpl) scope).addSubScope(childScope);
			root.registerSubScope(scopeHolder, childScope);
		}
		return scope;
	}

	public static class ScopeVisitor extends RubyElementVisitor
	{
		private List<PseudoScopeHolder> subHolders = new ArrayList<PseudoScopeHolder>();
		private List<RIdentifier> candidates = new ArrayList<RIdentifier>();

		@Override
		public void visitRIdentifier(@Nonnull final RIdentifier rIdentifier)
		{
			if(rIdentifier.isParameter() ||
					RAssignmentExpressionNavigator.getAssignmentByLeftPart(rIdentifier) != null ||
					RSelfAssingmentExpressionNavigator.getSelfAssignmentByLeftPart(rIdentifier) != null)
			{
				candidates.add(rIdentifier);
			}
		}

		@Override
		public void visitRBlockVariables(@Nonnull final RBlockVariables blockVariables)
		{
			for(RIdentifier identifier : blockVariables.getVariables())
			{
				candidates.add(identifier);
			}
		}

		@Override
		public void visitElement(@Nonnull final PsiElement element)
		{
			if(element instanceof PseudoScopeHolder)
			{
				subHolders.add((PseudoScopeHolder) element);
				return;
			}
			element.acceptChildren(this);
		}

		@Nonnull
		public List<PseudoScopeHolder> getSubHolders()
		{
			return subHolders;
		}

		@Nonnull
		public List<RIdentifier> getCandidates()
		{
			return candidates;
		}
	}
}
