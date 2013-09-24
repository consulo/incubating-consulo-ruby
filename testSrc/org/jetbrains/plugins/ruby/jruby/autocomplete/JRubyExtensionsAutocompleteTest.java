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

package org.jetbrains.plugins.ruby.jruby.autocomplete;

import org.jetbrains.plugins.ruby.PathUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 24, 2008
 */
public class JRubyExtensionsAutocompleteTest extends AbstractJRubyAutocompleteTest {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(JRubyExtensionsAutocompleteTest.class);
    }

    public void testNameConv() throws Exception {
        doTest("name_conv_test.rb");
    }

    public void testStaticNameConv() throws Exception {
        doTest("static_name_conv_test.rb");
    }

    public void testCollection() throws Exception {
        doTest("collection_test.rb");
    }

    public void testComparable() throws Exception {
        doTest("comparable_test.rb");
    }

    public void testEnumeration() throws Exception {
        doTest("enumeration_test.rb");
    }

    public void testIterator() throws Exception {
        doTest("iterator_test.rb");
    }

    public void testList() throws Exception {
        doTest("list_test.rb");
    }

    public void testMap() throws Exception {
        doTest("map_test.rb");
    }

    public void testRunnable() throws Exception {
        doTest("runnable_test.rb");
    }
}
