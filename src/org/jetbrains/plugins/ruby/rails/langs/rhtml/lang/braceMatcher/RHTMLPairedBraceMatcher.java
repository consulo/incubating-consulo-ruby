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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.braceMatcher;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.RHTMLTokenType;
import com.intellij.lang.BracePair;
import com.intellij.lang.PairedBraceMatcher;
import com.intellij.psi.PsiFile;
import com.intellij.psi.tree.IElementType;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 20.04.2007
 */
public class RHTMLPairedBraceMatcher implements PairedBraceMatcher {
    
    private final static BracePair[] BRACE_PAIRS = new BracePair[] {
            new BracePair(RHTMLTokenType.RHTML_EXPRESSION_START,
                         RHTMLTokenType.RHTML_EXPRESSION_END, false),

            new BracePair(RHTMLTokenType.RHTML_SCRIPTLET_START,
                          RHTMLTokenType.RHTML_SCRIPTLET_END, false),

            new BracePair( RHTMLTokenType.RHTML_COMMENT_START,
                          RHTMLTokenType.RHTML_COMMENT_END, false)
    };

    @Override
	public BracePair[] getPairs() {
        return BRACE_PAIRS;
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
