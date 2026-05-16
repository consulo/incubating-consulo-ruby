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

package org.jetbrains.plugins.ruby.ruby.lang.psi.holders;

import java.util.List;

import consulo.annotation.access.RequiredReadAction;
import consulo.navigation.ItemPresentation;
import consulo.project.Project;
import consulo.virtualFileSystem.VirtualFile;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowOwner;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 20.07.2006
 */

public interface RContainer extends RPsiElement, ScopeHolder, RControlFlowOwner, RStructuralElement
{
	/**
	 * @return this container access modifier
	 */
	@Nonnull
	public AccessModifier getAccessModifier();

	/**
	 * @return default access modifier for subclasses
	 */
	@Nonnull
	public AccessModifier getDefaultChildAccessModifier();

	@Nonnull
	public String getContainingFileUrl();

	@Nullable
	public VirtualFile getVirtualFile();

	@Nullable
	public ItemPresentation getPresentation();

	@Nonnull
	public List<RStructuralElement> getVirtualStructureElements();

	public int getIndexOf(@Nonnull RStructuralElement element);

	@Nonnull
    @RequiredReadAction
	public String getName();

	@Nonnull
    @RequiredReadAction
	public List<String> getFullPath();

	@Nonnull
	public String getFullName();

	public boolean isGlobal();

	public Project getProject();

	@Nullable
	public RContainer getParentContainer();

	@Nonnull
	public List<RStructuralElement> getStructureElements();
}
