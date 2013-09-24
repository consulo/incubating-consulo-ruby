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

package org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.methods;

import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualMethod;
import org.jetbrains.plugins.ruby.ruby.lang.formatter.models.wrap.RWrapLastChild;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.RFormatStructureElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.blocks.RCompoundStatement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.names.RMethodName;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 18.07.2006
 */
public interface RMethod extends RVirtualMethod,
        RFormatStructureElement, RWrapLastChild, PsiNamedElement, RContainer {

    @NonNls String INITIALIZE = "initialize";
    @NonNls String NEW = "new";
    @NonNls String JAVA_CLASS = "java_class";
    @NonNls String RESPONDS_TO = "responds_to?";


    @SuppressWarnings({"NullableProblems"})
    @Nullable
    public RMethodName getMethodName();

    @Nullable
    public RArgumentList getArgumentList();

    public boolean isConstructor();

    @NotNull
    public String getPresentableName(final boolean includeDefaultArgs);

    RCompoundStatement getCompoundStatement();
}
