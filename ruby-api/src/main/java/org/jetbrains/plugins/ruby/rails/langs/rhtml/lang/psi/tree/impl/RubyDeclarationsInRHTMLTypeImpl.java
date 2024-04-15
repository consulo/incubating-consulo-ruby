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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.tree.impl;

import consulo.language.ast.ASTNode;
import consulo.language.ast.IFileElementType;
import consulo.language.parser.PsiBuilder;
import consulo.project.Project;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer.RHTMLRubyLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser.rubyInjections.RHTMLRubyParser;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import consulo.language.parser.PsiBuilderFactory;
import consulo.language.lexer.Lexer;
import consulo.language.version.LanguageVersion;
import consulo.language.version.LanguageVersionUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 07.04.2007
 */
public class RubyDeclarationsInRHTMLTypeImpl extends IFileElementType
{//extends TemplateWithOuterFragmentsTypeImpl {

	public RubyDeclarationsInRHTMLTypeImpl(final String debugName)
	{
		super(debugName, RubyLanguage.INSTANCE);
	}

	@Override
	public ASTNode parseContents(final ASTNode chameleon)
	{
		final PsiBuilderFactory factory = PsiBuilderFactory.getInstance();

		final Lexer lexer = new RHTMLRubyLexer();

		LanguageVersion defaultVersion = LanguageVersionUtil.findDefaultVersion(getLanguage());
		final Project project = chameleon.getPsi().getProject();

		final PsiBuilder builder = factory.createBuilder(project, chameleon, lexer, getLanguage(), defaultVersion, chameleon.getChars());

		return new RHTMLRubyParser().parse(chameleon.getElementType(), builder, defaultVersion);
	}
}
