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

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import junit.framework.Assert;
import junit.framework.Test;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.testCases.PerfomanceFileSetTestCase;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jul 1, 2008
 */
public class RStackTraceFilterPerfomanceTest extends PerfomanceFileSetTestCase {
    public static final int TIMEOUT = 200;

    private Ref<String> fileContentWrapper = new Ref<String>();
    private RStackTraceFilter myFilter;

    public RStackTraceFilterPerfomanceTest(String path) {
        super(path);
    }

    public int getTimeout() {
        return TIMEOUT;
    }

    public Runnable getTest() {
        return new Runnable(){
            public void run(){
                try {
                    AbstractRubyFilterTest.assertMatchesFilter(false, myFilter, fileContentWrapper.get());
                } catch (Exception e) {
                    Assert.fail("File not found");
                }
            }
        };
    }

    private void prepareTestData(final File file) throws IOException {
        try {
           fileContentWrapper.set(new String(FileUtil.loadFileText(file)));
        } catch (Exception e) {
            Assert.fail("File not found");
        }
    }

    protected void runTest(final File file) throws Throwable {
        prepareTestData(file);
        super.runTest(file);
    }

    protected void setUp() {
        super.setUp();

        myFilter = new RStackTraceFilter(null);
    }

    public static Test suite() {
        return new RStackTraceFilterPerfomanceTest(PathUtil.getDataPath(RStackTraceFilterPerfomanceTest.class));
    }
}

