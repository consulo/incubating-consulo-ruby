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

package org.jetbrains.plugins.ruby.ruby.lang.formatter;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.testFramework.fixtures.IdeaProjectTestFixture;
import com.intellij.testFramework.fixtures.IdeaTestFixtureFactory;
import com.intellij.testFramework.fixtures.TestFixtureBuilder;
import com.intellij.util.IncorrectOperationException;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.testCases.BaseRubyFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 03.08.2006
 */
public class FormatterTest extends BaseRubyFileSetTestCase {
    private static Logger LOG = Logger.getInstance(FormatterTest.class.getName());

    @NonNls private static final String DATA_PATH = PathUtil.getDataPath(FormatterTest.class);

    private Project project;
    private IdeaProjectTestFixture fixture;

    public FormatterTest(final String dataPath) {
        super(dataPath);
    }


    protected void setUp() {
        super.setUp();

        TestFixtureBuilder<IdeaProjectTestFixture> fixtureBuilder = IdeaTestFixtureFactory.getFixtureFactory().createLightFixtureBuilder();
        fixture = fixtureBuilder.getFixture();

        try {
            fixture.setUp();
        } catch (Exception e) {
            LOG.error(e);
        }

        project = fixture.getProject();
    }


    protected void tearDown() {
        try {
            fixture.tearDown();
        } catch (Exception e) {
            LOG.error(e);
        }
        super.tearDown();
    }


    private void performFormatting(final Project project, final PsiFile file) throws IncorrectOperationException {
        TextRange myTextRange = file.getTextRange();
        CodeStyleManager.getInstance(project).reformatText(file, myTextRange.getStartOffset(), myTextRange.getEndOffset());
    }


    public String transform(List<String> data) throws Exception {
        String fileText = data.get(0);
        final PsiFile psiFile = createFileFromText(fileText, project);
        CommandProcessor.getInstance().executeCommand(project, new Runnable() {
          public void run() {
            ApplicationManager.getApplication().runWriteAction(new Runnable() {
              public void run() {
                  try {
                      performFormatting(project, psiFile);
                  } catch (IncorrectOperationException e) {
                      e.printStackTrace();
                  }
              }
            });
          }
        }, null, null);
        return psiFile.getText();
    }

    protected PsiFile createFileFromText(final String fileText,
                                         final Project project) {
        return TestUtil.createPseudoPhysicalFile(project, fileText);
    }

    public static Test suite() {
        return new FormatterTest(DATA_PATH);
    }
}
