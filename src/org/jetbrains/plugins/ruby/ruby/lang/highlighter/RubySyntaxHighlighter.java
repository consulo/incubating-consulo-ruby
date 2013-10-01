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

package org.jetbrains.plugins.ruby.ruby.lang.highlighter;

import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyLexer;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;


public class RubySyntaxHighlighter extends SyntaxHighlighterBase implements RubyTokenTypes
{


	private static final TokenSet tBRACES = TokenSet.orSet(BNF.tLPARENS, BNF.tLBRACKS, BNF.tLBRACES, TokenSet.create(tRPAREN, tRBRACK, tRBRACE));


	private static final Map<IElementType, TextAttributesKey> ATTRIBUTES = new HashMap<IElementType, TextAttributesKey>();

	@Override
	@NotNull
	public Lexer getHighlightingLexer()
	{
		return new RubyLexer();
	}

	static
	{
		fillMap(ATTRIBUTES, BNF.kALL_RESWORDS, RubyHighlighter.KEYWORD);
		fillMap(ATTRIBUTES, BNF.tOPS, RubyHighlighter.OPERATION_SIGN);
		fillMap(ATTRIBUTES, BNF.tCOMMENTS, RubyHighlighter.COMMENT);
		fillMap(ATTRIBUTES, BNF.tNUMBERS, RubyHighlighter.NUMBER);

		fillMap(ATTRIBUTES, tBRACES, RubyHighlighter.BRACKETS);

		ATTRIBUTES.put(T_STRING_CONTENT, RubyHighlighter.STRING);
		fillMap(ATTRIBUTES, BNF.tSTRING_DELIMITERS, RubyHighlighter.STRING);

		ATTRIBUTES.put(tREGEXP_CONTENT, RubyHighlighter.REGEXPS);
		fillMap(ATTRIBUTES, BNF.tREGEXP_DELIMITERS, RubyHighlighter.REGEXPS);

		ATTRIBUTES.put(tWORDS_CONTENT, RubyHighlighter.WORDS);
		fillMap(ATTRIBUTES, BNF.tWORDS_DELIMITERS, RubyHighlighter.WORDS);

		ATTRIBUTES.put(tHEREDOC_CONTENT, RubyHighlighter.HEREDOC_CONTENT);
		fillMap(ATTRIBUTES, BNF.tHEREDOC_ALL_IDS, RubyHighlighter.HEREDOC_ID);

		fillMap(ATTRIBUTES, BNF.tEXPR_SUBT_TOKENS, RubyHighlighter.EXPR_SUBST_MARKS);

		ATTRIBUTES.put(tCOMMA, RubyHighlighter.COMMA);
		ATTRIBUTES.put(tSEMICOLON, RubyHighlighter.SEMICOLON);
		ATTRIBUTES.put(tLINE_CONTINUATION, RubyHighlighter.LINE_CONTINUATION);

		ATTRIBUTES.put(tIDENTIFIER, RubyHighlighter.IDENTIFIER);
		ATTRIBUTES.put(tCONSTANT, RubyHighlighter.CONSTANT);
		ATTRIBUTES.put(tCVAR, RubyHighlighter.CVAR);
		ATTRIBUTES.put(tGVAR, RubyHighlighter.GVAR);
		ATTRIBUTES.put(tIVAR, RubyHighlighter.IVAR);

		ATTRIBUTES.put(tNTH_REF, RubyHighlighter.NTH_REF);
		ATTRIBUTES.put(tBACK_REF, RubyHighlighter.BACK_REF);

		ATTRIBUTES.put(tESCAPE_SEQUENCE, RubyHighlighter.ESCAPE_SEQUENCE);
		ATTRIBUTES.put(tINVALID_ESCAPE_SEQUENCE, RubyHighlighter.INVALID_ESCAPE_SEQUENCE);

		ATTRIBUTES.put(tBAD_CHARACTER, RubyHighlighter.BAD_CHARACTER);

		ATTRIBUTES.put(tASSOC, RubyHighlighter.HASH_ASSOC);

	}


	@Override
	@NotNull
	public TextAttributesKey[] getTokenHighlights(IElementType tokenType)
	{
		return pack(ATTRIBUTES.get(tokenType));
	}

}

