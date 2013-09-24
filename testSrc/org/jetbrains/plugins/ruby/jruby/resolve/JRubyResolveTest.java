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

package org.jetbrains.plugins.ruby.jruby.resolve;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.jruby.AbstractJRubyModuleTest;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RQualifiedReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 31, 2008
 */
public class JRubyResolveTest extends AbstractJRubyModuleTest {
    protected RFile myFile;
    protected PsiReference myReference;

    protected String getDataDirPath() {
        return PathUtil.getDataPath(JRubyResolveTest.class);
    }

    public void testBoolean() throws Exception {
        init("boolean.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiMethod);
        final PsiMethod method = (PsiMethod) element;
        assertEquals("isBoo", method.getName());
    }
    
    public void testGetter() throws Exception {
        init("getter.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiMethod);
        final PsiMethod method = (PsiMethod) element;
        assertEquals("getBoo", method.getName());
    }

    public void testSetter() throws Exception {
        init("setter.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiMethod);
        final PsiMethod method = (PsiMethod) element;
        assertEquals("setBoo", method.getName());
    }

    public void testRubyBoolean() throws Exception {
        init("ruby_boolean.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof RMethod);
        final RMethod method = (RMethod) element;
        assertEquals("boo?", method.getName());
    }

    public void testRubyGetter() throws Exception {
        init("ruby_getter.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof RMethod);
        final RMethod method = (RMethod) element;
        assertEquals("boo", method.getName());
    }

    public void testRubySetter() throws Exception {
        init("ruby_setter.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof RMethod);
        final RMethod method = (RMethod) element;
        assertEquals("boo=", method.getName());
    }

    public void testStaticBoolean() throws Exception {
        init("static_boolean.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiMethod);
        final PsiMethod method = (PsiMethod) element;
        assertEquals("isFoo", method.getName());
    }

    public void testStaticGetter() throws Exception {
        init("static_getter.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiMethod);
        final PsiMethod method = (PsiMethod) element;
        assertEquals("getFoo", method.getName());
    }

    public void testStaticSetter() throws Exception {
        init("static_setter.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiMethod);
        final PsiMethod method = (PsiMethod) element;
        assertEquals("setFoo", method.getName());
    }

    public void testJavaClass() throws Exception {
        init("java_class.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiClass);
        final PsiClass clazzz = (PsiClass) element;
        assertEquals("java.TestClass", clazzz.getQualifiedName());
    }

    public void testJavaClass2() throws Exception {
        init("java_class2.rb");
        assertTrue(myReference instanceof RQualifiedReference);
        final PsiElement element = myReference.resolve();
        assertTrue(element instanceof PsiClass);
        final PsiClass clazzz = (PsiClass) element;
        assertEquals("java.TestClass", clazzz.getQualifiedName());
    }

    @NonNls
    protected static final String CARET_MARKER = "#caret#";
    protected FileSymbol myFileSymbol;

    protected void init(final String path) throws IOException {
        final VirtualFile file = getFile(path, myModule);
        assertNotNull(file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");

        final IntRef caretMarker = new IntRef(fileText.indexOf(CARET_MARKER));
        assertTrue(caretMarker.get() >= 0);


        fileText = TestUtil.removeSubstring(fileText, caretMarker.get(), CARET_MARKER.length(), caretMarker);
        VfsUtil.saveText(file, fileText);

        myFile = (RFile) PsiManager.getInstance(myProject).findFile(file);
        myFileSymbol = myFile.getFileSymbol();
        myReference = myFile.findReferenceAt(caretMarker.get());
    }
}
