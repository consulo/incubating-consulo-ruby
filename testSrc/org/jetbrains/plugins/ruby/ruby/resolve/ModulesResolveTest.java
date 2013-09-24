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

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.modules.RModule;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 23, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class ModulesResolveTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(ConstantsResolveTest.class) + "/modules";
    }


    // Simple module test
    public void testModule() throws Exception {
        final PsiReference ref = getReference("module.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.MODULE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RModule);
    }

    // inner module test
    public void testModuleInner() throws Exception {
        final PsiReference ref = getReference("module_inner.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.MODULE);
        assertTrue(symbols.get(0).getParentSymbol().getType() == Type.MODULE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RModule);
    }

    // module in superclass test
    public void testModuleInSuperclass() throws Exception {
        final PsiReference ref = getReference("module_superclass.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.MODULE);
        assertTrue(symbols.get(0).getParentSymbol().getType() == Type.CLASS);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RModule);
    }

    // module in mixin
    public void testModuleInMixin() throws Exception {
        final PsiReference ref = getReference("module_mixin.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.MODULE);
        assertTrue(symbols.get(0).getParentSymbol().getType() == Type.MODULE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RModule);
    }
}