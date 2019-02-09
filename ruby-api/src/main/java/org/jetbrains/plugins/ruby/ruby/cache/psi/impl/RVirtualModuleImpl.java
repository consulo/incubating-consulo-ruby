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

package org.jetbrains.plugins.ruby.ruby.cache.psi.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.Icon;

import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualModule;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.presentation.RModulePresentationUtil;
import com.intellij.navigation.ItemPresentation;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Oct 2, 2006
 */
public class RVirtualModuleImpl extends RVirtualFieldContantContainerImpl implements RVirtualModule
{
	public RVirtualModuleImpl(@Nonnull final RVirtualContainer parentContainer, @Nonnull final RVirtualName virtualName, @Nonnull final AccessModifier defaultChildAccessModifier, @Nonnull final RFileInfo containingFileInfo)
	{
		super(parentContainer, virtualName, defaultChildAccessModifier, containingFileInfo);
	}


	@Override
	@Nonnull
	public ItemPresentation getPresentation()
	{
		return RModulePresentationUtil.getPresentation(this);
	}

	@Nullable
	public Icon getIcon(final int flags)
	{
		return RModulePresentationUtil.getIcon(this, flags);
	}

	@Override
	public void accept(@Nonnull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualModule(this);
	}

	public String toString()
	{
		return "module [" + ((RVirtualElementBase) getVirtualName()).getId() + "] " + getFullName();
	}

	@Override
	public StructureType getType()
	{
		return StructureType.MODULE;
	}
}
