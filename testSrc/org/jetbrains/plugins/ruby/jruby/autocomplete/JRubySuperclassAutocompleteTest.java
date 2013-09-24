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

import com.intellij.openapi.projectRoots.ProjectJdk;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkUtil;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 24, 2008
 */
public class JRubySuperclassAutocompleteTest extends AbstractJRubyAutocompleteTest {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(JRubySuperclassAutocompleteTest.class);
    }

    public void testRNamedReference() throws Exception {
        doTest("superclass/rnamedref.rb");
    }

    public void testReference() throws Exception {
        doTest("superclass/reference.rb");
    }

    public void testReferenceJava() throws Exception {
        doTest("superclass/reference_java.rb");
    }

    public void testReference3() throws Exception {
        doTest("superclass/reference3.rb");
    }

    protected ProjectJdk getTestProjectJdk() {
        return JRubySdkUtil.getMockSdkWithoutStubs("empty-mock-sdk");
    }
}