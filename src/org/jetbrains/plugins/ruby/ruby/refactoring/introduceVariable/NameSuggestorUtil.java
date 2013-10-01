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

package org.jetbrains.plugins.ruby.ruby.refactoring.introduceVariable;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.text.StringUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 27, 2007
 */
public class NameSuggestorUtil
{

	private static String deleteNonLetterFromString(@NotNull final String string)
	{
		Pattern pattern = Pattern.compile("[^a-zA-Z_]");
		Matcher matcher = pattern.matcher(string);
		return matcher.replaceAll("");
	}

	public static void addNames(@NotNull final List<String> possibleNames, @NotNull String name)
	{
		name = StringUtil.decapitalize(deleteNonLetterFromString(name));
		if(name.startsWith("get"))
		{
			name = name.substring(3);
		}
		else if(name.startsWith("is"))
		{
			name = name.substring(2);
		}
		while(name.startsWith("_"))
		{
			name = name.substring(1);
		}
		final int length = name.length();
		for(int i = 0; i < length; i++)
		{
			if(Character.isLetter(name.charAt(i)) && (i == 0 ||
					name.charAt(i - 1) == '_' ||
					Character.isLowerCase(name.charAt(i - 1)) && Character.isUpperCase(name.charAt(i))))
			{
				possibleNames.add(name.substring(i));
			}
		}
	}

	public static void addNamesByType(@NotNull final List<String> possibleNames, @NotNull String name)
	{
		name = StringUtil.decapitalize(deleteNonLetterFromString(name));
		possibleNames.add(name);
		possibleNames.add(name.substring(0, 1));
	}


}
