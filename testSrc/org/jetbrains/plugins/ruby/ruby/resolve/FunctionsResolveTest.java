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
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RubyPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 21, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class FunctionsResolveTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(ConstantsResolveTest.class) + "/functions";
    }

    public void testCall() throws Exception {
        final PsiReference ref = getReference("function.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.INSTANCE_METHOD, symbols.get(0).getType());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testOverride1() throws Exception {
        final PsiReference ref = getReference("override.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.INSTANCE_METHOD, symbols.get(0).getType());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
        assertNotNull(RubyPsiUtil.getElementToLeftWithSameParent(element, RMethod.class));
    }

    public void testOverrideInClass() throws Exception {
        final PsiReference ref = getReference("overrideinclass.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.INSTANCE_METHOD, symbols.get(0).getType());
        assertEquals("B", symbols.get(0).getParentSymbol().getName());
    }

    public void testSuperMixin() throws Exception {
        final PsiReference ref = getReference("supermixin.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CLASS_METHOD, symbols.get(0).getType());
        assertEquals("B::N::foo", SymbolUtil.getPresentablePath(symbols.get(0)));

        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testSuperMixin2() throws Exception {
        final PsiReference ref = getReference("supermixin2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CLASS_METHOD, symbols.get(0).getType());
        assertEquals("C::N::foo", SymbolUtil.getPresentablePath(symbols.get(0)));

        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testSuperMixin3() throws Exception {
        final PsiReference ref = getReference("supermixin3.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CLASS_METHOD, symbols.get(0).getType());
        assertEquals("A::N::foo", SymbolUtil.getPresentablePath(symbols.get(0)));

        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testConstructor() throws Exception {
        final PsiReference ref = getReference("constructor.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CLASS_METHOD, symbols.get(0).getType());
        assertEquals("initialize", symbols.get(0).getName());

        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testConstructor2() throws Exception {
        final PsiReference ref = getReference("constructor2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertEquals(1, symbols.size());
        assertEquals(Type.CLASS_METHOD, symbols.get(0).getType());
        assertEquals("initialize", symbols.get(0).getName());

        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }
}