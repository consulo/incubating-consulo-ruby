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

package org.jetbrains.plugins.ruby.ruby.lang.parser;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ASTNode;
import consulo.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 20, 2007
 */
public class RubyMockParser implements PsiParser
{
	@Override
	@NotNull
	public ASTNode parse(@NotNull final IElementType root, @NotNull final PsiBuilder builder, LanguageVersion languageVersion)
	{
		List<IElementType> lexems = new ArrayList<IElementType>();
		final PsiBuilder.Marker marker = builder.mark();
		while(builder.getTokenType() != null)
		{
			lexems.add(builder.getTokenType());
			builder.advanceLexer();
		}
		marker.done(root);
		System.out.println("----------------------------------");
		for(IElementType lexem : lexems)
		{
			System.out.println(lexem);
		}
		return builder.getTreeBuilt();
	}

}
