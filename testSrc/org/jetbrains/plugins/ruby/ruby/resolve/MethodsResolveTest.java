/*
 * Copyright (c) 2008, Your Corporation. All Rights Reserved.
 */

package org.jetbrains.plugins.ruby.ruby.resolve;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.Type;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RSingletonMethod;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 23, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class MethodsResolveTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(ConstantsResolveTest.class) + "/methods";
    }


    public void testSimple() throws Exception {
        final PsiReference ref = getReference("simple.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_METHOD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testSimpleStatic() throws Exception {
        final PsiReference ref = getReference("simple_static.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.CLASS_METHOD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RSingletonMethod);
    }

    public void testIncludeInner() throws Exception {
        final PsiReference ref = getReference("include_inner.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_METHOD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testIncludeOutter() throws Exception {
        final PsiReference ref = getReference("include_outter.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_METHOD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testExtendInner() throws Exception {
        final PsiReference ref = getReference("extend_inner.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_METHOD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }

    public void testExtendOutter() throws Exception {
        final PsiReference ref = getReference("extend_outter.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.INSTANCE_METHOD);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RMethod);
    }
}