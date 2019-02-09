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

package org.jetbrains.plugins.ruby.ruby.lang;

import javax.annotation.Nonnull;

import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyMergeLexer;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import org.jetbrains.plugins.ruby.ruby.lang.parser.bnf.BNF;
import com.intellij.lang.cacheBuilder.WordOccurrence;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import com.intellij.util.Processor;


/**
 * This is a modified copy of DefaultWordsScanner
 */
public class RubyWordsScanner implements WordsScanner
{

	public static final TokenSet SEARCHABLE = TokenSet.orSet(BNF.tVARIABLES, BNF.tOPS, TokenSet.create(RubyTokenTypes.tFID, RubyTokenTypes.tAID));


	@Override
	public void processWords(@Nonnull final CharSequence fileText, @Nonnull final Processor<WordOccurrence> processor)
	{
		final RubyMergeLexer lexer = new RubyMergeLexer();
		lexer.start(fileText, 0, fileText.length(), 0);
		WordOccurrence occurence = null; // shared occurence

		while(lexer.getTokenType() != null)
		{
			final IElementType type = lexer.getTokenType();
			int start = lexer.getTokenStart();
			int end = lexer.getTokenEnd();

			if(SEARCHABLE.contains(type))
			{
				// instance variable always starts with @
				if(type == RubyTokenTypes.tIVAR)
				{
					start += 1;
				}
				else
					// class variable always starts with @@
					if(type == RubyTokenTypes.tCVAR)
					{
						start += 2;
					}
				// global variable always starts with $
				if(type == RubyTokenTypes.tGVAR)
				{
					start += 1;
				}
				// tFID or tAID always ends with ? or ! or =
				if(type == RubyTokenTypes.tFID || type == RubyTokenTypes.tAID)
				{
					end -= 1;
				}

				if(occurence == null)
				{
					occurence = new WordOccurrence(fileText, start, end, WordOccurrence.Kind.CODE);
				}
				else
				{
					occurence.init(fileText, start, end, WordOccurrence.Kind.CODE);
				}
				if(!processor.process(occurence))
				{
					return;
				}
			}
			else if(BNF.tCOMMENTS.contains(type))
			{
				if(!stripWords(processor, fileText, start, end, WordOccurrence.Kind.COMMENTS, occurence))
				{
					return;
				}
			}
			else if(BNF.tSTRING_LIKE_CONTENTS.contains(type))
			{
				if(!stripWords(processor, fileText, start, end, WordOccurrence.Kind.LITERALS, occurence))
				{
					return;
				}
			}
			lexer.advance();
		}
	}

	private static boolean stripWords(final Processor<WordOccurrence> processor, final CharSequence tokenText, int from, int to, final WordOccurrence.Kind kind, WordOccurrence occurence)
	{
		// This code seems strange but it is more effective as Character.isJavaIdentifier_xxx_ is quite costly operation due to unicode
		int index = from;

		ScanWordsLoop:
		while(true)
		{
			while(true)
			{
				if(index == to)
				{
					break ScanWordsLoop;
				}
				char c = tokenText.charAt(index);
				if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9') ||
						(Character.isJavaIdentifierStart(c) && c != '$'))
				{
					break;
				}
				index++;
			}
			int index1 = index;
			while(true)
			{
				index++;
				if(index == to)
				{
					break;
				}
				char c = tokenText.charAt(index);
				if((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9'))
				{
					continue;
				}
				if(!Character.isJavaIdentifierPart(c) || c == '$')
				{
					break;
				}
			}

			if(occurence == null)
			{
				occurence = new WordOccurrence(tokenText, index1, index, kind);
			}
			else
			{
				occurence.init(tokenText, index1, index, kind);
			}
			if(!processor.process(occurence))
			{
				return false;
			}
		}
		return true;
	}

}
