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

package org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.parsing.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import junit.framework.Assert;
import org.jetbrains.plugins.ruby.rails.langs.rhtml.lang.psi.RHTMLFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.testCases.BaseRubyFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.util.List;

public abstract class AbstractRHTMLParserTest extends BaseRubyFileSetTestCase {

    public AbstractRHTMLParserTest(final String data_path) {
        super(data_path);
    }


    protected void setUp() {
        super.setUp();
        TestUtil.loadRailsSupport();
    }

    public String transform(List<String> data) throws Exception {
        final String fileText = data.get(0);

        final RHTMLFile psiFile = TestUtil.createPseudoPhysicalRHTMLFile(myProject, fileText);
        final String psiLeafText = gatherTextFromPsiFile(psiFile);

        Assert.assertEquals(fileText, psiLeafText);
        Assert.assertEquals(psiFile.getText(), fileText);
        Assert.assertEquals(psiFile.getTextLength(), fileText.length());

        return dump(psiFile);
    }

    protected abstract String dump(final RHTMLFile file);

    private String gatherTextFromPsiFile(PsiFile psiFile) {
        final StringBuilder result = new StringBuilder();
        PsiElementVisitor myVisitor = new RubyElementVisitor() {
            public void visitElement(PsiElement element) {
// if child is leaf
                if (element.getFirstChild() == null) {
                    result.append(element.getText());
                } else {
                    element.acceptChildren(this);
                }
            }

        };
        psiFile.accept(myVisitor);
        return result.toString();
    }
}