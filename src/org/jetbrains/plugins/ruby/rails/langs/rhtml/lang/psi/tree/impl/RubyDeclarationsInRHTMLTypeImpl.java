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

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer.RHTMLRubyLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser.rubyInjections.RHTMLRubyParser;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.util.LanguageVersionUtil;

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

		LanguageVersion<Language> defaultVersion = LanguageVersionUtil.findDefaultVersion(getLanguage());
		final Project project = chameleon.getPsi().getProject();

		final PsiBuilder builder = factory.createBuilder(project, chameleon, lexer, getLanguage(), defaultVersion, chameleon.getChars());

		return new RHTMLRubyParser().parse(chameleon.getElementType(), builder, defaultVersion);
	}
}
