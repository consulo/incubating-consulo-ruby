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

package org.jetbrains.plugins.ruby.jruby.codeInsight.resolve;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: Sep 13, 2007
 */
public class JavaResolveUtil {

    public static final String[] TOP_LEVEL_JAVA_ALLOWED = new String[]{
            "java",
            "javax",
            "org",
            "com"
    };


    public static boolean isTopLevelPackageOk(@NotNull final PsiPackage packaggge){
        final String name = packaggge.getName();
        for (String s : TOP_LEVEL_JAVA_ALLOWED) {
            if (s.equals(name)){
                return true;
            }
        }
        return false;
    }

    @NotNull
    public static List<PsiElement> getTopLevelPackagesAndClasses(@NotNull final Project project) {
        final PsiManager psiManager = PsiManager.getInstance(project);
        final ArrayList<PsiElement> list = new ArrayList<PsiElement>();
        final PsiPackage topLevel = psiManager.findPackage("");
        if (topLevel != null) {
// Add ALL top level packages
            for (PsiPackage psiPackage : topLevel.getSubPackages()) {
                if (psiPackage.isValid()){
                    list.add(psiPackage);
                }
            }
// Add top level classes
            for (PsiClass psiClass : topLevel.getClasses()) {
                if (psiClass.isValid()){
                    list.add(psiClass);
                }
            }
        }
        return list;
    }

    @NotNull
    public static List<PsiElement> getAllowedTopLevelPackagesAndClasses(@NotNull final Project project) {
        final PsiManager psiManager = PsiManager.getInstance(project);
        final ArrayList<PsiElement> list = new ArrayList<PsiElement>();
// Add ALLOWED top level packages
        for (String name : TOP_LEVEL_JAVA_ALLOWED) {
            final PsiPackage psiPackage = psiManager.findPackage(name);
            if (psiPackage!=null && psiPackage.isValid()){
                list.add(psiPackage);
            }
        }

// Add top level classes
        final PsiPackage topLevel = psiManager.findPackage("");
        if (topLevel != null && topLevel.isValid()) {
            for (PsiClass psiClass : topLevel.getClasses()) {
                if (psiClass.isValid()){
                    list.add(psiClass);
                }
            }
        }
        return list;
    }

    @Nullable
    public static PsiElement getPackageOrClass(@NotNull final Project project, @NotNull final String fullName) {
        final PsiManager psiManager = PsiManager.getInstance(project);
        final PsiPackage psiPackage = psiManager.findPackage(fullName);
        if (psiPackage!=null && psiPackage.isValid()){
            return psiPackage;
        }
        final PsiClass psiClass = psiManager.findClass(fullName, GlobalSearchScope.allScope(project));
        if (psiClass!=null && psiClass.isValid()){
            return psiClass;
        }
        return null;
    }

    @Nullable
    public static PsiElement getPackageOrClass(@NotNull final Project project,
                                               @NotNull final List<String> path) {
        final StringBuffer buffer = new StringBuffer();
        for (String name : path) {
            if (buffer.length()>0){
                buffer.append('.');
            }
            buffer.append(name);
        }
        return getPackageOrClass(project, buffer.toString());
    }
}
