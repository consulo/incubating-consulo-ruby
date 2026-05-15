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

package consulo.jruby.lang.searcher;

import com.intellij.java.indexing.search.searches.OverridingMethodsSearch;
import com.intellij.java.indexing.search.searches.OverridingMethodsSearchExecutor;
import com.intellij.java.language.psi.PsiMethod;
import consulo.application.ApplicationManager;
import consulo.application.util.function.Computable;
import java.util.function.Predicate;
import consulo.application.util.query.QueryExecutor;
import consulo.language.psi.PsiElement;
import consulo.language.psi.search.PsiSearchHelper;
import consulo.language.psi.search.UsageSearchContext;
import jakarta.annotation.Nonnull;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 17, 2008
 */
public class JRubyOverridingMethodsSearch implements OverridingMethodsSearchExecutor
{
	@Override
	public boolean execute(@Nonnull OverridingMethodsSearch.SearchParameters params, @Nonnull Predicate<? super PsiMethod> consumer)
	{
		final PsiMethod method = params.getMethod();

		final String name = ApplicationManager.getApplication().runReadAction((Computable<String>) () -> method.getName());

		final JRubyOverridingMethodsProcessor processor = new JRubyOverridingMethodsProcessor(method, name, consumer);
		return PsiSearchHelper.SERVICE.getInstance(method.getProject()).
				processElementsWithWord(processor, params.getScope(), name, UsageSearchContext.IN_CODE, true);
	}
}
