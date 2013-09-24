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

package rb.implement;

import com.intellij.codeInsight.generation.ClassMember;
import com.intellij.openapi.projectRoots.ProjectJdk;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.jruby.AbstractJRubyModuleTest;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.RubyOverrideImplementUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.SymbolUtil;
import org.jetbrains.plugins.ruby.ruby.lang.RubyLanguage;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.sdk.jruby.JRubySdkUtil;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 30, 2008
 */
public class RubyImplementVariantsTest extends AbstractJRubyModuleTest {
    protected String myName;
    protected String myVariants;
    protected RFile myFile;

    protected String getDataDirPath() {
        return PathUtil.getDataPath(RubyImplementVariantsTest.class);
    }

    public void testAbstract() throws Exception {
        doTest("abstract.rb");
    }

    public void testAbstractImport() throws Exception {
        doTest("abstract_import.rb");
    }

    public void testAbstractIncludeClass() throws Exception {
        doTest("abstract_include_class.rb");
    }

    public void testAbstractIncludePackage() throws Exception {
        doTest("abstract_include_package.rb");
    }

    public void testInterface() throws Exception {
        doTest("interface.rb");
    }

    public void testInterfaceWithSomeImplemented() throws Exception {
        doTest("interfaceWithSomeImplemented.rb");
    }

    public void testInterfaceWithSuperClass() throws Exception {
        doTest("interfaceWithSuperClass.rb");
    }

    public void testNoting() throws Exception {
        doTest("nothing.rb");
    }


    private void doTest(@NotNull final String name) throws Exception {
        init(name);
        final ImplementHandler handler = (ImplementHandler) RubyLanguage.INSTANCE.getImplementMethodsHandler();
        assertNotNull(handler);
        //noinspection ConstantConditions
        final List<ClassMember> list = handler.create_implement_members(SymbolUtil.getTopLevelClassByName(myFileSymbol, myName));
        assertEquals(myVariants, RubyOverrideImplementUtil.classMembersToString(list));
    }

    @NonNls
    protected static final String NAME_MARKER = "#name#";
    @NonNls protected static final String RESULT_MARKER = "#result#";
    protected FileSymbol myFileSymbol;

    protected void init(final String path) throws IOException {
        final VirtualFile file = getFile(path, myModule);
        assertNotNull(file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");

        final IntRef nameMarker = new IntRef(fileText.indexOf(NAME_MARKER));
        assertTrue(nameMarker.get()>=0);
        final IntRef resultMarker = new IntRef(fileText.indexOf(RESULT_MARKER));
        assertTrue(resultMarker.get()>=0);


        fileText = TestUtil.removeSubstring(fileText, nameMarker.get(), NAME_MARKER.length(), nameMarker, resultMarker);
        fileText = TestUtil.removeSubstring(fileText, resultMarker.get(), RESULT_MARKER.length(), nameMarker, resultMarker);

        myName = fileText.substring(nameMarker.get(), resultMarker.get()).trim();
        myVariants = fileText.substring(resultMarker.get()).trim();

        fileText = fileText.substring(0, nameMarker.get());
        VfsUtil.saveText(file, fileText);

        myFile = (RFile) PsiManager.getInstance(myProject).findFile(file);
        myFileSymbol = myFile.getFileSymbol();
    }

    protected ProjectJdk getTestProjectJdk() {
        return JRubySdkUtil.getMockSdkWithoutStubs("empty-mock-sdk");
    }
}
