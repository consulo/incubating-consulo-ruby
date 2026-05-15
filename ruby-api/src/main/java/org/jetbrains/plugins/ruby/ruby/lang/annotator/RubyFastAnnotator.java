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

package org.jetbrains.plugins.ruby.ruby.lang.annotator;

import jakarta.annotation.Nonnull;

import consulo.language.psi.PsiElement;
import consulo.application.progress.ProgressManager;
import consulo.language.Language;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import consulo.language.editor.annotation.AnnotationHolder;
import consulo.language.editor.annotation.Annotator;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 09.06.2006
 */
public class RubyFastAnnotator implements Annotator
{

	@Override
	public void annotate(@Nonnull final PsiElement psiElement, @Nonnull final AnnotationHolder holder)
	{
		// We hope it`s enough often operation
		ProgressManager.getInstance().checkCanceled();

		final RubyFastAnnotatorVisitor fastAnnotatorVisitor = new RubyFastAnnotatorVisitor(holder);
		psiElement.accept(fastAnnotatorVisitor);
	}
}
