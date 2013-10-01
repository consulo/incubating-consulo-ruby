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

package org.jetbrains.plugins.ruby.ruby.codeInsight.paramDefs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubySimpleLookupItem;
import org.jruby.RubySymbol;

/**
 * @author yole
 */
public class EnumParam extends ParamDef
{
	private Collection myVariants;

	public EnumParam(Collection variants)
	{
		myVariants = variants;
	}

	@Override
	@Nullable
	public List<RubyLookupItem> getVariants(ParamContext context)
	{
		List<RubyLookupItem> result = new ArrayList<RubyLookupItem>();
		for(Object variant : myVariants)
		{
			String text;
			if(variant instanceof RubySymbol)
			{
				text = ':' + variant.toString();
			}
			else
			{
				text = variant.toString();
			}
			result.add(new RubySimpleLookupItem(text, null, 0, true, RubyIcons.RUBY_ICON));
		}
		return result;
	}
}
