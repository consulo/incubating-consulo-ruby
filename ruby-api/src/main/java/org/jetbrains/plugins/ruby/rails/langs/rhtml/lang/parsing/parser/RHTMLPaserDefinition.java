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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser;

import javax.annotation.Nonnull;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer._RHTMLLexer;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLElementType;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.eRubyElementTypes;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.impl.RHTMLFileImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lang.html.HTMLLanguage;
import com.intellij.lexer.Lexer;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import consulo.lang.LanguageVersion;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 03.04.2007
 */

public class RHTMLPaserDefinition implements ParserDefinition
{
	private TokenSet myCommentTokens;
	private TokenSet myWhitespaceTokens;
	private ParserDefinition myTemplateParserDefinition;

	public RHTMLPaserDefinition()
	{
		myTemplateParserDefinition = LanguageParserDefinitions.INSTANCE.forLanguage(HTMLLanguage.INSTANCE);
		assert myTemplateParserDefinition != null;


	}

	@Override
	@Nonnull
	public Lexer createLexer(LanguageVersion languageVersion)
	{
		return new _RHTMLLexer();
	}

	@Override
	public IFileElementType getFileNodeType()
	{
		return eRubyElementTypes.RHTML_FILE;
	}

	@Override
	@Nonnull
	public TokenSet getWhitespaceTokens(LanguageVersion languageVersion)
	{
		if(myWhitespaceTokens == null)
		{
			myWhitespaceTokens = TokenSet.orSet(RHTMLTokenType.RHTML_WHITE_SPECE_TOKENS, myTemplateParserDefinition.getWhitespaceTokens(languageVersion));
		}
		return myWhitespaceTokens;
	}

	@Override
	@Nonnull
	public TokenSet getCommentTokens(LanguageVersion languageVersion)
	{
		if(myCommentTokens == null)
		{
			myCommentTokens = TokenSet.orSet(TokenSet.create(RHTMLElementType.RHTML_COMMENT_ELEMENT,

					RHTMLTokenType.RHTML_COMMENT_START, RHTMLTokenType.RHTML_COMMENT_CHARACTERS, RHTMLTokenType.RHTML_COMMENT_END), myTemplateParserDefinition.getCommentTokens(languageVersion));
		}
		return myCommentTokens;
	}

	@Nonnull
	@Override
	public TokenSet getStringLiteralElements(@Nonnull LanguageVersion languageVersion)
	{
		return myTemplateParserDefinition.getStringLiteralElements(languageVersion);
	}

	@Override
	@Nonnull
	public PsiParser createParser(LanguageVersion languageVersion)
	{
		//We use RHML_FILE instead of this. Shouldn't be invoked!
		//throw new UnsupportedOperationException("Should'n be invoked");
		return new RHTMLParser();
	}

	@Override
	public PsiFile createFile(final FileViewProvider viewProvider)
	{
		return new RHTMLFileImpl(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(final ASTNode left, final ASTNode right)
	{
		if(RHTMLTokenType.RHTML_SEPARATORS.contains(left.getElementType()) || RHTMLTokenType.RHTML_SEPARATORS.contains(right.getElementType()))
		{
			return SpaceRequirements.MUST_LINE_BREAK;
		}
		return SpaceRequirements.MAY;
	}

	@Override
	@Nonnull
	public PsiElement createElement(final ASTNode node)
	{
		return RHTMLPsiCreator.createElement(node);
	}
}

