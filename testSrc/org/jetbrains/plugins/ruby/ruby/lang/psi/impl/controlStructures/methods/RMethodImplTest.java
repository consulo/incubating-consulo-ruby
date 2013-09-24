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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.methods;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.InterpretationMode;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolBuilder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods.RMethod;
import org.jetbrains.plugins.ruby.ruby.presentation.RPresentationConstants;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: Nov 23, 2007
 */
@SuppressWarnings({"ConstantConditions"})
public class RMethodImplTest  extends AbstractRubyModuleCacheTest implements RPresentationConstants {
    private VirtualFile rMethodVFile;

    protected void setUp() throws Exception {
        super.setUp();

        rMethodVFile = getFile("rmethod.rb", myModule);
        final RFileInfo info =
                myModuleCacheManager.getFilesCache().getUp2DateFileInfo(rMethodVFile);

        assertNotNull(info);
        RVirtualFile rvFile = info.getRVirtualFile();

        // update symbols relative to this file
        final FileSymbol fileSymbol = new FileSymbol(null, myProject, false);
        new SymbolBuilder(fileSymbol, rvFile, InterpretationMode.FULL).process();
    }

    public void testFormatName() {
        RMethod method = getDirectMethodOfClass("CM1", "method", rMethodVFile);

        assertEquals("method (param1, param2=..., param3=...)", method.getPresentableName(false));
        assertEquals("method (param1, param2=\"test\", param3=10)", method.getPresentableName(true));
    }
}
