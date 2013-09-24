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

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 18.01.2007
 */
public class RStackTraceFilterTest extends AbstractRubyFilterTest {

    protected void setUp() throws Exception {
        super.setUp();
        myFilter = new RStackTraceFilter(null);
    }

    public void testGetSrcLinkPattern_General() {
        myPattern = ((RStackTraceFilter) myFilter).getSrcLinkCPattern(false);

        assertWholeMatchesPattern("../ruby/w.rb:11");
        assertWholeMatchesPattern("./ruby/w.rb:11");
        assertWholeMatchesPattern("\tfrom ../ruby2/w.rb:7");
        assertWholeMatchesPattern("\tfrom ./ruby/w.rb:7");

        assertWholeMatchesPattern("\tfrom ../ruby/w.rb:7");
        assertWholeMatchesPattern("  from ../ruby/w.rb:7");
        assertWholeMatchesPattern("  from ./ruby/w.rb:7");
        assertWholeMatchesPattern("  from ./ruby/w.rb:7");
        assertWholeMatchesPattern("  from ./ruby//w.rb:7");
    }

    public void testGetSrcLinkPattern_Unix() {
        myPattern = ((RStackTraceFilter) myFilter).getSrcLinkCPattern(false);

        assertWholeMatchesPattern("/ruby.rb:11");
        assertWholeMatchesPattern("/ruby/w.rb:11");
        assertWholeMatchesPattern("/ruby\\dir1\\w.rb:11");
        assertWholeMatchesPattern("/ruby/~dir1/w.rb:11");
        assertWholeMatchesPattern("~/ruby/w.rb:11");
        assertWholeMatchesPattern("~/ruby/w.rb:11");

        assertWholeMatchesPattern("  from ~/ruby/w.rb:7");
        assertWholeMatchesPattern("  from /ruby/w.rb:7");
        assertWholeMatchesPattern("  from /ruby/w.rb:7");

        assertWholeMatchesPattern("  from /ruby /w.rb:7");
        
        assertNotMatchesPattern("C:/dir/ dir2/script.rb:1");
        assertNotMatchesPattern("a/dir1/script.rb:1");
    }

    public void testGetSrcLinkPattern_Windows() {
        myPattern = ((RStackTraceFilter) myFilter).getSrcLinkCPattern(true);

        assertWholeMatchesPattern("c:\\ruby.rb:11");
        assertWholeMatchesPattern("c:/ruby/w.rb:11");
        assertWholeMatchesPattern("C:/ruby\\dir1\\w.rb:11");
        assertWholeMatchesPattern("C:/ruby/~dir1/w.rb:11");

        assertWholeMatchesPattern("  from C:\\ruby\\w.rb:7");
        assertWholeMatchesPattern("  from C:/ruby/w.rb:7");

        assertWholeMatchesPattern("  from c:/ruby /w.rb:7");
        assertWholeMatchesPattern("c:/dir/ dir2/script.rb:1");

        assertNotMatchesPattern("~/ruby/w.rb:11");
        assertNotMatchesPattern("a/dir1/script.rb:1");
        assertNotMatchesPattern("/dir1/script.rb:1");
    }

    public void testApplyFilter_General() {
        assertMatchesFilter(" from ", "file1.rb:1");
        assertMatchesFilter(null, "file1.rb:1");

        assertNotMatchesFilter(null, "file:1.rb:2");
    }

    public void testApplyFilter_Lines() {
         assertMatchesFilter(null, "file1.rb:1", 1);
         assertMatchesFilter(null, "file1.rb:2", 2);
    }

    public void testApplyFilter_Pathes() {
         assertMatchesFilter(null, "file1.rb:1", "file1.rb");
         assertMatchesFilter(null, "file1.rb:20", "file1.rb");
    }
}