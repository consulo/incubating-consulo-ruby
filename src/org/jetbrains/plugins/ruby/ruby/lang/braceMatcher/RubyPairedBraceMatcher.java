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

import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.lang.lexer.RubyTokenTypes;


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
            new BracePair('d',      kDEF,        'e',       kEND, true),
            new BracePair('c',      kCLASS,      'e',       kEND, true),
            new BracePair('m',      kMODULE,     'e',       kEND, true),


// control structures
            new BracePair('b',      kBEGIN,      'e',       kEND, true),
            new BracePair('i',      kIF,         'e',       kEND, true),
            new BracePair('u',      kUNLESS,     'e',       kEND, true),
            new BracePair('w',      kWHILE,      'e',       kEND, true),
            new BracePair('u',      kUNTIL,      'e',       kEND, true),
            new BracePair('c',      kCASE,       'e',       kEND, true),
            new BracePair('f',      kFOR,        'e',       kEND, true),

            new BracePair('d',      kDO,         'e',       kEND, true),

// braces
            new BracePair('(', tLPAREN,         ')', tRPAREN, false),
            new BracePair('(', tfLPAREN,        ')', tRPAREN, false),
            new BracePair('(', tLPAREN_ARG,        ')', tRPAREN, false),

            new BracePair('[', tLBRACK,         ']', tRBRACK, false),
            new BracePair('[', tfLBRACK,        ']', tRBRACK, false),

            new BracePair('{', tLBRACE,         '}', tRBRACE, false),
            new BracePair('{', tfLBRACE,        '}', tRBRACE, false),

// strigns and regexps
            new BracePair('s', tDOUBLE_QUOTED_STRING_BEG,  's', tSTRING_END, false),
            new BracePair('s', tSINGLE_QUOTED_STRING_BEG,  's', tSTRING_END, false),
            new BracePair('s', tXSTRING_BEG,               's', tSTRING_END, false),
            new BracePair('s', tREGEXP_BEG,                's', tREGEXP_END, false),
            new BracePair('s', tWORDS_BEG,                 's', tWORDS_END, false),
            new BracePair('s', tQWORDS_BEG,                's', tWORDS_END, false),

// expr subtitution tokens
            new BracePair('#' , tSTRING_DBEG,    '}', tSTRING_DEND, false),

    };

    @NotNull
    public BracePair[] getPairs() {
        return PAIRS;
    }

    @SuppressWarnings({"UnusedParameters"})
    public boolean isPairedBracesAllowedBeforeType(@NotNull IElementType iElementType, @Nullable IElementType iElementType1) {
        return true;
    }
}

