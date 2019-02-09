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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 19.05.2007
 */
public class NamingConventions
{
	//TODO rename to "isCamelized"
	@SuppressWarnings({"ConstantConditions"})
	public static boolean isInMixedCase(@Nullable final String name)
	{
		return !TextUtil.isEmpty(name) && name.matches("[A-Z][A-Za-z0-9]*");
	}

	@SuppressWarnings({"ConstantConditions"})
	public static boolean isInUnderscoredCase(@Nullable final String name)
	{
		return !TextUtil.isEmpty(name) && name.matches("[_a-z][_a-z0-9]*");
	}

	//TODO rename to "camelize"
	@Nonnull
	public static String toMixedCase(@Nullable final String name)
	{
		if(name == null)
		{
			return TextUtil.EMPTY_STRING;
		}
		//lower_case_and_underscored_word.to_s.gsub(/\/(.?)/) { "::" + $1.upcase }.gsub(/(^|_)(.)/) { $2.upcase }
		StringBuilder buffer = new StringBuilder();
		boolean isUpperCase = true;
		final int length = name.length();
		for(int i = 0; i < length; i++)
		{
			final char ch = name.charAt(i);
			if(ch == '_' && (i + 1 < length && name.charAt(i + 1) == '_'))
			{
				continue;
			}
			if(i + 1 < length && ((i > 0 && ((ch == '_' && name.charAt(i - 1) != '_'))) || (ch == ':' && name.charAt(i + 1) == ':')))
			{

				isUpperCase = true;
				if(ch == ':')
				{
					buffer.append(RubyUtil.MODULES_PATH_SEPARATOR);
					i++;
				}
				continue;
			}
			if(Character.isUpperCase(ch))
			{
				isUpperCase = true;
			}
			buffer.append(isUpperCase ? Character.toUpperCase(ch) : Character.toLowerCase(ch));
			isUpperCase = false;
		}
		return buffer.toString();
	}

	@Nonnull
	public static String toUnderscoreCase(@Nullable final String name)
	{
		if(name == null)
		{
			return TextUtil.EMPTY_STRING;
		}

		/** We use :
		 camel_cased_word.to_s.gsub(/::/, '/').
		 gsub(/([A-Z]+)([A-Z][a-z])/,'\1_\2').
		 gsub(/([a-z\d])([A-Z])/,'\1_\2').
		 tr("-", "_").
		 downcase
		 */

		final String normalizedName = name.replaceAll("::", "/");
		StringBuilder buffer = new StringBuilder();
		final int length = normalizedName.length();

		for(int i = 0; i < length; i++)
		{
			final char ch = normalizedName.charAt(i);
			if(ch != '-')
			{
				buffer.append(Character.toLowerCase(ch));
			}
			else
			{
				buffer.append("_");
			}

			if(Character.isLetterOrDigit(ch))
			{
				if(Character.isUpperCase(ch))
				{
					//gsub(/([A-Z]+)([A-Z][a-z])/,'\1_\2').
					if(i + 2 < length)
					{
						final char chNext = normalizedName.charAt(i + 1);
						final char chNextNext = normalizedName.charAt(i + 2);

						if(Character.isUpperCase(chNext) && Character.isLowerCase(chNextNext))
						{

							buffer.append('_');
						}
					}
				}
				else if(Character.isLowerCase(ch) || Character.isDigit(ch))
				{
					//gsub(/([a-z\d])([A-Z])/,'\1_\2').
					if(i + 1 < length)
					{
						final char chNext = normalizedName.charAt(i + 1);
						if(Character.isUpperCase(chNext))
						{
							buffer.append('_');
						}
					}
				}
			}
		}
		return buffer.toString();
	}
}
