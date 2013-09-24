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

package org.jetbrains.plugins.ruby.rails.run.filters;

import com.intellij.execution.filters.Filter;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.rails.facet.RailsFacetUtil;
import org.jetbrains.plugins.ruby.ruby.run.filters.FileLinksFilterUtil;
import org.jetbrains.plugins.ruby.ruby.run.filters.OpenIOFileHyperlinkInfo;
import org.jetbrains.plugins.ruby.ruby.run.filters.RStackTraceFilter;
import org.jetbrains.plugins.ruby.settings.RApplicationSettings;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik, oleg
 * @date: 11.01.2007
 */

/**
 * This filter is used to create links from the console for Rails generators output
 *  exists  test/functional/
 *  overwrite app/controllers/accounts_controller.rb? [Ynaqd] n
 *      skip  app/controllers/accounts_controller.rb
 *  overwrite test/functional/accounts_controller_test.rb? [Ynaqd] y
 *      force  test/functional/accounts_controller_test.rb
 *  identical  app/helpers/accounts_helper.rb
 */
public class GeneratorsLinksFilter implements Filter {
    @NonNls protected static final String PREFIX_PATTERN = "(\\s+|^)(" + RStackTraceFilter.FILENAME_PATTERN + "\\s*)?";

    // Path use both type separators(for Unix and Windows )
    @NonNls private static final String PATTERN = "(/|\\\\)?[^\\s\\?:]+("+ RStackTraceFilter.EXT_PATTERN+")?[^:]";

    @NonNls private static final String WIN_PATTERN = PREFIX_PATTERN +  "(" + RStackTraceFilter.DISK_PATTERN + "|" + RStackTraceFilter.SPECIAL_FOLDER_PATTERN + ")?" + PATTERN;
    @NonNls private static final String UNIX_PATTERN = PREFIX_PATTERN +  "(" + RStackTraceFilter.HOME_FOLDER_PATTERN + "|"+ RStackTraceFilter.SPECIAL_FOLDER_PATTERN +")?" + PATTERN;

    private static final Pattern WIN_CPATTERN = Pattern.compile(WIN_PATTERN);
    private static final Pattern UNIX_CPATTERN = Pattern.compile(UNIX_PATTERN);

    public Project myProject;
    @Nullable
    public String myRootDir;
    public Module myModule;

    private RApplicationSettings appSettings;
    
    public GeneratorsLinksFilter(final Module module) {
        myModule = module;
        if (module != null) {
            myRootDir  = RailsFacetUtil.getRailsAppHomeDirPath(module);
            myProject = myModule.getProject();
        }
        appSettings = RApplicationSettings.getInstance();
    }

    protected Pattern getSrcLinkCPattern(final boolean isWindows) {
        return isWindows ? WIN_CPATTERN : UNIX_CPATTERN;
    }

    public Result applyFilter(final String line, final int entireLength) {
        //if filter is disabled
        if (!appSettings.useConsoleOutputOtherFilters) {
            return null;
        }

        final String cuttedLine =  FileLinksFilterUtil.cutLineIfLong(line);
        final Matcher matcher = getSrcLinkCPattern(SystemInfo.isWindows).matcher(cuttedLine);
        int matcherStartIndex = 0;
        while (matcher.find(matcherStartIndex)) {
            int startIndex = matcher.start();
            int endIndex = matcher.end() - 1;
            while (Character.isWhitespace(cuttedLine.charAt(startIndex))) {
                startIndex++;
            }
            while (Character.isWhitespace(cuttedLine.charAt(endIndex))) {
                endIndex--;
            }
            matcherStartIndex = endIndex;

            String fileLink = cuttedLine.substring(startIndex, endIndex + 1);
            final int lastSpace = fileLink.lastIndexOf(' ');
            if (lastSpace > -1) {
                startIndex += lastSpace + 1;
                fileLink = fileLink.substring(lastSpace + 1);
            }

            File srcFile = null;
            if (myRootDir != null) {
                srcFile = FileLinksFilterUtil.getFileByRubyLink(myRootDir + File.separator + fileLink);
            }
            if (srcFile == null) {
                srcFile = FileLinksFilterUtil.getFileByRubyLink(fileLink);
            }

            if (srcFile != null) {
                if (FileLinksFilterUtil.hasExeExtention(srcFile)) {
                    return null;
                }

                final int textStartOffset = entireLength - cuttedLine.length();
                final int highlightStartOffset = textStartOffset + startIndex;
                final int highlightEndOffset = textStartOffset + endIndex + 1;
                final OpenIOFileHyperlinkInfo info =
                        new OpenIOFileHyperlinkInfo(myProject, srcFile, 0);
                return new Result(highlightStartOffset, highlightEndOffset, info);
            }
        }
        return null;
    }
}