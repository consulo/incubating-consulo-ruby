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

package org.jetbrains.plugins.ruby.ruby.testCases;

import com.intellij.openapi.project.Project;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import org.jetbrains.plugins.ruby.support.TestUtil;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 22.08.2006
 */
public abstract class RubyFileSetTestCase extends FileSetTestCase {
    protected Project myProject;
    private IdeaProjectTestFixture myFixture;


    public RubyFileSetTestCase(String path) {
        super(path);
    }

    protected void setUp() {
        super.setUp();

        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
        myFixture = fixtureBuilder.getFixture();

        try {
            myFixture.setUp();
        } catch (Exception e) {
            //ignore
        }

        myProject = myFixture.getProject();

        TestUtil.loadRubySupport();
    }

    protected void tearDown()  {
        try {
            myFixture.tearDown();
        } catch (Exception e) {
            //ignore
        }
        super.tearDown();
    }

}
