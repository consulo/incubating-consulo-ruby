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

package org.jetbrains.plugins.ruby.ruby.codeInsight.paramInfo;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.PathUtil;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPossibleCall;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jan 29, 2008
 */
public class ParamInfoTest extends AbstractRubyModuleCacheTest {
    @NonNls
    protected static final String CARET_MARKER = "<caret>";
    protected FileSymbol myFileSymbol;

    @Nullable
    private RPossibleCall getCall(final String path) throws IOException {
        final VirtualFile file = getFile(path, myModule);
        assertNotNull(file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");
        final IntRef caretOffset = new IntRef(fileText.indexOf(CARET_MARKER));

        if (caretOffset.get()>=0){
            fileText = TestUtil.removeSubstring(fileText, caretOffset.get(), CARET_MARKER.length(), caretOffset);
        }
        VfsUtil.saveText(file, fileText);
        final RFile myFile = (RFile) PsiManager.getInstance(myProject).findFile(file);
        myFileSymbol = myFile.getFileSymbol();
        return RubyParameterInfoHandler.findCall(myFile, caretOffset.get());
    }

    protected String getDataDirPath() {
        return PathUtil.getDataPath(ParamInfoTest.class);
    }


    public void testCallWithParameters() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithParameters.rb");
        assertNotNull(possibleCall);
        assertEquals("foo", possibleCall.getText());
    }

    public void testCallWithParameters2() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithParameters2.rb");
        assertNotNull(possibleCall);
        assertEquals("foo 1,2,3,4", possibleCall.getText());
    }

    public void testCallWithParameters3() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithParameters3.rb");
        assertNotNull(possibleCall);
        assertEquals("foo 1,2,3,4", possibleCall.getText());
    }

    public void testCallWithoutParameters() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithoutParameters.rb");
        assertNotNull(possibleCall);
        assertEquals("foo", possibleCall.getText());
    }

    public void testCallWithoutParameters2() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithoutParameters2.rb");
        assertNotNull(possibleCall);
        assertEquals("AAA.fooo", possibleCall.getText());
    }

    public void testCallWithoutParameters3() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithoutParameters3.rb");
        assertNotNull(possibleCall);
        assertEquals("AAA.fooo", possibleCall.getText());
    }

    public void testCallInvalid() throws Exception {
        final RPossibleCall possibleCall = getCall("callInvalid.rb");
        assertNull(possibleCall);
    }

    public void testCallWithInnerCall() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithInnerCall.rb");
        assertNotNull(possibleCall);
        assertEquals("foo", possibleCall.getText());
    }

    public void testCallWithInnerCall2() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithInnerCall2.rb");
        assertNotNull(possibleCall);
        assertEquals("barbaz 1,  2, foo", possibleCall.getText());
    }

    public void testCallWithLocalVariable() throws Exception {
        final RPossibleCall possibleCall = getCall("callWithLocalVariable.rb");
        assertNotNull(possibleCall);
        assertEquals("foo a,b,c", possibleCall.getText());
    }
}