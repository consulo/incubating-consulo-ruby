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

package org.jetbrains.plugins.ruby.ruby.lang.documentation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 11, 2007
 * see http://rdoc.sourceforge.net/doc/index.html for more details
 */
public class MarkupUtil implements MarkupConstants
{
	private static final Pattern DIRECTIVE = Pattern.compile("(?<![:\\w]):\\w+:");
	private static final Pattern BOLD = Pattern.compile("\\*\\w+\\*");
	private static final Pattern ITALIC = Pattern.compile("(?<!\\w|\\*)_\\w+_");
	private static final Pattern CODE = Pattern.compile("\\+\\w+\\+");
	private static final Pattern HEADINGS = Pattern.compile("(?<=\\n\\s)=+\\s[^\\n]*");
	private static final Pattern RULES = Pattern.compile("(?<=\\s)-{3,}(?>\\s)");
	private static final Pattern TODOS = Pattern.compile("[tT][oO][dD][oO] [^\\n]*");

	public static String processText(@NotNull String help)
	{
		help = help.trim();
		if(help.startsWith("#"))
		{
			help = help.substring(1);
		}
		help = help.replaceAll("(?<=\\n)#", "").replace("<", LT).replace(">", GT);

		help = processHeadings(help);

		help = processTodos(help);

		help = processRules(help);

		help = processCode(help);

		help = processItalic(help);

		help = processBold(help);

		help = help.replaceAll(" \" ", PRIME).replaceAll("\n", BR);
		help = help.replaceAll("(?<!<font) ", SPACE);

		help = processDirective(help);


		return help;
	}

	private static String processTodos(String help)
	{
		final StringBuffer buffer = new StringBuffer();
		final Matcher matcher = TODOS.matcher(help);
		while(matcher.find())
		{
			final String matched = matcher.group(0);
			matcher.appendReplacement(buffer, TODO_PREFIX + matched + TODO_SUFFIX);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String processRules(String help)
	{
		final Matcher matcher = RULES.matcher(help);
		return matcher.replaceAll(HR);
	}

	private static String processHeadings(String help)
	{
		// it`s a hack to get pattern working
		if(help.startsWith(" ="))
		{
			help = "\n" + help;
		}
		final StringBuffer buffer = new StringBuffer();
		final Matcher matcher = HEADINGS.matcher(help);
		while(matcher.find())
		{
			final String matched = matcher.group(0);
			int i = 0;
			while(i < matched.length() && matched.charAt(i) == '=')
			{
				i++;
			}
			matcher.appendReplacement(buffer, "<h" + i + ">" + matched.substring(i) + "</h" + i + ">");
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String processCode(String help)
	{
		final StringBuffer buffer = new StringBuffer();
		final Matcher matcher = CODE.matcher(help);
		while(matcher.find())
		{
			final String matched = matcher.group(0);
			matcher.appendReplacement(buffer, CODE_PREFIX + matched.substring(1, matched.length() - 1) + CODE_SUFFIX);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String processItalic(String help)
	{
		final StringBuffer buffer = new StringBuffer();
		final Matcher matcher = ITALIC.matcher(help);
		while(matcher.find())
		{
			final String matched = matcher.group(0);
			matcher.appendReplacement(buffer, ITALIC_PREFIX + matched.substring(1, matched.length() - 1) + ITALIC_SUFFIX);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String processBold(String help)
	{
		final StringBuffer buffer = new StringBuffer();
		final Matcher matcher = BOLD.matcher(help);
		while(matcher.find())
		{
			final String matched = matcher.group(0);
			matcher.appendReplacement(buffer, BOLD_PREFIX + matched.substring(1, matched.length() - 1) + BOLD_SUFFIX);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	private static String processDirective(String help)
	{
		final StringBuffer buffer = new StringBuffer();
		final Matcher matcher = DIRECTIVE.matcher(help);
		while(matcher.find())
		{
			final String matched = matcher.group(0);
			matcher.appendReplacement(buffer, DIRECTIVE_PREFIX + matched.substring(1, matched.length() - 1) + DIRECTIVE_SUFFIX);
		}
		matcher.appendTail(buffer);
		return buffer.toString();
	}

	public static void appendBold(@NotNull final StringBuilder builder, @Nullable final String s)
	{
		builder.append(BOLD_PREFIX).append(s).append(BOLD_SUFFIX);
	}

	public static void appendCode(@NotNull final StringBuilder builder, @Nullable final String s)
	{
		builder.append(CODE_PREFIX).append(s).append(CODE_SUFFIX);
	}

	public static void appendBoldCode(@NotNull final StringBuilder builder, @Nullable final String s)
	{
		builder.append(BOLD_PREFIX).append(CODE_PREFIX).append(s).append(CODE_SUFFIX).append(BOLD_SUFFIX);
	}

	public static String boldCode(@Nullable final String s)
	{
		return BOLD_PREFIX + CODE_PREFIX + s + CODE_SUFFIX + BOLD_SUFFIX;
	}
}
