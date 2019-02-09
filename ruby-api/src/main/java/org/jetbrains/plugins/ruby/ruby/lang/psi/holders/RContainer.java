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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.codeInsight.resolve.scope.ScopeHolder;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RPsiElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.RStructuralElement;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlFlow.RControlFlowOwner;

/**
 * Created by IntelliJ IDEA.
 * User: oleg
 * Date: 20.07.2006
 */

public interface RContainer extends RVirtualContainer, RPsiElement, ScopeHolder, RControlFlowOwner, RStructuralElement
{

	@Nonnull
	public List<RStructuralElement> getStructureElements();

	/**
	 * Container always have only one copy
	 */
	@Override
	@SuppressWarnings({"JavaDoc"})
	@Nonnull
	public abstract RVirtualContainer createVirtualCopy(@Nullable final RVirtualContainer container, @Nonnull final RFileInfo info);

}
