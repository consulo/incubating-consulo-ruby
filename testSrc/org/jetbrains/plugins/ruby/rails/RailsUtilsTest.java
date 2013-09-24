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

package org.jetbrains.plugins.ruby.rails;

import com.intellij.testFramework.IdeaTestCase;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 14.10.2006
 */
public class RailsUtilsTest extends IdeaTestCase {

    public void testToModulesPath() {
        assertEquals("",
                org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath(null));
        assertEquals("", org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath(""));
        assertEquals("Server1",
                org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath("server1"));
        assertEquals("Server1",
                org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath("server1/"));
        assertEquals("Server1::Admin::LoginModule",
                org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath("server1/admin/login_module"));
        assertEquals("Server1::Admin::LoginModule",
                org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath("Server1/Admin/LoginModule"));
        assertEquals("Server1::Admin::LoginModule",
                org.jetbrains.plugins.ruby.rails.RailsUtil.toModulesPath("server1/Admin/LoginModule"));

    }
}
