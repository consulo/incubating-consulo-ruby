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

package org.jetbrains.plugins.ruby.ruby.codeInsight.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.documentation.RubyHelpUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: May 12, 2008
 */
public class TypeCommentsHelper
{

	@Nullable
	public static RType tryToExtractTypeFromComment(@Nonnull final RMethod method, final FileSymbol fileSymbol)
	{
		final String name = method.getName();
		if("<=>".equals(name))
		{
			return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, CoreTypes.Fixnum), Context.INSTANCE, true);
		}
		if(name.endsWith("?"))
		{
			return RTypeUtil.getBooleanType(fileSymbol);
		}

		// Here we try to extract substring, containing type description
		String help = RubyHelpUtil.getPsiHelp(method);
		if(help == null)
		{
			return null;
		}
		int i = 0;
		while(true)
		{
			int i1 = help.indexOf("=>", i);
			int i2 = help.indexOf("->", i);

			i = Math.min(i1, i2);
			if(i == -1)
			{
				i = Math.max(i1, i2);
			}
			if(i == -1)
			{
				return null;
			}
			// we don`t want to see "<=>" method name instead of type!
			if(help.charAt(i - 1) != '<')
			{
				break;
			}
		}
		help = help.substring(i + 2);
		final int nlIndex = help.indexOf('\n');
		if(nlIndex != -1)
		{
			help = help.substring(0, nlIndex);
		}
		final int commaIndex = help.indexOf(',');
		if(commaIndex != -1)
		{
			help = help.substring(0, commaIndex);
		}

		help = help.trim();
		final int spaceIndex = help.indexOf(' ');
		if(spaceIndex != -1)
		{
			help = help.substring(0, spaceIndex);
		}
		if(help.startsWith("("))
		{
			help = help.substring(1);
		}

		// remove prefixes if needed
		help = chechPrefix(help, "a");
		help = chechPrefix(help, "an");
		help = chechPrefix(help, "new");
		help = chechPrefix(help, "other");

		help = help.toLowerCase();
		if(help.endsWith("_result"))
		{
			help = help.substring(0, help.length() - "_result".length());
		}

		// Hash
		if("hsh".equals(help))
		{
			help = "hash";
		}
		if("value".equals(help))
		{
			help = "object";
		}
		if("key".equals(help))
		{
			help = "object";
		}

		// here we compare extracted type string with core types
		for(String coreType : CoreTypes.AllValues)
		{
			if(coreType.toLowerCase().startsWith(help))
			{
				if(CoreTypes.TrueClass.equals(coreType) || CoreTypes.FalseClass.equals(coreType))
				{
					return RTypeUtil.getBooleanType(fileSymbol);
				}
				return RTypeUtil.createTypeBySymbol(fileSymbol, SymbolUtil.getTopLevelClassByName(fileSymbol, coreType), Context.INSTANCE, true);
			}
		}
		return null;
	}

	private static String chechPrefix(@Nonnull final String s, @Nonnull final String prefix)
	{
		if(s.startsWith(prefix) && s.length() > prefix.length() && Character.isUpperCase(s.charAt(prefix.length())))
		{
			return s.substring(prefix.length());
		}
		if(s.startsWith(prefix + '_'))
		{
			return s.substring(prefix.length() + 1);
		}
		return s;
	}
}
