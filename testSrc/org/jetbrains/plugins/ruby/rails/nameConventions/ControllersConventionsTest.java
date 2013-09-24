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

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 23, 2007
 */
public class ControllersConventionsTest extends IdeaTestCase {
    public void testGetControllerUrlByViewUrl() {
        String fileUrl =
                "file://C:/home/idea_proj2/commit_test/rails1/app/views/admin/admin/eee.rhtml";
        String viewsRoot =
                "file://C:/home/idea_proj2/commit_test/rails1/app/views";
        String controllersRoot =
                "file://C:/home/idea_proj2/commit_test/rails1/app/controllers";
        assertEquals("file://C:/home/idea_proj2/commit_test/rails1/app/controllers/"
                + "admin/admin_controller.rb",
                ControllersConventions.getControllerUrlByViewUrl(fileUrl, viewsRoot,
                        controllersRoot));
    }

    public void testIsValidActionName() {
        assertFalse(ControllersConventions.isValidActionName(null));

        assertFalse(ControllersConventions.isValidActionName(""));
        assertFalse(ControllersConventions.isValidActionName("_a"));
        assertFalse(ControllersConventions.isValidActionName("1a"));
        assertFalse(ControllersConventions.isValidActionName("B"));
        assertFalse(ControllersConventions.isValidActionName("aB"));
        assertFalse(ControllersConventions.isValidActionName("a_B"));
        assertFalse(ControllersConventions.isValidActionName("aBb"));
        assertFalse(ControllersConventions.isValidActionName("action()"));

        assertTrue(ControllersConventions.isValidActionName("a"));
        assertTrue(ControllersConventions.isValidActionName("a_b"));
        assertTrue(ControllersConventions.isValidActionName("a_b_c"));
        assertTrue(ControllersConventions.isValidActionName("action1_a2"));
    }

    public void testGetControllerNameByHelperFileName() {
        assertNull(ControllersConventions.getControllerNameByHelperFileName(null));
        assertNull(ControllersConventions.getControllerNameByHelperFileName("helper"));
        assertNull(ControllersConventions.getControllerNameByHelperFileName("my_melper"));
        assertEquals("",
                ControllersConventions.getControllerNameByHelperFileName("_helper"));
        assertEquals("application",
                ControllersConventions.getControllerNameByHelperFileName("application_helper"));
        assertEquals("my_control",
                ControllersConventions.getControllerNameByHelperFileName("my_control_helper"));
    }

    public void testGetControllerName() {
        assertEquals("application", ControllersConventions.getControllerName("application.rb"));
        assertEquals("admin", ControllersConventions.getControllerName("admin_controller.rb"));
    }

    public void testGetControllerNameByClassName() {
        assertNull(ControllersConventions.getControllerClassNameByFileName(null));
        assertEquals("application",
                ControllersConventions.getControllerNameByClassName("ApplicationController"));
        assertEquals("admin",
                ControllersConventions.getControllerNameByClassName("AdminController"));
        assertNull(ControllersConventions.getControllerNameByClassName("AdmiController1"));
        assertNull(ControllersConventions.getControllerNameByClassName("Admi"));
    }

    public void testGetControllerClassNameByFileName() {
        assertNull(ControllersConventions.getControllerClassNameByFileName(null));
        assertEquals("ApplicationController",
                ControllersConventions.getControllerClassNameByFileName("application.rb"));
        assertEquals("AdminController",
                ControllersConventions.getControllerClassNameByFileName("Admin_controller.rb"));
        assertNull(ControllersConventions.getControllerClassNameByFileName("Admin_controll.rb"));
    }

    public void testGetControllerClassName() {
        assertNull(ControllersConventions.getControllerClassName(null));
        assertEquals("AdminController",
                ControllersConventions.getControllerClassName("admin"));
        assertEquals("ApplicationController",
                ControllersConventions.getControllerClassName("application"));
    }

    public void testGetControllerFileName() {
        assertNull(ControllersConventions.getControllerFileName(null));
        assertEquals("application.rb",
                ControllersConventions.getControllerFileName("ApplicationController"));
        assertEquals("admin_controller.rb",
                ControllersConventions.getControllerFileName("AdminController"));

        assertEquals("admin_controller1.rb",
                ControllersConventions.getControllerFileName("AdminController1"));

        assertEquals("admin1_controller.rb",
                ControllersConventions.getControllerFileName("Admin1Controller"));
    }
}
