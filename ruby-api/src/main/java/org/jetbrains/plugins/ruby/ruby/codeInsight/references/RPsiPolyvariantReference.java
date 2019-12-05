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

package org.jetbrains.plugins.ruby.ruby.codeInsight.references;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPolyVariantReference;
import consulo.util.dataholder.Key;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Aug 31, 2007
 */
public interface RPsiPolyvariantReference extends PsiPolyVariantReference
{
	public static final Key<Boolean> REFERENCE_BEING_COMPLETED = Key.create("Ruby.ReferenceBeingCompleted");

	@Nonnull
	public PsiElement getRefValue();

	/**
	 * resolves to symbols
	 *
	 * @param fileSymbol FileSymbol
	 *                   in "empty context"
	 * @return List of symbols
	 */
	@Nonnull
	public List<Symbol> multiResolveToSymbols(@Nullable FileSymbol fileSymbol);

}
