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

package org.jetbrains.plugins.ruby.ruby.presentation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.ruby.testCases.BaseRubyFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @autor: oleg
 * @date: Dec 9, 2007
 */
public class ContainerLocationTest extends BaseRubyFileSetTestCase {
    @NonNls
    private static final String DATA_PATH = PathUtil.getDataPath(ContainerLocationTest.class);
    @NonNls protected static final String CARET_MARKER = "<caret>";

    public ContainerLocationTest() {
        super(DATA_PATH);
    }


    public String transform(List<String> data) throws Exception {
        String fileText = data.get(0);

        final IntRef caretOffset = new IntRef(fileText.indexOf(CARET_MARKER));

        if (caretOffset.get()>=0){
            fileText = TestUtil.removeSubstring(fileText, caretOffset.get(), CARET_MARKER.length(), caretOffset);
        }

        final RFile rFile = TestUtil.createPseudoPhysicalFile(myProject, fileText);
        final PsiElement element = rFile.findElementAt(caretOffset.get());
        final RContainer container = PsiTreeUtil.getParentOfType(element, RContainer.class);
        return RContainerPresentationUtil.getLocation(container);
    }

    public static Test suite() {
        return new ContainerLocationTest();
    }
}
