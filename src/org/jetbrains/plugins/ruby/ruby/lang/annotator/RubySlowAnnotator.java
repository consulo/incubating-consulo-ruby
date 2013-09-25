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

package org.jetbrains.plugins.ruby.ruby.lang.annotator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RFile;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.ExternalAnnotator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiFile;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 6, 2007
 */
public class RubySlowAnnotator extends ExternalAnnotator {

    public void annotate(@NotNull final PsiFile file, @NotNull final AnnotationHolder holder) {
        // We hope it`s enough often operation
        ProgressManager.getInstance().checkCanceled();

        assert file instanceof RFile;
// Force Updating symbol before annotattng
        ((RFile) file).getFileSymbol();

        final RubySlowAnnotatorVisitor slowAnnotatorVisitor = new RubySlowAnnotatorVisitor(holder, (RFile) file);
        file.accept(slowAnnotatorVisitor);
    }
}
