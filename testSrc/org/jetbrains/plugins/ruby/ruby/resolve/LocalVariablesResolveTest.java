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
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 21, 2007
 */
public class LocalVariablesResolveTest extends ResolveTestBase {

    protected String getDataDirPath() {
        return PathUtil.getDataPath(LocalVariablesResolveTest.class) + "/localvars";
    }


    // Simple local variable test
    public void testLocalVariable() throws Exception {
        final PsiReference ref = getReference("localvar.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.LOCAL_VARIABLE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RIdentifier);
        assertTrue(((RIdentifier) element).isLocalVariable());
    }

    // local variable in multiassignment test
    public void testLocalVariableInMultiassign() throws Exception {
        final PsiReference ref = getReference("localvar_multiassign.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.LOCAL_VARIABLE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RIdentifier);
        assertTrue(((RIdentifier) element).isLocalVariable());
    }

    // Test inner local variable with same name
    public void testInnerLVariable() throws Exception {
        final PsiReference ref = getReference("innerlv.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.LOCAL_VARIABLE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RIdentifier);
        assertTrue(((RIdentifier) element).isLocalVariable());
    }

    // Test function parameter
    public void testParameter() throws Exception {
        final PsiReference ref = getReference("parameter.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.LOCAL_VARIABLE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RIdentifier);
        assertTrue(((RIdentifier) element).isParameter());
    }

    // Test function parameter#2
    public void testParamter2() throws Exception {
        final PsiReference ref = getReference("parameter2.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.LOCAL_VARIABLE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RIdentifier);
        assertTrue(((RIdentifier) element).isLocalVariable());
    }

    // Test method
    public void testMethod() throws Exception {
        final PsiReference ref = getReference("method.rb");
        // symbols
        assertTrue(ref instanceof RPsiPolyvariantReference);
        final List<Symbol> symbols = ((RPsiPolyvariantReference) ref).multiResolveToSymbols(myFileSymbol);
        assertTrue(symbols.size() == 1);
        assertTrue(symbols.get(0).getType() == Type.LOCAL_VARIABLE);
        // psi
        final PsiElement element = ref.resolve();
        assertTrue(element instanceof RIdentifier);
        assertTrue(((RIdentifier) element).isLocalVariable());
    }
}
