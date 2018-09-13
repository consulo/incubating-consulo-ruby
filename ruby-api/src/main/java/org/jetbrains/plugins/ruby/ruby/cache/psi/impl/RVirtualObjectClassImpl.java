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

import com.intellij.navigation.ItemPresentation;
import consulo.awt.TargetAWT;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.ruby.ruby.cache.info.RFileInfo;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RVirtualName;
import org.jetbrains.plugins.ruby.ruby.cache.psi.RubyVirtualElementVisitor;
import org.jetbrains.plugins.ruby.ruby.cache.psi.StructureType;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualContainer;
import org.jetbrains.plugins.ruby.ruby.cache.psi.containers.RVirtualObjectClass;
import org.jetbrains.plugins.ruby.ruby.lang.psi.controlStructures.AccessModifier;
import org.jetbrains.plugins.ruby.ruby.presentation.RObjectClassPresentationUtil;

import javax.swing.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author: oleg, Roman Chernyatchik
 * @date: Oct 2, 2006
 */
public class RVirtualObjectClassImpl extends RVirtualFieldContantContainerImpl implements RVirtualObjectClass
{
	public RVirtualObjectClassImpl(@NotNull final RVirtualContainer parentContainer, @NotNull final RVirtualName name, final AccessModifier accessModifier, @NotNull RFileInfo containingFileInfo)
	{
		super(parentContainer, name, accessModifier, containingFileInfo);
	}

	@Override
	@NotNull
	public ItemPresentation getPresentation()
	{
		return RObjectClassPresentationUtil.getPresentation(this);
	}

	public Icon getIcon(int flags)
	{
		return TargetAWT.to(RObjectClassPresentationUtil.getIcon());
	}

	@Override
	@NotNull
	public String getPresentableName()
	{
		return "<<" + getFullName();
	}

	@Override
	public void accept(@NotNull RubyVirtualElementVisitor visitor)
	{
		visitor.visitRVirtualObjectClass(this);
	}

	public String toString()
	{
		return "class [" + ((RVirtualElementBase) getVirtualName()).getId() + "] " + getPresentableName();
	}

	@Override
	public StructureType getType()
	{
		return StructureType.OBJECT_CLASS;
	}
}
