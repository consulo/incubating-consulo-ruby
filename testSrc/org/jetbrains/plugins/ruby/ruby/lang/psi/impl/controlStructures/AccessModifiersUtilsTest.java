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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures;

import junit.framework.TestCase;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.support.AssertionUtil;
import org.jetbrains.plugins.ruby.support.IllegalArgumentExceptionCase;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 10.03.2007
 */
public class AccessModifiersUtilsTest extends TestCase {
    
    public void testGetModifierByName_Null() {
        AssertionUtil.assertException(new IllegalArgumentExceptionCase() {
            public void tryToInovke() {
                //noinspection ConstantConditions
                AccessModifiersUtil.getModifierByName(null);
            }
        });
    }

    public void testGetModifierByName_Public() {
        assertEquals(AccessModifier.PUBLIC,
                     AccessModifiersUtil.getModifierByName("public"));
        assertEquals(AccessModifier.UNKNOWN,
                     AccessModifiersUtil.getModifierByName("Public"));
    }

    public void testGetModifierByName_Protected() {
        assertEquals(AccessModifier.PROTECTED,
                     AccessModifiersUtil.getModifierByName("protected"));
        assertEquals(AccessModifier.UNKNOWN,
                     AccessModifiersUtil.getModifierByName("Protected"));
    }

    public void testGetModifierByName_Private() {
        assertEquals(AccessModifier.PRIVATE,
                     AccessModifiersUtil.getModifierByName("private"));
        assertEquals(AccessModifier.UNKNOWN,
                     AccessModifiersUtil.getModifierByName("Private"));
    }

    public void testExistModifierByString() {
        assertFalse(AccessModifiersUtil.existModifierByString(null));
        assertFalse(AccessModifiersUtil.existModifierByString(""));
        assertFalse(AccessModifiersUtil.existModifierByString("q"));
        assertFalse(AccessModifiersUtil.existModifierByString("pa"));

        assertTrue(AccessModifiersUtil.existModifierByString("p"));
        assertTrue(AccessModifiersUtil.existModifierByString("pro"));
        assertTrue(AccessModifiersUtil.existModifierByString("pu"));
        assertTrue(AccessModifiersUtil.existModifierByString("pr"));
        assertTrue(AccessModifiersUtil.existModifierByString("public"));
        assertTrue(AccessModifiersUtil.existModifierByString("protected"));
        assertTrue(AccessModifiersUtil.existModifierByString("private"));
        assertTrue(AccessModifiersUtil.existModifierByString("public_anytext"));
        assertTrue(AccessModifiersUtil.existModifierByString("protected_anytext"));
        assertTrue(AccessModifiersUtil.existModifierByString("private_anytext"));
    }
}
