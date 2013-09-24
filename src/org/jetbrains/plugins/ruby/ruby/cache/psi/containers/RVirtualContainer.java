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
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Iconable;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg
 * @date: Oct 2, 2006
 */
public interface RVirtualContainer extends RVirtualStructuralElement, Iconable {
    /**
     * @return this container access modifier
     */
    @NotNull
    public AccessModifier getAccessModifier();

    /**
     * @return default access modifier for subclasses
     */
    @NotNull
    AccessModifier getDefaultChildAccessModifier();

    @Nullable
    public RFileInfo getContainingFileInfo();

    @NotNull
    public String getContainingFileUrl();

    @Nullable
    public VirtualFile getVirtualFile();

    @Nullable
    public ItemPresentation getPresentation();

    @NotNull
    public List<RVirtualStructuralElement> getVirtualStructureElements();

    public int getIndexOf(@NotNull RVirtualStructuralElement element);

    /**
     * @return only name without path
     */
    @NotNull
    public String getName();

    /**
     * @return List of paths
     */
    @NotNull
    public List<String> getFullPath();

    /**
     * @return full name,i.e. name with path
     */
    @NotNull
    public String getFullName();

    public boolean isGlobal();

    public Project getProject();
}
