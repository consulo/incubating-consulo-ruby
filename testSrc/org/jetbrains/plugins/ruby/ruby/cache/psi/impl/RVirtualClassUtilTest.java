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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.LastSymbolStorage;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.08.2007
 */
public class RVirtualClassUtilTest extends AbstractRubyModuleCacheTest {
    private VirtualFile rClassVFile;

    protected void setUp() throws Exception {
        super.setUp();

        rClassVFile = getFile("rclass1.rb", myModule);
        final RFileInfo info =
                myModuleCacheManager.getFilesCache().getUp2DateFileInfo(rClassVFile);
        assert info != null;
        final RVirtualFile rVFile = info.getRVirtualFile();

        // update symbols relative to this file
        final RFile rFile = (RFile)RVirtualPsiUtil.findPsiByVirtualElement(rVFile, myProject);
        assert rFile != null;
        FileSymbolUtil.getFileSymbol(rFile);
    }

    public void testGetVirtualSuperClasses() {
        RVirtualClass rClass = getClassByQualifiedName("C0", rClassVFile);

        final FileSymbol fileSymbol = LastSymbolStorage.getInstance(myProject).getSymbol();
        assert fileSymbol != null;

        Symbol symbol = SymbolUtil.getSymbolByContainer(fileSymbol, rClass);
        assert symbol != null;

        List<RVirtualClass> classes = RVirtualClassUtil.getVirtualSuperClasses(symbol, fileSymbol);
        assertEquals(0, classes.size());

        rClass = getClassByQualifiedName("C1", rClassVFile);
        symbol = SymbolUtil.getSymbolByContainer(fileSymbol, rClass);
        assert symbol != null;
        classes = RVirtualClassUtil.getVirtualSuperClasses(symbol, fileSymbol);
        assertEquals(2, classes.size());

        rClass = getClassByQualifiedName("C2", rClassVFile);
        symbol = SymbolUtil.getSymbolByContainer(fileSymbol, rClass);
        assert symbol != null;
        classes = RVirtualClassUtil.getVirtualSuperClasses(symbol, fileSymbol);
        assertEquals(1, classes.size());
    }
}
