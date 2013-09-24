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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.formatter;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import junit.framework.Test;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.FormatterTest;
import org.jetbrains.plugins.ruby.support.TestUtil;

/**
 * Created by IntelliJ IDEA.
 * User: Roman.Chernyatchik
 * Date: 11.09.2007
 */
public class RHTMLFormatterTest extends FormatterTest {
    private static final String DATA_PATH = PathUtil.getDataPath(RHTMLFormatterTest.class);

    protected void setUp() {
        super.setUp();
        TestUtil.loadRailsSupport();
        TestUtil.loadRORAppComponents();
    }

    public RHTMLFormatterTest(final String dataPath) {
        super(dataPath);
    }

    public static Test suite() {
        return new RHTMLFormatterTest(DATA_PATH);
    }

    protected PsiFile createFileFromText(final String fileText, final Project project) {
        return TestUtil.createPseudoPhysicalRHTMLFile(project, fileText);
    }
//    protected String getSearchPattern() {
//        return "(.*)js/(.*)\\.txt";
//    }
}