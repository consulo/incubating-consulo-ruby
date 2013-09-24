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

import org.jetbrains.plugins.ruby.ruby.run.filters.AbstractRubyFilterTest;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 18.01.2007
 */
public class GeneratorsLinksFilterTest extends AbstractRubyFilterTest {

    protected void setUp() throws Exception {
        super.setUp();

        myFilter = new GeneratorsLinksFilter(null);
    }

    public void testGetSrcLinkPattern_General() {
        myPattern = ((GeneratorsLinksFilter) myFilter).getSrcLinkCPattern(false);
        assertWholeMatchesPattern("hello file");
        assertSubstringMatches(" exists ../script.rb ", 0, 21);
        assertWholeMatchesPattern(" exists ./script.rb");
        assertNotMatchesPattern(" exists ./script.rb:");
        assertWholeMatchesPattern(" exists script.rb");
    }

    public void testGetSrcLinkPattern_Unix() {
        myPattern = ((GeneratorsLinksFilter) myFilter).getSrcLinkCPattern(false);

        assertWholeMatchesPattern("/script.rb");
        assertWholeMatchesPattern("/script.rb");
        assertWholeMatchesPattern("exists");
        assertWholeMatchesPattern("\nexists /_dir/~dir2/2dir_3/script.rb");
        assertWholeMatchesPattern("\nexists\n/_dir/~dir2/2dir_3/script.rb");
        assertWholeMatchesPattern("\nexists\n\t/_dir/~dir2/2dir_3/script.rb");
        assertWholeMatchesPattern("/_dir/~dir2/2dir_3/script.js");
    }

    public void testGetSrcLinkPattern_Windows() {
        myPattern = ((GeneratorsLinksFilter) myFilter).getSrcLinkCPattern(true);

        assertWholeMatchesPattern("C:/script.rb");
        assertWholeMatchesPattern("D:/script.rb");
        assertWholeMatchesPattern("z:/script.rb");
        assertWholeMatchesPattern("C:/script.rb");
        assertWholeMatchesPattern("C:/script.rb");
        assertSubstringMatches("C:/script.rb? sss", 0, 13);
        assertWholeMatchesPattern(" exists C:/script.rb");
        assertSubstringMatches(" exists C:/script.rb sdd", 0, 21);
        assertWholeMatchesPattern("exists C:/script.rb");
        assertWholeMatchesPattern("C:/dir/script.rb");
        assertWholeMatchesPattern("C:/dir/dir2/script.rb");
        assertWholeMatchesPattern("C:/_dir/~dir2/2dir_3/script.rb");

        assertWholeMatchesPattern("C://script.rb");
        assertWholeMatchesPattern("C:/r//script.rb");

        assertNotMatchesPattern("c1:/dir/dir2/script.rb");
        assertNotMatchesPattern("1:/dir/dir2/script.rb");
        assertNotMatchesPattern(":/script.rb");
        assertNotMatchesPattern("c1:/script.rb");
    }
}