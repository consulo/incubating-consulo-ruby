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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Dec 21, 2007
 */

import javax.annotation.Nullable;
import com.intellij.formatting.Block;
import com.intellij.formatting.Indent;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.formatter.xml.AnotherLanguageBlockWrapper;
import com.intellij.psi.formatter.xml.XmlFormattingPolicy;

//TODO separate with AnotherLanguageBlockWrapper after IDEA #7613
public class ForeignLanguageBlock extends AnotherLanguageBlockWrapper
{
	public ForeignLanguageBlock(ASTNode node, XmlFormattingPolicy policy, Block original, Indent indent, int offset, @Nullable TextRange range)
	{
		super(node, policy, original, indent, offset, range);
	}
}
