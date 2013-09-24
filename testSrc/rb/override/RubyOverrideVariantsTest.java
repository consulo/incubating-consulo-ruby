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

package rb.override;

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
public class RubyOverrideVariantsTest extends AbstractJRubyModuleTest {
    protected String myName;
    protected String myVariants;
    protected RFile myFile;

    protected String getDataDirPath() {
        return PathUtil.getDataPath(RubyOverrideVariantsTest.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        TestUtil.loadJRubySupport();
    }

    public void testEmpty() throws Exception {
        runAbstractFile("empty.rb");
    }

    public void testStatic() throws Exception {
        runAbstractFile("static.rb");
    }

    public void testAll() throws Exception {
        runAbstractFile("all.rb");
    }

    public void testNotAll() throws Exception {
        runAbstractFile("not_all.rb");
    }

    public void testNothing() throws Exception {
        runAbstractFile("nothing.rb");
    }

    public void testJava() throws Exception {
        runAbstractFile("java.rb");
    }

    public void testJavaImport() throws Exception {
        runAbstractFile("java_import.rb");
    }

    public void testJavaInclude() throws Exception {
        runAbstractFile("java_include.rb");
    }

    public void testJavaIncludePackage() throws Exception {
        runAbstractFile("java_include_package.rb");
    }

    private void runAbstractFile(@NotNull final String name) throws Exception {
        init(name);
        final OverrideHandler handler = (OverrideHandler) RubyLanguage.RUBY.getOverrideMethodsHandler();
        assertNotNull(handler);
        //noinspection ConstantConditions
        final List<ClassMember> list = handler.create_override_members(myProject, SymbolUtil.getTopLevelClassByName(myFileSymbol, myName));
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
        return JRubySdkUtil.getMockSdk("empty-mock-sdk");
    }
}
