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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.basicTypes.stringLiterals;

import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.basicTypes.stringLiterals.RBaseString;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 16.07.2007
 */
public class RBaseStringNavigator
{
	@Nullable
	public static RBaseString getByPsiElement(@Nullable final PsiElement element)
	{
		if(element == null)
		{
			return null;
		}
		ASTNode node = element.getNode();
		if(node == null)
		{
			return null;
		}
		IElementType type = node.getElementType();
		if(BNF.tSTRING_TOKENS.contains(type))
		{
			return PsiTreeUtil.getParentOfType(element, RBaseString.class);
		}
		return null;
	}
}
