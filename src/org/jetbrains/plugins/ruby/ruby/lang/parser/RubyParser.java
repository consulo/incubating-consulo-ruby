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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsing.PROGRAM;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilderImpl;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RMarker;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 24.04.2006
 */
public class RubyParser implements PsiParser
{
	private static final Logger LOG = Logger.getInstance(RubyParser.class.getName());

	@Override
	@NotNull
	public ASTNode parse(@NotNull final IElementType root, @NotNull final PsiBuilder builder, LanguageVersion languageVersion)
	{

		final RBuilder rBuilder = createBuilder(builder);

		//        rBuilder.setDEBUG(true);

		final RMarker rootMarker = rBuilder.mark(false);

		PROGRAM.parse(rBuilder);

		rootMarker.done(root);

		LOG.assertTrue(builder.eof(), "NOT ALL TOKENS WERE PARSED!!!");

		//        rBuilder.printDebugStats();

		return rBuilder.getTreeBuilt();
	}

	protected RBuilderImpl createBuilder(@NotNull final PsiBuilder builder)
	{
		return new RBuilderImpl(builder);
	}
}
