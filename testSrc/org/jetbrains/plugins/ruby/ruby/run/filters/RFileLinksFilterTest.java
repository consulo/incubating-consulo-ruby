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
public class RFileLinksFilterTest extends AbstractRubyFilterTest {

    protected void setUp() throws Exception {
        super.setUp();

        myFilter = new RFileLinksFilter(null);
    }

    public void testGetSrcLinkPattern_General() {
        myPattern = ((RFileLinksFilter) myFilter).getSrcLinkCPattern(false);

        assertNotMatchesPattern("hello word");
    }

    public void testGetSrcLinkPattern_Unix() {
        myPattern = ((RFileLinksFilter) myFilter).getSrcLinkCPattern(false);

        assertWholeMatchesPattern("/script.rb");
        assertWholeMatchesPattern("/script");
        assertWholeMatchesPattern("/script.rb");
        assertWholeMatchesPattern("\\script.rb");
        assertSubstringMatches("\nexists /_dir/~dir2/2dir_3/script.rb", 7, 36);
        assertSubstringMatches("\nexists\n/_dir/~dir2/2dir_3/script.rb", 7, 36);
        assertSubstringMatches("\nexists\n\t/_dir/~dir2\\2dir_3/script.rb", 8, 37);
        assertWholeMatchesPattern("/_dir/~dir2/2dir_3/script.js");

        assertWholeMatchesPattern("/script//qq.rb");

        assertNotMatchesPattern("/script.rb:");
        assertNotMatchesPattern("exists");
        assertNotMatchesPattern("app/models/script.rb");
    }

    public void testGetSrcLinkPattern_Windows() {
        myPattern = ((RFileLinksFilter) myFilter).getSrcLinkCPattern(true);

        assertWholeMatchesPattern("C:/script.rb");
        assertWholeMatchesPattern("D:/script.rb");
        assertWholeMatchesPattern("z:/script.rb");
        assertWholeMatchesPattern("C:/script.rb");
        assertWholeMatchesPattern("C:/script.rb");
        assertSubstringMatches("C:/script.rb? sss", 0, 13);
        assertSubstringMatches("C:/script.rb s\n", 0, 13);
        assertSubstringMatches(" exists C:/script.rb ", 7, 21);
        assertSubstringMatches(" exists C:/script.rb s", 7, 21);
        assertSubstringMatches(" exists C:/script.rb sdd", 7, 21);
        assertWholeMatchesPattern("C:/dir/script.rb");
        assertWholeMatchesPattern("C:\\dir\\script.rb");
        assertWholeMatchesPattern("C:/dir/dir2/script.rb");
        assertWholeMatchesPattern("C:/_dir/~dir2/2dir_3/script.rb");

        assertWholeMatchesPattern("C:/script.rb?");
        assertWholeMatchesPattern("C://script.rb");
        assertWholeMatchesPattern("C:/r//script.rb");
        
        assertNotMatchesPattern("c1:/dir/dir2/script.rb");
        assertNotMatchesPattern("1:/dir/dir2/script.rb");
        assertNotMatchesPattern(":/script.rb");
        assertNotMatchesPattern("c1:/script.rb");
    }
}
