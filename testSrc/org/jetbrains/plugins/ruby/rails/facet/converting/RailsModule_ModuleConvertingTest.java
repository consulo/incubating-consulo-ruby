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

import org.jdom.Element;
import org.jetbrains.plugins.ruby.RComponents;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 10, 2008
 */
public class RailsModule_ModuleConvertingTest extends AbstractRailsModuleConvertingTestCase {
    public void testEqualConstants() throws Exception {
        assertEquals(RComponents.RMODULE_SETTINGS_STORAGE, RailsModule_JDomConstants.RMODULE_SETTINGS_STORAGE);
      }

      public void testRailsModule() throws Exception {
        doTest("railsModule");
      }

      private void doTest(final String fileName) throws Exception {
        final String oldFilePath = "module" + File.separator + fileName + ".iml";
        final String newFilePath = "module" + File.separator + fileName + ".new.iml";
        final String backFilePath = "module" + File.separator + fileName + ".back.iml";
        doTestForwardConversion(fileName, oldFilePath, newFilePath);
        doTestBackwardConversion(fileName, newFilePath, backFilePath);
      }

      private void doTestBackwardConversion(String moduleName, final String newFilePath, final String oldFilePath) throws Exception {
        final Element root = loadElement(newFilePath);
        RailsModule_BackwardConversionUtil.convertRootElement(root, moduleName);
        checkElement(oldFilePath, root);
      }

      private void doTestForwardConversion(final String fileName, final String oldFilePath, final String newFilePath) throws Exception {
        final Element root = loadElement(oldFilePath);
        RailsModule_ConvertingUtil.convertRootElement(root, fileName, getContext());
        checkElement(newFilePath, root);
      }
}
