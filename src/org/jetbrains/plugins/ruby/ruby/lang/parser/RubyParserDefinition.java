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
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyLexer;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RFileImpl;
import com.intellij.lang.ASTNode;
import com.intellij.lang.LanguageVersion;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;


public class RubyParserDefinition implements ParserDefinition, RubyElementTypes
{

	@Override
	@NotNull
	public Lexer createLexer(Project project, LanguageVersion languageVersion)
	{
		return new RubyLexer();
	}

	@Override
	@NotNull
	public PsiParser createParser(Project project, LanguageVersion languageVersion)
	{
		return new RubyParser();
		//        return new RubyMockParser();
	}

	@Override
	@NotNull
	public IFileElementType getFileNodeType()
	{
		return RubyElementTypes.FILE;
	}

	@Override
	@NotNull
	public TokenSet getWhitespaceTokens(LanguageVersion languageVersion)
	{
		return BNF.tWHITESPACES;
	}

	@Override
	@NotNull
	public TokenSet getCommentTokens(LanguageVersion languageVersion)
	{
		return BNF.tCOMMENTS;
	}

	@NotNull
	@Override
	public TokenSet getStringLiteralElements(@NotNull LanguageVersion languageVersion)
	{
		return BNF.tSTRING_TOKENS;
	}

	@Override
	@NotNull
	public PsiElement createElement(@NotNull ASTNode node)
	{
		return RubyPsiCreator.create(node);
	}

	@Override
	public PsiFile createFile(FileViewProvider viewProvider)
	{
		return new RFileImpl(viewProvider);
	}

	@Override
	public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right)
	{
		return SpaceRequirements.MAY;
	}

}
