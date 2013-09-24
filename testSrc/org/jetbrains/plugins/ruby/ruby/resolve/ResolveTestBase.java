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

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.ruby.cache.AbstractRubyModuleCacheTest;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Nov 21, 2007
 */
public abstract class ResolveTestBase extends AbstractRubyModuleCacheTest{
    @NonNls protected static final String CARET_MARKER = "#caret#";
    protected FileSymbol myFileSymbol;

    protected PsiReference getReference(final String path) throws IOException {
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
        return myFile.findReferenceAt(caretOffset.get());
    }
}