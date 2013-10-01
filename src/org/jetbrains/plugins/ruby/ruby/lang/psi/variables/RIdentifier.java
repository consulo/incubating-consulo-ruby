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

package org.jetbrains.plugins.ruby.ruby.lang.psi.variables;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeVariable;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPossibleCall;
import com.intellij.psi.PsiNamedElement;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 05.09.2006
 */
public interface RIdentifier extends RPossibleCall, RNamedElement, PsiNamedElement
{

	/**
	 * @return true if identifier is local variable, false otherwise
	 */
	public boolean isLocalVariable();

	/**
	 * @return true if identifier is parameter
	 */
	public boolean isParameter();

	/**
	 * @return true if identifier is method parameter
	 */
	public boolean isMethodParameter();

	/**
	 * @return true if identifier is block parameter
	 */
	public boolean isBlockParameter();

	/**
	 * @return ScopeVariable for given identifier
	 */
	@Nullable
	public ScopeVariable getScopeVariable();


}
