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

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.testFramework.IdeaTestCase;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.rails.facet.versions.BaseRailsFacet;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 10, 2008
 */
public abstract class AbstractRailsModuleConvertingTestCase extends IdeaTestCase {
    protected void setUp() throws Exception {
        super.setUp();

        //getApplication().registerService(FacetTypeRegistry.class, new FacetTypeRegistryImpl());
        BaseRailsFacetType.load();
    }

    protected File getBaseDataPath() {
        return new File(PathUtil.getDataPath(AbstractRailsModuleConvertingTestCase.class));
    }

    protected void checkElement(final String filePath, final Element root) throws JDOMException, IOException {
        final File file = new File(getBaseDataPath(), filePath);
        checkElement(file, root);
    }

    protected void checkElement(final File file, final Element root) throws JDOMException, IOException {
        final Element expected = JDOMUtil.loadDocument(file).getRootElement();
        assertElementEquals(expected, root, "See expected data in " + file.getName());
    }

    private void assertElementEquals(final Element expected, final Element actual, final String errorMsg) {
        String expectedText = JDOMUtil.createOutputter("\n").outputString(expected);
        String actualText = JDOMUtil.createOutputter("\n").outputString(actual);
        assertEquals(errorMsg, expectedText, actualText);
    }

    protected Element loadElement(final String filePath) throws Exception {
      final Document document = JDOMUtil.loadDocument(new File(getBaseDataPath(), filePath));
      return document.getRootElement();
    }

    protected RailsModule_ConvertingContext getContext() {
        return new RailsModule_ConvertingContext() {
          protected FacetTypeId<? extends BaseRailsFacet> getFacetType(final String moduleName) {
              return BaseRailsFacet.getRailsFacetID();
          }
        };
      }
}