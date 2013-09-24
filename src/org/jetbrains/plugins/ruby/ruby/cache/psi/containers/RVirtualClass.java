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

package org.jetbrains.plugins.ruby.ruby.cache.psi.containers;

import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 2, 2006
 */
public interface RVirtualClass extends RVirtualFieldContantContainer {

    /**
     * Is used only by pure virtual elements. For PsiElements it is null;
     * TODO
     *   it is strange... the idea of Virtual Interface for Psi elements was
     *   similar virtual behavior on this interface... maybe we should separate
     *   virtual and psi interfeces 
     * @return name
     */
    @Nullable
    public RVirtualName getVirtualSuperClass();

    @NotNull
    public ItemPresentation getPresentation();
}
