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

package org.jetbrains.plugins.ruby.ruby.run.filters;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.RubyUtil;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;
import org.jetbrains.plugins.ruby.support.utils.RModuleUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFile;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.07.2007
 */

/**
 * This filter is used to create links from the console to files in project
 * test/ffoooo/rb
 * test/foo/rb.rb
 * All the pathes are relative to the module root. No spaces in file names!!!
 */
public class RFileLinksFilter implements Filter
{
	// Prefix may be whitespace, '[', '(' ot '{'
	@NonNls
	protected static final String PREFIX_PATTERN = "(\\s|^|\\[|\\(|\\{)";

	// Path use both type separators(for Unix and Windows )
	@NonNls
	private static final String PATTERN = "(/|\\\\)[^:\\s\\?:]+(" + RStackTraceFilter.EXT_PATTERN + ")?[^:]";

	@NonNls
	private static final String WIN_PATTERN = PREFIX_PATTERN + "(" + RStackTraceFilter.DISK_PATTERN + "|" + RStackTraceFilter.SPECIAL_FOLDER_PATTERN + ")" + PATTERN;
	@NonNls
	private static final String UNIX_PATTERN = PREFIX_PATTERN + "(" + RStackTraceFilter.HOME_FOLDER_PATTERN + "|" + RStackTraceFilter.SPECIAL_FOLDER_PATTERN + ")?" + PATTERN;

	private static final Pattern WIN_CPATTERN = Pattern.compile(WIN_PATTERN);
	private static final Pattern UNIX_CPATTERN = Pattern.compile(UNIX_PATTERN);

	public Project myProject;
	@Nullable
	public String myScriptPath;

	private RApplicationSettings appSettings;


	public RFileLinksFilter(final Module module)
	{
		this(module, null);
	}

	public RFileLinksFilter(final Module module, @Nullable final String script_path)
	{
		//TODO think about support for JRuby without rails
		myScriptPath = script_path;
		if(module != null)
		{
			myProject = module.getProject();
			if(script_path == null)
			{
				final String railsAppHomeDir = RailsFacetUtil.getRailsAppHomeDirPath(module);
				if(railsAppHomeDir != null)
				{
					myScriptPath = railsAppHomeDir;
				}
				else if(RubyUtil.isRubyModuleType(module))
				{
					final VirtualFile moduleRoot = RModuleUtil.getRubyModuleTypeRoot(module);
					myScriptPath = moduleRoot != null ? moduleRoot.getPath() : null;
				}
			}
		}
		appSettings = RApplicationSettings.getInstance();
	}

	protected Pattern getSrcLinkCPattern(final boolean isWindows)
	{
		return isWindows ? WIN_CPATTERN : UNIX_CPATTERN;
	}

	@Override
	public Result applyFilter(final String line, final int entireLength)
	{
		//if filter is disabled
		if(!appSettings.useConsoleOutputOtherFilters)
		{
			return null;
		}

		final String cuttedLine = FileLinksFilterUtil.cutLineIfLong(line);
		final Matcher matcher = getSrcLinkCPattern(SystemInfo.isWindows).matcher(cuttedLine);
		int matcherStartIndex = 0;
		while(matcher.find(matcherStartIndex))
		{
			int startIndex = matcher.start();
			int endIndex = matcher.end() - 1;
			while(Character.isWhitespace(cuttedLine.charAt(startIndex)))
			{
				startIndex++;
			}
			while(Character.isWhitespace(cuttedLine.charAt(endIndex)))
			{
				endIndex--;
			}
			matcherStartIndex = endIndex;
			String filePath = cuttedLine.substring(startIndex, endIndex + 1);

			//if file path starts with . or ..
			if(filePath.startsWith(".") && !TextUtil.isEmpty(myScriptPath))
			{
				filePath = myScriptPath + "/" + filePath;
			}

			final File srcFile = FileLinksFilterUtil.getFileByRubyLink(filePath);
			if(srcFile != null)
			{
				if(FileLinksFilterUtil.hasExeExtention(srcFile))
				{
					return null;
				}

				final int textStartOffset = entireLength - line.length();
				final int highlightStartOffset = textStartOffset + startIndex;
				final int highlightEndOffset = textStartOffset + endIndex + 1;
				final OpenIOFileHyperlinkInfo info = new OpenIOFileHyperlinkInfo(myProject, srcFile, 0);
				return new Result(highlightStartOffset, highlightEndOffset, info);
			}
		}
		return null;
	}
}
