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

package org.jetbrains.plugins.ruby.jruby.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiPackage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.RBundle;
import org.jetbrains.plugins.ruby.jruby.codeInsight.resolve.JavaResolveUtil;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.ResolveUtil;
import org.jetbrains.plugins.ruby.ruby.inspections.RubyInspectionVisitor;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.variables.RIdentifier;

import java.util.List;

/**
 * @author: oleg
 */
public class WrongTopLevelPackageInspectionVisitor extends RubyInspectionVisitor {

    public WrongTopLevelPackageInspectionVisitor(final ProblemsHolder holder) {
        super(holder);
    }

    @Override
    public void visitRIdentifier(final RIdentifier rIdentifier) {
        // It`s often operation
        ProgressManager.getInstance().checkCanceled();
        visitLeftElement(rIdentifier);
    }

    public void visitLeftElement(@NotNull final RPsiElement leftElement) {
// RUBY-1679
        final List<PsiElement> list = ResolveUtil.multiResolve(leftElement);
        if (list.size()==1){
            final PsiElement element = list.get(0);
            if (element instanceof PsiPackage && !JavaResolveUtil.isTopLevelPackageOk((PsiPackage) element)){
                registerProblem(leftElement,
                        RBundle.message("inspection.wrong.top.level.package.should.be.one.of", "java, javax, org, com"),
                        new WrongTopLevelPackageFix(leftElement));
            }
        }
    }
}