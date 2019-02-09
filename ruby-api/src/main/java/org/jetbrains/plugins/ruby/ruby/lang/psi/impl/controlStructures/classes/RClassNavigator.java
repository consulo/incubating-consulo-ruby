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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.classes;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RBodyStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.classes.RClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 12.03.2007
 */
public class RClassNavigator
{
	@Nullable
	public static RClass getByRBodyStatement(@Nonnull final RBodyStatement statement)
	{
		final PsiElement parent = statement.getParent();
		return (parent instanceof RClass) ? (RClass) parent : null;
	}

	@Nullable
	public static RClass getByPsiWhiteSpace(@Nonnull final PsiWhiteSpace whiteSpace)
	{
		final PsiElement parent = whiteSpace.getParent();
		return (parent instanceof RClass) ? (RClass) parent : null;
	}
}
