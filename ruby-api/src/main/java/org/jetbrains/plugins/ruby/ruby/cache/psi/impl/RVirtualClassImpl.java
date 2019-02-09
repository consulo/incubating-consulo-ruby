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
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualClass;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.presentation.RClassPresentationUtil;
import com.intellij.navigation.ItemPresentation;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyathcik
 * @date: Oct 2, 2006
 */
public class RVirtualClassImpl extends RVirtualFieldContantContainerImpl implements RVirtualClass
{

	private RVirtualName myVirtualSuperClass;

	public RVirtualClassImpl(@Nonnull final RVirtualContainer parentContainer, @Nonnull final RVirtualName virtualName, @Nullable final RVirtualName virtualSuperClass, @Nonnull final AccessModifier defaultChildAccessModifier, @Nonnull final RFileInfo containingFileInfo)
	{
		super(parentContainer, virtualName, defaultChildAccessModifier, containingFileInfo);
		myVirtualSuperClass = virtualSuperClass;
	}


	@Override
	@Nonnull
	public ItemPresentation getPresentation()
	{
		return RClassPresentationUtil.getPresentation(this);
	}

	@Nullable
	public Icon getIcon(final int flags)
	{
		return RClassPresentationUtil.getIcon(this, flags);
	}

	@Override
	@Nullable
	public RVirtualName getVirtualSuperClass()
	{
		return myVirtualSuperClass;
	}

	@Override
	public void accept(@Nonnull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualClass(this);
	}

	public String toString()
	{
		return "class [" + ((RVirtualElementBase) getVirtualName()).getId() + "] " + getFullName() +
				(myVirtualSuperClass != null ? " < [" + ((RVirtualElementBase) myVirtualSuperClass).getId() + "] " + myVirtualSuperClass.getFullName() : "");
	}

	@Override
	public StructureType getType()
	{
		return StructureType.CLASS;
	}
}
