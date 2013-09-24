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

package org.jetbrains.plugins.ruby.ruby.testCases;

import junit.framework.Assert;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 11, 2006
 */
public abstract class PerfomanceFileSetTestCase extends RubyFileSetTestCase{
    protected File myFile;
    public abstract int getTimeout();

    public abstract Runnable getTest();

    public PerfomanceFileSetTestCase(String path) {
        super(path);
    }


    protected void runTest(final File file) throws Throwable {
        myFile = file;
        Thread testThread = new Thread(getTest());
        testThread.start();
        testThread.join(getTimeout());
        if (testThread.isAlive()){
            testThread.interrupt();
            Assert.fail("Timeout error");
        }
    }

}
