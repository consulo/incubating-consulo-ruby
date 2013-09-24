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

import com.intellij.execution.filters.Filter;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 18.01.2007
 */
public abstract class AbstractRubyFilterTest extends AbstractRubyModuleCacheTest {
    protected Filter myFilter;
    protected Pattern myPattern;
    protected String myDataRootPath;

    public static Filter.Result assertMatchesFilter(boolean matches, final Filter filter,
                                                    final String data) {
        final Filter.Result result = filter.applyFilter(data, data.length());
        assertEquals(matches,  result != null);

        return result;
    }

    protected Filter.Result assertMatchesFilter(@Nullable final String dataPrefix,
                                                final String dataSuffix,
                                                boolean matches) {
        return assertMatchesFilter(matches, myFilter, (dataPrefix == null ? "" : dataPrefix) + toTestDataPath(dataSuffix));
    }

    @SuppressWarnings({"ConstantConditions"})
    protected void setUp() throws Exception {
        super.setUp();

        myDataRootPath = PathUtil.getDataPath(this.getClass());
    }

    protected Module createMainModule() throws IOException {
        return createModule(myProject.getName());
    }
    
    protected void assertWholeMatches(final String string, final boolean expectedResult) {
        final Matcher matcher = myPattern.matcher(string);
        boolean realResult = matcher.find();

        if (realResult) {
            //Debug output
            //System.out.println("[" + matcher.start() + ", " +matcher.end() + "] : " + string.substring(matcher.start(), matcher.end()));
            assertEquals(expectedResult,
                         0 == matcher.start() && string.length() == matcher.end());
        } else {
            assertEquals(expectedResult, realResult);            
        }
    }

    protected void assertMatchesPattern(final String expectedString, final String string) {
        final Matcher matcher = myPattern.matcher(string);
        while (matcher.find()) {
            if (matcher.group().equals(expectedString)) {
                return;
            }
        }
        fail();
    }

    protected void assertFirstMatches(final String string,
                                 final boolean expectedResult,
                                 final int start, final int end) {

        final Matcher matcher = myPattern.matcher(string);
        boolean realResult = matcher.find();

        if (realResult) {
            //Debug output
            //System.out.println("[" + matcher.start() + ", " +matcher.end() + "] : " + string.substring(matcher.start(), matcher.end()));
            
            assertEquals(expectedResult,
                         start == matcher.start() && end == matcher.end());
        } else {
            assertEquals(expectedResult, realResult);
        }
        
    }

    protected void assertWholeMatchesPattern(final String string) {
        assertWholeMatches(string, true);
    }

    protected void assertSubstringMatches(final String string, final int start, final int end) {
        assertFirstMatches(string, true, start, end);
    }

    protected void assertNotMatchesPattern(final String string) {
        assertWholeMatches(string, false);
    }

    /**
     * Applies filter to given string:
     *     dataPrefix + toTestDataPath(dataSuffix)
     *
     * @param dataPrefix prefix string
     * @param dataSuffix suffix string
     * @return merged string
     */
    protected Filter.Result assertMatchesFilter(@Nullable final String dataPrefix, final String dataSuffix) {
        return assertMatchesFilter(dataPrefix, dataSuffix, true);
    }

    protected void assertMatchesFilter(final String dataPrefix, final String dataSuffix,
                                        final int lineNo) {
        final Filter.Result result = assertMatchesFilter(dataPrefix, dataSuffix);

        final OpenIOFileHyperlinkInfo info = (OpenIOFileHyperlinkInfo) result.hyperlinkInfo;
        assertEquals(lineNo, info.getLine() + 1);
    }

    protected void assertMatchesFilter(final String dataPrefix, final String dataSuffix,
                                        final String filePathSuffix) {

        final Filter.Result result = assertMatchesFilter(dataPrefix, dataSuffix);

        final OpenIOFileHyperlinkInfo info = (OpenIOFileHyperlinkInfo) result.hyperlinkInfo;
        assertTrue(info.getFile().getPath().endsWith(filePathSuffix));
    }

    protected void assertMatchesFilter(final String dataPrefix, final String dataSuffix,
                                        final String filePathSuffix,
                                        final int lineNo) {
        assertMatchesFilter(dataPrefix, dataSuffix, filePathSuffix);
        assertMatchesFilter(dataPrefix, dataSuffix, lineNo);
    }

    protected void assertNotMatchesFilter(@Nullable final String dataPrefix, final String dataSuffix) {
        assertMatchesFilter(dataPrefix, dataSuffix, false);
    }

    protected String toTestDataPath(final String relativePath) {
        return myDataRootPath + File.separatorChar + relativePath;
    }
}
