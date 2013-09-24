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
import com.intellij.psi.ResolveResult;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RFieldAttrReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.methodCall.RCall;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.fields.RInstanceVariable;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 21, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class FieldsResolveTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(FieldsResolveTest.class) + "/fields";
    }

    // Simple test
    public void testField() throws Exception {
        final PsiReference ref = getReference("field.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_FIELD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RInstanceVariable);
    }

    // Superclass test
    public void testSuperClassField() throws Exception {
        final PsiReference ref = getReference("superclass.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_FIELD);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RInstanceVariable);
    }

    // Superclass2 test
    public void testSuperClass2Field() throws Exception {
        final PsiReference ref = getReference("superclass2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_FIELD);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RInstanceVariable);
    }

    // Mixin test
    public void testMixinField() throws Exception {
        final PsiReference ref = getReference("mixin.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_FIELD);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RInstanceVariable);
    }

    // Attr reader inner
    public void testAttrReaderInner() throws Exception {
        final PsiReference ref = getReference("attr_reader_inner.rb");
        // symbols
        assertTrue(ref instanceof RFieldAttrReference);
        final ResolveResult[] results = ((RFieldAttrReference) ref).multiResolve(false);
        assertTrue(results.length == 1);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
    }

    // Attr reader inner2
    public void testAttrReaderInner2() throws Exception {
        final PsiReference ref = getReference("attr_reader_inner2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_READER);
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr_reader outer test
    public void testAttrReaderOuter() throws Exception {
        final PsiReference ref = getReference("attr_reader_outer.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_READER);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr writer inner
    public void testAttrWriterInner() throws Exception {
        final PsiReference ref = getReference("attr_writer_inner.rb");
        // symbols
        assertTrue(ref instanceof RFieldAttrReference);
        final ResolveResult[] results = ((RFieldAttrReference) ref).multiResolve(false);
        assertTrue(results.length == 1);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
    }

    // Attr writer inner2
    public void testAttrWriterInner2() throws Exception {
        final PsiReference ref = getReference("attr_writer_inner2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_WRITER);
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr_writer outer test
    public void testAttrWriterOuter() throws Exception {
        final PsiReference ref = getReference("attr_writer_outer.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_WRITER);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr accessor inner
    public void testAttrAccessorInner() throws Exception {
        final PsiReference ref = getReference("attr_accessor_inner.rb");
        // symbols
        assertTrue(ref instanceof RFieldAttrReference);
        final ResolveResult[] results = ((RFieldAttrReference) ref).multiResolve(false);
        assertTrue(results.length == 1);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
    }

    // Attr accessor read inner2
    public void testAttrAccessorReadInner2() throws Exception {
        final PsiReference ref = getReference("attr_accessor_read_inner2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_READER);
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr_accessor read outer test
    public void testAttrAccessorReadOuter() throws Exception {
        final PsiReference ref = getReference("attr_accessor_read_outer.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_READER);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr accessor write inner2
    public void testAttrAccessorWriteInner2() throws Exception {
        final PsiReference ref = getReference("attr_accessor_write_inner2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_WRITER);
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }

    // Attr_accessor write outer test
    public void testAttrAccessorWriteOuter() throws Exception {
        final PsiReference ref = getReference("attr_accessor_write_outer.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.FIELD_WRITER);
        assertEquals("A", symbols.get(0).getParentSymbol().getName());
        // psi
        final ResolveResult[] results = ((RPsiPolyvariantReference) ref).multiResolve(false);
        assertTrue(results.length == 2);
        assertTrue(results[0].getElement() instanceof RInstanceVariable);
        assertTrue(results[1].getElement() instanceof RCall && ((RCall) results[1].getElement()).getCallType().isAttributeCall());
    }
}
