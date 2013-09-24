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

package org.jetbrains.plugins.ruby.support;

import junit.framework.TestCase;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 25.11.2006
 */
public class AssertionUtil {
    public static void assertException(final ExceptionCase exceptionCase) {
        assertException(exceptionCase, null);
    }
    public static void assertException(final ExceptionCase exceptionCase,
                                       final String expectedErrorMsg) {
        try {
            exceptionCase.tryToInovke();
        } catch (Exception e) {
            TestCase.assertEquals(exceptionCase.getMessage(),
                         exceptionCase.getExceptionClass(),
                         e.getClass());
            if (expectedErrorMsg != null) {
                TestCase.assertEquals("Compare error messages",
                                      expectedErrorMsg, e.getMessage());
            }
            return;
        }
        TestCase.fail(exceptionCase.getMessage());
    }
}
