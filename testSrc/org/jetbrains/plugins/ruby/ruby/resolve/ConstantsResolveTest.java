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
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RConstant;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 21, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class ConstantsResolveTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(ConstantsResolveTest.class) + "/constants";
    }


    // Simple constant test
    public void testConstant() throws Exception {
        final PsiReference ref = getReference("constant.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CONSTANT, symbols.get(0).getType());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RConstant);
        assertTrue(((RConstant) element).isInDefinition());
    }

    // Simple constant in multi assign test
    public void testConstantMultiAssign() throws Exception {
        final PsiReference ref = getReference("constantmultiassing.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CONSTANT, symbols.get(0).getType());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RConstant);
        assertTrue(((RConstant) element).isInDefinition());
    }

    // Constant in superclass test
    public void testConstantInSuperclass() throws Exception {
        final PsiReference ref = getReference("constantinsuperclass.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CONSTANT, symbols.get(0).getType());
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RConstant);
        assertTrue(((RConstant) element).isInDefinition());
    }

    // Constant in mixin test
    public void testConstantInMixin() throws Exception {
        final PsiReference ref = getReference("constantinmixin.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CONSTANT, symbols.get(0).getType());
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RConstant);
        assertTrue(((RConstant) element).isInDefinition());
    }

    // constant3 test
    public void testConstant3() throws Exception {
        final PsiReference ref = getReference("constant3.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CONSTANT, symbols.get(0).getType());
        assertEquals(Type.FILE, symbols.get(0).getParentSymbol().getType());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RConstant);
        assertTrue(((RConstant) element).isInDefinition());
    }

    // constant4 test
    public void testConstantHere() throws Exception {
        final PsiReference ref = getReference("constanthere.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CONSTANT, symbols.get(0).getType());
        assertEquals(Type.FILE, symbols.get(0).getParentSymbol().getType());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RConstant);
        assertTrue(((RConstant) element).isInDefinition());
    }
}