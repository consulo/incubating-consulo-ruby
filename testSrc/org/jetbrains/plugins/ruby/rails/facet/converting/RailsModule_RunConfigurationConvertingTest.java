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
import org.jetbrains.plugins.ruby.RBundle;
import static org.jetbrains.plugins.ruby.rails.facet.converting.RailsModule_RunConfigurationConvertingUtil.RunConfFactories;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Jun 10, 2008
 */
public class RailsModule_RunConfigurationConvertingTest extends AbstractRailsModuleConvertingTestCase {
    protected void doTest(final String fileName) throws Exception {
        final String oldFileName = "runConfig" + File.separator + fileName + ".old.xml";
        final String newFileName = "runConfig" + File.separator + fileName + ".new.xml";
        doTestForward(oldFileName, newFileName);
        doTestBackward(oldFileName, newFileName);
    }

    private void doTestBackward(final String oldFileName, final String newFileName) throws Exception {
        final Element root = loadElement(newFileName);
        RailsModule_RunConfigurationConvertingUtil.convertRunConfigurationBackward(root);
        checkElement(oldFileName, root);
    }

    private void doTestForward(final String oldFileName, final String newFileName) throws Exception {
        final Element root = loadElement(oldFileName);
        RailsModule_RunConfigurationConvertingUtil.convertRunConfiguration(root, getContext());
        checkElement(newFileName, root);
    }

    public void testRSpec() throws Exception {
        doTest("railsModule_rSpec");
    }

    public void testRubyScript() throws Exception {
        doTest("railsModule_rubyScript");
    }

    public void testRubyTest() throws Exception {
        doTest("railsModule_rubyTest");
    }

    public void testServer() throws Exception {
        doTest("railsModule_Server");
    }

    public void testFactoryNames() {
        assertEquals(RBundle.message("run.configuration.script.name"), RunConfFactories.RUBY_SCRIPT.getAttrName());
        assertEquals(RBundle.message("run.configuration.test.name"), RunConfFactories.RUBY_TEST.getAttrName());
        assertEquals(RBundle.message("run.configuration.server.title"), RunConfFactories.SERVER.getAttrName());
        assertEquals(RBundle.message("rspec.run.configuration.type.name"), RunConfFactories.RSPEC.getAttrName());
    }
}
