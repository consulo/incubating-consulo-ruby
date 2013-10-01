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
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;
import org.jetbrains.plugins.ruby.support.utils.VirtualFileUtil;
import com.intellij.execution.filters.Filter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 17.07.2007
 */

/**
 * This filter is used to create links from stacktrace output, e.g.
 * <p/>
 * ../ruby2/w.rb:11:in `rrr': unhandled exception
 * from ../ruby2/w.rb:7:in `rr'
 * from C:/home/examples/ex4/ruby2/qq.rb:19:in `p4'
 * from C:/home/examples/ex4/ruby2/qq.rb:16:in `p3'
 * from C:/home/examples/ex4/ruby2/qq.rb:13:in `p2'
 * from C:/home/examples/ex4/ruby2/qq.rb:10:in `p1'
 * from C:/home/examples/ex4/ruby2/qq.rb:36
 * from -e:1:in `load'
 * from -e:1
 */
public class RStackTraceFilter implements Filter
{
	// Disk name for Windows
	@NonNls
	public static final String DISK_PATTERN = "\\p{Alpha}:";
	// Current of prev. folder \.\. or \.
	@NonNls
	public static final String SPECIAL_FOLDER_PATTERN = "\\.(\\.?)";
	// Unix home direcotry
	@NonNls
	public static final String HOME_FOLDER_PATTERN = "~";
	// Extension pattern
	@NonNls
	public static final String EXT_PATTERN = "\\.[\\p{Graph}^:/\\\\\\?]+";
	// File name may contain whitespaces
	@NonNls
	public static final String FILENAME_PATTERN = "[^ :/\\\\]+";

	// Whitespaces or beginning of line or whitespaces ans "form "
	@NonNls
	protected static final String FROM_PATTERN = "from";
	@NonNls
	protected static final String PREFIX_PATTERN = "(\\s*(" + FROM_PATTERN + "\\s)?|^)";

	// Path use both type separators(for Unix and Windows )
	@NonNls
	private static final String FILENAME_WITH_POSTFIX_PATTERN = "(/|\\\\)[^:]*" + FILENAME_PATTERN + "(" + EXT_PATTERN + ")?:\\d+";

	@NonNls
	private static final String WIN_PATTERN = PREFIX_PATTERN + "(" + DISK_PATTERN + "|" + SPECIAL_FOLDER_PATTERN + ")" + FILENAME_WITH_POSTFIX_PATTERN;
	@NonNls
	private static final String UNIX_PATTERN = PREFIX_PATTERN + "(" + SPECIAL_FOLDER_PATTERN + "|" + HOME_FOLDER_PATTERN + ")?" + FILENAME_WITH_POSTFIX_PATTERN;

	@NonNls
	private static final Pattern WIN_CPATTERN = Pattern.compile(WIN_PATTERN);
	@NonNls
	private static final Pattern UNIX_CPATTERN = Pattern.compile(UNIX_PATTERN);

	private final Project myProject;
	private final String myScriptPath;
	private final boolean canUseScriptPath;
	private RApplicationSettings appSettings;


	public RStackTraceFilter(final Project project)
	{
		this(project, null);
	}

	public RStackTraceFilter(final Project project, @Nullable final String script_path)
	{
		myProject = project;
		canUseScriptPath = !TextUtil.isEmpty(script_path);
		appSettings = RApplicationSettings.getInstance();
		myScriptPath = canUseScriptPath ? script_path + VirtualFileUtil.VFS_PATH_SEPARATOR : null;
	}

	protected Pattern getSrcLinkCPattern(final boolean isWindows)
	{
		return isWindows ? WIN_CPATTERN : UNIX_CPATTERN;
	}

	@Override
	public Result applyFilter(final String line, final int entireLength)
	{
		//if filter is disabled
		if(!appSettings.useConsoleOutputRubyStacktraceFilter)
		{
			return null;
		}
		final String cuttedLine = FileLinksFilterUtil.cutLineIfLong(line);
		final Matcher matcher = getSrcLinkCPattern(SystemInfo.isWindows).matcher(cuttedLine);
		if(!matcher.find())
		{
			return null;
		}

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
		// Starts from "from/s"
		if(cuttedLine.substring(startIndex).startsWith(FROM_PATTERN))
		{
			startIndex += FROM_PATTERN.length() + 1;
		}
		final String link = FileLinksFilterUtil.cutLineIfLong(cuttedLine.substring(startIndex, endIndex + 1));

		final int index = link.lastIndexOf(':');
		String filePath = link.substring(0, index);

		//if file path starts with . or ..
		if(canUseScriptPath && filePath.startsWith("."))
		{
			filePath = myScriptPath + filePath;
		}

		final File srcFile = FileLinksFilterUtil.getFileByRubyLink(filePath);
		if(srcFile == null)
		{
			return null;
		}

		if(FileLinksFilterUtil.hasExeExtention(srcFile))
		{
			return null;
		}

		final int lineNum;
		try
		{
			lineNum = Integer.parseInt(link.substring(index + 1));
		}
		catch(NumberFormatException e)
		{
			//shouldnt happen
			return null;
		}
		final int textStartOffset = entireLength - line.length();
		final int highlightStartOffset = textStartOffset + startIndex;
		final int highlightEndOffset = textStartOffset + endIndex + 1;
		final OpenIOFileHyperlinkInfo info = new OpenIOFileHyperlinkInfo(myProject, srcFile, lineNum - 1);
		return new Result(highlightStartOffset, highlightEndOffset, info);
	}
}