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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.lexer;

import java.io.Reader;

import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.MergingLexerAdapter;
import com.intellij.psi.tree.TokenSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.04.2007
 */

/**
 * This wrapper for flex lexer.
 */
public class _RHTMLLexer extends MergingLexerAdapter
{
	private static final TokenSet TOKENS_TO_MERGE = TokenSet.create(RHTMLTokenType.TEMPLATE_CHARACTERS_IN_RHTML);

	public _RHTMLLexer()
	{
		super(new FlexAdapter(new _RHTMLFlexLexer((Reader) null)), TOKENS_TO_MERGE);
	}
}