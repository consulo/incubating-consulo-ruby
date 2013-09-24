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

package org.jetbrains.plugins.ruby.ruby.run;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jan 19, 2008
 */
public class RubyScriptRunnerArgumentsProviderTest extends TestCase {

    public void testCollectArguments_withList() {
        final List<String> params = new ArrayList<String>();
        params.add("we_are_the_champions");

        final List<String> result = RubyScriptRunnerArgumentsProvider.collectArguments("test1 test2", params);
        assertSame(params, result);
        assertTrue(result.contains("we_are_the_champions"));
        assertTrue(result.contains("test2"));
    }

    public void testCollectArguments_simple() {
        final String s = "test1 test2 --test4 \"test5\" 'test6' test7=value";
        final List<String> result = RubyScriptRunnerArgumentsProvider.collectArguments(s);
        assertTrue(result.contains("test1"));
        assertTrue(result.contains("test2"));
        assertTrue(result.contains("--test4"));
        assertTrue(result.contains("\"test5\""));
        assertTrue(result.contains("'test6'"));
        assertTrue(result.contains("test7=value"));
        assertEquals(6, result.size());
    }

    public void testCollectArguments_limitations() {
        final String s = "test1=\"p1, p2\" 'part1 part2' \"path with spaces\"";
        final List<String> result = RubyScriptRunnerArgumentsProvider.collectArguments(s);
        assertTrue(result.contains("test1=\"p1,"));
        assertTrue(result.contains("p2\""));
        assertTrue(result.contains("'part1"));
        assertTrue(result.contains("part2'"));
        assertTrue(result.contains("\"path"));
        assertTrue(result.contains("with"));
        assertTrue(result.contains("spaces\""));
        assertEquals(7, result.size());
    }
}
