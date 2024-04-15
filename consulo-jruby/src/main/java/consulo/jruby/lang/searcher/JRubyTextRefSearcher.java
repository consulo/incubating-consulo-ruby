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

import com.intellij.java.indexing.search.searches.MethodReferencesSearch;
import com.intellij.java.indexing.search.searches.MethodReferencesSearchExecutor;
import com.intellij.java.language.psi.PsiMethod;
import consulo.annotation.component.ExtensionImpl;
import consulo.application.ApplicationManager;
import consulo.application.util.function.Computable;
import consulo.application.util.function.Processor;
import consulo.language.psi.PsiReference;
import consulo.language.psi.search.PsiSearchHelper;
import consulo.language.psi.search.UsageSearchContext;
import jakarta.annotation.Nonnull;
import org.jetbrains.plugins.ruby.jruby.codeInsight.types.JRubyNameConventions;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 12, 2008
 */
@ExtensionImpl
public class JRubyTextRefSearcher implements MethodReferencesSearchExecutor
{
	@Override
	public boolean execute(@Nonnull final MethodReferencesSearch.SearchParameters params, @Nonnull final Processor<? super PsiReference> psiReferenceProcessor)
	{
		final PsiMethod method = params.getMethod();
		final String name = ApplicationManager.getApplication().runReadAction(new Computable<String>()
		{
			@Override
			public String compute()
			{
				return method.getName();
			}
		});
		// We should search only if JRubyName differs from name
		final String jrubyName = JRubyNameConventions.getMethodName(name).replace("=", "");
		if(name.equals(jrubyName))
		{
			return true;
		}

		final JRubyOcurrenceProcessor processor = new JRubyOcurrenceProcessor(method, jrubyName, psiReferenceProcessor, false);
		short searchContext = UsageSearchContext.IN_CODE;
		return PsiSearchHelper.SERVICE.getInstance(method.getProject()).
				processElementsWithWord(processor, params.getScope(), jrubyName, searchContext, true);
	}
}
