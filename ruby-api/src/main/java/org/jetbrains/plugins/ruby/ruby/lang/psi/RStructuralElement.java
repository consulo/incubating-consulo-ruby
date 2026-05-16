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

import consulo.annotation.access.RequiredReadAction;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.lang.psi.holders.RContainer;

/**
 * This interface is provide the same interface for structural ruby elements such as
 * container, alias call, require call, include call etc.
 */
public interface RStructuralElement extends RPsiElement {
    @RequiredReadAction
    public StructureType getType();

    @Nullable
    public RContainer getVirtualParentContainer();

    boolean equalsToVirtual(@Nonnull final RStructuralElement element);
}
