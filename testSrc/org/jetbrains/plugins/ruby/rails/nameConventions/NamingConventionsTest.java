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
public class NamingConventionsTest extends IdeaTestCase {

    public void testToToMixedCase() {
        assertEquals(TextUtil.EMPTY_STRING, NamingConventions.toMixedCase(null));
        assertEquals("AddUser", NamingConventions.toMixedCase("add_user"));
        assertEquals("AddUser", NamingConventions.toMixedCase("Add_User"));
        assertEquals("AddUSer_mike", NamingConventions.toMixedCase("add_uSer__mike"));
        assertEquals("_add", NamingConventions.toMixedCase("_add"));
        assertEquals("_add", NamingConventions.toMixedCase("__add"));
        assertEquals("Add_", NamingConventions.toMixedCase("add_"));
        assertEquals("Add_", NamingConventions.toMixedCase("add__"));
    }

    public void testToUnderscoreCase() {
        assertEquals(TextUtil.EMPTY_STRING, NamingConventions.toUnderscoreCase(null));
        assertEquals("add_user", NamingConventions.toUnderscoreCase("AddUser"));
        assertEquals("add_user_mike", NamingConventions.toUnderscoreCase("AddUserMike"));
        assertEquals("add_user_mike", NamingConventions.toUnderscoreCase("AddUser_Mike"));
        assertEquals("my_kind_admin/add_user_mike",
                NamingConventions.toUnderscoreCase("My_KindAdmin/AddUser_Mike"));
        assertEquals("my_kind_admin/add_user_mike",
                NamingConventions.toUnderscoreCase("my_kind_admin/AddUser_Mike"));
    }

    public void testToUnderscoreCase_RUBY_1516() {
        assertEquals("rrar2_r_bbb_cac_yx_cc",
                     NamingConventions.toUnderscoreCase("RRAR2RBbbCACYxCC"));
        assertEquals("a_",
                     NamingConventions.toUnderscoreCase("a-"));
    }

    public void testToMixedCase_RUBY_1516() {
        assertEquals("Rrar2RBbbCacYxCc",
                     NamingConventions.toMixedCase("rrar2_r_bbb_cac_yx_cc"));
        assertEquals("2A",
                     NamingConventions.toMixedCase("2_a"));
    }

    public void testIsInMixedCase() {
        assertFalse(NamingConventions.isInMixedCase(null));
        assertFalse(NamingConventions.isInMixedCase(""));
        assertFalse(NamingConventions.isInMixedCase("admin"));
        assertFalse(NamingConventions.isInMixedCase("adminController"));
        assertFalse(NamingConventions.isInMixedCase("admin_Controller"));
        assertFalse(NamingConventions.isInMixedCase("Admin_Controller"));
        assertFalse(NamingConventions.isInMixedCase("_Controller"));
        assertTrue(NamingConventions.isInMixedCase("Controller"));
        assertTrue(NamingConventions.isInMixedCase("AdminController"));
    }

    public void testIsInUnderscoredCase() {
        assertFalse(NamingConventions.isInUnderscoredCase(null));
        assertFalse(NamingConventions.isInUnderscoredCase(""));
        assertFalse(NamingConventions.isInUnderscoredCase("Login"));
        assertFalse(NamingConventions.isInUnderscoredCase("Secure_Login"));
        assertFalse(NamingConventions.isInUnderscoredCase("SecureLogin"));
        assertFalse(NamingConventions.isInUnderscoredCase("Secure_login"));
        assertFalse(NamingConventions.isInUnderscoredCase("secure_Login"));
        assertFalse(NamingConventions.isInUnderscoredCase("secureLogin"));
        assertTrue(NamingConventions.isInUnderscoredCase("_login"));
        assertTrue(NamingConventions.isInUnderscoredCase("secure_login"));
    }
}
