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

package org.jetbrains.plugins.ruby.rails.nameConventions;

import com.intellij.testFramework.IdeaTestCase;
import org.jetbrains.plugins.ruby.ruby.lang.TextUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 23, 2007
 */
public class HelpersConventionsTest extends IdeaTestCase {

    public void testGetHelperFileName() {
        assertEquals(TextUtil.EMPTY_STRING, HelpersConventions.getHelperFileName(null));
        assertEquals("application_helper.rb",
                HelpersConventions.getHelperFileName("ApplicationController"));
        assertEquals("admin_helper.rb",
                HelpersConventions.getHelperFileName("AdminController"));

        assertEquals(TextUtil.EMPTY_STRING,
                HelpersConventions.getHelperFileName("AdminController1"));

        assertEquals("admin1_helper.rb",
                HelpersConventions.getHelperFileName("Admin1Controller"));
    }

    public void testGetHelperModuleName() {
        assertEquals(TextUtil.EMPTY_STRING, HelpersConventions.getHelperModuleName(null));
        assertEquals("ApplicationHelper",
                HelpersConventions.getHelperModuleName("ApplicationController"));
        assertEquals("AdminHelper",
                HelpersConventions.getHelperModuleName("AdminController"));
        assertEquals("Admin1Helper",
                HelpersConventions.getHelperModuleName("Admin1Controller"));
        assertEquals(TextUtil.EMPTY_STRING, HelpersConventions.getHelperModuleName("AdminController1"));
    }
}
