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

package org.jetbrains.plugins.ruby.ruby.inspections.scopes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.PseudoScopeHolder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.RootScope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.Scope;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeBuilder;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeHolder;
import org.jetbrains.plugins.ruby.ruby.inspections.RubyInspectionVisitor;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Jul 12, 2007
 */
public class RubyScopesVisitor extends RubyInspectionVisitor
{

	public RubyScopesVisitor(@NotNull final ProblemsHolder holder)
	{
		super(holder);
	}

	@Override
	public void visitElement(@NotNull final PsiElement element)
	{
		// It`s often operation
		ProgressManager.getInstance().checkCanceled();

		if(element instanceof ScopeHolder)
		{
			visitScopeHolder((ScopeHolder) element);
		}
	}

	//TODO: optimize. n^2 time. But scopes number is not so big!!!
	private void visitScopeHolder(@NotNull final ScopeHolder scopeHolder)
	{
		final RootScope rootScope = scopeHolder.getScope();
		for(Scope scope : rootScope.getAllChildScopes())
		{
			PseudoScopeHolder holder = scope.getHolder();
			final ScopeBuilder.ScopeVisitor scopeVisitor = new ScopeBuilder.ScopeVisitor();
			holder.acceptChildren(scopeVisitor);

			final HashMap<String, RIdentifier> name2Identifier = new HashMap<String, RIdentifier>();
			for(RIdentifier identifier : scopeVisitor.getCandidates())
			{
				if(identifier.isParameter())
				{
					name2Identifier.put(identifier.getName(), identifier);
				}
			}

			while(holder != scopeHolder)
			{
				holder = PsiTreeUtil.getParentOfType(holder, PseudoScopeHolder.class);
				if(holder == null)
				{
					break;
				}
				final Scope parentScope = rootScope.getChildScope(holder);
				final Set<String> parentScopeNames;
				if(parentScope != null)
				{
					parentScopeNames = parentScope.getScopeNames();
				}
				else
				{
					parentScopeNames = Collections.emptySet();
				}
				for(String name : parentScopeNames)
				{
					final RIdentifier id = name2Identifier.get(name);
					if(id != null && id.isParameter())
					{
						registerProblem(id, RBundle.message("inspection.scopes.parameter.shadows.outer"));
					}
				}
			}
		}
	}

}
