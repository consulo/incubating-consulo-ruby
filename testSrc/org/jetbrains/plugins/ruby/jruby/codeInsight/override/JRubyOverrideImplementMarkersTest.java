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

package org.jetbrains.plugins.ruby.jruby.codeInsight.override;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.jruby.AbstractJRubyModuleTest;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RubyOverrideImplementUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.cache.SymbolsCache;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Mar 17, 2008
 */
public class JRubyOverrideImplementMarkersTest extends AbstractJRubyModuleTest {
    protected RFile myFile;
    protected RContainer myContainer;
    protected int myResult;

    protected String getDataDirPath() {
        return PathUtil.getDataPath(JRubyOverrideImplementMarkersTest.class);
    }

    public void testClassImplement1() throws Exception {
        init("class_implement1.rb");
        final RVirtualContainer rVirtualContainer = RVirtualPsiUtil.findVirtualContainer(myContainer);
        assertNotNull(rVirtualContainer);
        final Symbol symbol = SymbolUtil.getSymbolByContainer(myFileSymbol, rVirtualContainer);
        assertNotNull(symbol);
        final List elements = RubyOverrideImplementUtil.getImplementedJavaMethods(
                RubyOverrideImplementUtil.getOverridenSymbols(myFileSymbol, symbol));
        assertEquals(myResult, elements.size());
    }

    public void testClassImplement2() throws Exception {
        init("class_implement2.rb");
        final RVirtualContainer rVirtualContainer = RVirtualPsiUtil.findVirtualContainer(myContainer);
        assertNotNull(rVirtualContainer);
        final Symbol symbol = SymbolUtil.getSymbolByContainer(myFileSymbol, rVirtualContainer);
        assertNotNull(symbol);
        final List elements = RubyOverrideImplementUtil.getImplementedJavaMethods(
                RubyOverrideImplementUtil.getOverridenSymbols(myFileSymbol, symbol));
        assertEquals(myResult, elements.size());
    }

    public void testClassOverride1() throws Exception {
        init("class_override1.rb");
        final RVirtualContainer rVirtualContainer = RVirtualPsiUtil.findVirtualContainer(myContainer);
        assertNotNull(rVirtualContainer);
        final Symbol symbol = SymbolUtil.getSymbolByContainer(myFileSymbol, rVirtualContainer);
        assertNotNull(symbol);
        final List elements = RubyOverrideImplementUtil.getOverridenElements(myFileSymbol, symbol, rVirtualContainer);
        assertEquals(myResult, elements.size());
    }

    public void testClassOverride2() throws Exception {
        init("class_override2.rb");
        final RVirtualContainer rVirtualContainer = RVirtualPsiUtil.findVirtualContainer(myContainer);
        assertNotNull(rVirtualContainer);
        final Symbol symbol = SymbolUtil.getSymbolByContainer(myFileSymbol, rVirtualContainer);
        assertNotNull(symbol);
        final List elements = RubyOverrideImplementUtil.getOverridenElements(myFileSymbol, symbol, rVirtualContainer);
        assertEquals(myResult, elements.size());
    }

    @NonNls
    protected static final String CARET_MARKER = "#caret#";
    @NonNls
    protected static final String RESULT_MARKER = "#result#";
    protected FileSymbol myFileSymbol;

    protected void init(final String path) throws IOException {
        final VirtualFile file = getFile(path, myModule);
        assertNotNull(file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");

        final IntRef caretMarker = new IntRef(fileText.indexOf(CARET_MARKER));
        assertTrue(caretMarker.get() >= 0);

        final IntRef resultMarker = new IntRef(fileText.indexOf(RESULT_MARKER));
        assertTrue(caretMarker.get() >= 0);


        fileText = TestUtil.removeSubstring(fileText, caretMarker.get(), CARET_MARKER.length(), caretMarker, resultMarker);
        fileText = TestUtil.removeSubstring(fileText, resultMarker.get(), RESULT_MARKER.length(), caretMarker, resultMarker);
        myResult = Integer.parseInt(fileText.substring(resultMarker.get()).trim());
        fileText = fileText.substring(0, resultMarker.get());

        VfsUtil.saveText(file, fileText);

        myFile = (RFile) PsiManager.getInstance(myProject).findFile(file);

        SymbolsCache.getInstance(myProject).clearCaches();
        myFileSymbol = myFile.getFileSymbol();
        final PsiElement element = myFile.findElementAt(caretMarker.get());
        myContainer = PsiTreeUtil.getParentOfType(element, RContainer.class);
    }
}
