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

package org.jetbrains.plugins.ruby.ruby.resolve;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.ResolveResult;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFileReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.RPsiElementBase;

import java.util.HashSet;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 23, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class RequireTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(ConstantsResolveTest.class) + "/require";
    }


    // Simple require test
    public void testRequire() throws Exception {
        final PsiReference ref = getReference("src/require1.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "src");
    }

    // File.dirname(__FILE__) + '...'
    public void testRequireFileDirname() throws Exception {
        final PsiReference ref = getReference("src/require2.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "src");
    }

    // File.expandpath('...')
    public void testRequireFileExpandPath() throws Exception {
        final PsiReference ref = getReference("src/require3.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "src");
    }

    // require relative
    public void testRequireRelative() throws Exception {
        final PsiReference ref = getReference("src/require4.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "foo");
    }

    // require File.join
    public void testRequireFileJoin() throws Exception {
        final PsiReference ref = getReference("src/require5.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "foo");
    }

    // require in loadpath
    public void testRequireWithLoadPath() throws Exception {
        final PsiReference ref = getReference("src/require6.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final VirtualFile fooDir = getFile("src/foo", myModule);
        fileSymbol.addLoadPathUrl(fooDir.getUrl());
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "foo");
    }

    // multiplyrequire in loadpath
    public void testMultiplyRequireWithLoadPath() throws Exception {
        final PsiReference ref = getReference("src/require6.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final VirtualFile fooDir = getFile("src/foo", myModule);
        fileSymbol.addLoadPathUrl(fooDir.getUrl());
        final VirtualFile barDir = getFile("src/bar", myModule);
        fileSymbol.addLoadPathUrl(barDir.getUrl());
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 2);
        final HashSet<String> dirNames = new HashSet<String>();
        dirNames.add(((PsiFile) results[0].getElement()).getParent().getName());
        dirNames.add(((PsiFile) results[1].getElement()).getParent().getName());
        assertTrue(dirNames.contains("bar"));
        assertTrue(dirNames.contains("foo"));
    }

    // multiplyrequire in loadpath
    public void testRequireWithLoadPath2() throws Exception {
        final PsiReference ref = getReference("src/require7.rb");
        assertTrue(ref instanceof RFileReference);
        final FileSymbol fileSymbol = ((RPsiElementBase) ref.getElement()).forceFileSymbolUpdate();
        final VirtualFile fooDir = getFile("src/foo", myModule);
        fileSymbol.addLoadPathUrl(fooDir.getUrl());
        final VirtualFile barDir = getFile("src/bar", myModule);
        fileSymbol.addLoadPathUrl(barDir.getUrl());
        final ResolveResult[] results = ((RFileReference) ref).multiResolve(fileSymbol);
        assertTrue(results.length == 1);
        assertEquals(((PsiFile) results[0].getElement()).getParent().getName(), "foo");
    }
}