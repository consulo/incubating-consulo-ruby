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

package org.jetbrains.plugins.ruby.ruby.lang.parser;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementVisitor;
import com.intellij.psi.PsiFile;
import junit.framework.Assert;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.visitors.RubyElementVisitor;
import org.jetbrains.plugins.ruby.ruby.testCases.BaseRubyFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;
import org.jetbrains.plugins.ruby.support.utils.DebugUtil;

import java.util.List;

public class ParserTest extends BaseRubyFileSetTestCase {
    @NonNls private static final String DATA_PATH = PathUtil.getDataPath(ParserTest.class);

    public ParserTest() {
        super(DATA_PATH);
    }


    public String transform(List<String> data) throws Exception {
        final String fileText = data.get(0);

        final PsiFile psiFile = TestUtil.createPseudoPhysicalFile(myProject, fileText);
        final String psiLeafText = gatherTextFromPsiFile(psiFile);

        Assert.assertEquals(fileText, psiLeafText);
        Assert.assertEquals(psiFile.getText(), fileText);
        Assert.assertEquals(psiFile.getTextLength(), fileText.length());

        return DebugUtil.psiToString(psiFile, false, false);
    }

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

    public static Test suite() {
        return new ParserTest();
    }



//    public String getSearchPattern(){
//        return ".*/return5\\.txt";
//    }

}
