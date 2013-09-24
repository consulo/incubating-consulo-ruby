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

package org.jetbrains.plugins.ruby.ruby.lang.psi.impl.controlStructures.names;

import com.intellij.psi.PsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.references.RReference;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Feb 27, 2007
 */
public class RNameNavigator {
    /**
     * Returns RName by elemen
     * @param element PsiElement, representing cname
     * @return RCpath object, if found, null otherwise
     */
    public static RName getRName(final PsiElement element){
        PsiElement candidate = element.getParent();
        if (candidate instanceof RName){
            return (RName) candidate;
        }
        if (candidate instanceof RReference &&
                ((RReference) candidate).getValue() == element &&
                candidate.getParent() instanceof RName){
            return (RName) candidate.getParent();
        }
        return null;
    }
}
