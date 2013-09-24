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

package org.jetbrains.plugins.ruby.jruby.autocomplete;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.IntRef;
import org.jetbrains.plugins.ruby.jruby.AbstractJRubyModuleTest;
import org.jetbrains.plugins.ruby.ruby.codeInsight.completion.RubyLookupItem;
import org.jetbrains.plugins.ruby.ruby.codeInsight.references.RPsiPolyvariantReference;
import org.jetbrains.plugins.ruby.ruby.codeInsight.symbols.structure.FileSymbol;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import org.jetbrains.plugins.ruby.support.TestUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 4, 2008
 */
public class AbstractJRubyAutocompleteTest extends AbstractJRubyModuleTest {
    protected RFile myFile;
    protected PsiReference myReference;
    protected String myResult;
    @NonNls
    protected static final String CARET_MARKER = "#caret#";
    @NonNls
    protected static final String RESULT_MARKER = "#result#";
    protected FileSymbol myFileSymbol;

    public void doTest(@NotNull final String path) throws Exception {
        init(path);
        assertTrue(myReference instanceof RPsiPolyvariantReference);
        assertEquals(myResult, variantsToString(myReference.getVariants()));
    }

    private String variantsToString(final Object[] variants) {
        final ArrayList<String> vars = new ArrayList<String>();
        for (Object variant : variants) {
            if (variant instanceof RubyLookupItem){
                vars.add(((RubyLookupItem) variant).getName());
            }
        }
        Collections.sort(vars);
        final StringBuilder builder = new StringBuilder();
        for (String var : vars) {
            builder.append(var).append("\n");
        }
        return builder.toString().trim();
    }

    protected void init(final String path) throws IOException {
        final VirtualFile file = getFile(path, myModule);
        assertNotNull(file);

        String fileText = StringUtil.convertLineSeparators(VfsUtil.loadText(file), "\n");

        final IntRef caretMarker = new IntRef(fileText.indexOf(CARET_MARKER));
        assertTrue(caretMarker.get() >= 0);
        final IntRef resultMarker = new IntRef(fileText.indexOf(RESULT_MARKER));
        assertTrue(resultMarker.get() >= 0);

        fileText = TestUtil.removeSubstring(fileText, caretMarker.get(), CARET_MARKER.length(), caretMarker, resultMarker);
        fileText = TestUtil.removeSubstring(fileText, resultMarker.get(), RESULT_MARKER.length(), caretMarker, resultMarker);

        myResult = fileText.substring(resultMarker.get()).trim();

        fileText = fileText.substring(0, resultMarker.get());

        VfsUtil.saveText(file, fileText);

        myFile = (RFile) PsiManager.getInstance(myProject).findFile(file);
        myFileSymbol = myFile.getFileSymbol();
        myReference = myFile.findReferenceAt(caretMarker.get());
    }
}
