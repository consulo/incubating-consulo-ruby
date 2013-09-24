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

package org.jetbrains.plugins.ruby.ruby.lang.psi;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Jun 20, 2007
 */

/**
 * This interface is provide the same interface for structural ruby elements such as
 * container, alias call, require call, include call etc.
 */
public interface RStructuralElement extends RVirtualStructuralElement, RPsiElement {
    /**
     * Creates virtual copy for this structural element with container as parent
     * In plural, because require, include can have more than one argument
     * @param container Parent container
     * @param info RFileInfo to store
     * @return RVirtualStructuralElement - the copy
     */
    @NotNull
    public RVirtualStructuralElement createVirtualCopy(@Nullable final RVirtualContainer container,
                                                       final RFileInfo info);

    boolean equalsToVirtual(@NotNull final RVirtualStructuralElement element);
}
