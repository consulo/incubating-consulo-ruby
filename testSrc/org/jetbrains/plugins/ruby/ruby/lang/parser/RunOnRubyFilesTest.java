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

import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.psi.PsiErrorElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiRecursiveElementVisitor;
import junit.framework.Assert;
import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.ruby.sdk.RubySdkUtil;
import org.jetbrains.plugins.ruby.ruby.testCases.PerfomanceFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 11, 2006
 */
public class RunOnRubyFilesTest extends PerfomanceFileSetTestCase {
    public static final int TIMEOUT = 1000;
    @NonNls private static final String RUBY_FILE_PATTERN = "(.*)\\.rb";
    private static Ref<PsiFile> myFileWrapper = new Ref<PsiFile>();

    public RunOnRubyFilesTest(String path) {
        super(path);
    }

    public int getTimeout() {
        return TIMEOUT;
    }

    public Runnable getTest() {
        return new Runnable(){
            public void run(){
                final char[] fileText;
                try {
                    fileText = FileUtil.loadFileText(myFile);
                    myFileWrapper.set(TestUtil.createPseudoPhysicalFile(myProject, new String(fileText)));
                } catch (Exception e) {
                    Assert.fail("File not found");
                }
            }
        };
    }

    protected void runTest(final File file) throws Throwable {
        myFileWrapper.set(null);
        super.runTest(file);
        final PsiFile psiFile = myFileWrapper.get();
        if (psiFile!=null){
            PsiRecursiveElementVisitor visitor = new PsiRecursiveElementVisitor() {
                public void visitErrorElement(PsiErrorElement element) {
                    Assert.fail("Error in psiFile");
                }
            };
            psiFile.accept(visitor);
        }
    }

    @NonNls
    private static final String DATA_PATH = RubySdkUtil.suggestRubyHomePath()+"/lib";

    public RunOnRubyFilesTest() {
        this(DATA_PATH);
    }

    public static Test suite() {
        return new RunOnRubyFilesTest();
    }

    public String getSearchPattern(){
        return RUBY_FILE_PATTERN;
    }
}
