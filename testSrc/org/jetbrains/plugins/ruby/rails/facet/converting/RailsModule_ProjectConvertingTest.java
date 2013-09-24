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

package org.jetbrains.plugins.ruby.rails.facet.converting;

import com.intellij.openapi.components.ExpandMacroToPathMap;
import com.intellij.openapi.util.JDOMUtil;
import com.intellij.openapi.util.io.FileUtil;
import org.jdom.Element;

import java.io.File;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 10, 2008
 */
public class RailsModule_ProjectConvertingTest extends AbstractRailsModuleConvertingTestCase {
    public void testConvertProject() throws Exception {
        final File directory = FileUtil.createTempDirectory("railsModuleConvert", "test");
        FileUtil.copyDir(new File(getBaseDataPath(), "project"), directory);
        FileUtil.copyDir(new File(getBaseDataPath(), "module"), directory);

        final String projectPath = FileUtil.toSystemIndependentName(directory.getPath()) + "/railsProject.ipr";
        final RailsModule_ProjectConverter converter = new RailsModule_ProjectConverter(projectPath) {
            protected void addMacros(final ExpandMacroToPathMap macros) {
            }
        };
        converter.prepare();
        final File[] affectedFiles = converter.getAffectedFiles();
        assertEquals(1, affectedFiles.length);

        converter.convert();

        final File[] files = directory.listFiles();
        for (File file : files) {
            if (Arrays.asList(affectedFiles).contains(file)) {
                final String name = FileUtil.getNameWithoutExtension(file);
                String extension = FileUtil.getExtension(file.getName());
                File expected = new File(file.getParentFile(), name + ".new." + extension);
                final Element actual = JDOMUtil.loadDocument(file).getRootElement();
                checkElement(expected, actual);
            }
        }
    }
}
