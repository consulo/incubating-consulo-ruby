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

package org.jetbrains.plugins.ruby.ruby.symbols;

import junit.framework.Test;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfoFactory;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.psi.impl.RVirtualElementBase;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.Symbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.testCases.BaseRubyFileSetTestCase;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 6, 2007
 */
public class SymbolBuilderTest extends BaseRubyFileSetTestCase {
    @NonNls
    private static final String DATA_PATH = PathUtil.getDataPath(SymbolBuilderTest.class);

    public SymbolBuilderTest() {
        super(DATA_PATH);
    }


    public String transform(List<String> data) throws Exception {
        final String fileText = data.get(0);

        final RFile rFile = TestUtil.createPseudoPhysicalFile(myProject, fileText);

        RVirtualElementBase.resetIdCounter();
        final RFileInfo fileInfo = RFileInfoFactory.createRFileInfoByPseudPhysicalRFile(rFile);
        final RVirtualFile rVirtualFile = fileInfo.getRVirtualFile();
        final String dump = ((RVirtualElementBase) rVirtualFile).dump();

        Symbol.resetIdCounter();
        final FileSymbol fileSymbol = new FileSymbol(null, myProject, false);
        new SymbolBuilder(fileSymbol, rVirtualFile, InterpretationMode.FULL).process();
        return "VirtualFile:\n" + dump + "\n\nSymbol:\n" + fileSymbol.dump();
    }

    public static Test suite() {
        return new SymbolBuilderTest();
    }

//    protected String getSearchPattern() {
//        return ".*/alias4\\.txt";
//    }
}
