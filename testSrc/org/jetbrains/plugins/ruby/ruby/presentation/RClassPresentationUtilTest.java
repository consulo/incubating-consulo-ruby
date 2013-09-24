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

package org.jetbrains.plugins.ruby.ruby.presentation;

import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.plugins.ruby.ruby.RubyIcons;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualFile;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.FileSymbolUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RVirtualPsiUtil;
import org.jetbrains.plugins.ruby.ruby.lang.psi.impl.holders.utils.RContainerUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: Roman Chernyatchik
 * @date: 11.08.2007
 */
public class RClassPresentationUtilTest extends AbstractRubyModuleCacheTest {
    private RVirtualFile rClassRVFile;
    private VirtualFile rClassVFile;

    protected void setUp() throws Exception {
        super.setUp();

        rClassVFile = getFile("rclass.rb", myModule);
        final RFileInfo info =
                myModuleCacheManager.getFilesCache().getUp2DateFileInfo(rClassVFile);
        assert info != null;
        rClassRVFile = info.getRVirtualFile();

        // update symbols relative to this file
        final RFile rFile = (RFile)RVirtualPsiUtil.findPsiByVirtualElement(rClassRVFile, myProject);
        assert rFile != null;
        FileSymbolUtil.getFileSymbol(rFile);
    }

    public void testGetRuntimeQualifiedName() {
        final List<RVirtualClass> classes = new ArrayList<RVirtualClass>();
        collectAllClassesInFile(rClassRVFile, classes);
        final List<String> qualifiedNames = new ArrayList<String>();
        final FileSymbol fileSymbol = FileSymbolUtil.getFileSymbol(rClassRVFile, true);
        for (RVirtualClass aClass : classes) {
            //noinspection ConstantConditions
            qualifiedNames.add(RClassPresentationUtil.getRuntimeQualifiedName(fileSymbol, aClass));
        }
        assertEquals(5, qualifiedNames.size());

        qualifiedNames.remove("C0");
        qualifiedNames.remove("M2::C1");
        qualifiedNames.remove("M1::C1");
        qualifiedNames.remove("M3::M4::M5::C6");
        qualifiedNames.remove("M3::M4::M5::C6::C7::C8");

        assertEmpty(qualifiedNames);
    }

    public void testGetNameByQualifiedName() {
        assertEquals("", RClassPresentationUtil.getNameByQualifiedName(""));
        assertEquals("C0", RClassPresentationUtil.getNameByQualifiedName("C0"));   
        assertEquals("C1", RClassPresentationUtil.getNameByQualifiedName("M2::C1"));
        assertEquals("C7", RClassPresentationUtil.getNameByQualifiedName("M3::M4::M5::M6::C7"));
    }

    public void testFormatName() {
        final RVirtualClass rClass = getClassByQualifiedName("M3::M4::M5::C6::C7::C8", rClassVFile);
        assertEquals("", RClassPresentationUtil.formatName(rClass, 0));
        assertEquals("C8", RClassPresentationUtil.formatName(rClass, RPresentationConstants.SHOW_NAME));
        assertEquals("C7::C8", RClassPresentationUtil.formatName(rClass, RPresentationConstants.SHOW_FULL_NAME));
    }

    public void testGetIcon() {
        RVirtualClass rClass = getClassByQualifiedName("M2::C1", rClassVFile);
        assert rClass != null;
        Icon icon = RClassPresentationUtil.getIcon(rClass);
        assertNotNull(icon);
        assertSame(icon, RubyIcons.RUBY_CLASS_NODE);

        icon = RClassPresentationUtil.getIcon(rClass, Iconable.ICON_FLAG_VISIBILITY);
        assertNotNull(icon);
        assertNotSame(icon, RubyIcons.RUBY_CLASS_NODE);
    }

    private void collectAllClassesInFile(final RVirtualContainer container,
                                         final List<RVirtualClass> list) {
        final List<RVirtualStructuralElement> classes
                = RContainerUtil.selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.CLASS);
        for (RVirtualStructuralElement aClass : classes) {
            list.add((RVirtualClass)aClass);
            collectAllClassesInFile((RVirtualContainer)aClass, list);
        }
        final List<RVirtualStructuralElement> modules
                = RContainerUtil.selectVirtualElementsByType(container.getVirtualStructureElements(), StructureType.MODULE);
        for (RVirtualStructuralElement module : modules) {
            collectAllClassesInFile((RVirtualContainer)module, list);
        }
    }
}
