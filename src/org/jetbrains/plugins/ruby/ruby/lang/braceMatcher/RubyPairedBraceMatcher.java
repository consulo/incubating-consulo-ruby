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

package org.jetbrains.plugins.ruby.ruby.lang.braceMatcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;


public class RubyPairedBraceMatcher implements PairedBraceMatcher, RubyTokenTypes {
    public static final TokenSet kEND_BRACE_TOKENS = TokenSet.create(
            kDEF,
            kCLASS,
            kMODULE,
            kBEGIN,
            kIF,
            kUNLESS,
            kWHILE,
            kUNTIL,
            kCASE,
            kFOR,
            kDO);

    private static final BracePair[] PAIRS = new BracePair[]{
// class, module, method
            new BracePair(      kDEF,               kEND, true),
            new BracePair(      kCLASS,             kEND, true),
            new BracePair(      kMODULE,            kEND, true),


// control structures
            new BracePair(      kBEGIN,             kEND, true),
            new BracePair(      kIF,                kEND, true),
            new BracePair(      kUNLESS,            kEND, true),
            new BracePair(      kWHILE,             kEND, true),
            new BracePair(      kUNTIL,             kEND, true),
            new BracePair(      kCASE,              kEND, true),
            new BracePair(      kFOR,               kEND, true),

            new BracePair(      kDO,                kEND, true),

// braces
            new BracePair(tLPAREN,         tRPAREN, false),
            new BracePair(tfLPAREN,        tRPAREN, false),
            new BracePair(tLPAREN_ARG,         tRPAREN, false),

            new BracePair(tLBRACK,         tRBRACK, false),
            new BracePair(tfLBRACK,       tRBRACK, false),

            new BracePair(tLBRACE,          tRBRACE, false),
            new BracePair(tfLBRACE,         tRBRACE, false),

// strigns and regexps
            new BracePair( tDOUBLE_QUOTED_STRING_BEG,   tSTRING_END, false),
            new BracePair( tSINGLE_QUOTED_STRING_BEG,   tSTRING_END, false),
            new BracePair( tXSTRING_BEG,                tSTRING_END, false),
            new BracePair( tREGEXP_BEG,                 tREGEXP_END, false),
            new BracePair( tWORDS_BEG,                  tWORDS_END, false),
            new BracePair( tQWORDS_BEG,                 tWORDS_END, false),

// expr subtitution tokens
            new BracePair(tSTRING_DBEG,     tSTRING_DEND, false),

    };

    @Override
	@NotNull
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @Override
	@SuppressWarnings({"UnusedParameters"})
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
        return true;
    }

	@Override
	public int getCodeConstructStart(PsiFile psiFile, int i)
	{
		return 0;
	}
}

