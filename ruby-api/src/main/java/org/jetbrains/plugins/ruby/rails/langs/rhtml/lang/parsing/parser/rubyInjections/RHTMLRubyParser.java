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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser.rubyInjections;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.parser.RubyParser;
import org.jetbrains.plugins.ruby.ruby.lang.parser.parsingUtils.RBuilderImpl;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 21.04.2007
 */
public class RHTMLRubyParser extends RubyParser implements PsiParser
{

	@Override
	protected RBuilderImpl createBuilder(@NotNull final PsiBuilder builder)
	{
		return new RHTMLRubyBuilder(builder);
	}

}
