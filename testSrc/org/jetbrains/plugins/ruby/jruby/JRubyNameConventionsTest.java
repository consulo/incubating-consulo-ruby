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

package org.jetbrains.plugins.ruby.jruby;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.jetbrains.plugins.ruby.jruby.codeInsight.types.JRubyNameConventions;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Dec 24, 2007
 */
public class JRubyNameConventionsTest extends TestCase {

    public void testGetter() {
        Assert.assertEquals("foo", JRubyNameConventions.getMethodName("getFoo"));
    }

    public void testGetter2() {
        Assert.assertEquals("foo_bar_baz", JRubyNameConventions.getMethodName("getFooBarBaz"));
    }

    public void testSetter() {
        Assert.assertEquals("foo=", JRubyNameConventions.getMethodName("setFoo"));
    }

    public void testSetter2() {
        Assert.assertEquals("foo_bar_baz=", JRubyNameConventions.getMethodName("setFooBarBaz"));
    }

    public void testSetter3() {
        Assert.assertEquals("settings=", JRubyNameConventions.getMethodName("setSettings"));
    }

    public void testBoolean() {
        Assert.assertEquals("foo?", JRubyNameConventions.getMethodName("isFoo"));
    }

    public void testBoolean2() {
        Assert.assertEquals("foo_bar_baz?", JRubyNameConventions.getMethodName("isFooBarBaz"));
    }

    public void testBoolean3() {
        Assert.assertEquals("visible?", JRubyNameConventions.getMethodName("isVisible"));
    }

    public void testName() {
        Assert.assertEquals("bla_bla_bla", JRubyNameConventions.getMethodName("blaBlaBla"));
    }

}
